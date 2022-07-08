package main.school.factory;

import main.school.data.DataException;
import main.school.services.AbstractSchoolService;

public abstract class ServiceAbstractFactory {
    public static String type;

    public static void setType(String type) {
        ServiceAbstractFactory.type = type;
    }

    public static  ServiceAbstractFactory getInstance(){

        if (type.equals("memory")){
            return new InMemoryServiceFactory();
        }
        if (type.equals("text")){
            return new TextFileServiceFactory();
        }
        if(type.equals("database")){
            return new JDBCServiceFactory();
        }
        throw new RuntimeException("wrong factory type specified");
    }

    public abstract AbstractSchoolService createSchoolService() throws DataException;
}
