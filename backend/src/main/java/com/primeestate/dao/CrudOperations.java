package com.primeestate.dao;

import com.primeestate.model.BaseEntity;

import java.sql.SQLException;
import java.util.List;

/**
 * OOP - Polymorphism: Interface that defines the CRUD contract for all DAO classes.
 * Every module DAO (AgentDAO, UserDAO, AppointmentDAO, AdvertisementDAO) implements
 * these methods differently, demonstrating runtime polymorphism.
 */
public interface CrudOperations<T extends BaseEntity> {

    int      create(T entity)    throws SQLException;
    T        readById(int id)    throws SQLException;
    List<T>  readAll()           throws SQLException;
    boolean  update(T entity)    throws SQLException;
    boolean  delete(int id)      throws SQLException;
}
