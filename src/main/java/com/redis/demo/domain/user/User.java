package com.redis.demo.domain.user;

public class User {
    private Long id;
    private String emailId;
    private String firstName;
    private String lastName;
    private String passwordHash;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmailId() { return emailId; }
    public void setEmailId(String emailId) { this.emailId = emailId; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
}

