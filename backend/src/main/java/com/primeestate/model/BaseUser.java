package com.primeestate.model;
import com.primeestate.model.enums.UserStatus;
import jakarta.persistence.*;
@MappedSuperclass
public abstract class BaseUser {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Integer id;
    @Column(name = "full_name") private String name;
    @Column(unique = true, nullable = false) private String email;
    private String password;
    private String contactNumber;
    @Enumerated(EnumType.STRING) private UserStatus accountStatus = UserStatus.ACTIVE;

    public Integer getId() { return id; }
    protected void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public UserStatus getAccountStatus() { return accountStatus; }
    public void setAccountStatus(UserStatus accountStatus) { this.accountStatus = accountStatus; }
}
