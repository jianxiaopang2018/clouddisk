package top.jianxiaopang.clouddisk.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import top.jianxiaopang.clouddisk.pojo.Result;
import top.jianxiaopang.clouddisk.pojo.User;
import top.jianxiaopang.clouddisk.service.UserService;
import top.jianxiaopang.clouddisk.utils.JWT;
import top.jianxiaopang.clouddisk.utils.VerificationCode;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;

	/**
	 * 注册账号
	 *
	 * @param request
	 * @param response
	 * @param user
	 * @return
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping("/register")
	public Result register(HttpServletRequest request, HttpServletResponse response, User user) {
		Result result = userService.register(user);
		return result;
	}

	/**
	 * 查询邮箱是否已被注册和验证邮箱格式
	 *
	 * @param request
	 * @param response
	 * @param email
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping("/selectIsRegister")
	@ResponseBody
	public Result selectIsRegister(HttpServletRequest request, HttpServletResponse response, String email) throws UnsupportedEncodingException {
		Result result = userService.selectIsRegister(email);
		return result;
	}

	/**
	 * 验证密码格式正确否
	 *
	 * @param password
	 * @return
	 */
	@RequestMapping("/checkPassword")
	@ResponseBody
	public Result checkPassword(String password) {
		Result result = userService.checkPassword(password);
		return result;
	}

	/**
	 * 账号激活
	 *
	 * @param request
	 * @param response
	 * @param verificationCode
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@ResponseBody
	@RequestMapping("/activate")
	public Result activate(HttpServletRequest request, HttpServletResponse response, String verificationCode) throws UnsupportedEncodingException {
		Result result = userService.activate(verificationCode);
		return result;
	}

	/**
	 * 得到验证码图片
	 *
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/getCodeImage")
	public void getCodeImage(HttpServletRequest request, HttpServletResponse response) throws IOException {
		VerificationCode verificationCode = new VerificationCode();
		//获取验证码图片
		BufferedImage image = verificationCode.getImage();
		//获取验证码内容
		String text = verificationCode.getText();
		//System.out.println(text);
		// 将验证码保存到Session中。
		HttpSession session = request.getSession();
		session.setAttribute("code", text);
		// 禁止图像缓存。
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setContentType("image/jpeg");
		// 将图像输出到Servlet输出流中。
		ServletOutputStream sos = response.getOutputStream();
		ImageIO.write(image, "jpeg", sos);
		sos.flush();
		sos.close();
	}

	/**
	 * 图片验证码验证
	 *
	 * @param request
	 * @param response
	 * @param code
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/validate")
	public Result validate(HttpServletRequest request, HttpServletResponse response, String code) {
		HttpSession session = request.getSession();
		String sessionCode = (String) session.getAttribute("code");
		//System.out.println(sessionCode);
		//System.out.println(code);
		//验证的时候不区分大小写
		if (code.equalsIgnoreCase(sessionCode)) {
			return Result.message(200, "验证码正确。");
		}
		return Result.message(400, "验证码错误，请重新输入！");
	}

	/**
	 * 账号登录
	 *
	 * @param request
	 * @param response
	 * @param user
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/login")
	public Result login(HttpServletRequest request, HttpServletResponse response, User user) {
		Result result = userService.login(user);
		return result;
	}

	/**
	 * 从token中获得用户信息并返回
	 *
	 * @param token
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getInfo")
	public Result getInfo(@RequestParam String token) {
		User user = JWT.unsign(token, User.class);
		if (user == null) {
			return Result.message(400, "登录超时，请重新登录！");
		}
		return Result.success(user);
	}

	/**
	 * 更新用户信息
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/updateUser")
	public Result updateUser(HttpServletRequest request, HttpServletResponse response) {
		Result result = userService.updateUser(request);
		return result;
	}
}
