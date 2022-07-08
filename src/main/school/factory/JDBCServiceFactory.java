package main.school.factory;

import main.school.data.DataException;
import main.school.services.AbstractSchoolService;
import main.school.services.InMemorySchoolService;
import main.school.services.JDBCSchoolService;

public class JDBCServiceFactory extends ServiceAbstractFactory{

    @Override
    public AbstractSchoolService createSchoolService()  {
        try {
            return new JDBCSchoolService();
        } catch (DataException e) {
            System.out.println(e.getMessage());
            System.exit(1);
            return null;

        }
    }
}
