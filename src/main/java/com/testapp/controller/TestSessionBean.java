package com.testapp.controller;

import com.testapp.dao.QuestionDAO;
import com.testapp.dao.TestSessionDAO;
import com.testapp.dao.TestAttemptDAO;
import com.testapp.entity.Question;
import com.testapp.entity.Reponse;
import com.testapp.entity.TestAttempt;
import com.testapp.entity.TestSession;
import com.testapp.entity.Theme;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Named("testSessionBean")
@SessionScoped
public class TestSessionBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private TestSession currentSession;
    private Theme currentTheme;
    private TestAttempt currentAttempt;

    private List<Question> examQuestions;

    private int currentIndex = 0;

    private int score = 0;
    private int total = 0;

    private int durationSeconds = 120;

    private Map<Long, Set<Long>> userAnswers = new HashMap<>();

    private Long selectedSingleAnswer;
    private Set<Long> selectedMultipleAnswers = new HashSet<>();

    @Inject
    private QuestionDAO questionDAO;

    @Inject
    private TestSessionDAO testSessionDAO;

    @Inject
    private TestAttemptDAO testAttemptDAO;

    // ================= SESSION =================
    public TestSession getCurrentSession() {
        if (currentSession == null) {
            currentSession = (TestSession) FacesContext
                    .getCurrentInstance()
                    .getExternalContext()
                    .getSessionMap()
                    .get("currentSession");
        }
        return currentSession;
    }

    public void setCurrentSession(TestSession session) {
        this.currentSession = session;
        FacesContext.getCurrentInstance()
                .getExternalContext()
                .getSessionMap()
                .put("currentSession", session);
    }

    public Theme getCurrentTheme() {
        return currentTheme;
    }

    public TestAttempt getCurrentAttempt() {
        return currentAttempt;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(int durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    // appelé depuis TestLoginBean.entrer(...)
    public void startSession(TestSession session, Theme theme, TestAttempt attempt) {
        setCurrentSession(session);
        this.currentTheme = theme;
        this.currentAttempt = attempt;

        examQuestions = questionDAO.findByTheme(theme);
        currentIndex = 0;

        score = 0;
        total = examQuestions != null ? examQuestions.size() : 0;

        userAnswers.clear();
        selectedSingleAnswer = null;
        selectedMultipleAnswers = new HashSet<>();
    }

    // ================= QUESTIONS EXAM =================
    public List<Question> getExamQuestions() {
        if (examQuestions == null && currentTheme != null) {
            examQuestions = questionDAO.findByTheme(currentTheme);
            total = examQuestions.size();
        }
        return examQuestions;
    }

    public Question getCurrentQuestion() {
        List<Question> qs = getExamQuestions();
        if (qs != null && !qs.isEmpty() && currentIndex < qs.size()) {
            return qs.get(currentIndex);
        }
        return null;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public boolean isLastQuestion() {
        return examQuestions != null &&
               currentIndex == examQuestions.size() - 1;
    }

    // ================= NAVIGATION =================
    public String nextQuestion() {
        saveCurrentAnswer();
        resetTempAnswers();

        if (examQuestions != null && currentIndex < examQuestions.size() - 1) {
            currentIndex++;
        }
        return null;
    }

    // ================= VALIDATION FINALE =================
    public String validerExam() {
        saveCurrentAnswer();
        calculateScore();

        // marquer la tentative (par thème) comme FINISHED
        if (currentAttempt != null) {
            currentAttempt.setStatus("FINISHED");
            currentAttempt.setEndTime(LocalDateTime.now());
            currentAttempt.setScore(score);
            testAttemptDAO.update(currentAttempt);
        }

        // optionnel : tu peux décider de mettre la session globale à FINISHED
        // seulement si tous les thèmes possibles sont terminés

        return "fin-exam.xhtml?faces-redirect=true";
    }

    // ================= SAUVEGARDE DES REPONSES =================
    private void saveCurrentAnswer() {
        Question q = getCurrentQuestion();
        if (q == null) return;

        if ("UNIQUE".equals(q.getType()) && selectedSingleAnswer != null) {
            Set<Long> set = new HashSet<>();
            set.add(selectedSingleAnswer);
            userAnswers.put(q.getId(), set);
        }

        if ("MULTIPLE".equals(q.getType()) && selectedMultipleAnswers != null) {
            userAnswers.put(q.getId(), new HashSet<>(selectedMultipleAnswers));
        }
    }

    private void resetTempAnswers() {
        selectedSingleAnswer = null;
        selectedMultipleAnswers.clear();
    }

    // ================= CALCUL DU SCORE =================
    private void calculateScore() {

        score = 0;
        if (examQuestions == null) {
            return;
        }

        for (Question q : examQuestions) {

            Set<Long> correctAnswers = new HashSet<>();
            for (Reponse r : q.getReponses()) {
                if (r.isCorrect()) {
                    correctAnswers.add(r.getId());
                }
            }

            Set<Long> user = userAnswers.get(q.getId());

            if (user != null && user.equals(correctAnswers)) {
                score++;
            }
        }
    }

    // ================= GETTERS POUR JSF =================
    public Long getSelectedSingleAnswer() {
        return selectedSingleAnswer;
    }

    public void setSelectedSingleAnswer(Long selectedSingleAnswer) {
        this.selectedSingleAnswer = selectedSingleAnswer;
    }

    public Set<Long> getSelectedMultipleAnswers() {
        return selectedMultipleAnswers;
    }

    public void setSelectedMultipleAnswers(Set<Long> selectedMultipleAnswers) {
        this.selectedMultipleAnswers = selectedMultipleAnswers;
    }

    public int getScore() {
        return score;
    }

    public int getTotal() {
        return total;
    }
}
