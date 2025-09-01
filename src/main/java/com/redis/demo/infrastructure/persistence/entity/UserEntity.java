package com.redis.demo.infrastructure.persistence.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("users")
public class UserEntity {
    @Id
    private Long id;

    @Column("email_id")
    private String emailId;

    @Column("first_name")
    private String firstName;

    @Column("last_name")
    private String lastName;

    @Column("password_hash")
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

