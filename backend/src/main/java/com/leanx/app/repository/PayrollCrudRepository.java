package main.java.com.leanx.app.repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import main.java.com.leanx.app.model.Payroll;

public class PayrollCrudRepository implements CrudRepository<Payroll>{

    @Override
    public void create(Payroll entity) throws IllegalArgumentException, SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public Payroll read(Integer id) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'read'");
    }

    @Override
    public void update(Payroll entity, Map<String, Object> updates) throws IllegalArgumentException, SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public void delete(Integer id) throws IllegalArgumentException, SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public List<Payroll> getAll() throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAll'");
    }
    
}
