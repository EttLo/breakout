package main.school.factory;

import main.school.data.DataException;
import main.school.services.AbstractSchoolService;
import main.school.services.InMemorySchoolService;
import main.school.services.JDBCSchoolService;

public class JDBCServiceFactory extends ServiceAbstractFactory{

    @Override
    public AbstractSchoolService createSchoolService() throws DataException {
        return new JDBCSchoolService();
    }
}
