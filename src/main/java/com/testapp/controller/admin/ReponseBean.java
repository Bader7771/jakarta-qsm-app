package com.testapp.controller.admin;

import com.testapp.dao.QuestionDAO;
import com.testapp.dao.ReponseDAO;
import com.testapp.dao.ThemeDAO;
import com.testapp.entity.Question;
import com.testapp.entity.Reponse;
import com.testapp.entity.Theme;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named("reponseBean")
@ViewScoped
public class ReponseBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private ReponseDAO reponseDAO;

    @Inject
    private QuestionDAO questionDAO;

    @Inject
    private ThemeDAO themeDAO;

    private Long selectedThemeId;
    private Long selectedQuestionId;
    private Reponse reponse = new Reponse();

    private List<Theme> themes;
    private List<Question> questions;
    private List<Reponse> reponses;

    // ============ GETTERS / SETTERS ============

    public Long getSelectedThemeId() {
        return selectedThemeId;
    }

    public void setSelectedThemeId(Long selectedThemeId) {
        this.selectedThemeId = selectedThemeId;
    }

    public Long getSelectedQuestionId() {
        return selectedQuestionId;
    }

    public void setSelectedQuestionId(Long selectedQuestionId) {
        this.selectedQuestionId = selectedQuestionId;
    }

    public Reponse getReponse() {
        return reponse;
    }

    public void setReponse(Reponse reponse) {
        this.reponse = reponse;
    }

    public List<Theme> getThemes() {
        if (themes == null) {
            themes = themeDAO.findAll();
        }
        return themes;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public List<Reponse> getReponses() {
        if (reponses == null && selectedQuestionId != null) {
            reponses = reponseDAO.findByQuestion(selectedQuestionId);
        }
        return reponses;
    }

    // ============ FILTRE PAR THÈME ============

    public void loadQuestionsByTheme() {
        if (selectedThemeId != null) {
            Theme t = themeDAO.findById(selectedThemeId);
            questions = questionDAO.findByTheme(t);
        } else {
            questions = null;
        }
        // reset question & réponses
        selectedQuestionId = null;
        reponses = null;
        reponse = new Reponse();
    }

    // ============ CHARGER LES RÉPONSES POUR LA QUESTION ============

    public void loadReponses() {
        if (selectedQuestionId != null) {
            reponses = reponseDAO.findByQuestion(selectedQuestionId);
        } else {
            reponses = null;
        }
        reponse = new Reponse();
    }

    // ============ AJOUT / MODIF ============

    public void ajouterReponse() {

        if (selectedQuestionId == null) {
            return;
        }

        Question q = questionDAO.findById(selectedQuestionId);
        reponse.setQuestion(q);

        if ("UNIQUE".equals(q.getType()) && reponse.isCorrect()) {
            reponseDAO.resetCorrectAnswers(selectedQuestionId);
        }

        if (reponse.getId() == null) {
            reponseDAO.save(reponse);
        } else {
            reponseDAO.update(reponse);
        }

        reponses = reponseDAO.findByQuestion(selectedQuestionId);
        reponse = new Reponse();
    }

    public void editReponse(Reponse r) {
        this.reponse = r;
        this.selectedQuestionId = r.getQuestion().getId();
        this.selectedThemeId = r.getQuestion().getTheme().getId();
    }

    // ============ SUPPRESSION ============

    public void supprimerReponse(Long id) {
        reponseDAO.delete(id);
        if (selectedQuestionId != null) {
            reponses = reponseDAO.findByQuestion(selectedQuestionId);
        }
    }
}
