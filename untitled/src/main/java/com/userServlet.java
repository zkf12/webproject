package com;

import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class userServlet extends HttpServlet {
    private static final int ROWS_PER_PAGE = 3;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int page = 1;
        String pageParam = request.getParameter("page");
        if (pageParam != null) {
            try {
                page = Integer.parseInt(pageParam);
            } catch (NumberFormatException ignored) {}
        }

        List<User> usersList = new ArrayList<>();
        int totalRows = 0;

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/userdb", "root", "wanzkf123");
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE permiss = 0 LIMIT ?, ?")) {

            stmt.setInt(1, (page - 1) * ROWS_PER_PAGE);
            stmt.setInt(2, ROWS_PER_PAGE);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                usersList.add(new User(
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getInt("id")
                ));
            }

            // Get total number of rows for pagination
            PreparedStatement countStmt = conn.prepareStatement("SELECT COUNT(*) AS total FROM users");
            ResultSet countRs = countStmt.executeQuery();
            if (countRs.next()) {
                totalRows = countRs.getInt("total");
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }

        // Create a response object
        PaginatedResponse responseObject = new PaginatedResponse();
        responseObject.setUsersList(usersList);
        responseObject.setCurrentPage(page);
        responseObject.setTotalPages((int) Math.ceil((double) totalRows / ROWS_PER_PAGE));

        // Convert to JSON and write to response
        response.setContentType("application/json;charset=UTF-8");
        Gson gson = new Gson();
        response.getWriter().write(gson.toJson(responseObject));
    }

    // Helper class for paginated response
    private static class PaginatedResponse {
        private List<User> usersList;
        private int currentPage;
        private int totalPages;

        public void setUsersList(List<User> usersList) {
            this.usersList = usersList;
        }

        public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        public List<User> getUsersList() {
            return usersList;
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public int getTotalPages() {
            return totalPages;
        }
    }

    // User class for representing each user
    private static class User {
        private String userName;
        private String email;
        private int id;

        public User(String userName, String email, int id) {
            this.userName = userName;
            this.email = email;
            this.id = id;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getEmail() {
            return email;
        }

        public int getId() {
            return id;
        }
    }
}
