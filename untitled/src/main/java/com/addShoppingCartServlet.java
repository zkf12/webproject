package com;

import com.google.gson.Gson;

import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

public class addShoppingCartServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(addShoppingCartServlet.class.getName());
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        String lover = request.getParameter("username");

        //HttpSession session = request.getSession();
        //String lover = (String)session.getAttribute("username");

        // Convert to JSON and write to response
        response.setContentType("application/json;charset=UTF-8");
        Gson gson = new Gson();

        // 数据库连接操作
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/userdb", "root", "wanzkf123")) {
            // 检查购物车中是否已有该商品
            String checkQuery = "SELECT * FROM shoppingcart WHERE id = ? AND lover = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setInt(1, id);
                checkStmt.setString(2, lover);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    // 商品已在购物车中，返回失败消息
                    response.getWriter().write(gson.toJson("该物品已在购物车中，加入失败"));
                    return;
                }
            }
            // 如果商品不在购物车中，插入该商品到购物车
            String insertQuery = "INSERT INTO shoppingcart (id, lover) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
                stmt.setInt(1, id);
                stmt.setString(2, lover);
                // 执行插入操作
                int result = stmt.executeUpdate();
                if (result > 0) {
                    // 插入成功，返回成功消息
                    response.getWriter().write(gson.toJson("该物品已加入购物车"));
                } else {
                    // 插入失败，返回失败消息
                    response.getWriter().write(gson.toJson("加入购物车失败，请稍后重试"));
                }
            }
        } catch (SQLException e) {
            // 处理 SQL 异常
            logger.warning("This is a warning message.");
            e.printStackTrace();
            response.getWriter().write(gson.toJson("数据库错误，请稍后重试"));
        }
    }
}
