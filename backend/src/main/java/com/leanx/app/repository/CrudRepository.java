package com.leanx.app.repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

// Generisches CRUD Repository Interface
public interface CrudRepository<T> {

    /**
     * Creates a new entity in the database.
     * @param entity The entity object containing the details to be persisted.
     * @return The number of rows affected by the delete operation.
     * @throws IllegalArgumentException if the entity object is invalid or null.
     * @throws SQLException if there is an issue accessing the database.
     */
    int create(T entity) throws IllegalArgumentException, SQLException;

    /**
     * Retrieves an entity from the database based on the given ID.
     * @param id The unique identifier of the entity to retrieve.
     * @return The entity object if found, or null if the entity does not exist.
     * @throws SQLException if there is an issue accessing the database.
     */
    T read(Integer id) throws SQLException;

    /**
     * Updates an existing entity in the database with the given data.
     * @param id The unique identifier of the entity to update.
     * @param updates A map of the field names to update and their new values.
     * @return The number of rows affected by the delete operation.
     * @throws IllegalArgumentException if the entity is invalid or incomplete.
     * @throws SQLException if there is an issue accessing the database.
     */
    int update(Integer id,  Map<String, Object> updates) throws IllegalArgumentException, SQLException;

    /**
     * Deletes an entity from the database based on the given ID.
     * @param id The ID of the entity to delete.
     * @return The number of rows affected by the delete operation.
     * @throws IllegalArgumentException if the ID is invalid or the entity does not exist.
     * @throws SQLException if there is an issue accessing the database.
     */
    int delete(Integer id) throws IllegalArgumentException, SQLException;

    /**
     * Retrieves all entities of the specified type from the database.
     * @return A list of all entity objects, or an empty list if no entities are found.
     * @throws SQLException if there is an issue accessing the database.
     */
    List<T> findAll() throws SQLException;

}
