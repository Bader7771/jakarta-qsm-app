package com.testapp.controller.admin;

import com.testapp.dao.ThemeDAO;
import com.testapp.entity.Theme;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

@Named("themeBean")
@RequestScoped
public class ThemeBean implements Serializable {

    private Theme theme = new Theme();

    @Inject
    private ThemeDAO themeDAO;

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    public List<Theme> getThemes() {
        return themeDAO.findAll();
    }

    public String ajouter() {

        if (theme.getId() == null) {
            // création : vérifier doublon
            if (themeDAO.findByLibelle(theme.getLibelle()) != null) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Ce thème existe déjà", null));
                return null;
            }
            themeDAO.save(theme);
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("Thème ajouté avec succès"));
        } else {
            // modification
            themeDAO.update(theme);
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("Thème mis à jour avec succès"));
        }

        theme = new Theme(); // reset
        return null;
    }

    public void editTheme(Theme t) {
        this.theme = t;
    }

    public void deleteTheme(Long id) {
        try {
            themeDAO.delete(id);
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("Thème supprimé"));
        } catch (Exception e) {
            // Message clair pour l'utilisateur
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Impossible de supprimer ce thème car il est utilisé dans des tentatives de test.", null));
        }
    }

}
