package com.testapp.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "test_session")
public class TestSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_code", nullable = false, unique = true)
    private String sessionCode;

    @Column(nullable = false)
    private String status;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "score")
    private Integer score;

    // ===== RELATIONS =====

    @ManyToOne(optional = false)
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    @ManyToOne(optional = true)
    @JoinColumn(name = "theme_id", nullable = true)
    private Theme theme;

    @ManyToOne(optional = true)
    @JoinColumn(name = "exam_slot_id", nullable = true)
    private ExamSlot examSlot;

    // ===== getters / setters =====

    public Long getId() { return id; }

    public String getSessionCode() { return sessionCode; }
    public void setSessionCode(String sessionCode) { this.sessionCode = sessionCode; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public Candidate getCandidate() { return candidate; }
    public void setCandidate(Candidate candidate) { this.candidate = candidate; }

    public Theme getTheme() { return theme; }
    public void setTheme(Theme theme2) { this.theme = theme2; }

    public ExamSlot getExamSlot() { return examSlot; }
    public void setExamSlot(ExamSlot examSlot) { this.examSlot = examSlot; }
}
