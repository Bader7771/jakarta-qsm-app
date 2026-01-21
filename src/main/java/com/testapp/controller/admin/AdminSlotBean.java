package com.testapp.controller.admin;

import com.testapp.dao.ExamSlotDAO;
import com.testapp.entity.ExamSlot;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

@Named("adminSlotBean")
@ViewScoped
public class AdminSlotBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private ExamSlotDAO slotDAO;

    private List<ExamSlot> slots;
    private ExamSlot slot = new ExamSlot();
    private boolean editMode = false;

    @PostConstruct
    public void init() {
        loadSlots();
    }

    public void loadSlots() {
        slots = slotDAO.findAll();
    }

    // Getters & Setters
    public List<ExamSlot> getSlots() {
        return slots;
    }

    public ExamSlot getSlot() {
        return slot;
    }

    public void setSlot(ExamSlot slot) {
        this.slot = slot;
    }

    public boolean isEditMode() {
        return editMode;
    }

    // Ajouter ou modifier un créneau
    public String ajouter() {
        try {
            if (editMode && slot.getId() != null) {
                slotDAO.update(slot);
                addMessage(FacesMessage.SEVERITY_INFO, 
                          "Succès", 
                          "Créneau modifié avec succès");
            } else {
                slotDAO.save(slot);
                addMessage(FacesMessage.SEVERITY_INFO, 
                          "Succès", 
                          "Créneau créé avec succès");
            }
            
            cancelEdit();
            loadSlots();
            
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, 
                      "Erreur", 
                      "Impossible d'enregistrer le créneau : " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // Éditer un créneau
    public void edit(ExamSlot s) {
        if (s != null) {
            this.slot = new ExamSlot();
            this.slot.setId(s.getId());
            this.slot.setDateExam(s.getDateExam());
            this.slot.setStartTime(s.getStartTime());
            this.slot.setEndTime(s.getEndTime());
            this.editMode = true;
        }
    }

    // Annuler l'édition
    public void cancelEdit() {
        this.slot = new ExamSlot();
        this.editMode = false;
    }

    // Supprimer avec sessions liées
    public String supprimerAvecSessions(Long id) {
        try {
            slotDAO.deleteWithSessions(id);
            addMessage(FacesMessage.SEVERITY_INFO, 
                      "Succès", 
                      "Créneau et sessions associées supprimés avec succès");
            loadSlots();
            
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, 
                      "Erreur", 
                      "Impossible de supprimer : " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(severity, summary, detail));
    }
}
