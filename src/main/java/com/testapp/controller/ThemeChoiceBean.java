package com.testapp.controller;

import com.testapp.dao.ThemeDAO;
import com.testapp.entity.TestSession;
import com.testapp.entity.Theme;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

@Named("themeChoiceBean")
@SessionScoped
public class ThemeChoiceBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long selectedThemeId;
    private List<Theme> themes;

    @Inject
    private ThemeDAO themeDAO;

    @Inject
    private TestSessionBean testSessionBean;

    @PostConstruct
    public void init() {
        themes = themeDAO.findAll();
    }

    public String confirmTheme() {

        if (selectedThemeId == null) {
            FacesContext.getCurrentInstance().addMessage(
                null,
                new FacesMessage("Veuillez choisir un thème")
            );
            return null;
        }

        Theme theme = themeDAO.findById(selectedThemeId);

        // 🔥 إنشاء session إذا ما كانتش
        if (testSessionBean.getCurrentSession() == null) {
            TestSession session = new TestSession();
            session.setTheme(theme);
            testSessionBean.setCurrentSession(session);
        } else {
            testSessionBean.getCurrentSession().setTheme(theme);
        }

        return "exam.xhtml?faces-redirect=true";
    }

    public Long getSelectedThemeId() { return selectedThemeId; }
    public void setSelectedThemeId(Long selectedThemeId) { this.selectedThemeId = selectedThemeId; }
    public List<Theme> getThemes() { return themes; }
}
