package com;

import com.google.gson.Gson;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class shoppingCartServlet extends HttpServlet {
    private static final int ROWS_PER_PAGE = 3;
    private static final Logger logger = Logger.getLogger(shoppingCartServlet.class.getName());
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int page = 1;
        String lover = request.getParameter("lover");
        String pageParam = request.getParameter("page");

        if (pageParam != null) {
            try {
                page = Integer.parseInt(pageParam);
            } catch (NumberFormatException ignored) {}
        }

        List<lover> loverList = new ArrayList<>();
        int totalRows = 0;

        String query = "SELECT g.id, g.goods_name, g.price, g.owner FROM goods g " +
                "JOIN shoppingcart sc ON g.id = sc.id WHERE sc.lover = ? " +
                "LIMIT ?, ?";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/userdb", "root", "wanzkf123");
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, lover);
            stmt.setInt(2, (page - 1) * ROWS_PER_PAGE);
            stmt.setInt(3, ROWS_PER_PAGE);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                loverList.add(new lover(
                        rs.getInt("id"),
                        rs.getString("goods_name"),
                        rs.getString("owner"),
                        rs.getDouble("price")
                ));
            }

            String countQuery = "SELECT COUNT(*) AS total FROM shoppingcart WHERE lover = ?";
            PreparedStatement countStmt = conn.prepareStatement(countQuery);
            countStmt.setString(1, lover);

            ResultSet countRs = countStmt.executeQuery();
            if (countRs.next()) {
                totalRows = countRs.getInt("total");
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }

        // 从session中获取已勾选的商品ID
        HttpSession session = request.getSession();
        List<Integer> selectedGoodsIds = (List<Integer>) session.getAttribute("selectedGoodsIds");
        if (selectedGoodsIds == null) {
            selectedGoodsIds = new ArrayList<>();
        }
        PaginatedResponse responseObject = new PaginatedResponse();
        responseObject.setLoverList(loverList);
        responseObject.setCurrentPage(page);
        responseObject.setTotalPages((int) Math.ceil((double) totalRows / ROWS_PER_PAGE));
        responseObject.setSelectedGoodsIds(selectedGoodsIds);

        response.setContentType("application/json;charset=UTF-8");
        Gson gson = new Gson();
        response.getWriter().write(gson.toJson(responseObject));
    }
    private static class PaginatedResponse {
        private List<lover> loverList;
        private int currentPage;
        private int totalPages;
        private List<Integer> selectedGoodsIds; // 更新为List<Integer>类型

        public void setSelectedGoodsIds(List<Integer> selectedGoodsIds) {
            this.selectedGoodsIds = selectedGoodsIds;
        }

        public List<Integer> getSelectedGoodsIds() {
            return selectedGoodsIds;
        }

        public void setLoverList(List<lover> loverList) {
            this.loverList = loverList;
        }

        public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        public List<lover> getLoverList() {
            return loverList;
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public int getTotalPages() {
            return totalPages;
        }
    }

}

