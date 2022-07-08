package main.school.factory;

import main.school.data.abstractions.CourseRepository;
import main.school.data.abstractions.EditionRepository;
import main.school.data.abstractions.InstructorRepository;

public class JDBCRepositoryFactory extends RepositoryAbstractFactory {
    @Override
    public CourseRepository createCourseRepository() {
        return null;
    }

    @Override
    public EditionRepository createEditionRepository() {
        return null;
    }

    @Override
    public InstructorRepository createInstructorRepository() {
        return null;
    }
}
