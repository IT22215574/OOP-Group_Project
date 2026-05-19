package com.primeestate.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * MODULE 5 – Property
 * OOP - Encapsulation: Used encapsulation to keep their fields private and expose them through getters and setters.
 */
public class Property {

    private int          id;
    private String       title;
    private String       description;
    private BigDecimal   price;
    private String       type;        // sale | rent
    private String       category;   // house | apartment | villa | land | commercial
    private String       status;     // available | sold | rented
    private int          bedrooms;
    private int          bathrooms;
    private double       areaSqft;
    private String       address;
    private String       city;
    private String       state;
    private String       zipCode;
    private int          agentId;
    private String       imageUrl;
    private Timestamp    createdAt;
    private List<String> additionalImages; // from property_images table

    public Property() {}

    // Getters
    public int        getId()          { return id; }
    public String     getTitle()       { return title; }
    public String     getDescription() { return description; }
    public BigDecimal getPrice()       { return price; }
    public String     getType()        { return type; }
    public String     getCategory()    { return category; }
    public String     getStatus()      { return status; }
    public int        getBedrooms()    { return bedrooms; }
    public int        getBathrooms()   { return bathrooms; }
    public double     getAreaSqft()    { return areaSqft; }
    public String     getAddress()     { return address; }
    public String     getCity()        { return city; }
    public String     getState()       { return state; }
    public String     getZipCode()     { return zipCode; }
    public int        getAgentId()     { return agentId; }
    public String       getImageUrl()         { return imageUrl; }
    public Timestamp    getCreatedAt()        { return createdAt; }
    public List<String> getAdditionalImages() { return additionalImages; }

    // Setters
    public void setId(int id)                     { this.id          = id; }
    public void setTitle(String title)            { this.title       = title; }
    public void setDescription(String desc)       { this.description = desc; }
    public void setPrice(BigDecimal price)        { this.price       = price; }
    public void setType(String type)              { this.type        = type; }
    public void setCategory(String category)      { this.category    = category; }
    public void setStatus(String status)          { this.status      = status; }
    public void setBedrooms(int bedrooms)         { this.bedrooms    = bedrooms; }
    public void setBathrooms(int bathrooms)       { this.bathrooms   = bathrooms; }
    public void setAreaSqft(double areaSqft)      { this.areaSqft    = areaSqft; }
    public void setAddress(String address)        { this.address     = address; }
    public void setCity(String city)              { this.city        = city; }
    public void setState(String state)            { this.state       = state; }
    public void setZipCode(String zipCode)        { this.zipCode     = zipCode; }
    public void setAgentId(int agentId)           { this.agentId     = agentId; }
    public void setImageUrl(String imageUrl)                   { this.imageUrl         = imageUrl; }
    public void setCreatedAt(Timestamp createdAt)             { this.createdAt         = createdAt; }
    public void setAdditionalImages(List<String> imgs)        { this.additionalImages  = imgs; }
}
