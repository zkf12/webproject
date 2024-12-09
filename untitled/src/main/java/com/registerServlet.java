package com;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;

public class registerServlet extends HttpServlet {

    // 数据库连接信息
    private static final String DB_URL = "jdbc:mysql://localhost:3306/userdb"; // 数据库URL
    private static final String DB_USER = "root";  // 数据库用户名
    private static final String DB_PASSWORD = "wanzkf123";  // 数据库密码

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获取注册表单参数
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String email = request.getParameter("email");

        // 数据库连接对象
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            // 连接到数据库
            Class.forName("com.mysql.cj.jdbc.Driver"); // 加载JDBC驱动
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // 检查用户名是否已存在
            String checkUserSql = "SELECT * FROM users WHERE username = ?";
            preparedStatement = connection.prepareStatement(checkUserSql);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (username.length() > 50) {
                // 如果用户名过长，返回错误消息
                response.setContentType("text/html; charset=UTF-8");
                PrintWriter out = response.getWriter();
                out.println("<html><body>");
                out.println("<h3>用户名太长，请选择其他用户名。</h3>");
                out.println("<a href='register.jsp'>返回注册页面</a>");
                out.println("</body></html>");
            }
            else if (!username.matches("^[0-9]+$")) {
                response.setContentType("text/html; charset=UTF-8");
                PrintWriter out = response.getWriter();
                out.println("<html><body>");
                out.println("<h3>用户名不合法。</h3>");
                out.println("<a href='register.jsp'>返回注册页面</a>");
                out.println("</body></html>");
            }
            else if (password.length() > 100) {
                // 如果用户名过长，返回错误消息
                response.setContentType("text/html; charset=UTF-8");
                PrintWriter out = response.getWriter();
                out.println("<html><body>");
                out.println("<h3>密码太长。</h3>");
                out.println("<a href='register.jsp'>返回注册页面</a>");
                out.println("</body></html>");
            }
            else if (resultSet.next()) {
                // 如果用户名已经存在，返回错误消息
                response.setContentType("text/html; charset=UTF-8");
                PrintWriter out = response.getWriter();
                out.println("<html><body>");
                out.println("<h3>用户名已存在，请选择其他用户名。</h3>");
                out.println("<a href='register.jsp'>返回注册页面</a>");
                out.println("</body></html>");
            } else {
                // 如果用户名没有重复，插入新用户
                String insertSql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
                preparedStatement = connection.prepareStatement(insertSql);
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                preparedStatement.setString(3, email);

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    // 注册成功，跳转到注册成功页面，并设置5秒后跳转回登录页面
                    response.setContentType("text/html; charset=UTF-8");
                    PrintWriter out = response.getWriter();
                    out.println("<html><body>");
                    out.println("<h3>注册成功！</h3>");
                    out.println("<p>系统将在 <span id='countdown'>5</span> 秒后自动跳转到登录页面。</p>");
                    out.println("<script>");
                    out.println("var countdown = 5;");
                    out.println("var countdownElement = document.getElementById('countdown');");
                    out.println("setInterval(function() {");
                    out.println("    countdown--; ");
                    out.println("    countdownElement.innerText = countdown;");
                    out.println("    if (countdown <= 0) {");
                    out.println("        window.location.href = 'login.jsp';");
                    out.println("    }");
                    out.println("}, 1000);");
                    out.println("</script>");
                    out.println("</body></html>");
                } else {
                    // 注册失败，返回错误信息
                    response.setContentType("text/html; charset=UTF-8");
                    PrintWriter out = response.getWriter();
                    out.println("<html><body>");
                    out.println("<h3>注册失败，请稍后再试。</h3>");
                    out.println("<a href='register.jsp'>返回注册页面</a>");
                    out.println("</body></html>");
                }
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
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

