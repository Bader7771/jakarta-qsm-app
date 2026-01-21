package com.testapp.dao;

import com.testapp.entity.TestAttempt;
import com.testapp.entity.TestSession;
import com.testapp.entity.Theme;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class TestAttemptDAO {

    @PersistenceContext
    private EntityManager em;

    public void create(TestAttempt attempt) {
        em.persist(attempt);
    }

    public TestAttempt update(TestAttempt attempt) {
        return em.merge(attempt);
    }

    public TestAttempt findById(Long id) {
        return em.find(TestAttempt.class, id);
    }

    public List<TestAttempt> findBySession(TestSession session) {
        return em.createQuery(
                "SELECT a FROM TestAttempt a WHERE a.testSession = :session",
                TestAttempt.class)
            .setParameter("session", session)
            .getResultList();
    }

    public TestAttempt findBySessionAndTheme(TestSession session, Theme theme) {
        List<TestAttempt> list = em.createQuery(
                "SELECT a FROM TestAttempt a " +
                "WHERE a.testSession = :session AND a.theme = :theme",
                TestAttempt.class)
            .setParameter("session", session)
            .setParameter("theme", theme)
            .getResultList();

        return list.isEmpty() ? null : list.get(0);
    }
    public List<TestAttempt> findFinishedAttempts() {
        return em.createQuery(
                "SELECT a FROM TestAttempt a " +
                "WHERE a.status = 'FINISHED'",
                TestAttempt.class)
            .getResultList();
    }

    public void deleteByCandidateId(Long candidateId) {
        em.createQuery("DELETE FROM TestAttempt ta " +
                       "WHERE ta.testSession.candidate.id = :cid")
          .setParameter("cid", candidateId)
          .executeUpdate();
    }

	public List<TestAttempt> findAll() {
		// TODO Auto-generated method stub
		return null;
	}
}
