package main.school.factory;

import main.school.data.abstractions.CourseRepository;
import main.school.data.abstractions.EditionRepository;
import main.school.data.abstractions.InstructorRepository;
import main.school.jdbc.JDBCCourseRepository;
import main.school.jdbc.JDBCEditionRepository;
import main.school.jdbc.JDBCInstructorRepository;

public class JDBCRepositoryFactory extends RepositoryAbstractFactory {
    @Override
    public CourseRepository createCourseRepository() {
        return new JDBCCourseRepository();
    }

    @Override
    public EditionRepository createEditionRepository() {
        return new JDBCEditionRepository();
    }

    @Override
    public InstructorRepository createInstructorRepository() {
        return new JDBCInstructorRepository();
    }
}

