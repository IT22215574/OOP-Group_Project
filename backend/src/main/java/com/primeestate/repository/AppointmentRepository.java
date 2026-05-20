package com.primeestate.repository;
import com.primeestate.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {}
