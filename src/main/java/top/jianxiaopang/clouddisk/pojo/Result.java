package top.jianxiaopang.clouddisk.pojo;

/**
 * 结果封装类
 *
 * @param <T>
 */
public class Result<T> {
	private int status;//状态码，200代表成功，400代表失败
	private String message;//返回状态说明
	private T data;//返回对象

	/**
	 * 返回对象
	 */
	private Result(T data) {
		this.status = 200;
		this.message = "成功";
		this.data = data;
	}

	/**
	 * 自定义状态码和信息
	 */
	private Result(int status, String message) {
		this.status = status;
		this.message = message;
	}

	/**
	 * 成功时候的调用
	 *
	 * @return
	 */
	public static <T> Result<T> success(T data) {
		return new Result<T>(data);
	}

	public static <T> Result<T> message(int status, String message) {
		return new Result<T>(status, message);
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
}
