package com;

import com.google.gson.Gson;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.List;
import java.util.logging.Logger;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class purchaseServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(purchaseServlet.class.getName());

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();

        // 读取请求体中的JSON数据
        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
        StringBuilder requestBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }

        // 使用Gson解析JSON数据
        Gson gson = new Gson();
        RequestData data = gson.fromJson(requestBody.toString(), RequestData.class);

        List<Integer> selectedGoodsIds = data.selectedGoodsIds;

        // 准备JSON响应数据
        Map<String, Object> jsonResponse = new HashMap<>();

        if (selectedGoodsIds == null || selectedGoodsIds.isEmpty()) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "未选择任何商品！");
            writeJsonResponse(response, gson.toJson(jsonResponse));
            return;
        }

        double totalPrice = 0.0;
        Connection conn = null;
        try {
            // 获取数据库连接（根据你的数据库连接方法修改）
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/userdb", "root", "wanzkf123");
            conn.setAutoCommit(false);

            // 处理购物车中移除选中的商品
            String deleteCartQuery = "DELETE FROM shoppingcart WHERE id IN (";
            for (int i = 0; i < selectedGoodsIds.size(); i++) {
                deleteCartQuery += "?";
                if (i < selectedGoodsIds.size() - 1) {
                    deleteCartQuery += ",";
                }
            }
            deleteCartQuery += ")";
            PreparedStatement stmt = conn.prepareStatement(deleteCartQuery);
            for (int i = 0; i < selectedGoodsIds.size(); i++) {
                stmt.setInt(i + 1, selectedGoodsIds.get(i));
            }
            stmt.executeUpdate();

            // 获取商品信息并插入到purchaseHistory表
            StringBuilder query = new StringBuilder("SELECT * FROM goods WHERE id IN (");
            for (int i = 0; i < selectedGoodsIds.size(); i++) {
                query.append("?");
                if (i < selectedGoodsIds.size() - 1) {
                    query.append(",");
                }
            }
            query.append(")");

            stmt = conn.prepareStatement(query.toString());
            for (int i = 0; i < selectedGoodsIds.size(); i++) {
                stmt.setInt(i + 1, selectedGoodsIds.get(i));
            }

            ResultSet rs = stmt.executeQuery();

            // 获取商品信息并插入purchaseHistory
            String insertHistoryQuery = "INSERT INTO purchaseHistory (id, goodsname, price, customer, time, owner) VALUES (?, ?, ?, ?, ?, ?)";
            while (rs.next()) {
                // 从goods表获取商品信息
                int goodsId = rs.getInt("id");
                String goodsName = rs.getString("goods_name");
                double price = rs.getDouble("price");
                LocalDateTime currentDateTime = LocalDateTime.now();  // 获取当前时间
                String owner = rs.getString("owner");

                // 插入到purchaseHistory表
                stmt = conn.prepareStatement(insertHistoryQuery);
                stmt.setInt(1, goodsId);
                stmt.setString(2, goodsName);
                stmt.setDouble(3, price);
                stmt.setString(4, (String) session.getAttribute("username"));
                stmt.setObject(5, currentDateTime);
                stmt.setString(6, owner);
                stmt.executeUpdate();

                // 计算总价
                totalPrice += price;
            }

            conn.commit();  // 提交事务

            // 设置成功响应
            jsonResponse.put("success", true);
            jsonResponse.put("totalPrice", totalPrice);
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();  // 如果出错则回滚
                } catch (SQLException se) {
                    se.printStackTrace();
                }
            }
            jsonResponse.put("success", false);
            jsonResponse.put("message", "购买过程中出错：" + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();  // 关闭连接
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        // 返回JSON响应
        writeJsonResponse(response, gson.toJson(jsonResponse));
    }

    private void writeJsonResponse(HttpServletResponse response, String json) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.print(json);
        }
    }

    public class RequestData {
        List<Integer> selectedGoodsIds;
        // Getters and Setters
    }
}



