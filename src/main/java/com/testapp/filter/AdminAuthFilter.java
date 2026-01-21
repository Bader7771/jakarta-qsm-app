package com.testapp.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter("/admin/*")
public class AdminAuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String uri = req.getRequestURI();

        System.out.println("=== FILTER DEBUG ===");
        System.out.println("URI: " + uri);

        // Permettre l'accès aux ressources JSF (CSS, JS, images)
        if (uri.contains("jakarta.faces.resource") || uri.contains("javax.faces.resource")) {
            System.out.println("-> Allowing JSF resource");
            chain.doFilter(request, response);
            return;
        }

        // Permettre l'accès à la page de login
        if (uri.endsWith("login.xhtml")) {
            System.out.println("-> Allowing login page");
            chain.doFilter(request, response);
            return;
        }

        // Vérifier la session
        HttpSession session = req.getSession(false);
        System.out.println("Session: " + session);
        
        if (session != null) {
            Boolean loggedIn = (Boolean) session.getAttribute("adminLoggedIn");
            Object adminUser = session.getAttribute("adminUser");
            System.out.println("adminLoggedIn: " + loggedIn);
            System.out.println("adminUser: " + adminUser);

            if (loggedIn != null && loggedIn && adminUser != null) {
                System.out.println("-> Access GRANTED");
                chain.doFilter(request, response);
                return;
            }
        }

        // Rediriger vers login si non connecté
        System.out.println("-> Access DENIED - Redirecting to login");
        String contextPath = req.getContextPath();
        res.sendRedirect(contextPath + "/admin/login.xhtml");
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("=== AdminAuthFilter INITIALIZED ===");
    }

    @Override
    public void destroy() {
        System.out.println("=== AdminAuthFilter DESTROYED ===");
    }
}
