package com;

import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class statsServlet extends HttpServlet {
    private static final int ROWS_PER_PAGE = 3; // 每页显示的行数
    private static final Logger logger = Logger.getLogger(historyServlet.class.getName());
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int page = 1; // 默认第一页
        String pageParam = request.getParameter("page");
        if (pageParam != null) {
            try {
                page = Integer.parseInt(pageParam);
            } catch (NumberFormatException ignored) {}
        }

        List<Map<String, Object>> resultList = new ArrayList<>();
        int totalRows = 0;

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/userdb", "root", "wanzkf123")) {

            // 获取所有商品记录
            String sql = "SELECT * FROM purchasehistory";
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {

                // 收集结果集
                while (resultSet.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("goodsname", resultSet.getString("goodsname"));
                    row.put("price", resultSet.getDouble("price"));
                    resultList.add(row);
                }
            }

            // 获取总行数
            try (Statement countStmt = connection.createStatement();
                 ResultSet countRs = countStmt.executeQuery("SELECT COUNT(DISTINCT goodsname) AS total FROM purchasehistory")) {
                if (countRs.next()) {
                    totalRows = countRs.getInt("total");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException("Database access error", e);
        }

        // 按 goodsname 进行分组
        Map<String, Double> totalPriceByGoodsName = resultList.stream()
                .collect(Collectors.groupingBy(
                        row -> (String) row.get("goodsname"),
                        Collectors.summingDouble(row -> (Double) row.get("price"))
                ));

        // 转换为 List<Goods>
        List<Goods> goodsList = totalPriceByGoodsName.entrySet().stream()
                .map(entry -> new Goods(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        // 计算分页信息，并根据分页选择返回的数据块
        int totalPages = (int) Math.ceil((double) totalRows / ROWS_PER_PAGE);
        List<Goods> paginatedGoodsList = goodsList.stream()
                .skip((page - 1) * ROWS_PER_PAGE)
                .limit(ROWS_PER_PAGE)
                .collect(Collectors.toList());

        // 构建分页响应
        PaginatedResponse paginatedResponse = new PaginatedResponse();
        paginatedResponse.setGoodsList(paginatedGoodsList);
        paginatedResponse.setCurrentPage(page);
        paginatedResponse.setTotalPages(totalPages);

        // 转换为 JSON 并返回
        String jsonResponse = new Gson().toJson(paginatedResponse);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonResponse);

    }

    private static class Goods {
        private String goodsname;
        private double totalprice;

        public Goods(String goodsname, double totalprice) {
            this.goodsname = goodsname;
            this.totalprice = totalprice;
        }

        public String getGoodsname() {
            return goodsname;
        }

        public double getTotalprice() {
            return totalprice;
        }
    }

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

