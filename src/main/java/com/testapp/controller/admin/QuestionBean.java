package com.testapp.controller.admin;

import com.testapp.dao.QuestionDAO;
import com.testapp.dao.ThemeDAO;
import com.testapp.entity.Question;
import com.testapp.entity.Theme;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named("questionBean")
@ViewScoped
public class QuestionBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private QuestionDAO questionDAO;

    @Inject
    private ThemeDAO themeDAO;

    private Question question = new Question();  // utilisé par le formulaire
    private Long selectedThemeId;

    private List<Theme> themes;
    private List<Question> questions;

    // ================== GETTERS / SETTERS ==================

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
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

    public List<Question> getQuestions() {
        if (questions == null) {
            questions = questionDAO.findAllWithTheme();
        }
        return questions;
    }

    // ================== AJOUT / MODIF ==================

    public void ajouterQuestion() {

        // lier le thème choisi
        Theme theme = themeDAO.findById(selectedThemeId);
        question.setTheme(theme);

        if (question.getId() == null) {
            // nouvelle question
            questionDAO.save(question);
        } else {
            // modification
            questionDAO.update(question);
        }

        // rafraîchir la liste
        questions = questionDAO.findAllWithTheme();

        // réinitialiser le formulaire
        question = new Question();
        selectedThemeId = null;
    }

    // appelé quand on clique sur "Modifier" dans la table
    public void editQuestion(Question q) {
        this.question = q;
        this.selectedThemeId = q.getTheme().getId();
    }

    // ================== SUPPRESSION ==================

    public void deleteQuestion(Question q) {
        questionDAO.delete(q);
        questions = questionDAO.findAllWithTheme();
    }
}
