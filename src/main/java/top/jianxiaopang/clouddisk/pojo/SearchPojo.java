package top.jianxiaopang.clouddisk.pojo;

/**
 * 搜索封装类
 */
public class SearchPojo {
	private String keyWord1;//搜索关键字
	private String keyWord2;//选择按文件名排序或更新时间排序 filename/updatetime
	private String order;//选择升序或降序排序 asc/desc

	public String getKeyWord1() {
		return keyWord1;
	}

	public void setKeyWord1(String keyWord1) {
		this.keyWord1 = keyWord1;
	}

	public String getKeyWord2() {
		return keyWord2;
	}

	public void setKeyWord2(String keyWord2) {
		this.keyWord2 = keyWord2;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}
}
