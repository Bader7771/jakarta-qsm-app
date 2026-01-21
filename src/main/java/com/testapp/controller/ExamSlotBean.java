package com.testapp.controller;

import com.testapp.dao.CandidateDAO;
import com.testapp.dao.ExamSlotDAO;
import com.testapp.dao.TestSessionDAO;
import com.testapp.entity.Candidate;
import com.testapp.entity.ExamSlot;
import com.testapp.entity.TestSession;
import com.testapp.service.MailService;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.mail.MessagingException;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Named("examSlotBean")
@SessionScoped
public class ExamSlotBean implements Serializable {

    private static final long serialVersionUID = 1L;
    @Inject
    private MailService mailService;
    

    private ExamSlot newSlot = new ExamSlot();

    public ExamSlot getNewSlot() {
        return newSlot;
    }

    public void setNewSlot(ExamSlot newSlot) {
        this.newSlot = newSlot;
    }

    public List<ExamSlot> getSlots() {
        return examSlotDAO.findAll();
    }

    public String createSlot() {
        examSlotDAO.save(newSlot);
        newSlot = new ExamSlot();
        return null;
    }

    public String deleteSlot(Long id) {
        examSlotDAO.deleteWithSessions(id);
        return null;
    }

    private Long selectedSlotId;
    private ExamSlot selectedSlot;
    private String sessionCode;

    @Inject
    private ExamSlotDAO examSlotDAO;

    @Inject
    private TestSessionDAO testSessionDAO;

    @Inject
    private CandidateBean candidateBean;

    @Inject
    private CandidateDAO candidateDAO;

    public Long getSelectedSlotId() {
        return selectedSlotId;
    }

    public void setSelectedSlotId(Long selectedSlotId) {
        this.selectedSlotId = selectedSlotId;
    }

    public String getSessionCode() {
        return sessionCode;
    }

    public LocalDate getDateExam() {
        return selectedSlot != null ? selectedSlot.getDateExam() : null;
    }

    public LocalTime getStartTime() {
        return selectedSlot != null ? selectedSlot.getStartTime() : null;
    }

    public List<ExamSlot> getAvailableSlots() {
        return examSlotDAO.findDisponibles();
    }

    public String saveSlot() {

        Candidate sessionCandidate = candidateBean.getCandidate();
        if (sessionCandidate == null || sessionCandidate.getEmail() == null) {
            return "login-test.xhtml?faces-redirect=true";
        }

        Candidate candidate = candidateDAO.findByEmail(sessionCandidate.getEmail());
        if (candidate == null) {
            return "login-test.xhtml?faces-redirect=true";
        }

        ExamSlot slot = examSlotDAO.findById(selectedSlotId);
        if (slot == null) {
            return null;
        }

        this.selectedSlot = slot;
        this.sessionCode = generateCode();

        TestSession session = new TestSession();
        session.setCandidate(candidate);
        session.setExamSlot(slot);
        session.setStatus("CREATED");
        session.setSessionCode(this.sessionCode);

        testSessionDAO.save(session);
        
        try {
            mailService.sendRegistrationMailToCandidate(candidate.getEmail(), candidate.getNom(),candidate.getPrenom());
        } catch (MessagingException e) {
            e.printStackTrace(); // ou log + message JSF
        }

        return "confirmation.xhtml?faces-redirect=true";
    }

    private String generateCode() {
        return UUID.randomUUID()
                   .toString()
                   .substring(0, 8)
                   .toUpperCase();
    }

    public String deleteAllSlots() {
        examSlotDAO.deleteAll();
        return null;
    }
}
