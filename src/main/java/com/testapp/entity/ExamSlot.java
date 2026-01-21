package com.testapp.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "exam_slot")
public class ExamSlot implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_exam", nullable = false)
    private LocalDate dateExam;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    // ⭐ AJOUTER CE CHAMP MANQUANT
    @Column(name = "disponible", nullable = false)
    private Boolean disponible = true;  // Valeur par défaut

    public ExamSlot() {}

    public ExamSlot(LocalDate dateExam, LocalTime startTime, LocalTime endTime) {
        this.dateExam = dateExam;
        this.startTime = startTime;
        this.endTime = endTime;
        this.disponible = true;
    }

    // Getters et setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDateExam() {
        return dateExam;
    }

    public void setDateExam(LocalDate dateExam) {
        this.dateExam = dateExam;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Boolean getDisponible() {
        return disponible;
    }

    public void setDisponible(Boolean disponible) {
        this.disponible = disponible;
    }

    @Override
    public String toString() {
        return "ExamSlot{id=" + id + ", dateExam=" + dateExam + 
               ", startTime=" + startTime + ", endTime=" + endTime + 
               ", disponible=" + disponible + '}';
    }
}
