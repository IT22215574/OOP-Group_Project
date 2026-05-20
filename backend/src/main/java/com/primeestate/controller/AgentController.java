package com.primeestate.controller;

import com.primeestate.model.Agent;
import com.primeestate.repository.AgentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/agents")
@CrossOrigin(origins = "*")
public class AgentController {
    private final AgentRepository agentRepository;

    public AgentController(AgentRepository agentRepository) {
        this.agentRepository = agentRepository;
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(agentRepository.findAll());
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Agent agent) {
        Agent saved = agentRepository.save(agent);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", saved);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> creds) {
        String email = creds.get("email");
        String password = creds.get("password");
        
        Optional<Agent> agentOpt = agentRepository.findAll().stream()
                .filter(a -> email.equals(a.getEmail()) && password.equals(a.getPassword()))
                .findFirst();

        Map<String, Object> response = new HashMap<>();
        if (agentOpt.isPresent()) {
            response.put("success", true);
            response.put("data", agentOpt.get());
        } else {
            response.put("success", false);
            response.put("message", "Invalid agent credentials");
        }
        return ResponseEntity.ok(response);
    }
}
