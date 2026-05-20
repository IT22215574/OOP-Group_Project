package com.primeestate.repository;
import com.primeestate.model.Property;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PropertyRepository extends JpaRepository<Property, Integer> {}
