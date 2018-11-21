package top.jianxiaopang.clouddisk.interceptor;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 拦截器类
 * 允许跨越访问，并设置相同的session和请求返回字符格式
 */
public class Interceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		//设置编码
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json; charset=utf-8");
		//允许访问的url跨域
		String url = request.getHeader("Origin");
		response.setHeader("Access-Control-Allow-Origin", url);
		// 允许跨域请求中携带cookie
		response.addHeader("Access-Control-Allow-Credentials", "true");
		return true;
	}
}



