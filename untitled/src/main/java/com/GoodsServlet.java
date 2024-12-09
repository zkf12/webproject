package com;
import java.io.BufferedReader;
import org.json.JSONObject;

import java.util.function.Supplier;
import java.util.logging.Logger;
import com.google.gson.Gson;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.*;
import javax.servlet.http.*;

public class GoodsServlet extends HttpServlet {
    private static final int ROWS_PER_PAGE = 3;
    private static final Logger logger = Logger.getLogger(GoodsServlet.class.getName());
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int page = 1;
        String pageParam = request.getParameter("page");
        if (pageParam != null) {
            try {
                page = Integer.parseInt(pageParam);
            } catch (NumberFormatException ignored) {}
        }

        List<Goods> goodsList = new ArrayList<>();
        int totalRows = 0;

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/userdb", "root", "wanzkf123");
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM goods LIMIT ?, ?")) {

            stmt.setInt(1, (page - 1) * ROWS_PER_PAGE);
            stmt.setInt(2, ROWS_PER_PAGE);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                goodsList.add(new Goods(
                        rs.getInt("id"),
                        rs.getString("goods_name"),
                        rs.getString("owner"),
                        rs.getDouble("price"),
                        rs.getInt("quantity")
                ));
            }

            // Get total number of rows for pagination
            PreparedStatement countStmt = conn.prepareStatement("SELECT COUNT(*) AS total FROM goods");
            ResultSet countRs = countStmt.executeQuery();
            if (countRs.next()) {
                totalRows = countRs.getInt("total");
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }

        // Create a response object
        PaginatedResponse responseObject = new PaginatedResponse();
        responseObject.setGoodsList(goodsList);
        responseObject.setCurrentPage(page);
        responseObject.setTotalPages((int) Math.ceil((double) totalRows / ROWS_PER_PAGE));

        // Convert to JSON and write to response
        response.setContentType("application/json;charset=UTF-8");
        Gson gson = new Gson();
        response.getWriter().write(gson.toJson(responseObject));
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 设置字符编码
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        // 读取请求体中的数据
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }

        // 解析 JSON 数据
        JSONObject jsonObject = new JSONObject(stringBuilder.toString());
        String defaultIntValueStr = jsonObject.getString("defaultIntValue");

        int defaultIntValue = 0;

        try {
            // 尝试将参数转换为整数
            if (defaultIntValueStr != null) {
                defaultIntValue = Integer.parseInt(defaultIntValueStr);
            }
        } catch (NumberFormatException e) {
            // 如果转换失败，提供错误响应
            response.getWriter().write("Invalid defaultIntValue parameter.");
            return;
        }

        // 基于defaultIntValue的值执行不同的逻辑
        switch (defaultIntValue) {
            case 1:
                handleCase1(request, response, jsonObject);
                break;
            case 2:
                handleCase2(request, response, jsonObject);
                break;
            case 3:
                handleCase3(request, response, jsonObject);
                break;
            case 4:
                handleCase4(request, response, jsonObject);
                break;
        }
    }

    private void handleCase1(HttpServletRequest request, HttpServletResponse response, JSONObject jsonObject) throws IOException, ServletException {
        int page = 1;  // Assuming page is a constant or received from the request, you might want to adjust this.
        String username = jsonObject.getString("itemId");
        String goodsName = jsonObject.getString("itemName");
        List<Goods> goodsList = new ArrayList<>();
        int totalRows = 0;

        StringBuilder query = new StringBuilder("SELECT * FROM goods");
        StringBuilder countQuery = new StringBuilder("SELECT COUNT(*) AS total FROM goods");
        boolean hasWhereClause = false;

        if (username != null && !username.trim().isEmpty()) {
            query.append(" WHERE owner = ?");
            countQuery.append(" WHERE owner = ?");
            hasWhereClause = true;
        }

        if (goodsName != null && !goodsName.trim().isEmpty()) {
            if (hasWhereClause) {
                query.append(" AND goods_name = ?");
                countQuery.append(" AND goods_name = ?");
            } else {
                query.append(" WHERE goods_name = ?");
                countQuery.append(" WHERE goods_name = ?");
            }
        }

        query.append(" LIMIT ?, ?");

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/userdb", "root", "wanzkf123")) {

            // Prepare statement for counting total rows
            PreparedStatement countStmt = conn.prepareStatement(countQuery.toString());
            int paramIndex = 1;
            if (username != null && !username.trim().isEmpty()) {
                countStmt.setString(paramIndex++, username);
            }
            if (goodsName != null && !goodsName.trim().isEmpty()) {
                countStmt.setString(paramIndex++, goodsName);
            }

            ResultSet countRs = countStmt.executeQuery();
            if (countRs.next()) {
                totalRows = countRs.getInt("total");
            }

            // Prepare statement for fetching the goods
            PreparedStatement stmt = conn.prepareStatement(query.toString());
            paramIndex = 1;
            if (username != null && !username.trim().isEmpty()) {
                stmt.setString(paramIndex++, username);
            }
            if (goodsName != null && !goodsName.trim().isEmpty()) {
                stmt.setString(paramIndex++, goodsName);
            }
            stmt.setInt(paramIndex++, (page - 1) * ROWS_PER_PAGE);
            stmt.setInt(paramIndex, ROWS_PER_PAGE);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                goodsList.add(new Goods(
                        rs.getInt("id"),
                        rs.getString("goods_name"),
                        rs.getString("owner"),
                        rs.getDouble("price"),
                        rs.getInt("quantity")
                ));
            }

        } catch (SQLException e) {

            throw new ServletException(e);
        }

        // Create a response object
        PaginatedResponse responseObject = new PaginatedResponse();
        responseObject.setGoodsList(goodsList);
        responseObject.setCurrentPage(page);
        responseObject.setTotalPages((int) Math.ceil((double) totalRows / ROWS_PER_PAGE));

        // Convert to JSON and write to response
        response.setContentType("application/json;charset=UTF-8");
        Gson gson = new Gson();
        response.getWriter().write(gson.toJson(responseObject));
    }



    private void handleCase2(HttpServletRequest request, HttpServletResponse response, JSONObject jsonObject) throws IOException {
        String username = jsonObject.getString("username");
        String goodsname = jsonObject.getString("goodsname");
        double price = jsonObject.getDouble("price");
        int quantity = jsonObject.getInt("quantity");
        logger.info(goodsname);
        String message;
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/userdb", "root", "wanzkf123")) {
            String sql = "INSERT INTO goods (owner, goods_name, price, quantity) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, username);
                pstmt.setString(2, goodsname);
                pstmt.setDouble(3, price);
                pstmt.setInt(4, quantity);

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    message = "Insert successful!";
                } else {
                    message = "Insert failed!";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            message = "Database error: " + e.getMessage();
        }

        // 创建一个 JSON 对象，并将消息写入响应
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("message", message);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonResponse.toString());
    }


    private void handleCase3(HttpServletRequest request, HttpServletResponse response, JSONObject jsonObject) throws IOException {
        String username = jsonObject.getString("username");
        String goodsname = jsonObject.getString("goodsname");
        String message;

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/userdb", "root", "wanzkf123")) {
            String sql = "DELETE FROM goods WHERE owner = ? AND goods_name = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, username);
                pstmt.setString(2, goodsname);

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    message = "Delete successful!";
                } else {
                    message = "Delete failed! No matching record found.";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            message = "Database error: " + e.getMessage();
        }

        // 创建一个 JSON 对象，并将消息写入响应
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("message", message);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonResponse.toString());
    }

    private void handleCase4(HttpServletRequest request, HttpServletResponse response, JSONObject jsonObject) throws IOException {
        String username = jsonObject.getString("username");
        String goodsname = jsonObject.getString("goodsname");
        double price = jsonObject.getDouble("price");
        String message;

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/userdb", "root", "wanzkf123")) {
            String sql = "UPDATE goods SET price = ? WHERE owner = ? AND goods_name = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setDouble(1, price);
                pstmt.setString(2, username);
                pstmt.setString(3, goodsname);

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    message = "Update successful!";
                } else {
                    message = "Update failed! No matching record found.";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            message = "Database error: " + e.getMessage();
        }

        // 创建一个 JSON 对象，并将消息写入响应
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("message", message);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonResponse.toString());
    }


    // Helper class for paginated response
    private static class PaginatedResponse {
        private List<Goods> goodsList;
        private int currentPage;
        private int totalPages;

        public void setGoodsList(List<Goods> goodsList) {
            this.goodsList = goodsList;
        }

        public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        public List<Goods> getGoodsList() {
            return goodsList;
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public int getTotalPages() {
            return totalPages;
        }
    }

}


