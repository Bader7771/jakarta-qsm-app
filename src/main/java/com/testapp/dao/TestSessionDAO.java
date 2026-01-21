package com.testapp.dao;

import java.util.ArrayList;
import java.util.List;

import com.testapp.entity.TestSession;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class TestSessionDAO {

    @PersistenceContext(unitName = "testPU")
    private EntityManager em;

    @Transactional
    public void save(TestSession session) {
        em.persist(session);
        em.flush(); // force l'INSERT tout de suite pour que le code soit visible
    }

    /**
     * Mise à jour sécurisée : on travaille sur l'entité managée.
     */
    @Transactional
    public void update(TestSession detachedSession) {

        if (detachedSession == null || detachedSession.getId() == null) {
            return;
        }

        TestSession managed = em.find(TestSession.class, detachedSession.getId());
        if (managed == null) {
            return;
        }

        managed.setTheme(detachedSession.getTheme());
        managed.setStatus(detachedSession.getStatus());
        managed.setStartTime(detachedSession.getStartTime());
        managed.setScore(detachedSession.getScore());
    }

    public TestSession findByCode(String code) {
        if (code == null) {
            return null;
        }

        List<TestSession> list = em.createQuery(
                "SELECT ts FROM TestSession ts WHERE UPPER(ts.sessionCode) = :code",
                TestSession.class
        )
        .setParameter("code", code.trim().toUpperCase())
        .getResultList();

        return list.isEmpty() ? null : list.get(0);
    }
    public void deleteByCandidateId(Long candidateId) {
        em.createQuery("DELETE FROM TestSession ts " +
                       "WHERE ts.candidate.id = :cid")
          .setParameter("cid", candidateId)
          .executeUpdate();
    }

    public List<TestSession> findFinishedSessions() {
        return em.createQuery(
            "SELECT ts FROM TestSession ts " +
            "WHERE ts.status = 'FINISHED' " +
            "ORDER BY ts.startTime DESC",
            TestSession.class
        ).getResultList();
    }
    public List<TestSession> findAll() {
        try {
            return em.createQuery("SELECT ts FROM TestSession ts", TestSession.class)
                     .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    
}
