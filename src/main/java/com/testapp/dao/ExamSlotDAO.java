package com.testapp.dao;

import com.testapp.entity.ExamSlot;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@Stateless
public class ExamSlotDAO {

    @PersistenceContext(unitName = "testPU")
    private EntityManager em;

    public void save(ExamSlot slot) {
        em.persist(slot);
    }

    public void update(ExamSlot detachedSlot) {
        if (detachedSlot == null || detachedSlot.getId() == null) {
            return;
        }

        ExamSlot managed = em.find(ExamSlot.class, detachedSlot.getId());
        if (managed == null) {
            return;
        }

        managed.setDateExam(detachedSlot.getDateExam());
        managed.setStartTime(detachedSlot.getStartTime());
        managed.setEndTime(detachedSlot.getEndTime());
    }

    public List<ExamSlot> findAll() {
        return em.createQuery(
            "SELECT e FROM ExamSlot e ORDER BY e.dateExam, e.startTime",
            ExamSlot.class
        ).getResultList();
    }

    public ExamSlot findById(Long id) {
        return em.find(ExamSlot.class, id);
    }

    public List<ExamSlot> findDisponibles() {
        return em.createQuery(
            "SELECT e FROM ExamSlot e ORDER BY e.dateExam, e.startTime",
            ExamSlot.class
        ).getResultList();
    }

    // Supprimer un créneau + sessions + attempts liés
    public void deleteWithSessions(Long slotId) {
        // 1. Supprimer les TestAttempt liés aux sessions
        em.createQuery("DELETE FROM TestAttempt ta WHERE ta.testSession.examSlot.id = :slotId")
          .setParameter("slotId", slotId)
          .executeUpdate();

        // 2. Supprimer les TestSession liés au créneau
        em.createQuery("DELETE FROM TestSession ts WHERE ts.examSlot.id = :slotId")
          .setParameter("slotId", slotId)
          .executeUpdate();

        // 3. Supprimer le créneau
        ExamSlot slot = em.find(ExamSlot.class, slotId);
        if (slot != null) {
            em.remove(slot);
        }
    }

    public void delete(Long id) {
        Long count = em.createQuery(
            "SELECT COUNT(ts) FROM TestSession ts WHERE ts.examSlot.id = :id",
            Long.class
        )
        .setParameter("id", id)
        .getSingleResult();

        if (count > 0) {
            throw new IllegalStateException("CRENEAU_UTILISE");
        }

        ExamSlot slot = em.find(ExamSlot.class, id);
        if (slot != null) {
            em.remove(slot);
        }
    }

    public void deleteAll() {
        em.createQuery("DELETE FROM ExamSlot").executeUpdate();
    }
}
