package com.testapp.controller.admin;

import java.io.Serializable;

public class ActivityDTO implements Serializable {

    private String date;
    private String candidateName;
    private String type;
    private String detail;

    public ActivityDTO() {}

    // Getters et Setters
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getCandidateName() { return candidateName; }
    public void setCandidateName(String candidateName) { this.candidateName = candidateName; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }
}
