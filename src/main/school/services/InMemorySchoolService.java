package main.school.services;

import main.school.data.abstractions.EditionRepository;
import main.school.data.abstractions.CourseRepository;
import main.school.data.DataException;
import main.school.data.abstractions.InstructorRepository;
import main.school.factory.RepositoryAbstractFactory;
import main.school.factory.ServiceAbstractFactory;
import main.school.model.Edition;
import main.school.model.EntityNotFoundException;
import main.school.model.Instructor;

import java.util.Optional;

public class InMemorySchoolService implements AbstractSchoolService{
    private EditionRepository editionRepo;
    private CourseRepository courseRepo;
    private InstructorRepository instructorRepo;

    // stiamo facendo iniezione delle dipendenze per realizzare il pattern Inversione delle Dipendenze
    // guardare video sui design pattern SOLID dello "Zio Bob", in particolare "D" come Dependency Inversion
    // spiegare lunedì...
    public InMemorySchoolService(){
        var factory = RepositoryAbstractFactory.getInstance();
        this.courseRepo = factory.createCourseRepository();
        this.editionRepo = factory.createEditionRepository();
        this.instructorRepo = factory.createInstructorRepository();
    }
    public InMemorySchoolService(CourseRepository courseRepo, EditionRepository editionRepo, InstructorRepository instructorRepo) {
        this.courseRepo = courseRepo;
        this.editionRepo = editionRepo;
        this.instructorRepo = instructorRepo;
    }

    @Override
    public EditionRepository getEditionRepository() {
        return editionRepo;
    }

    @Override
    public InstructorRepository getInstructorRepository() {
        return instructorRepo;
    }

    @Override
    public CourseRepository getCourseRepository() {
        return courseRepo;
    }

    @Override
    public Optional<Instructor> findInstructorById(long id) throws DataException {
        return this.instructorRepo.findById(id);
    }

    @Override
    public void addOrReplaceInstructorInEdition(long editionId, long instructorID) throws DataException, EntityNotFoundException {
        Optional<Edition> oce = editionRepo.findEditionById(editionId);
        if(oce.isEmpty()) {
            throw new EntityNotFoundException(String.format("Edition with id %d not found.", editionId), editionId);
        }
        Optional<Instructor> oi = instructorRepo.findById(instructorID);
        if(oi.isEmpty()) {
            throw new EntityNotFoundException(String.format("Instructor with id %d not found.", instructorID), instructorID);
        }
        Instructor i = oi.get(); //estrae dall'Optional
        Edition ce = oce.get();
        ce.setInstructor(i);
    }

    @Override
    public void commit() throws DataException {

    }

    @Override
    public void rollBack() throws DataException {

    }

    @Override
    public Iterable<Instructor> getAllInstructors() throws DataException {
        return instructorRepo.getAll();
    }

    @Override
    public Edition addEdition(Edition edition) throws DataException{
        editionRepo.addEdition(edition);
        return edition;
    }
}
