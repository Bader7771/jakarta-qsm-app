package com.testapp.dao;

import com.testapp.entity.Candidate;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class CandidateDAO {

    @PersistenceContext(unitName = "testPU")
    private EntityManager em;

    public Candidate findByEmail(String email) {
        List<Candidate> list = em.createQuery(
                "SELECT c FROM Candidate c WHERE c.email = :email",
                Candidate.class
        )
        .setParameter("email", email)
        .getResultList();

        return list.isEmpty() ? null : list.get(0);
    }

    public Candidate findById(Long id) {
        return em.find(Candidate.class, id);
    }

    public List<Candidate> findAll() {
        return em.createQuery("SELECT c FROM Candidate c", Candidate.class)
                 .getResultList();
    }

    public void save(Candidate candidate) {
        if (candidate.getId() == null) {
            em.persist(candidate);   // nouvel enregistrement
        } else {
            em.merge(candidate);     // mise à jour
        }
    }

    public void update(Candidate candidate) {
        em.merge(candidate);
    }

    /** Nouvelle méthode : suppression complète du candidat par id. */
    public void deleteById(Long id) {
        Candidate c = em.find(Candidate.class, id);
        if (c != null) {
            em.remove(c);
        }
    }
}
