package top.jianxiaopang.clouddisk.pojo;

import java.util.Date;

/**
 * 用户封装类
 */
public class User {

	private long id;                //用户id
	private String email;            //邮箱
	private String password;        //密码
	private String username;        //用户名
	private int status;                //激活状态0为未激活，1为已激活
	private String verificationCode;    //激活邮箱验证码
	private String imagePath;            //头像地址
	private String visitPath;				//头像访问地址

	public String getVisitPath() {
		return visitPath;
	}

	public void setVisitPath(String visitPath) {
		this.visitPath = visitPath;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getVerificationCode() {
		return verificationCode;
	}

	public void setVerificationCode(String verificationCode) {
		this.verificationCode = verificationCode;
	}

	public long getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}
