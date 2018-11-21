package top.jianxiaopang.clouddisk.interceptor;

import com.google.gson.Gson;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import top.jianxiaopang.clouddisk.pojo.Result;
import top.jianxiaopang.clouddisk.pojo.User;
import top.jianxiaopang.clouddisk.utils.JWT;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * 登录状态验证
 */
public class TokenInterceptor implements HandlerInterceptor {
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
		String token = request.getParameter("token");
		Boolean bl = false;
		if(null != token) {
			User user = JWT.unsign(token, User.class);
			String email = request.getParameter("email");
			if(null != user && null != email) {
				if(user.getEmail().equals(email)) {
					bl = true;
				} else{
					bl = false;
				}
			} else {
				bl = false;
			}
		} else {
			bl = false;
		}
		if(bl == false) {
			Result result = Result.message(400,"登录超时，请重新登录！");
			//用gson来格式化类成json
			Gson gson = new Gson();
			String json = gson.toJson(result);
			PrintWriter out = response.getWriter();
			out.print(json);
			out.flush();
			out.close();
		}
		return bl;
	}

	@Override
	public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

	}

	@Override
	public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

	}
}
