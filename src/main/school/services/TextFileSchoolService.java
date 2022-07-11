package main.school.services;

import main.school.data.DataException;
import main.school.data.abstractions.CourseRepository;
import main.school.data.abstractions.EditionRepository;
import main.school.data.abstractions.InstructorRepository;
import main.school.data.implementations.CourseUtils;
import main.school.data.implementations.EditionUtils;
import main.school.data.implementations.InstructorUtils;
import main.school.factory.RepositoryAbstractFactory;
import main.school.model.Course;
import main.school.model.Edition;
import main.school.model.EntityNotFoundException;
import main.school.model.Instructor;

import java.io.*;
import java.util.Optional;

public class TextFileSchoolService implements AbstractSchoolService {
    public static final String COURSE_PATH = "./data/myCourses.txt";
    public static final String EDITION_PATH = "./data/myEditions.txt";
    public static final String INSTRUCTOR_PATH = "./data/myInstructor.txt";
    private final EditionRepository editionRepo;
    private final CourseRepository courseRepo;
    private final InstructorRepository instructorRepo;

    public TextFileSchoolService(){
        var factory = RepositoryAbstractFactory.getInstance();
        this.courseRepo = factory.createCourseRepository();
        this.editionRepo = factory.createEditionRepository();
        this.instructorRepo = factory.createInstructorRepository();
    };

    public TextFileSchoolService(CourseRepository courseRepo, EditionRepository editionRepo,
                                 InstructorRepository instructorRepo) throws DataException {
        this.courseRepo = courseRepo;
        this.editionRepo = editionRepo;
        this.instructorRepo = instructorRepo;

        //qui l'ordine è importante
        readCourseFromFile();
        readInstructorFromFile();
        readEditionFromFile();

    }

    @Override
    public EditionRepository getEditionRepository() {
        return this.editionRepo;
    }

    @Override
    public InstructorRepository getInstructorRepository() {
        return this.instructorRepo;
    }

    @Override
    public CourseRepository getCourseRepository() {
        return this.courseRepo;
    }

    @Override
    public Optional<Instructor> findInstructorById(long id) throws DataException {
        return this.instructorRepo.findById(id);
    }

    @Override
    public void addOrReplaceInstructorInEdition(long editionId, long instructorID) throws DataException, EntityNotFoundException {

    }
    @Override
    public void commit() throws DataException {
        //qui vogliamo invocare il metodo write della classe TextFileCourseRepository
        this.writeCourseOnFile();
        this.writeInstructorOnFile();
        this.writeEditionOnFile();
    }
    @Override
    public void rollBack() throws DataException {
        //
    }

    @Override
    public Iterable<Instructor> getAllInstructors() throws DataException {
        return instructorRepo.getAll();
    }

    @Override
    public Edition addEdition(Edition edition) throws DataException {
        assignCourseAndInstructorToEdition(edition);
        editionRepo.addEdition(edition);
        commit();
        return edition;
    }

    private void writeCourseOnFile() throws DataException {
        //br & bw vengono automaticamente chiusi dal try with resources!!!!
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(COURSE_PATH))) {
            for (Course element : courseRepo.getAll(true)) {
                //scrivi
                bw.write(CourseUtils.toTextLine(element) + "\n");
            }
        } catch (IOException e) {
            throw new DataException ("Errore scrittura sul file di testo", e);
        }
    }
    private void readCourseFromFile() throws DataException {
        try (BufferedReader br = new BufferedReader(new FileReader(COURSE_PATH))) {
            this.courseRepo.clear();// svuoto HashMap
            String s;
            while ((s = br.readLine())!= null) {
                Course c = CourseUtils.fromTextLine(s);
                this.courseRepo.addCourse(c);
            }
        } catch (IOException e) {
            throw new DataException("Errore di lettura del file di testo", e);
        }
    }
    private void writeEditionOnFile() throws DataException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(EDITION_PATH))) {
            for (Edition e : editionRepo.getAll()) {
                //scrivi
                bw.write(EditionUtils.toTextLine(e) + "\n");
            }
        } catch (IOException e) {
            throw new DataException ("Errore scrittura sul file di testo", e);
        }
    }
    private void readEditionFromFile() throws DataException {
        try (BufferedReader br = new BufferedReader(new FileReader(EDITION_PATH))) {
            this.editionRepo.clear();// svuoto l'HashMap
            String s;
            while ((s = br.readLine())!= null) {
                Edition e = EditionUtils.fromTextLine(s); // qui gli arrivano course e instructor falsi
                //dobbiamo prendere dal repository di corso e instructor giusti
                assignCourseAndInstructorToEdition(e);
                this.editionRepo.addEdition(e);
            }
        } catch (IOException e) {
            throw new DataException("Errore di lettura del file di testo", e);
        }
    }

    private void assignCourseAndInstructorToEdition(Edition e) throws DataException {
        Optional<Course> oc = this.courseRepo.findByID(e.getCourse().getId());
        // l'optional ha un metodo isEmpty che in caso sia null non lancia eccezione quindi chi riceve un
        // Optional sa che può ottenere un valore null e quindi ti educa a controllare che la funzione abbia il
        // risultato atteso

        //se non esistono bisogna lanciare un'eccezione "non esiste.."
        if(oc.isEmpty()) {
            throw new DataException(String.format("edizione corso referenzia un corso con id %d inesistente",
                    e.getCourse().getId()), null);
        }
        Optional<Instructor> oi = this.instructorRepo.findById(e.getInstructor().getId());
        if (oi.isEmpty()) {
            throw new DataException(String.format("edizione corso referenzia un istruttore con id %d inesistente",
                    e.getInstructor().getId()), null);
        }
        //essendo sicuri di Course e Instructor li possiamo settare nella Edition
        e.setCourse(oc.get());
        e.setInstructor(oi.get());
    }

    private void writeInstructorOnFile() throws DataException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(INSTRUCTOR_PATH))) {
            for (Instructor i : instructorRepo.getAll()) {
                //scrivi
                bw.write(InstructorUtils.toTextLine(i) + "\n");
            }
        } catch (IOException e) {
            throw new DataException ("Errore scrittura sul file di testo", e);
        }
    }
    private void readInstructorFromFile() throws DataException {
        try (BufferedReader br = new BufferedReader(new FileReader(INSTRUCTOR_PATH))) {
            this.instructorRepo.clear();// svuoto HashMap
            String s;
            while ((s = br.readLine())!= null) { //s deve contenere tutti i parametri per istanziare un instructor
                Instructor i = InstructorUtils.fromTextLine(s);
                this.instructorRepo.addInstructor(i);
            }
        } catch (IOException e) {
            throw new DataException("Errore di lettura del file di testo", e);
        }
    }
}
