package com.testapp.controller;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Named("testDBBean")
@RequestScoped
public class TestDBBean {

    @PersistenceContext(unitName = "testPU")
    private EntityManager em;

    public void testerConnexion() {
        try {
            em.createNativeQuery("SELECT 1").getSingleResult();

            FacesContext.getCurrentInstance().addMessage(
                null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "SUCCESS",
                        "Connexion MySQL OK ✅")
            );

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(
                null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "ERROR",
                        "Connexion MySQL FAILED ❌")
            );
            e.printStackTrace();
        }
    }
}
