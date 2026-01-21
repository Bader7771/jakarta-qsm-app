package com.testapp.dao;

import com.testapp.entity.AdminUser;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.NoResultException;

@Stateless
public class AdminUserDAO {

    @PersistenceContext(unitName = "testPU")
    private EntityManager em;

    public AdminUser findByUsername(String username) {
        try {
            System.out.println("=== DAO: Searching for username: " + username);
            AdminUser admin = em.createQuery(
                "SELECT a FROM AdminUser a WHERE a.username = :username", 
                AdminUser.class)
                .setParameter("username", username)
                .getSingleResult();
            System.out.println("=== DAO: Admin found: " + admin.getUsername());
            return admin;
        } catch (NoResultException e) {
            System.out.println("=== DAO: No admin found with username: " + username);
            return null;
        } catch (Exception e) {
            System.out.println("=== DAO: Error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
