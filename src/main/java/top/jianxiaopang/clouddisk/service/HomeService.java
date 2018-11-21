package top.jianxiaopang.clouddisk.service;

import top.jianxiaopang.clouddisk.pojo.FileNode;
import top.jianxiaopang.clouddisk.pojo.Result;
import top.jianxiaopang.clouddisk.pojo.SearchPojo;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface HomeService {
	public Result upload(HttpServletRequest request);

	public Result listFileNode(Integer parentNodeId, Integer userId);

	//单个下载
	//public Result download(Integer nodeId, HttpServletResponse response);

	//批量下载
	public Result download(List<Integer> ids, HttpServletResponse response);

	public Result createFolder(FileNode fileNode);

	public Result deleteFile(List<Integer> ids);

	public Result moveFile(String logicalPath, String parentNodeId, List<Integer> ids);

	public Result getProgress(String sessionId);

	public Result orderBy(FileNode fileNode, String method);

	public Result search(SearchPojo searchPojo);

	public String preview(FileNode fileNode);

	public Result share(List<Integer> ids, String type, Integer deadline);

	public Result shareCheck(String token, String code, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
}
