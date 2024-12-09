package com;

import com.google.gson.Gson;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.servlet.*;
import javax.servlet.http.*;

public class historyServlet extends HttpServlet {
    private static final int ROWS_PER_PAGE = 3;
    private static final Logger logger = Logger.getLogger(historyServlet.class.getName());
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int page = 1;
        String pageParam = request.getParameter("page");
        String user = request.getParameter("user");

        if (pageParam != null) {
            try {
                page = Integer.parseInt(pageParam);
            } catch (NumberFormatException ignored) {}
        }

        List<PurchaseHistory> historyList = new ArrayList<>();
        int totalRows = 0;

        // Base query without user condition
        String queryBase = "SELECT goodsname, time, owner, price, status FROM purchasehistory";
        String countQueryBase = "SELECT COUNT(*) AS total FROM purchasehistory";
        String queryCondition = (user != null && !user.isEmpty()) ? " WHERE customer = ?" : "";  // Check if user exists

        String queryPagination = " LIMIT ?, ?";

        // Construct the full query
        String query = queryBase + queryCondition + queryPagination;
        String countQuery = countQueryBase + queryCondition;

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/userdb", "root", "wanzkf123");
             PreparedStatement stmt = conn.prepareStatement(query)) {

            int paramIndex = 1;

            // Set user parameter if exists
            if (user != null && !user.isEmpty()) {
                stmt.setString(paramIndex++, user);
            }

            // Set pagination parameters
            stmt.setInt(paramIndex++, (page - 1) * ROWS_PER_PAGE);
            stmt.setInt(paramIndex, ROWS_PER_PAGE);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                historyList.add(new PurchaseHistory(
                        rs.getString("goodsname"),
                        rs.getString("time"),
                        rs.getString("owner"),
                        rs.getDouble("price"),
                        rs.getInt("status")
                ));
            }

            // Prepare the count statement for total rows
            try (PreparedStatement countStmt = conn.prepareStatement(countQuery)) {
                // If user is specified, set the user parameter
                if (user != null && !user.isEmpty()) {
                    countStmt.setString(1, user);
                }

                ResultSet countRs = countStmt.executeQuery();
                if (countRs.next()) {
                    totalRows = countRs.getInt("total");
                }
            }

        } catch (SQLException e) {
            throw new ServletException(e);
        }

        // Prepare the response object
        PaginatedResponse responseObject = new PaginatedResponse();
        responseObject.setHistoryList(historyList);
        responseObject.setCurrentPage(page);
        responseObject.setTotalPages((int) Math.ceil((double) totalRows / ROWS_PER_PAGE));

        // Convert to JSON and send as response
        response.setContentType("application/json;charset=UTF-8");
        Gson gson = new Gson();
        response.getWriter().write(gson.toJson(responseObject));
    }


    // Helper class for paginated response
    private static class PaginatedResponse {
        private List<PurchaseHistory> historyList;
        private int currentPage;
        private int totalPages;

        public void setHistoryList(List<PurchaseHistory> historyList) {
            this.historyList = historyList;
        }

        public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        public List<PurchaseHistory> getHistoryList() {
            return historyList;
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public int getTotalPages() {
            return totalPages;
        }
    }

    // PurchaseHistory model class
    private static class PurchaseHistory {
        private String goodsName;
        private String time;
        private String owner;
        private double price;
        private int status;

        public PurchaseHistory(String goodsName, String time, String owner, double price, int status) {
            this.goodsName = goodsName;
            this.time = time;
            this.owner = owner;
            this.price = price;
            this.status = 0;
        }

        public String getGoodsName() {
            return goodsName;
        }

        public String getTime() {
            return time;
        }

        public String getOwner() {
            return owner;
        }

        public double getPrice() {
            return price;
        }
        public int getStatus() {
            return status;
        }
    }
}

