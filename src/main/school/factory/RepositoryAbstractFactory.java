package main.school.factory;

import main.school.data.abstractions.CourseRepository;
import main.school.data.abstractions.EditionRepository;
import main.school.data.abstractions.InstructorRepository;

public abstract class RepositoryAbstractFactory {

    protected static String type;

    public static RepositoryAbstractFactory getInstance() {
        if(type.equals("memory")){
            return new InMemoryRepositoryFactory();
        }
        else if (type.equalsIgnoreCase("JDBC")) {
            return new JDBCRepositoryFactory();
        }
        return null;
    }

    public static void setType(String t){
        type = t;
    }

    public abstract CourseRepository createCourseRepository();
    public abstract EditionRepository createEditionRepository();
    public abstract InstructorRepository createInstructorRepository();

}
