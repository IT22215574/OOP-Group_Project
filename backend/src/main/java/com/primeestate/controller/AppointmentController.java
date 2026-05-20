package com.primeestate.controller;
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
