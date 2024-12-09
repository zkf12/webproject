package com;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;

public class logoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获取当前的 session
        HttpSession session = request.getSession(false);  // 如果没有session，返回null
        if (session != null) {
            // 销毁服务器端的 session
            session.invalidate();
        }

        // 通过重置浏览器端的 session cookie 来清除浏览器端的会话
        // 设置 Cookie 的有效期为 0 来删除浏览器中的 session cookie
        Cookie sessionCookie = new Cookie("JSESSIONID", "");
        sessionCookie.setMaxAge(0); // 立即过期
        sessionCookie.setPath(request.getContextPath()); // 设置有效路径
        response.addCookie(sessionCookie);

        // 重定向到 login.jsp
        response.sendRedirect("login.jsp");
    }
}
