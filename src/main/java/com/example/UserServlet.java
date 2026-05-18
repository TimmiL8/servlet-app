package com.example;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@WebServlet("/user")
public class UserServlet extends HttpServlet {

    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource dataSource = (DataSource) envContext.lookup("jdbc/MyDB");

            userDAO = new UserDAO(dataSource);

            try (Connection conn = dataSource.getConnection();
                 Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "username VARCHAR(255) NOT NULL, " +
                        "email VARCHAR(255))");
            }
        } catch (NamingException | SQLException e) {
            throw new ServletException("Initialization failed", e);
        }
    }

    // handles create, update, and delete operations
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        try {
            if ("create".equals(action)) {
                String username = request.getParameter("username");
                String email = request.getParameter("email");
                User newUser = new User(username, email != null ? email : "no-email@example.com");
                userDAO.createUser(newUser);

            } else if ("update".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                String username = request.getParameter("username");
                String email = request.getParameter("email");
                User updatedUser = new User(id, username, email);
                userDAO.updateUser(updatedUser);

            } else if ("delete".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                userDAO.deleteUser(id);
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database or Input error");
            return;
        }

        response.sendRedirect("index.jsp");
    }

    // handles read operations
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        String action = request.getParameter("action");

        out.println("<html><body style='font-family: Arial;'>");

        try {
            if ("getById".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                User user = userDAO.getUserById(id);

                if (user != null) {
                    out.println("<h2>User Found:</h2>");
                    out.println("<p><strong>ID:</strong> " + user.getId() + "<br>");
                    out.println("<strong>Name:</strong> " + user.getName() + "<br>");
                    out.println("<strong>Email:</strong> " + user.getEmail() + "</p>");
                } else {
                    out.println("<h2>User not found with ID: " + id + "</h2>");
                }

            } else if ("print".equals(action)) {
                List<User> users = userDAO.getAllUsers();
                out.println("<h2>All Users in Database:</h2>");
                out.println("<ul>");
                for (User u : users) {
                    out.println("<li>ID: " + u.getId() + " | Name: " + u.getName() + " | Email: " + u.getEmail() + "</li>");
                }
                out.println("</ul>");
            } else {
                out.println("<h2>Invalid Action!</h2>");
            }
        } catch (SQLException | NumberFormatException e) {
            out.println("<h2>Error retrieving data.</h2>");
        }

        out.println("<br><br><a href=\"index.jsp\">Go Back to Dashboard</a>");
        out.println("</body></html>");
    }
}