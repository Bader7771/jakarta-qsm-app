package com.testapp.dao;

import com.testapp.entity.Question;
import com.testapp.entity.Theme;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class QuestionDAO {

    @PersistenceContext(unitName = "testPU")
    private EntityManager em;

    @Transactional
    public void save(Question q) {
        em.persist(q);
    }

    @Transactional
    public Question update(Question q) {
        return em.merge(q);
    }

    @Transactional
    public void delete(Question q) {
        Question managed = em.find(Question.class, q.getId());
        if (managed != null) {
            em.remove(managed);
        }
    }

    public Question findById(Long id) {
        return em.find(Question.class, id);
    }

    public List<Question> findAll() {
        return em.createQuery(
            "SELECT q FROM Question q JOIN FETCH q.theme",
            Question.class
        ).getResultList();
    }

    public List<Question> findAllWithTheme() {
        return em.createQuery(
            "SELECT q FROM Question q JOIN FETCH q.theme",
            Question.class
        ).getResultList();
    }

    public List<Question> findByTheme(Theme theme) {
        return em.createQuery(
            "SELECT q FROM Question q WHERE q.theme = :theme",
            Question.class
        )
        .setParameter("theme", theme)
        .getResultList();
    }

    public List<Question> findRandomByTheme(Theme theme, int limit) {
        return em.createQuery(
            "SELECT q FROM Question q WHERE q.theme = :theme ORDER BY FUNCTION('RAND')",
            Question.class
        )
        .setParameter("theme", theme)
        .setMaxResults(limit)
        .getResultList();
    }
}
