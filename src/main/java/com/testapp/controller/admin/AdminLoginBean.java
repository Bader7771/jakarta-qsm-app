package com.testapp.controller.admin;

import com.testapp.dao.AdminUserDAO;
import com.testapp.entity.AdminUser;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpSession;

import java.io.Serializable;
import java.security.MessageDigest;

@Named("adminLoginBean")
@RequestScoped
public class AdminLoginBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private AdminUserDAO adminUserDAO;

    private String username;
    private String password;

    public AdminLoginBean() {
        // Constructeur vide requis
    }

    public String login() {
        try {
            System.out.println("=== LOGIN ATTEMPT ===");
            System.out.println("Username: " + username);

            if (username == null || username.trim().isEmpty()) {
                addError("Le nom d'utilisateur est requis");
                return null;
            }

            if (password == null || password.trim().isEmpty()) {
                addError("Le mot de passe est requis");
                return null;
            }

            AdminUser admin = adminUserDAO.findByUsername(username.trim());

            if (admin == null) {
                System.out.println("Admin not found!");
                addError("Identifiants incorrects");
                return null;
            }

            if (!admin.isActive()) {
                addError("Compte désactivé");
                return null;
            }

            String hashedPassword = hashPassword(password);
            System.out.println("Hashed password: " + hashedPassword);
            System.out.println("DB password: " + admin.getPassword());

            if (!hashedPassword.equals(admin.getPassword())) {
                addError("Identifiants incorrects");
                return null;
            }

            // Créer la session
            HttpSession session = getSession();
            session.setAttribute("adminUser", admin);
            session.setAttribute("adminLoggedIn", true);
            session.setMaxInactiveInterval(3600); // 1 heure

            System.out.println("Login successful!");
            addSuccess("Connexion réussie !");

            return "/admin/dashboard?faces-redirect=true";

        } catch (Exception e) {
            e.printStackTrace();
            addError("Erreur lors de la connexion : " + e.getMessage());
            return null;
        }
    }

    public String logout() {
        try {
            HttpSession session = getSession();
            if (session != null) {
                session.invalidate();
            }
            addSuccess("Déconnexion réussie");
            return "/admin/login?faces-redirect=true";
        } catch (Exception e) {
            e.printStackTrace();
            return "/admin/login?faces-redirect=true";
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                String h = Integer.toHexString(0xff & b);
                if (h.length() == 1) hex.append('0');
                hex.append(h);
            }
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException("Erreur de hashage", e);
        }
    }

    private HttpSession getSession() {
        FacesContext context = FacesContext.getCurrentInstance();
        return (HttpSession) context.getExternalContext().getSession(true);
    }

    private void addError(String message) {
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
    }

    private void addSuccess(String message) {
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_INFO, message, null));
    }

    // GETTERS ET SETTERS

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
