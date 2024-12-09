package com;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;

public class loginServlet extends HttpServlet {

    // 数据库连接信息
    private static final String DB_URL = "jdbc:mysql://localhost:3306/userdb"; // 数据库URL
    private static final String DB_USER = "root";  // 数据库用户名
    private static final String DB_PASSWORD = "wanzkf123";  // 数据库密码


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获取请求参数
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // 数据库连接对象
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // 连接到数据库
            Class.forName("com.mysql.cj.jdbc.Driver"); // 加载JDBC驱动
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // SQL查询语句，查找用户和密码
            String sql = "SELECT permiss FROM users WHERE username = ? AND password = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            // 执行查询
            resultSet = preparedStatement.executeQuery();

            // 如果有结果，说明用户名和密码正确
            if (resultSet.next()) {
                HttpSession session = request.getSession();
                session.setAttribute("username", username);

                // 在验证成功后，将用户名信息存入 sessionStorage
                response.setContentType("text/html; charset=UTF-8");
                PrintWriter out = response.getWriter();
                out.println("<html><body>");
                out.println("<script>");
                out.println("sessionStorage.setItem('username', '" + username + "');");

                // 重定向使用 window.location.href 而不是 sendRedirect
                if (resultSet.getString("permiss").equals("1")) {
                    out.println("window.location.href = 'management.jsp';");
                } else if (resultSet.getString("permiss").equals("0")) {
                    out.println("window.location.href = 'user_page.jsp';");
                }

                out.println("</script>");
                out.println("</body></html>");

            } else {
                // 如果验证失败，返回登录页面并显示错误消息
                response.setContentType("text/html; charset=UTF-8");
                PrintWriter out = response.getWriter();
                out.println("<html><body>");
                out.println("<h3>用户名或密码错误</h3>");
                out.println("<a href='login.jsp'>返回登录</a>");
                out.println("</body></html>");

            }
        } catch (ClassNotFoundException e) {
            // JDBC驱动加载失败的处理
            e.printStackTrace();
        } catch (SQLException e) {
            // SQL错误的处理
            e.printStackTrace();
        } finally {
            // 关闭数据库连接
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
