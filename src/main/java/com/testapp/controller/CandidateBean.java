package com.testapp.controller;

import com.testapp.dao.CandidateDAO;
import com.testapp.entity.Candidate;
import com.testapp.service.MailService;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;

@Named("candidateBean")
@SessionScoped
public class CandidateBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private Candidate candidate = new Candidate();

    @Inject
    private CandidateDAO candidateDAO;

    @Inject
    private MailService mailService;

    public Candidate getCandidate() {
        return candidate;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    public String register() {
        // 1) enregistrer le candidat
        candidateDAO.save(candidate);

        // 2) envoyer un email d'accusé de réception
        try {
            String subject = "Inscription reçue - Test en ligne";
            String body =
                "Bonjour " + candidate.getPrenom() + " " + candidate.getNom() + ",\n\n" +
                "Votre inscription au test a bien été enregistrée.\n" +
                "Votre demande est maintenant en attente de validation par l'administrateur.\n\n" +
                "Vous recevrez un second email avec le lien d'accès au test une fois votre candidature validée.\n\n" +
                "Cordialement,\n" +
                "L'équipe des tests";

            mailService.sendMail(candidate.getEmail(), subject, body);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 3) redirection existante
        return "choose-slot.xhtml?faces-redirect=true";
    }
}
