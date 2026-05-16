package com.primeestate.model;

/**
 * MODULE 2 – User
 * OOP - Inheritance: extends BaseEntity (inherits id, createdAt, validate(), getDisplayName())
 * OOP - Encapsulation: all fields private, exposed via getters/setters
 */
public class User extends BaseEntity {

    private String fullName;
    private String email;
    private String password;
    private String phone;
    private String preferredLocation;
    private String accountStatus;
    private String role;

    public User() {}

    public User(int id, String fullName, String email, String phone,
                String preferredLocation, String accountStatus, String role) {
        setId(id);
        this.fullName          = fullName;
        this.email             = email;
        this.phone             = phone;
        this.preferredLocation = preferredLocation;
        this.accountStatus     = accountStatus;
        this.role              = role;
    }

    // Getters
    public String getFullName()          { return fullName; }
    public String getEmail()             { return email; }
    public String getPassword()          { return password; }
    public String getPhone()             { return phone; }
    public String getPreferredLocation() { return preferredLocation; }
    public String getAccountStatus()     { return accountStatus; }
    public String getRole()              { return role; }

    // Setters
    public void setFullName(String fullName)                   { this.fullName          = fullName; }
    public void setEmail(String email)                         { this.email             = email; }
    public void setPassword(String password)                   { this.password          = password; }
    public void setPhone(String phone)                         { this.phone             = phone; }
    public void setPreferredLocation(String preferredLocation) { this.preferredLocation = preferredLocation; }
    public void setAccountStatus(String accountStatus)         { this.accountStatus     = accountStatus; }
    public void setRole(String role)                           { this.role              = role; }

    @Override
    public String getDisplayName() { return fullName; }

    @Override
    public String validate() {
        if (fullName == null || fullName.isBlank()) return "Full name is required";
        if (email    == null || email.isBlank())    return "Email is required";
        if (password == null || password.isBlank()) return "Password is required";
        return null;
    }
}
