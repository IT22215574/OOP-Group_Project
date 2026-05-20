import os
import shutil

base_dir = r"c:\Users\saki\Desktop\OOP-Group_Project\backend\src\main\java\com\primeestate"

# Directories to create
dirs = [
    "model", "model/enums", "repository", "service", "service/impl", "controller", "dto"
]

for d in dirs:
    os.makedirs(os.path.join(base_dir, d), exist_ok=True)

# Delete old dao, servlet, util
old_dirs = ["dao", "servlet", "util"]
for od in old_dirs:
    p = os.path.join(base_dir, od)
    if os.path.exists(p):
        shutil.rmtree(p)

files = {}

# ----------------- MAIN APP & CONFIG -----------------
files["PrimeEstateApplication.java"] = """package com.primeestate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PrimeEstateApplication {
    public static void main(String[] args) {
        SpringApplication.run(PrimeEstateApplication.class, args);
    }
}
"""

res_dir = r"c:\Users\saki\Desktop\OOP-Group_Project\backend\src\main\resources"
os.makedirs(res_dir, exist_ok=True)
with open(os.path.join(res_dir, "application.properties"), "w") as f:
    f.write("""spring.datasource.url=jdbc:mysql://localhost:3306/primeestate?createDatabaseIfNotExist=true&useSSL=false
spring.datasource.username=root
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
server.port=8080
""")

# ----------------- MODULE 2: USER MANAGEMENT (OOP STRICT) -----------------
files["model/enums/UserStatus.java"] = """package com.primeestate.model.enums;
public enum UserStatus { ACTIVE, INACTIVE }
"""

files["model/BaseUser.java"] = """package com.primeestate.model;
import com.primeestate.model.enums.UserStatus;
import jakarta.persistence.*;
@MappedSuperclass
public abstract class BaseUser {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String name;
    @Column(unique = true, nullable = false) private String email;
    private String password;
    private String contactNumber;
    @Enumerated(EnumType.STRING) private UserStatus accountStatus = UserStatus.ACTIVE;

    public Long getId() { return id; }
    protected void setId(Long id) { this.id = id; }
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
"""

files["model/User.java"] = """package com.primeestate.model;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
@Entity
@Table(name = "users")
public class User extends BaseUser {
    private String preferredLocation;
    private String preferredAgentSpecialization;
    public String getPreferredLocation() { return preferredLocation; }
    public void setPreferredLocation(String preferredLocation) { this.preferredLocation = preferredLocation; }
    public String getPreferredAgentSpecialization() { return preferredAgentSpecialization; }
    public void setPreferredAgentSpecialization(String preferredAgentSpecialization) { this.preferredAgentSpecialization = preferredAgentSpecialization; }
}
"""

files["repository/UserRepository.java"] = """package com.primeestate.repository;
import com.primeestate.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByNameContainingIgnoreCase(String name);
    List<User> findByEmailContainingIgnoreCase(String email);
}
"""

# Implement other basic entities (Agent, Property, Appointment) just to make it compile
files["model/Agent.java"] = """package com.primeestate.model;
import jakarta.persistence.*;
@Entity
@Table(name = "agents")
public class Agent {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String fullName;
    private String email;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
"""

files["model/Property.java"] = """package com.primeestate.model;
import jakarta.persistence.*;
@Entity
@Table(name = "properties")
public class Property {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String title;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
}
"""

files["model/Appointment.java"] = """package com.primeestate.model;
import jakarta.persistence.*;
@Entity
@Table(name = "appointments")
public class Appointment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String date;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}
"""

files["repository/AgentRepository.java"] = """package com.primeestate.repository;
import com.primeestate.model.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AgentRepository extends JpaRepository<Agent, Long> {}
"""

files["repository/PropertyRepository.java"] = """package com.primeestate.repository;
import com.primeestate.model.Property;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PropertyRepository extends JpaRepository<Property, Long> {}
"""

files["repository/AppointmentRepository.java"] = """package com.primeestate.repository;
import com.primeestate.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {}
"""

# Controllers just basic stubs so they exist for the other members
files["controller/AgentController.java"] = """package com.primeestate.controller;
import com.primeestate.model.Agent;
import com.primeestate.repository.AgentRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/agents")
public class AgentController {
    private final AgentRepository agentRepository;
    public AgentController(AgentRepository agentRepository) { this.agentRepository = agentRepository; }
    @GetMapping public List<Agent> getAll() { return agentRepository.findAll(); }
    @PostMapping public Agent create(@RequestBody Agent agent) { return agentRepository.save(agent); }
}
"""

files["controller/PropertyController.java"] = """package com.primeestate.controller;
import com.primeestate.model.Property;
import com.primeestate.repository.PropertyRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/properties")
public class PropertyController {
    private final PropertyRepository propertyRepository;
    public PropertyController(PropertyRepository propertyRepository) { this.propertyRepository = propertyRepository; }
    @GetMapping public List<Property> getAll() { return propertyRepository.findAll(); }
    @PostMapping public Property create(@RequestBody Property property) { return propertyRepository.save(property); }
}
"""

files["controller/AppointmentController.java"] = """package com.primeestate.controller;
import com.primeestate.model.Appointment;
import com.primeestate.repository.AppointmentRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    private final AppointmentRepository appointmentRepository;
    public AppointmentController(AppointmentRepository appointmentRepository) { this.appointmentRepository = appointmentRepository; }
    @GetMapping public List<Appointment> getAll() { return appointmentRepository.findAll(); }
    @PostMapping public Appointment create(@RequestBody Appointment appointment) { return appointmentRepository.save(appointment); }
}
"""

files["dto/UserLoginRequest.java"] = """package com.primeestate.dto;
public class UserLoginRequest {
    private String email;
    private String password;
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
"""

files["dto/UserProfileResponse.java"] = """package com.primeestate.dto;
import com.primeestate.model.enums.UserStatus;
public class UserProfileResponse {
    private Long id;
    private String name;
    private String email;
    private String contactNumber;
    private String preferredLocation;
    private String preferredAgentSpecialization;
    private UserStatus accountStatus;

    public UserProfileResponse(Long id, String name, String email, String contactNumber, String preferredLocation, String preferredAgentSpecialization, UserStatus accountStatus) {
        this.id = id; this.name = name; this.email = email; this.contactNumber = contactNumber; this.preferredLocation = preferredLocation; this.preferredAgentSpecialization = preferredAgentSpecialization; this.accountStatus = accountStatus;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getContactNumber() { return contactNumber; }
    public String getPreferredLocation() { return preferredLocation; }
    public String getPreferredAgentSpecialization() { return preferredAgentSpecialization; }
    public UserStatus getAccountStatus() { return accountStatus; }
}
"""

files["service/UserService.java"] = """package com.primeestate.service;
import com.primeestate.model.User;
import com.primeestate.dto.UserLoginRequest;
import com.primeestate.dto.UserProfileResponse;
import java.util.List;
public interface UserService {
    UserProfileResponse registerUser(User user);
    UserProfileResponse loginUser(UserLoginRequest request);
    UserProfileResponse getUserProfile(Long id);
    List<UserProfileResponse> searchUsers(String query, boolean isAdmin);
    UserProfileResponse updateUser(Long id, User updatedUser);
    void deactivateUser(Long id);
}
"""

files["service/impl/UserServiceImpl.java"] = """package com.primeestate.service.impl;
import com.primeestate.model.User;
import com.primeestate.model.enums.UserStatus;
import com.primeestate.repository.UserRepository;
import com.primeestate.service.UserService;
import com.primeestate.dto.UserLoginRequest;
import com.primeestate.dto.UserProfileResponse;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    public UserServiceImpl(UserRepository userRepository) { this.userRepository = userRepository; }

    @Override public UserProfileResponse registerUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) throw new IllegalArgumentException("Email already exists");
        user.setAccountStatus(UserStatus.ACTIVE);
        return mapToResponse(userRepository.save(user));
    }

    @Override public UserProfileResponse loginUser(UserLoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
        if (!user.getPassword().equals(request.getPassword())) throw new IllegalArgumentException("Invalid email or password");
        if (user.getAccountStatus() == UserStatus.INACTIVE) throw new IllegalStateException("Account deactivated");
        return mapToResponse(user);
    }

    @Override public UserProfileResponse getUserProfile(Long id) {
        return mapToResponse(userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found")));
    }

    @Override public List<UserProfileResponse> searchUsers(String query, boolean isAdmin) {
        if (!isAdmin) throw new SecurityException("Unauthorized");
        return userRepository.findByNameContainingIgnoreCase(query).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override public UserProfileResponse updateUser(Long id, User updated) {
        User existing = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        existing.setName(updated.getName());
        existing.setContactNumber(updated.getContactNumber());
        existing.setPreferredLocation(updated.getPreferredLocation());
        existing.setPreferredAgentSpecialization(updated.getPreferredAgentSpecialization());
        return mapToResponse(userRepository.save(existing));
    }

    @Override public void deactivateUser(Long id) {
        User existing = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        existing.setAccountStatus(UserStatus.INACTIVE);
        userRepository.save(existing);
    }

    private UserProfileResponse mapToResponse(User user) {
        return new UserProfileResponse(user.getId(), user.getName(), user.getEmail(), user.getContactNumber(), user.getPreferredLocation(), user.getPreferredAgentSpecialization(), user.getAccountStatus());
    }
}
"""

files["controller/UserController.java"] = """package com.primeestate.controller;
import com.primeestate.model.User;
import com.primeestate.service.UserService;
import com.primeestate.dto.UserLoginRequest;
import com.primeestate.dto.UserProfileResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) { this.userService = userService; }

    @PostMapping("/register")
    public ResponseEntity<UserProfileResponse> register(@RequestBody User user) {
        try { return new ResponseEntity<>(userService.registerUser(user), HttpStatus.CREATED); }
        catch (IllegalArgumentException e) { return ResponseEntity.badRequest().build(); }
    }

    @PostMapping("/login")
    public ResponseEntity<UserProfileResponse> login(@RequestBody UserLoginRequest request) {
        try { return ResponseEntity.ok(userService.loginUser(request)); }
        catch (Exception e) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); }
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<UserProfileResponse> getProfile(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserProfile(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<UserProfileResponse> updateProfile(@PathVariable Long id, @RequestBody User updated) {
        return ResponseEntity.ok(userService.updateUser(id, updated));
    }

    @DeleteMapping("/deactivate/{id}")
    public ResponseEntity<Void> deactivateAccount(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserProfileResponse>> searchUsers(@RequestParam String query, @RequestHeader(value = "Role", defaultValue = "USER") String role) {
        try { return ResponseEntity.ok(userService.searchUsers(query, role.equalsIgnoreCase("ADMIN"))); }
        catch (SecurityException e) { return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); }
    }
}
"""

# Write all files
for rel_path, content in files.items():
    p = os.path.join(base_dir, rel_path.replace("/", os.sep))
    os.makedirs(os.path.dirname(p), exist_ok=True)
    with open(p, "w") as f:
        f.write(content)
