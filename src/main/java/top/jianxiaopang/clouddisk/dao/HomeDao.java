package top.jianxiaopang.clouddisk.dao;

import top.jianxiaopang.clouddisk.pojo.FileNode;
import top.jianxiaopang.clouddisk.pojo.SearchPojo;

import java.util.List;

public interface HomeDao {
	public void insertFile(FileNode fileNode);

	public List<FileNode> selectFileNodeByParentNodeId(FileNode fileNode);

	public FileNode getFileNode(Integer id);

	public void createFolder(FileNode fileNode);

	public int selectFolderByFileName(FileNode fileNode);

	public List<FileNode> selectFileNode(FileNode fileNode);

	public void deleteFile(FileNode fileNode);

	public String selectLogicalPathById(Integer id);

	public void updateFile(FileNode fileNode);

	public List<FileNode> selectNodeOrderByImage(FileNode fileNode);

	public List<FileNode> selectNodeOrderByDocument(FileNode fileNode);

	public List<FileNode> selectNodeOrderByVideo(FileNode fileNode);

	public List<FileNode> selectNodeOrderByMusic(FileNode fileNode);

	public List<FileNode> search(SearchPojo searchPojo);

	public void updateDownNum(FileNode fileNode);

	public String getDownUrl(FileNode fileNode);
}