package com.testapp.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "theme")
public class Theme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String libelle;

    // 🔥 التغيير الوحيد هنا
    @OneToMany(mappedBy = "theme", fetch = FetchType.EAGER)
    private List<Question> questions;

    public Long getId() {
        return id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public List<Question> getQuestions() {
        return questions;
    }
}
