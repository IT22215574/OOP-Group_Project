package com.primeestate.controller;

import com.primeestate.model.User;
import com.primeestate.model.Property;
import com.primeestate.model.Appointment;
import com.primeestate.repository.PropertyRepository;
import com.primeestate.repository.AppointmentRepository;
import com.primeestate.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import com.primeestate.dto.UserLoginRequest;
import com.primeestate.dto.UserProfileResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    private final UserService userService;
    
    @Autowired
    private PropertyRepository propertyRepository;
    
    @Autowired
    private AppointmentRepository appointmentRepository;

    public UserController(UserService userService) { 
        this.userService = userService; 
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try { 
            return new ResponseEntity<>(userService.registerUser(user), HttpStatus.CREATED); 
        }
        catch (IllegalArgumentException e) { 
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse); 
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequest request) {
        try { 
            return ResponseEntity.ok(userService.loginUser(request)); 
        }
        catch (Exception e) { 
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage() != null ? e.getMessage() : "Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse); 
        }
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<UserProfileResponse> getProfile(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.getUserProfile(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<UserProfileResponse> updateProfile(@PathVariable Integer id, @RequestBody User updated) {
        return ResponseEntity.ok(userService.updateUser(id, updated));
    }

    @DeleteMapping("/deactivate/{id}")
    public ResponseEntity<?> deactivateAccount(@PathVariable Integer id) {
        userService.deactivateUser(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Account deactivated successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history/{id}")
    public ResponseEntity<?> getUserHistory(@PathVariable Integer id) {
        // Fetch properties bought
        List<Property> propertiesBought = propertyRepository.findAll().stream()
                .filter(p -> id.equals(p.getBuyerId()))
                .collect(Collectors.toList());

        // Fetch agents contacted (appointments)
        List<Appointment> appointments = appointmentRepository.findAll().stream()
                .filter(a -> id.equals(a.getUserId()))
                .collect(Collectors.toList());

        Map<String, Object> history = new HashMap<>();
        history.put("properties", propertiesBought);
        history.put("appointments", appointments);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", history);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserProfileResponse>> searchUsers(@RequestParam String query, @RequestHeader(value = "Role", defaultValue = "USER") String role) {
        try { 
            return ResponseEntity.ok(userService.searchUsers(query, role.equalsIgnoreCase("ADMIN"))); 
        }
        catch (SecurityException e) { 
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); 
        }
    }
}
