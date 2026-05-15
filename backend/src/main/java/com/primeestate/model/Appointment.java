package com.primeestate.model;

import java.sql.Date;
import java.sql.Time;

/**
 *  Appointment
 * OOP - Inheritance: extends BaseEntity (inherits id, createdAt, validate(), getDisplayName())
 * OOP - Encapsulation: Used encapsulation to keep their fields private and expose them through getters and setters.
 * Connects: userId (Module 2) → agentId (Module 1)
 */
public class Appointment extends BaseEntity {

    private int    userId;
    private int    agentId;
    private Date   appointmentDate;
    private Time   appointmentTime;
    private String propertyType;
    private String appointmentStatus;
    private String agentMessage;

    // Read-only join fields (populated on fetch, not persisted directly)
    private String userName;
    private String agentName;

    public Appointment() {}

    public Appointment(int id, int userId, int agentId, Date appointmentDate,
                       Time appointmentTime, String propertyType, String appointmentStatus) {
        setId(id);
        this.userId            = userId;
        this.agentId           = agentId;
        this.appointmentDate   = appointmentDate;
        this.appointmentTime   = appointmentTime;
        this.propertyType      = propertyType;
        this.appointmentStatus = appointmentStatus;
    }

    // Getters
    public int    getUserId()            { return userId; }
    public int    getAgentId()           { return agentId; }
    public Date   getAppointmentDate()   { return appointmentDate; }
    public Time   getAppointmentTime()   { return appointmentTime; }
    public String getPropertyType()      { return propertyType; }
    public String getAppointmentStatus() { return appointmentStatus; }
    public String getAgentMessage()      { return agentMessage; }
    public String getUserName()          { return userName; }
    public String getAgentName()         { return agentName; }

    // Setters
    public void setUserId(int userId)                         { this.userId            = userId; }
    public void setAgentId(int agentId)                       { this.agentId           = agentId; }
    public void setAppointmentDate(Date appointmentDate)      { this.appointmentDate   = appointmentDate; }
    public void setAppointmentTime(Time appointmentTime)      { this.appointmentTime   = appointmentTime; }
    public void setPropertyType(String propertyType)          { this.propertyType      = propertyType; }
    public void setAppointmentStatus(String appointmentStatus){ this.appointmentStatus = appointmentStatus; }
    public void setAgentMessage(String agentMessage)          { this.agentMessage      = agentMessage; }
    public void setUserName(String userName)                  { this.userName          = userName; }
    public void setAgentName(String agentName)                { this.agentName         = agentName; }

    @Override
    public String getDisplayName() { return "Appointment #" + getId(); }

    @Override
    public String validate() {
        if (userId  <= 0)               return "User ID is required";
        if (agentId <= 0)               return "Agent ID is required";
        if (appointmentDate == null)    return "Appointment date is required";
        if (appointmentTime == null)    return "Appointment time is required";
        return null;
    }
}
