package com.testapp.dao;

import com.testapp.entity.Theme;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class ThemeDAO {

    @PersistenceContext(unitName = "testPU")
    private EntityManager em;

    public List<Theme> findAll() {
        return em.createQuery(
            "SELECT t FROM Theme t ORDER BY t.libelle",
            Theme.class
        ).getResultList();
    }

    public Theme findById(Long id) {
        return em.find(Theme.class, id);
    }

    @Transactional
    public void save(Theme theme) {
        em.persist(theme);
    }

    @Transactional
    public Theme update(Theme theme) {
        return em.merge(theme);
    }

    @Transactional
    public void delete(Long id) {
        Theme t = em.find(Theme.class, id);
        if (t != null) {
            em.remove(t);
        }
    }

    public Theme findByLibelle(String libelle) {
        List<Theme> list = em.createQuery(
            "SELECT t FROM Theme t WHERE t.libelle = :lib",
            Theme.class
        )
        .setParameter("lib", libelle)
        .getResultList();

        return list.isEmpty() ? null : list.get(0);
    }
}
