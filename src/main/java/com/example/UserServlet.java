package com.example;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/user")
public class UserServlet extends HttpServlet {

    // not secure database connection details but its for the demo)
    private static final String DB_URL = "jdbc:mysql://localhost:3306/mydb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            // load the mysql jdbc driver into memory
            Class.forName("com.mysql.cj.jdbc.Driver");

            // create table 'users' automatically on server startup
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 Statement stmt = conn.createStatement()) {

                String sql = "CREATE TABLE IF NOT EXISTS users (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "username VARCHAR(255) NOT NULL)";
                stmt.execute(sql);
            }
        } catch (ClassNotFoundException | SQLException e) {
            throw new ServletException("Database initialization failed", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        String username = request.getParameter("username");
        int totalUsers = 0;

        if (username != null && !username.trim().isEmpty()) {
            // execute sql queries for data insrrtion
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String insertSql = "INSERT INTO users (username) VALUES (?)";
                try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                    pstmt.setString(1, username);
                    pstmt.executeUpdate();
                }

                // updated total count
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
                    if (rs.next()) totalUsers = rs.getInt(1);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database write error");
                return;
            }
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String jsonResponse = String.format("{\"status\": \"success\", \"username\": \"%s\", \"totalUsers\": %d}", username, totalUsers);
        out.print(jsonResponse);
        out.flush();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String action = request.getParameter("action");

        HttpSession session = request.getSession();
        session.setAttribute("lastAction", action);

        Cookie visitCookie = new Cookie("visited_print_page", "true");
        visitCookie.setMaxAge(60 * 60);
        response.addCookie(visitCookie);

        // connect to mysql and fetch data
        List<String> users = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT username FROM users")) {

            while (rs.next()) {
                users.add(rs.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if ("api".equals(action)) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();

            StringBuilder jsonArray = new StringBuilder("[");
            for (int i = 0; i < users.size(); i++) {
                jsonArray.append("\"").append(users.get(i)).append("\"");
                if (i < users.size() - 1) jsonArray.append(", ");
            }
            jsonArray.append("]");

            out.print("{\"status\": \"success\", \"users\": " + jsonArray.toString() + "}");
            out.flush();
            return;
        }

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<html><body>");
        if ("print".equals(action)) {
            out.println("<h2>Existing Users (From MySQL Database):</h2>");
            out.println("<ul>");
            if (users.isEmpty()) {
                out.println("<li>No users found in database.</li>");
            } else {
                for (String user : users) {
                    out.println("<li>" + user + "</li>");
                }
            }
            out.println("</ul>");
        } else {
            out.println("<h2>Invalid Action!</h2>");
        }
        out.println("<br><a href=\"index.jsp\">Go Back to Home</a>");
        out.println("</body></html>");
    }
}