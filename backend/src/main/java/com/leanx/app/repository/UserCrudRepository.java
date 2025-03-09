package com.leanx.app.repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.leanx.app.model.User;

public class UserCrudRepository implements CrudRepository<User>  {

    @Override
    public void create(User user) throws IllegalArgumentException, SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public User read(Integer id) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'read'");
    }

    @Override
    public void update(User user, Map<String, Object> updates) throws IllegalArgumentException, SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public void delete(Integer id) throws IllegalArgumentException, SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public List<User> getAll() throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

    public User getUserByUsername(String username) throws IllegalArgumentException, SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUserByUsername'");
    }

    public List<User> getAllUsersLinkedToEmployeeId(Integer employeeId) throws IllegalArgumentException, SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

}
