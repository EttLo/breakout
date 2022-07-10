package main.school.factory;

import main.school.data.abstractions.CourseRepository;
import main.school.data.abstractions.EditionRepository;
import main.school.data.abstractions.InstructorRepository;
import main.school.data.abstractions.SectorRepository;
import main.school.data.implementations.InMemoryCourseRepository;
import main.school.data.implementations.InMemoryEditionRepository;
import main.school.data.implementations.InMemoryInstructorRepository;

public class InMemoryRepositoryFactory extends RepositoryAbstractFactory{
    @Override
    public CourseRepository createCourseRepository() {
        return new InMemoryCourseRepository();
    }

    @Override
    public EditionRepository createEditionRepository() {
        return new InMemoryEditionRepository();
    }

    @Override
    public InstructorRepository createInstructorRepository() {
        return new InMemoryInstructorRepository();
    }

    @Override
    public SectorRepository createSectorRepository() {
        // added in interface but not needed
        return null;
    }
}
