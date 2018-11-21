package top.jianxiaopang.clouddisk.pojo;

import java.sql.Timestamp;

/**
 * 文件封装类
 */
public class FileNode {
	private int id;                  //文件id
	private String uuidName;        //保存在内存的名字
	private String fileName;        //文件本来的名字
	private String savePath;        //保存的真实路径
	private String logicalPath;     //保存的逻辑路径
	private int userId;              //上传的用户id
	private int isFolder;           //是否为文件夹，1为是，0为否
	private int parentNodeId;       //父亲节点id
	private String updateTime;      //文件更新时间
	private String size;            //文件大小
	private Integer downNum;        //下载次数
	private String visitPath;        //文件访问地址

	public String getVisitPath() {
		return visitPath;
	}

	public void setVisitPath(String visitPath) {
		this.visitPath = visitPath;
	}

	public Integer getDownNum() {
		return downNum;
	}

	public void setDownNum(Integer downNum) {
		this.downNum = downNum;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public int getParentNodeId() {
		return parentNodeId;
	}

	public void setParentNodeId(int parentNodeId) {
		this.parentNodeId = parentNodeId;
	}

	public int getIsFolder() {
		return isFolder;
	}

	public void setIsFolder(int isFolder) {
		this.isFolder = isFolder;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUuidName() {
		return uuidName;
	}

	public void setUuidName(String uuidName) {
		this.uuidName = uuidName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getSavePath() {
		return savePath;
	}

	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}

	public String getLogicalPath() {
		return logicalPath;
	}

	public void setLogicalPath(String logicalPath) {
		this.logicalPath = logicalPath;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}
}
