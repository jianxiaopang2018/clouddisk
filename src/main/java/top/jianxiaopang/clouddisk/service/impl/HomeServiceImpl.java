package top.jianxiaopang.clouddisk.service.impl;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.jianxiaopang.clouddisk.dao.HomeDao;
import top.jianxiaopang.clouddisk.pojo.*;
import top.jianxiaopang.clouddisk.service.HomeService;
import top.jianxiaopang.clouddisk.utils.JWT;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class HomeServiceImpl implements HomeService {
	@Autowired
	private HomeDao homeDao;

	/**
	 * 文件批量上传
	 *
	 * @param request
	 * @return
	 */
	@Override
	public Result upload(HttpServletRequest request) {
		Map map = new HashMap();
		String savePath = null;
		String fileName = null;
		String uuidName = null;
		String sessionId = null;
		String visitPath = null;
		try {
			//使用Apache文件上传组件处理文件上传：
			//创建一个DiskFileItemFactory工厂
			DiskFileItemFactory factory = new DiskFileItemFactory();
			//创建一个文件上传解析器
			ServletFileUpload upload = new ServletFileUpload(factory);
			//解决上传文件名的中文乱码
			upload.setHeaderEncoding("UTF-8");
			//使用ServletFileUpload解析器解析上传数据，解析结果返回的是一个List<FileItem>集合，每一个FileItem对应一个Form表单的输入项
			List<FileItem> list = upload.parseRequest(request);
			for (FileItem item : list) {
				//如果是普通表单数据
				if (item.isFormField()) {
					String name = item.getFieldName();
				//解决普通输入项的数据的中文乱码问题
				String value = item.getString("UTF-8");
				//System.out.println(name + "=" + value);
				//把信息保存在map中，以便在插入时提取数据
				map.put(name, value);
			}
			}

			//以sessionId做唯一标志
			sessionId = request.getSession().getId();
			long size = 0;
			long progress = 0;
			//获取上传的文件大小
			for (FileItem item : list) {
				if (!item.isFormField()) {
					size += item.getSize();
				}
			}
			Progress.put(sessionId + "size", size);
			Progress.put(sessionId + "progress", size);

			//获取上传的文件
			for (FileItem item : list) {
				if (!item.isFormField()) {
					//得到文件的大小
					size = item.getSize();
					//得到上传的文件名称，
					fileName = item.getName();
					//System.out.println(fileName);
					if (fileName == null || fileName.equals("")) {
						continue;
					}
					//windows下的保存路径
					//savePath = "D:\\" + map.get("userId");
					//linux下的保存路径
					savePath = "/userdata/" + map.get("userId");
					File file = new File(savePath);
					if (!file.exists() && !file.isDirectory()) {
						//System.out.println(savePath + "目录不存在，需要创建");
						//创建目录
						file.mkdirs();
					}
					//处理获取到的上传文件的文件名的路径部分，只保留文件名部分
					fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);
					//给保存路径换一个新名，以防保存路径下文件重复
					uuidName = UUID.randomUUID().toString() + "_" + fileName;
					savePath = savePath + File.separator + uuidName;
					visitPath = "https://www.jianxiaopang.top/visit" + savePath;
					//获取item中的上传文件的输入流
					InputStream in = item.getInputStream();
					//创建一个文件输出流
					FileOutputStream out = new FileOutputStream(savePath);
					//创建一个缓冲区
					byte buffer[] = new byte[1024];
					//判断输入流中的数据是否已经读完
					int len = 0;
					//循环将输入流读入到缓冲区当中
					while ((len = in.read(buffer)) > 0) {
						//使用FileOutputStream输出流将缓冲区的数据写入到指定的目录(savePath + "\\" + filename)当中
						out.write(buffer, 0, len);
						//更新上传路径
						progress += len;
						Progress.put(sessionId + "progress", progress);
					}
					//关闭输入流
					in.close();
					//关闭输出流
					out.close();
					//删除处理文件上传时生成的临时文件
					item.delete();

					//创建文件对象，然后插入数据库
					FileNode fileNode = new FileNode();
					//如果为空则设置根目录
					if (map.get("logicalPath") == null || map.get("logicalPath").equals("")) {
						fileNode.setLogicalPath("/");
					} else {
						fileNode.setLogicalPath((String) map.get("logicalPath"));
					}
					//如果为空则设置0为根节点
					if (map.get("parentNodeId") == null || map.get("parentNodeId").equals("")) {
						fileNode.setParentNodeId(0);
					} else {
						fileNode.setParentNodeId(Integer.parseInt((String) map.get("parentNodeId")));
					}
					fileNode.setUserId(Integer.parseInt((String) map.get("userId")));
					fileNode.setFileName(fileName);
					fileNode.setIsFolder(0);
					fileNode.setSavePath(savePath);
					fileNode.setUuidName(uuidName);
					fileNode.setSize(transformSize(size));
					fileNode.setVisitPath(visitPath);
					int num = homeDao.selectFolderByFileName(fileNode);
					//有重名文件便改名
					if (num > 0) {
						fileNode.setFileName(fileName.substring(0, fileName.lastIndexOf(".") - 1) + "(" + num + ")" + fileName.substring(fileName.lastIndexOf(".")));
					}
					homeDao.insertFile(fileNode);
				}
			}
		} catch (FileUploadException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			//上传完毕清除进度
			Progress.remove(sessionId + "size");
			Progress.remove(sessionId + "progress");
		}
		if (map.get("parentNodeId") == null || map.get("parentNodeId").equals("")) {
			return listFileNode(0, Integer.parseInt((String) map.get("userId")));
		}
		//上传后重新展示列表
		return listFileNode(Integer.parseInt((String) map.get("parentNodeId")), Integer.parseInt((String) map.get("userId")));
	}

	/**
	 * 展示列表
	 *
	 * @param parentNodeId
	 * @param userId
	 * @return
	 */
	@Override
	public Result listFileNode(Integer parentNodeId, Integer userId) {
		Map map = new HashMap();
		String logicalPath = null;
		if (parentNodeId == null || parentNodeId == 0) {
			parentNodeId = 0;
			logicalPath = "/";
		} else logicalPath = homeDao.selectLogicalPathById(parentNodeId);
		FileNode fileNode = new FileNode();
		fileNode.setParentNodeId(parentNodeId);
		fileNode.setUserId(userId);
		List<FileNode> list = homeDao.selectFileNodeByParentNodeId(fileNode);
		map.put("userId", userId);
		map.put("parentNodeId", parentNodeId);
		map.put("logicalPath", logicalPath);
		map.put("file", list);
		return Result.success(map);
	}

	/**
	 * 批量下载文件
	 *
	 * @param ids
	 * @param response
	 * @return
	 */
	@Override
	public Result download(List<Integer> ids, HttpServletResponse response) {
		//获取要下载的文件List
		List<FileNode> fileNodeList = new ArrayList<>();
		//通过id去查询要下载的文件信息保存到fileNodeList中
		for (Integer id : ids) {
			FileNode fileNode = homeDao.getFileNode(id);
			//调用递归遍历文件
			recursion2(fileNode, fileNodeList);
		}
		//压缩包起个名字
		String fileName = fileNodeList.get(1).getFileName() + "等.zip";
		byte[] buffer = new byte[1024];
		//linux
		String strZipPath = File.separator + "temp" + File.separator + fileName;
		File file = new File(File.separator + "temp");
		//windows
		//String strZipPath = "D:" + File.separator + "temp" + File.separator + fileName;
		//File file = new File("D:" + File.separator + "temp");
		if (!file.isDirectory() && !file.exists()) {
			file.mkdirs();
		}
		try {
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(strZipPath));
			// 需要同时下载的多个文件
			for (FileNode fileNode : fileNodeList) {
				File f = new File(fileNode.getSavePath());
				FileInputStream fis = new FileInputStream(f);
				//System.out.println(fileNode.getFileName());
				out.putNextEntry(new ZipEntry(fileNode.getFileName()));
				//设置压缩文件内的字符编码，不然会变成乱码
				//out.setEncoding("UTF-8");
				int len;
				// 读入需要下载的文件的内容，打包到zip文件
				while ((len = fis.read(buffer)) > 0) {
					out.write(buffer, 0, len);
				}
				out.closeEntry();
				fis.close();
				//更新下载次数
				homeDao.updateDownNum(fileNode);
			}
			out.close();

			//设置响应头让浏览器下载
			response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));

			FileInputStream in = new FileInputStream(strZipPath);
			OutputStream out2 = response.getOutputStream();
			int len = 0;
			while ((len = in.read(buffer)) > 0) {
				out2.write(buffer, 0, len);
			}
			in.close();

			//删除生成的压缩包
			File temp = new File(strZipPath);
			if (temp.exists()) {
				temp.delete();
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Result.message(200, "下载成功！");
	}
//	/**
//	 * 单个下载
//	 * @param nodeId
//	 * @param response
//	 * @return
//	 */
	/*
	@Override
	public Result download(Integer nodeId, HttpServletResponse response) {
		FileNode node = homeDao.getFileNode(nodeId);
		String savePath = node.getSavePath();
		File file = new File(savePath);
		try {
			response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(node.getFileName(), "UTF-8"));
			FileInputStream in = new FileInputStream(file);
			OutputStream out = response.getOutputStream();
			int len = 0;
			byte buffer[] = new byte[1024];
			while ((len = in.read(buffer)) > 0) {
				out.write(buffer, 0, len);
			}
			in.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Result.message(200, "下载成功！");
	}
	*/

	/**
	 * 创建文件夹
	 *
	 * @param fileNode
	 * @return
	 */
	@Override
	public Result createFolder(FileNode fileNode) {
		fileNode.setIsFolder(1);
		int num = homeDao.selectFolderByFileName(fileNode);
		//有重名文件便改名
		if (num > 0) {
			fileNode.setFileName(fileNode.getFileName() + "(" + num + ")");
		}

		if (fileNode.getLogicalPath() == null || fileNode.getLogicalPath().equals("")) {
			fileNode.setLogicalPath("/");
		} else {
			String logicalPath = fileNode.getLogicalPath() + fileNode.getFileName() + "/";
			fileNode.setLogicalPath(logicalPath);
		}
		homeDao.createFolder(fileNode);
		Result result = Result.message(200, "创建文件夹成功！");
		return result;
	}

	/**
	 * 批量删除文件或者文件夹
	 *
	 * @param ids
	 * @return
	 */
	@Override
	public Result deleteFile(List<Integer> ids) {
		for (int id : ids) {
			FileNode fileNode = homeDao.getFileNode(id);
			List<FileNode> list = new ArrayList();
			list.add(fileNode);
			recursion(list);
		}
		return Result.message(200, "删除成功！");
	}

	/**
	 * 批量移动文件
	 *
	 * @param logicalPath
	 * @param parentNodeId
	 * @param ids
	 * @return
	 */
	@Override
	public Result moveFile(String logicalPath, String parentNodeId, List<Integer> ids) {
		for (Integer id : ids) {
			FileNode fileNode = homeDao.getFileNode(id);
			fileNode.setLogicalPath(logicalPath);
			fileNode.setParentNodeId(Integer.parseInt(parentNodeId));
			int num = homeDao.selectFolderByFileName(fileNode);
			//有重名文件并且是文件便改名
			if (num > 0 && fileNode.getIsFolder() == 0) {
				fileNode.setFileName(fileNode.getFileName().substring(0, fileNode.getFileName().lastIndexOf(".") - 1) + "(" + num + ")" + fileNode.getFileName().substring(fileNode.getFileName().lastIndexOf(".")));
			} else if (num > 0 && fileNode.getIsFolder() == 1) {
				fileNode.setFileName(fileNode.getFileName() + "(" + num + ")");
			}
			homeDao.updateFile(fileNode);
		}
		return Result.message(200, "移动文件or文件夹成功。");
	}

	/**
	 * 得到文件上传进度
	 *
	 * @param sessionId
	 * @return
	 */
	@Override
	public Result getProgress(String sessionId) {
		Object progress = Progress.get(sessionId + "progress");
		Object size = Progress.get(sessionId + "size");
		if (progress == null || size == null) {
			return Result.success("100%");
		}
		return Result.success((long) progress / (long) size * 100 + "%");
	}

	/**
	 * 文件分类
	 *
	 * @param fileNode
	 * @param method
	 * @return
	 */
	@Override
	public Result orderBy(FileNode fileNode, String method) {
		List<FileNode> list = null;
		if (method.equals("image")) {
			list = homeDao.selectNodeOrderByImage(fileNode);
		} else if (method.equals("document")) {
			list = homeDao.selectNodeOrderByDocument(fileNode);
		} else if (method.equals("video")) {
			list = homeDao.selectNodeOrderByVideo(fileNode);
		} else if (method.equals("music")) {
			list = homeDao.selectNodeOrderByMusic(fileNode);
		}
		return Result.success(list);
	}

	/**
	 * 搜索文件
	 *
	 * @param searchPojo
	 * @return
	 */
	@Override
	public Result search(SearchPojo searchPojo) {
		List<FileNode> list = homeDao.search(searchPojo);
		return Result.success(list);
	}

	/**
	 * 预览
	 *
	 * @param fileNode
	 * @return
	 */
	@Override
	public String preview(FileNode fileNode) {
		return homeDao.getDownUrl(fileNode);
	}

	/**
	 * 分享文件
	 *
	 * @param ids
	 * @param type
	 * @param deadline
	 * @return
	 */
	@Override
	public Result share(List<Integer> ids, String type, Integer deadline) {
		String code = null;
		String token = null;

		ShareMap map = new ShareMap();
		//如果是加密
		if (type!=null && type.equals("encrypt")) {
			code = getRandomCode();
			map.setCode(code);
		}
		map.setIds(ids);
		//有效期永久
		if (deadline == 99) {
			token = JWT.sign(map, 99999 * 24L * 3600L * 1000L);
		} else {
			token = JWT.sign(map, deadline * 24L * 3600L * 1000L);
		}
		String[] tokens=token.split("\\.");
		token = tokens[0] + "=" + tokens[1] + "=" + tokens[2];
		String url = "https://www.jianxiaopang.top/clouddisk/home/shareCheck/" + token;
		//加密就返回连接加提取码
		if (code != null && !code.equals("")) {
			Map map1 = new HashMap();
			map1.put("code", code);
			map1.put("url", url);
			return Result.success(map1);
		}
		return Result.success(url);
	}

	/**
	 * 分享验证
	 *
	 * @param token
	 * @param code
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@Override
	public Result shareCheck(String token, String code, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String[] tokens=token.split("=");
		token = tokens[0] + "." + tokens[1] + "." + tokens[2];
		//从token中取出数据
		ShareMap map = JWT.unsign(token, ShareMap.class);
		//等于null代表过期
		if (map != null) {
			//map.getCode() == null || map.getCode().equals("") 说明公开下载 | map.getCode().equals(code)说明加密并且输入的密码正确。
			if (map.getCode() == null || map.getCode().equals("") || map.getCode().equals(code)) {
				//取出分享的文件放入request，以便前端遍历显示
				List<FileNode> list = new ArrayList<>();
				for (Integer id : map.getIds()) {
					list.add(homeDao.getFileNode(id));
				}
				request.setAttribute("FileNodeList", list);
				//转发到分享页面
				request.getRequestDispatcher("/xxx").forward(request, response);
				return Result.message(200, "转发到分享页面");
			}
			//连接是加密下载，但密码不对 转发到输入提取码页面
			request.getRequestDispatcher("/xxx").forward(request, response);
			return Result.message(400, "请输入正确的提取码");
		}
		return Result.message(400, "文件已过期！");
	}

	/**
	 * 递归获取文件下的文件
	 */
	private List<FileNode> recursion2(FileNode fileNode, List<FileNode> fileNodelist) {
		//查询子节点
		List<FileNode> list = homeDao.selectFileNode(fileNode);
		//子节点是否为空，不为空则继续查子节点
		if (list != null && list.size() != 0) {
			for (FileNode node : list) {
				recursion2(node, fileNodelist);
			}
		} else if (fileNode.getIsFolder() != 1) { //是文件
			fileNodelist.add(fileNode);
		}
		return fileNodelist;
	}

	/**
	 * 递归查出文件夹下的文件
	 *
	 * @param fileNodes
	 * @return
	 */
	private void recursion(List<FileNode> fileNodes) {
		if (fileNodes == null) return;
		for (FileNode fileNode : fileNodes) {
			if (fileNode.getIsFolder() != 0) {
				recursion(homeDao.selectFileNode(fileNode));
				homeDao.deleteFile(fileNode);
			} else {
				File file = new File(fileNode.getSavePath());
				if (file.delete()) {
					System.out.println(file.getName() + "is deleted");
				} else {
					System.out.println("Delete failed.");
				}
				homeDao.deleteFile(fileNode);
			}
		}
		return;
	}

	/**
	 * 给大小加上单位
	 *
	 * @param size
	 * @return
	 */
	private String transformSize(long size) {
		float GB = 1024 * 1024 * 1024F;
		float MB = 1024 * 1024F;
		float KB = 1024F;
		String result = "";
		//格式化小数
		DecimalFormat df = new DecimalFormat("0.00");
		if (size / GB > 1) {
			result = df.format(size / GB) + "GB";
		} else if (size / MB > 1) {
			result = df.format(size / MB) + "MB";
		} else if (size / KB > 1) {
			result = df.format(size / KB) + "KB";
		} else {
			result = size + "B";
		}
		return result;
	}

	//随机获得提取码
	private String getRandomCode() {
		String codes = "123456789abcdefghjkmnopqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ";
		Random random = new Random();
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < 4; i++) {
			result.append(codes.charAt(random.nextInt(codes.length())));
		}
		return result + "";
	}
}
