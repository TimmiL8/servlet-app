package com.example;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@WebServlet("/user")
public class UserServlet extends HttpServlet {

    // thread-safe list to store users in memory while the server runs
    private final List<String> users = new CopyOnWriteArrayList<>();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");

        String username = request.getParameter("username");

        if (username != null && !username.trim().isEmpty()) {
            users.add(username);
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        // Showing the json to user
        String jsonResponse = String.format("{\"status\": \"success\", \"username\": \"%s\", \"totalUsers\": %d}", username, users.size());
        out.print(jsonResponse);
        out.flush();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String action = request.getParameter("action");

        HttpSession session = request.getSession();
        session.setAttribute("lastAction", action);

        // setting up the cookies
        Cookie visitCookie = new Cookie("visited_print_page", "true");
        visitCookie.setMaxAge(60 * 60);
        response.addCookie(visitCookie);

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        // returning the page with users
        out.println("<html><body>");

        if ("print".equals(action)) {
            out.println("<h2>Existing Users:</h2>");
            out.println("<ul>");
            if (users.isEmpty()) {
                out.println("<li>No users found.</li>");
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