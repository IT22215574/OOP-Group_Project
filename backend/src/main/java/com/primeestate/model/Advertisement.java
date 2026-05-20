package com.primeestate.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * MODULE 4 – Property Advertisement
 * OOP - Inheritance: extends BaseEntity (inherits id, createdAt, validate(), getDisplayName())
 * OOP - Encapsulation: Used encapsulation to keep their fields private and expose them through getters and setters.
/* INCOMING CHANGE COMMENTED OUT (merge conflict) BEGIN
 * OOP - Encapsulation: all fields private, exposed via getters/setters
INCOMING CHANGE COMMENTED OUT (merge conflict) END */
 /* Connects: agentId (Module 1); imagePaths up to 3 images stored in advertisement_images table
 */
public class Advertisement extends BaseEntity {

    private int        agentId;
    private String     propertyTitle;
    private String     propertyType;
    private BigDecimal price;
    private String     location;
    private String     description;
    private String     availabilityStatus;
/* INCOMING CHANGE COMMENTED OUT (merge conflict) BEGIN
    // List of uploaded image paths (limited to a maximum of 3 per advertisement)
INCOMING CHANGE COMMENTED OUT (merge conflict) END */
    private List<String> imagePaths;   // up to 3 image paths

    // Read-only join field
    private String agentName;

    public Advertisement() {
        this.imagePaths = new ArrayList<>();
    }

    public Advertisement(int id, int agentId, String propertyTitle, String propertyType,
                         BigDecimal price, String location, String description,
                         String availabilityStatus) {
        setId(id);
        this.agentId            = agentId;
        this.propertyTitle      = propertyTitle;
        this.propertyType       = propertyType;
        this.price              = price;
        this.location           = location;
        this.description        = description;
        this.availabilityStatus = availabilityStatus;
        this.imagePaths         = new ArrayList<>();
    }

    // Getters
    public int          getAgentId()            { return agentId; }
    public String       getPropertyTitle()      { return propertyTitle; }
    public String       getPropertyType()       { return propertyType; }
    public BigDecimal   getPrice()              { return price; }
    public String       getLocation()           { return location; }
    public String       getDescription()        { return description; }
    public String       getAvailabilityStatus() { return availabilityStatus; }
    public List<String> getImagePaths()         { return imagePaths; }
    public String       getAgentName()          { return agentName; }

    // Setters
    public void setAgentId(int agentId)                           { this.agentId            = agentId; }
    public void setPropertyTitle(String propertyTitle)            { this.propertyTitle      = propertyTitle; }
    public void setPropertyType(String propertyType)              { this.propertyType       = propertyType; }
    public void setPrice(BigDecimal price)                        { this.price              = price; }
    public void setLocation(String location)                      { this.location           = location; }
    public void setDescription(String description)                { this.description        = description; }
    public void setAvailabilityStatus(String availabilityStatus)  { this.availabilityStatus = availabilityStatus; }
    public void setImagePaths(List<String> imagePaths)            { this.imagePaths         = imagePaths; }
    public void setAgentName(String agentName)                    { this.agentName          = agentName; }

    public void addImagePath(String path) {
        if (this.imagePaths == null) this.imagePaths = new ArrayList<>();
        if (this.imagePaths.size() < 3) this.imagePaths.add(path);
    }

    @Override
    public String getDisplayName() { return propertyTitle; }

    @Override
    public String validate() {
        if (agentId <= 0)                              return "Agent ID is required";
        if (propertyTitle == null || propertyTitle.isBlank()) return "Property title is required";
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) return "Valid price is required";
        return null;
    }
}
