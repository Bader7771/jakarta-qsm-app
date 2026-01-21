package com.testapp.util;

import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceContext;

public class JPAUtil {
	
	@Produces
    @PersistenceContext(unitName = "testPU")
    private EntityManager em;

    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("testPU");

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
}
