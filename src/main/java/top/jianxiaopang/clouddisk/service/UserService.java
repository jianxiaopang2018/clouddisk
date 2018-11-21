package top.jianxiaopang.clouddisk.service;

import top.jianxiaopang.clouddisk.pojo.Result;
import top.jianxiaopang.clouddisk.pojo.User;

import javax.servlet.http.HttpServletRequest;

public interface UserService {
	public Result register(User user);

	public Result selectIsRegister(String email);

	public Result checkPassword(String password);

	public Result activate(String verificationCode);

	public Result login(User user);

	public Result updateUser(HttpServletRequest request);
}
