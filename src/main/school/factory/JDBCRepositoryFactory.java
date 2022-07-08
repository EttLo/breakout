package main.school.factory;

import main.school.data.abstractions.CourseRepository;
import main.school.data.abstractions.EditionRepository;
import main.school.data.abstractions.InstructorRepository;
import main.school.jdbc.implementation.JdbcCourseRepository;
import main.school.jdbc.implementation.JdbcEditionRepository;
import main.school.jdbc.implementation.JdbcInstructorRepository;

public class JDBCRepositoryFactory extends RepositoryAbstractFactory {

    @Override
    public CourseRepository createCourseRepository() {
        return new JdbcCourseRepository();
    }

    @Override
    public EditionRepository createEditionRepository() {
        return new JdbcEditionRepository();
    }

    @Override
    public InstructorRepository createInstructorRepository() {
        return new JdbcInstructorRepository();
    }

}
