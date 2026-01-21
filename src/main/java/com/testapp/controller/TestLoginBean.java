package com.testapp.controller;

import com.testapp.dao.CandidateDAO;
import com.testapp.dao.TestSessionDAO;
import com.testapp.dao.TestAttemptDAO;
import com.testapp.dao.ThemeDAO;
import com.testapp.entity.Candidate;
import com.testapp.entity.ExamSlot;
import com.testapp.entity.TestAttempt;
import com.testapp.entity.TestSession;
import com.testapp.entity.Theme;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Named("testLoginBean")
@SessionScoped
public class TestLoginBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String sessionCode;
    private Long selectedThemeId;

    private List<Theme> themes;

    @Inject
    private ThemeDAO themeDAO;

    @Inject
    private TestSessionDAO testSessionDAO;

    @Inject
    private TestAttemptDAO testAttemptDAO;

    @Inject
    private CandidateDAO candidateDAO;

    @Inject
    private CandidateBean candidateBean;

    @Inject
    private TestSessionBean testSessionBean;

    // ----------------------------------------------------
    // Getters / Setters
    // ----------------------------------------------------

    public String getSessionCode() {
        return sessionCode;
    }

    public void setSessionCode(String sessionCode) {
        this.sessionCode = sessionCode;
    }

    public Long getSelectedThemeId() {
        return selectedThemeId;
    }

    public void setSelectedThemeId(Long selectedThemeId) {
        this.selectedThemeId = selectedThemeId;
    }

    public List<Theme> getThemes() {
        if (themes == null) {
            themes = themeDAO.findAll();
        }
        return themes;
    }

    // ----------------------------------------------------
    // Action principale : entrer dans le test
    // ----------------------------------------------------

    public String entrer() {

        // 0) vérifier que le code est saisi
        if (sessionCode == null || sessionCode.trim().isEmpty()) {
            addErrorMessage("code", "Veuillez saisir un code de session.");
            return null;
        }

        String cleanedCode = sessionCode.trim();

        // 1) retrouver la session de test par code
        TestSession session = testSessionDAO.findByCode(cleanedCode);
        if (session == null) {
            addErrorMessage("code", "Code de session invalide.");
            return null;
        }

        // 2) NE PLUS BLOQUER GLOBAL SUR FINISHED ICI
        // La gestion du FINISHED se fait par thème via TestAttempt.

        // 3) récupérer le candidat lié à cette session
        Candidate candidate = session.getCandidate();
        if (candidate == null) {
            addErrorMessage(null, "Candidat introuvable pour ce code.");
            return null;
        }

        // 4) blocage si le candidat n'est pas validé par l'admin
        if (!candidate.isValidated()) {
            addErrorMessage(null,
                "Votre inscription n'est pas encore validée par l'administrateur.");
            return null;
        }

        // 5) blocage si on est en dehors du créneau
        ExamSlot slot = session.getExamSlot();
        if (slot != null) {

            LocalDateTime now = LocalDateTime.now();

            LocalDate dateExam = slot.getDateExam();
            LocalTime start    = slot.getStartTime();
            LocalTime end      = slot.getEndTime();

            LocalDateTime startDateTime = LocalDateTime.of(dateExam, start);
            LocalDateTime endDateTime   = LocalDateTime.of(dateExam, end);

            if (now.isBefore(startDateTime)) {
                addErrorMessage(null,
                    "Vous ne pouvez pas encore passer le test. "
                  + "Votre créneau est le " + dateExam + " de " + start + " à " + end + ".");
                return null;
            }

            if (now.isAfter(endDateTime)) {
                addErrorMessage(null,
                    "Votre créneau est terminé. Vous ne pouvez plus passer le test.");
                return null;
            }
        }

        // 6) vérifier le thème choisi par le candidat
        Theme theme = themeDAO.findById(selectedThemeId);
        if (theme == null) {
            addErrorMessage("theme", "Veuillez choisir un thème valide.");
            return null;
        }

        // 7) chercher ou créer une tentative pour (session, theme)
        TestAttempt attempt = testAttemptDAO.findBySessionAndTheme(session, theme);

        if (attempt == null) {
            // première fois sur ce thème avec ce code
            attempt = new TestAttempt();
            attempt.setTestSession(session);
            attempt.setTheme(theme);
            attempt.setStatus("CREATED");
            testAttemptDAO.create(attempt);
        } else {
            // tentative existe déjà pour ce thème
            if ("FINISHED".equals(attempt.getStatus())) {
                addErrorMessage(null,
                    "Vous avez déjà terminé le test pour ce thème avec ce code.");
                return null;
            }
        }

        // 8) si la tentative est CREATED, on la démarre
        if ("CREATED".equals(attempt.getStatus())) {
            attempt.setStatus("STARTED");
            attempt.setStartTime(LocalDateTime.now());
            testAttemptDAO.update(attempt);
        }

        // 9) marquer la session globale comme démarrée au premier test
        if ("CREATED".equals(session.getStatus())) {
            session.setStatus("STARTED");
            session.setStartTime(LocalDateTime.now());
            testSessionDAO.update(session);
        }

        // 10) stocker le candidat en session
        candidateBean.setCandidate(candidate);

        // 11) initialiser la session de test (questions, thème, etc.)
        // tu pourras plus tard faire passer aussi 'attempt' si nécessaire
        testSessionBean.startSession(session, theme, attempt);

        // 12) aller à la page d'examen
        return "exam.xhtml?faces-redirect=true";
    }

    // ----------------------------------------------------
    // Ajax : rafraîchir le statut de validation sans reload
    // ----------------------------------------------------

    public void checkValidation() {
        if (sessionCode == null || sessionCode.trim().isEmpty()) {
            addErrorMessage("code", "Veuillez saisir un code de session.");
            return;
        }

        TestSession session = testSessionDAO.findByCode(sessionCode.trim());
        if (session == null) {
            addErrorMessage("code", "Code de session invalide.");
            return;
        }

        Candidate candidate = session.getCandidate();
        if (candidate == null) {
            addErrorMessage(null, "Candidat introuvable pour ce code.");
            return;
        }

        if (!candidate.isValidated()) {
            addErrorMessage(null,
                "Votre inscription n'est pas encore validée par l'administrateur.");
        } else {
            addErrorMessage(null,
                "Votre inscription est maintenant validée. Vous pouvez cliquer sur Entrer.");
        }
    }

    // ----------------------------------------------------
    // Utilitaire pour messages
    // ----------------------------------------------------

    private void addErrorMessage(String clientId, String message) {
        FacesContext.getCurrentInstance().addMessage(
            clientId,
            new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null)
        );
    }

    // ----------------------------------------------------
    // Infos de créneau pour le compte à rebours
    // ----------------------------------------------------

    public LocalDate getSlotDate() {
        if (sessionCode == null || sessionCode.trim().isEmpty()) {
            return null;
        }
        TestSession session = testSessionDAO.findByCode(sessionCode.trim());
        if (session == null || session.getExamSlot() == null) {
            return null;
        }
        return session.getExamSlot().getDateExam();
    }

    public LocalTime getSlotStartTime() {
        if (sessionCode == null || sessionCode.trim().isEmpty()) {
            return null;
        }
        TestSession session = testSessionDAO.findByCode(sessionCode.trim());
        if (session == null || session.getExamSlot() == null) {
            return null;
        }
        return session.getExamSlot().getStartTime();
    }

    public LocalTime getSlotEndTime() {
        if (sessionCode == null || sessionCode.trim().isEmpty()) {
            return null;
        }
        TestSession session = testSessionDAO.findByCode(sessionCode.trim());
        if (session == null || session.getExamSlot() == null) {
            return null;
        }
        return session.getExamSlot().getEndTime();
    }
}
