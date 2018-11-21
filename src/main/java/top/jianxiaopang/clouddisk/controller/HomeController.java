package top.jianxiaopang.clouddisk.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import top.jianxiaopang.clouddisk.pojo.FileNode;
import top.jianxiaopang.clouddisk.pojo.Result;
import top.jianxiaopang.clouddisk.pojo.SearchPojo;
import top.jianxiaopang.clouddisk.service.HomeService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 主页控制类
 */
@Controller
@RequestMapping("/home")
public class HomeController {
	@Autowired
	private HomeService homeService;

	/**
	 * 批量上传
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/upload")
	@ResponseBody
	public Result upload(HttpServletRequest request, HttpServletResponse response) {
		Result result = homeService.upload(request);
		return result;
	}

	/**
	 * 展示文件列表
	 *
	 * @param parentNodeId
	 * @param userId
	 * @return
	 */
	@RequestMapping("/listFileNode")
	@ResponseBody
	public Result listFileNode(Integer parentNodeId, Integer userId) {
		Result result = homeService.listFileNode(parentNodeId, userId);
		return result;
	}

//	/**
//	 * 单个下载
//	 * @param request
//	 * @param response
//	 * @param nodeId
//	 * @return
//	 */
//	@RequestMapping("/download")
//	@ResponseBody
//	public Result download(HttpServletRequest request, HttpServletResponse response, Integer nodeId) {
//		Result result = homeService.download(nodeId, response);
//		return result;
//	}

	/**
	 * 批量下载
	 *
	 * @param response
	 * @param ids
	 * @return
	 */
	@RequestMapping("/download")
	@ResponseBody
	public Result download(HttpServletResponse response, String[] ids) {
		//字符串数组转整数集合
		List<Integer> idsList = stringToInteger(ids);
		Result result = homeService.download(idsList, response);
		return result;
	}

	/**
	 * 创建文件夹
	 *
	 * @param fileNode
	 * @return
	 */
	@RequestMapping("/createFolder")
	@ResponseBody
	public Result createFolder(FileNode fileNode) {
		Result result = homeService.createFolder(fileNode);
		return result;
	}

	/**
	 * 批量删除文件
	 *
	 * @param ids
	 * @return
	 */
	@RequestMapping("/deleteFile")
	@ResponseBody
	public Result deleteFile(String[] ids) {
		//字符串数组转整数集合
		List<Integer> idsList = stringToInteger(ids);
		Result result = homeService.deleteFile(idsList);
		return result;
	}

	/**
	 * 批量移动文件
	 *
	 * @param logicalPath
	 * @param parentNodeId
	 * @param ids
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/moveFile")
	public Result moveFile(String logicalPath, String parentNodeId, String[] ids) {
		//字符串数组转整数集合
		List<Integer> idsList = stringToInteger(ids);
		Result result = homeService.moveFile(logicalPath, parentNodeId, idsList);
		return result;
	}

	/**
	 * 获取上传进度
	 *
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getProgress")
	public Result getProgress(HttpServletRequest request) {
		String sessionId = request.getSession().getId();
		return homeService.getProgress(sessionId);
	}

	/**
	 * 对文件分类
	 *
	 * @param fileNode
	 * @param method
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/orderBy")
	public Result orderBy(FileNode fileNode, String method) {
		Result result = homeService.orderBy(fileNode, method);
		return result;
	}

	/**
	 * 搜索功能
	 *
	 * @param searchPojo
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/search")
	public Result search(SearchPojo searchPojo) {
		Result result = homeService.search(searchPojo);
		return result;
	}

	/**
	 * 图片，视频，音乐预览
	 *
	 * @param fileNode
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/preview")
	public String preview(FileNode fileNode) {
		String savePath = homeService.preview(fileNode);
		return savePath;
	}

	/**
	 * 文件分享
	 *
	 * @param ids
	 * @param type
	 * @param deadline
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/share")
	public Result share(String[] ids, String type, Integer deadline) {
		//字符串数组转整数集合
		List<Integer> idsList = stringToInteger(ids);
		Result result = homeService.share(idsList, type, deadline);
		return result;
	}

	/**
	 * 分享验证，转发到分享页面
	 *
	 * @param token
	 * @param code
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping("/shareCheck/{token}")
	public Result shareCheck(@PathVariable String token, String code, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Result result = homeService.shareCheck(token, code, request, response);
		return result;
	}

	/**
	 * 字符串数组转整数集合
	 *
	 * @param ids
	 * @return
	 */
	private List<Integer> stringToInteger(String[] ids) {
		List<Integer> idsList = new ArrayList<>();
		for (int i = 0; i < ids.length; i++) {
			idsList.add(Integer.parseInt(ids[i]));
		}
		return idsList;
	}
}
