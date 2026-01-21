package com.testapp.dao;

import com.testapp.entity.Reponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class ReponseDAO {

    @PersistenceContext(unitName = "testPU")
    private EntityManager em;

    @Transactional
    public void save(Reponse reponse) {
        em.persist(reponse);
    }

    @Transactional
    public Reponse update(Reponse reponse) {
        return em.merge(reponse);
    }

    public List<Reponse> findByQuestion(Long questionId) {
        return em.createQuery(
            "SELECT r FROM Reponse r WHERE r.question.id = :qid",
            Reponse.class
        )
        .setParameter("qid", questionId)
        .getResultList();
    }

    @Transactional
    public void resetCorrectAnswers(Long questionId) {
        em.createQuery(
            "UPDATE Reponse r SET r.correct = false WHERE r.question.id = :qid"
        )
        .setParameter("qid", questionId)
        .executeUpdate();
    }

    @Transactional
    public void delete(Long id) {
        Reponse r = em.find(Reponse.class, id);
        if (r != null) {
            em.remove(r);
        }
    }
}
