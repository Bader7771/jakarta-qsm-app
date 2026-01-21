package com.testapp.controller.user;

import com.testapp.dao.ThemeDAO;
import com.testapp.entity.Theme;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named("userHomeBean")
@RequestScoped
public class UserHomeBean implements Serializable {

    @Inject
    private ThemeDAO themeDAO;

    // Si tu n'as pas encore UserBean, commente ces 3 lignes pour le moment
    // @Inject
    // private UserBean userBean;

    public List<Theme> getThemesDisponibles() {
        return themeDAO.findAll();
    }

    // >>> méthode appelée par index.xhtml
    public String preparerTest(Long themeId) {

        // Si tu as un UserBean, tu peux mémoriser le thème et tester la connexion
        /*
        userBean.setThemeChoisiId(themeId);
        if (!userBean.isConnecte()) {
            return "session-or-register?faces-redirect=true";
        }
        */

        // Version simple : aller vers choisirTheme avec l'id du thème
        return "choisirTheme?faces-redirect=true&amp;themeId=" + themeId;
    }
}
