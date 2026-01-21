package com.testapp.controller.admin;

import com.testapp.dao.CandidateDAO;
import com.testapp.dao.TestAttemptDAO;
import com.testapp.dao.TestSessionDAO;
import com.testapp.entity.Candidate;
import com.testapp.entity.TestAttempt;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

@Named("adminResultsBean")
@ViewScoped
public class AdminResultsBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private TestAttemptDAO testAttemptDAO;

    @Inject
    private TestSessionDAO testSessionDAO;

    @Inject
    private CandidateDAO candidateDAO;

    private List<TestAttempt> finishedAttempts;

    // utilisé par le dataTable
    public List<TestAttempt> getFinishedAttempts() {
        if (finishedAttempts == null) {
            finishedAttempts = testAttemptDAO.findFinishedAttempts();
        }
        return finishedAttempts;
    }

    // appelé par le bouton dans admin-results.xhtml
    public void supprimerCandidatComplet(Candidate candidate) {
        if (candidate == null || candidate.getId() == null) {
            return;
        }

        Long candidateId = candidate.getId();

        // 1. supprimer d'abord toutes les tentatives de ce candidat
        testAttemptDAO.deleteByCandidateId(candidateId);

        // 2. puis toutes ses sessions
        testSessionDAO.deleteByCandidateId(candidateId);

        // 3. enfin le candidat lui-même
        candidateDAO.deleteById(candidateId);

        // 4. mettre à jour la liste affichée
        getFinishedAttempts().removeIf(a ->
                a.getTestSession() != null &&
                a.getTestSession().getCandidate() != null &&
                candidateId.equals(a.getTestSession().getCandidate().getId()));
    }
}
