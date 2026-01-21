package com.testapp.controller.admin;

import com.testapp.dao.CandidateDAO;
import com.testapp.entity.Candidate;
import com.testapp.service.MailService;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.mail.MessagingException;

import java.io.Serializable;
import java.util.List;

@Named("adminCandidateBean")
@ViewScoped
public class AdminCandidateBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private CandidateDAO candidateDAO;

    @Inject
    private MailService mailService;

    private List<Candidate> candidats;

    @PostConstruct
    public void init() {
        // charger tous les candidats
        candidats = candidateDAO.findAll();
    }

    public List<Candidate> getCandidats() {
        return candidats;
    }

    // Valider un candidat
    public String valider(Long id) {
        Candidate c = candidateDAO.findById(id);
        if (c != null) {
            c.setValidated(true);      // is_validated = 1
            candidateDAO.update(c);

            // envoyer l'email de validation au candidat
            try {
                mailService.sendAccountValidatedMailToCandidate(
                        c.getEmail(),
                        c.getNom(),
                        c.getPrenom()
                );
            } catch (MessagingException e) {
                e.printStackTrace(); // ou log + message JSF
            }
        }

        // rafraîchir la liste
        candidats = candidateDAO.findAll();
        return null;
    }

    // Invalider / bloquer
    public String invalider(Long id) {
        Candidate c = candidateDAO.findById(id);
        if (c != null) {
            c.setValidated(false);
            candidateDAO.update(c);
        }
        candidats = candidateDAO.findAll();
        return null;
    }
}
