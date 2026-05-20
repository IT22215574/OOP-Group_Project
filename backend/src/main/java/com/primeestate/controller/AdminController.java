package com.primeestate.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.primeestate.model.User;
import com.primeestate.repository.UserRepository;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final UserRepository userRepository;

    public AdminController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private void requireAdmin(String roleHeader) {
        if (roleHeader == null || !roleHeader.equalsIgnoreCase("ADMIN")) {
            throw new SecurityException("Unauthorized");
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> listUsers(@RequestHeader(value = "Role", defaultValue = "USER") String role) {
        try {
            requireAdmin(role);
            return ResponseEntity.ok(userRepository.findAll());
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id, @RequestHeader(value = "Role", defaultValue = "USER") String role) {
        try {
            requireAdmin(role);
            if (!userRepository.existsById(id)) return ResponseEntity.notFound().build();
            userRepository.deleteById(id);
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", true);
            resp.put("message", "User deleted");
            return ResponseEntity.ok(resp);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
