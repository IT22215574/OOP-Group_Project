package com.primeestate.model;

/**
 * MODULE 1 – Real Estate Agent
 * OOP - Inheritance: extends BaseEntity (inherits id, createdAt, validate(), getDisplayName())
 * OOP - Encapsulation: all fields private, exposed via getters/setters
 */
public class Agent extends BaseEntity {

    private String fullName;
    private String contactNumber;
    private String email;
    private String location;
    private int    experienceYears;
    private String propertySpecialization;
    private String availabilityStatus;

    public Agent() {}

    public Agent(int id, String fullName, String email, String contactNumber,
                 String location, int experienceYears,
                 String propertySpecialization, String availabilityStatus) {
        setId(id);
        this.fullName               = fullName;
        this.email                  = email;
        this.contactNumber          = contactNumber;
        this.location               = location;
        this.experienceYears        = experienceYears;
        this.propertySpecialization = propertySpecialization;
        this.availabilityStatus     = availabilityStatus;
    }

    // Getters
    public String getFullName()               { return fullName; }
    public String getContactNumber()          { return contactNumber; }
    public String getEmail()                  { return email; }
    public String getLocation()               { return location; }
    public int    getExperienceYears()        { return experienceYears; }
    public String getPropertySpecialization() { return propertySpecialization; }
    public String getAvailabilityStatus()     { return availabilityStatus; }

    // Setters
    public void setFullName(String fullName)                           { this.fullName               = fullName; }
    public void setContactNumber(String contactNumber)                 { this.contactNumber          = contactNumber; }
    public void setEmail(String email)                                 { this.email                  = email; }
    public void setLocation(String location)                           { this.location               = location; }
    public void setExperienceYears(int experienceYears)               { this.experienceYears        = experienceYears; }
    public void setPropertySpecialization(String propertySpecialization){ this.propertySpecialization = propertySpecialization; }
    public void setAvailabilityStatus(String availabilityStatus)       { this.availabilityStatus     = availabilityStatus; }

    @Override
    public String getDisplayName() { return fullName; }

    @Override
    public String validate() {
        if (fullName == null || fullName.isBlank()) return "Full name is required";
        if (email    == null || email.isBlank())    return "Email is required";
        return null;
    }
}
