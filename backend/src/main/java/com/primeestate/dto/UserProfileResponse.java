package com.primeestate.dto;
import com.primeestate.model.enums.UserStatus;
public class UserProfileResponse {
    private Integer id;
    private String name;
    private String email;
    private String contactNumber;
    private String preferredLocation;
    private String preferredAgentSpecialization;
    private UserStatus accountStatus;

    public UserProfileResponse(Integer id, String name, String email, String contactNumber, String preferredLocation, String preferredAgentSpecialization, UserStatus accountStatus) {
        this.id = id; this.name = name; this.email = email; this.contactNumber = contactNumber; this.preferredLocation = preferredLocation; this.preferredAgentSpecialization = preferredAgentSpecialization; this.accountStatus = accountStatus;
    }

    public Integer getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getContactNumber() { return contactNumber; }
    public String getPreferredLocation() { return preferredLocation; }
    public String getPreferredAgentSpecialization() { return preferredAgentSpecialization; }
    public UserStatus getAccountStatus() { return accountStatus; }
}
