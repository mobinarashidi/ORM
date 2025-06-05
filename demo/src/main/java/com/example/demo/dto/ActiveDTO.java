package com.example.demo.dto;

public class ActiveDTO {
    private String username;
    private String daysSpent;

    public ActiveDTO() {}

    public ActiveDTO(String username, String daysSpent) {
        this.username = username;
        this.daysSpent = daysSpent;
    }

    public String getUsername() {
        return username;
    }

    public String getDaysSpent() {
        return daysSpent;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setDaysSpent(String daysSpent) {
        this.daysSpent = daysSpent;
    }
}
