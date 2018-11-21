package top.jianxiaopang.clouddisk.dao;

import top.jianxiaopang.clouddisk.pojo.User;

public interface UserDao {
	public void insert(User user);

	public int selectByEmail(String email);

	public String selectByCode(String verificationCode);

	public void setStatus(String email);

	public User login(User user);

	public User loginWithoutPassword(User user);

	public void updateUser(User user);
}
