package com.primeestate.controller;

import com.primeestate.model.Property;
import com.primeestate.repository.PropertyRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/properties")
@CrossOrigin(origins = "*")
public class PropertyController {
    private final PropertyRepository propertyRepository;

    public PropertyController(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String city) {
            
        List<Property> all = propertyRepository.findAll();
        
        // Filtering
        if (type != null && !type.isEmpty()) {
            all = all.stream().filter(p -> type.equalsIgnoreCase(p.getType())).collect(Collectors.toList());
        }
        if (category != null && !category.isEmpty()) {
            all = all.stream().filter(p -> category.equalsIgnoreCase(p.getCategory())).collect(Collectors.toList());
        }
        if (city != null && !city.isEmpty()) {
            all = all.stream().filter(p -> city.equalsIgnoreCase(p.getCity())).collect(Collectors.toList());
        }

        Map<String, Object> data = new HashMap<>();
        data.put("properties", all);
        data.put("total", all.size());
        data.put("pages", 1);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", data);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProperty(@PathVariable Integer id) {
        Property property = propertyRepository.findById(id).orElse(null);
        Map<String, Object> response = new HashMap<>();
        if (property != null) {
            response.put("success", true);
            response.put("data", property);
        } else {
            response.put("success", false);
            response.put("message", "Not found");
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Property property) {
        Property saved = propertyRepository.save(property);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", saved);
        return ResponseEntity.ok(response);
    }
}
