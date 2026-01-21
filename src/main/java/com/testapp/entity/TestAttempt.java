package com.testapp.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "test_attempt")
public class TestAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // statut de la tentative : CREATED, STARTED, FINISHED
    @Column(nullable = false)
    private String status;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "score")
    private Integer score;

    // ===== RELATIONS =====

    @ManyToOne(optional = false)
    @JoinColumn(name = "test_session_id", nullable = false)
    private TestSession testSession;

    @ManyToOne(optional = false)
    @JoinColumn(name = "theme_id", nullable = false)
    private Theme theme;

    // ===== getters / setters =====

    public Long getId() { return id; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public TestSession getTestSession() { return testSession; }
    public void setTestSession(TestSession testSession) { this.testSession = testSession; }

    public Theme getTheme() { return theme; }
    public void setTheme(Theme theme) { this.theme = theme; }

	public Object getAttemptDate() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getCandidate() {
		// TODO Auto-generated method stub
		return null;
	}
}
