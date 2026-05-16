package com.primeestate.dao;

import com.primeestate.config.DBConnection;
import com.primeestate.model.BaseEntity;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * OOP - Inheritance: Abstract class shared by all DAO implementations.
 * OOP - Polymorphism: mapRow() is abstract — each subclass maps its own DB columns.
 * OOP - Encapsulation: getConnection() is protected (accessible to subclasses only).
 *
 * All 4 DAOs (AgentDAO, UserDAO, AppointmentDAO, AdvertisementDAO) extend this class.
 */
public abstract class BaseDAO<T extends BaseEntity> implements CrudOperations<T> {

    protected Connection getConnection() throws SQLException {
        return DBConnection.getInstance().getConnection();
    }

    // Polymorphic: each subclass maps ResultSet rows to its own model type
    protected abstract T mapRow(ResultSet rs) throws SQLException;
}
