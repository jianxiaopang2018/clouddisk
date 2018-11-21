package top.jianxiaopang.clouddisk.service.impl;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.jianxiaopang.clouddisk.dao.UserDao;
import top.jianxiaopang.clouddisk.pojo.FileNode;
import top.jianxiaopang.clouddisk.pojo.Result;
import top.jianxiaopang.clouddisk.pojo.User;
import top.jianxiaopang.clouddisk.service.UserService;
import top.jianxiaopang.clouddisk.utils.JWT;
import top.jianxiaopang.clouddisk.utils.Mail;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserDao userDao;

	/**
	 * 注册
	 *
	 * @param user
	 * @return
	 */
	@Override
	public Result register(User user) {
		if (user.getEmail() != null) {
			if (!user.getEmail().matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$")) {
				return Result.message(400, "邮箱格式错误！");
			}
			int i = userDao.selectByEmail(user.getEmail());
			if (i != 0) {
				return Result.message(400, "邮箱已被注册！");
			}
		} else {
			return Result.message(400, "邮箱不能为空！");
		}
		if (user.getPassword() != null) {
			if (!(user.getPassword().matches("^.*[a-zA-Z]+.*$")
					&& user.getPassword().matches("^.*[0-9]+.*$")
					&& user.getPassword().matches("^.{8,}$"))) {
				return Result.message(400, "密码格式错误，密码必须包含数字、字母两种，并且长度至少8位！");
			}
		} else {
			return Result.message(400, "密码不能为空！");
		}
		//随机生成邮箱验证码
		String verificationCode = UUID.randomUUID().toString().replace("-", "");
		user.setVerificationCode(verificationCode);
		user.setStatus(0);
		//windows下的图片
		user.setImagePath("D:" + File.separator + "image" + File.separator + "99f0fb42-51ea-4a6a-a133-4bb1b6fb8734_a6fcce1b106cdf3cc51b6c20ca9318a5.jpg");
		//linux下的图片
		user.setImagePath("/userdata/image/a6fcce1b106cdf3cc51b6c20ca9318a5.jpg");
		user.setVisitPath("https://www.jianxiaopang.top/visit/userdata/image/a6fcce1b106cdf3cc51b6c20ca9318a5.jpg");
		userDao.insert(user);
		//发送邮箱进行验证
		Mail.sendMail(user.getEmail(), verificationCode);
		return Result.message(200, "注册成功，请到邮箱激活。");
	}

	/**
	 * 查询邮箱是否已被注册
	 *
	 * @param email
	 * @return
	 */
	@Override
	public Result selectIsRegister(String email) {
		if (!email.matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$")) {
			return Result.message(400, "邮箱格式错误！");
		}
		int i = userDao.selectByEmail(email);
		if (i != 0) {
			return Result.message(400, "邮箱已被注册！");
		}
		return Result.message(200, "邮箱未被注册！");
	}

	/**
	 * 验证密码格式
	 *
	 * @param password
	 * @return
	 */
	@Override
	public Result checkPassword(String password) {
		if (password != null) {
			if (!(password.matches("^.*[a-zA-Z]+.*$")
					&& password.matches("^.*[0-9]+.*$")
					&& password.matches("^.{8,}$"))) {
				return Result.message(400, "密码格式错误，密码必须包含数字、字母两种，并且长度至少8位！");
			} else {
				return Result.message(200, "密码格式正确。");
			}
		} else {
			return Result.message(400, "密码不能为空！");
		}
	}

	/**
	 * 邮箱激活
	 *
	 * @param verificationCode
	 * @return
	 */
	@Override
	public Result activate(String verificationCode) {
		//通过验证码查用户邮箱
		String email = userDao.selectByCode(verificationCode);
		if (email != null && !"".equals(email)) {
			userDao.setStatus(email);
			return Result.message(200, "账号已激活");
		}
		return Result.message(400, "账号未注册或已激活。");
	}

	/**
	 * 登录
	 *
	 * @param user
	 * @return
	 */
	@Override
	public Result login(User user) {
		User newUser = userDao.login(user);
		if (newUser == null) {
			return Result.message(400, "邮箱或密码错误或邮箱未激活，请到邮箱激活。");
		}
		Map map = new HashMap<>();
		map.put("user", newUser);
		String token = JWT.sign(newUser, 30L * 24L * 3600L * 1000L);
		map.put("token", token);
		return Result.success(map);
	}

	/**
	 * 更新用户信息
	 *
	 * @param request
	 * @return
	 */
	@Override
	public Result updateUser(HttpServletRequest request) {
		Map map = new HashMap();
		String imageName = null;
		String imagePath = null;
		String visitPath = null;
		try {
			//创建一个DiskFileItemFactory工厂
			DiskFileItemFactory factory = new DiskFileItemFactory();
			//创建一个文件上传解析器
			ServletFileUpload upload = new ServletFileUpload(factory);
			//解决上传文件名的中文乱码
			upload.setHeaderEncoding("UTF-8");
			//使用ServletFileUpload解析器解析上传数据，解析结果返回的是一个List<FileItem>集合，每一个FileItem对应一个Form表单的输入项
			List<FileItem> list = upload.parseRequest(request);
			for (FileItem item : list) {
				if (item.isFormField()) {
					String name = item.getFieldName();
					//解决普通输入项的数据的中文乱码问题
					String value = item.getString("UTF-8");
					//value = new String(value.getBytes("iso8859-1"),"UTF-8");
					System.out.println(name + "=" + value);
					map.put(name, value);
				}
			}
			//得到头像
			for (FileItem item : list) {
				if (!item.isFormField()) {
					//得到上传的文件名称，
					imageName = item.getName();
					if (imageName == null || imageName.equals("")) {
						continue;
					}
					//windows下的路径
					//imagePath = "D:" + File.separator + "image";
					//linux下的路径
					imagePath = "/userdata/image";
					File file = new File(imagePath);
					if (!file.exists() && !file.isDirectory()) {
						//System.out.println(imagePath + "目录不存在，需要创建");
						//创建目录
						file.mkdir();
					}
					//处理获取到的上传文件的文件名的路径部分，只保留文件名部分
					imageName = imageName.substring(imageName.lastIndexOf("\\") + 1);
					imageName = UUID.randomUUID().toString() + "_" + imageName;
					imagePath = imagePath + File.separator + imageName;
					visitPath = "https://www.jianxiaopang.top/visit" + imagePath;
					//获取item中的上传文件的输入流
					InputStream in = item.getInputStream();
					//创建一个文件输出流
					FileOutputStream out = new FileOutputStream(imagePath);
					//创建一个缓冲区
					byte buffer[] = new byte[1024];
					int len = 0;
					while ((len = in.read(buffer)) > 0) {
						out.write(buffer, 0, len);
					}
					//关闭输入流
					in.close();
					//关闭输出流
					out.close();
					//删除处理文件上传时生成的临时文件
					item.delete();
				}
			}
		} catch (FileUploadException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		User user = new User();
		user.setUsername((String) map.get("username"));
		user.setEmail((String) map.get("email"));
		user.setImagePath(imagePath);
		user.setVisitPath(visitPath);
		userDao.updateUser(user);

		User newUser = userDao.loginWithoutPassword(user);
		Map returnMap = new HashMap<>();
		returnMap.put("user", newUser);
		String token = JWT.sign(newUser, 30L * 24L * 3600L * 1000L);
		returnMap.put("token", token);
		return Result.success(returnMap);
	}
}

