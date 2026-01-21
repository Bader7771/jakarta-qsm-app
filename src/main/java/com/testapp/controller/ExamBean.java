package com.testapp.controller;

import com.testapp.dao.QuestionDAO;
import com.testapp.dao.ReponseDAO;
import com.testapp.dao.TestSessionDAO;
import com.testapp.entity.Candidate;
import com.testapp.entity.Question;
import com.testapp.entity.Reponse;
import com.testapp.entity.TestSession;
import com.testapp.entity.Theme;

import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Named("examBean")
@ViewScoped
public class ExamBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Question> questions;
    private int index;
    private Question currentQuestion;
    private Long selectedReponseId;

    private Theme theme;
    private Candidate candidate;

    @Inject
    private QuestionDAO questionDAO;

    @Inject
    private ReponseDAO reponseDAO;

    @Inject
    private TestSessionDAO testSessionDAO;

    @PostConstruct
    public void init() {
        FacesContext fc = FacesContext.getCurrentInstance();

        // récupérer le code passé en paramètre URL ?code=XXXX
        String code = fc.getExternalContext()
                        .getRequestParameterMap()
                        .get("code");

        System.out.println(">>> EXAM INIT, CODE = " + code);

        if (code == null || code.isEmpty()) {
            System.out.println(">>> Aucun code de session dans l’URL");
            questions = Collections.emptyList();
            return;
        }

        TestSession session = testSessionDAO.findByCode(code);

        if (session == null) {
            System.out.println(">>> Aucune session trouvée pour ce code");
            questions = Collections.emptyList();
            return;
        }

        // Infos candidat + thème (thème peut être null si choisi plus tard)
        this.candidate = session.getCandidate();
        this.theme = session.getTheme();

        // Chargement des questions
        if (this.theme != null) {
            questions = questionDAO.findByTheme(theme);
        } else {
            // TEMPORAIRE : si pas de thème, on charge toutes les questions
            questions = questionDAO.findAll();
        }

        System.out.println(">>> QUESTIONS SIZE = " + (questions == null ? 0 : questions.size()));

        if (questions != null && !questions.isEmpty()) {
            index = 0;
            currentQuestion = questions.get(index);
        } else {
            questions = Collections.emptyList();
        }
    }

    public void next() {
        if (questions == null || questions.isEmpty()) {
            return;
        }
        if (index < questions.size() - 1) {
            index++;
            currentQuestion = questions.get(index);
            selectedReponseId = null;
        }
    }

    // ===== GETTERS pour JSF =====

    public Question getCurrentQuestion() {
        return currentQuestion;
    }

    public List<Reponse> getReponses() {
        if (currentQuestion == null) return Collections.emptyList();
        return reponseDAO.findByQuestion(currentQuestion.getId());
    }

    public int getIndex() {
        return index;
    }

    public boolean isLastQuestion() {
        return questions != null && !questions.isEmpty() && index == questions.size() - 1;
    }

    public Long getSelectedReponseId() {
        return selectedReponseId;
    }

    public void setSelectedReponseId(Long selectedReponseId) {
        this.selectedReponseId = selectedReponseId;
    }

    public Theme getTheme() {
        return theme;
    }

    public Candidate getCandidate() {
        return candidate;
    }

    // méthode finish() à implémenter plus tard (calcul score, etc.)
    public String finish() {
        // TODO : calculer le résultat, enregistrer, rediriger
        return null;
    }
}
