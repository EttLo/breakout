package main.school.services;

import main.school.data.DataException;
import main.school.data.abstractions.*;
import main.school.data.jdbc.JDBCCourseRepository;
import main.school.data.jdbc.JDBCEditionRepository;
import main.school.data.jdbc.JDBCInstructorRepository;
import main.school.data.jdbc.JDBCSectorRepository;
import main.school.factory.RepositoryAbstractFactory;
import main.school.model.Edition;
import main.school.model.EntityNotFoundException;
import main.school.model.Instructor;
import main.school.model.Sector;

import javax.swing.text.html.Option;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class JDBCSchoolService implements AbstractSchoolService {
    public static final String URL = "jdbc:oracle:thin:CODESCHOOL/codeschool@localhost:1521/XEPDB1";
    private CourseRepository courseRepository;
    private EditionRepository editionRepository;
    private InstructorRepository instructorRepository;
    private SectorRepository sectorRepository;

    private Connection conn;

    public JDBCSchoolService() throws DataException {
        var factory = RepositoryAbstractFactory.getInstance();
        this.courseRepository = factory.createCourseRepository();
        this.editionRepository = factory.createEditionRepository();
        this.instructorRepository = factory.createInstructorRepository();
        this.sectorRepository = factory.createSectorRepository();
        setConnectionOnRepositories();
        /*
        try {
            this.conn = createConnection();
            ((JDBCRepository) this.courseRepository).setConn(this.conn);
            ((JDBCRepository) this.editionRepository).setConn(this.conn);
            ((JDBCRepository) this.instructorRepository).setConn(this.conn);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataException("Error in creating the connection", e);
        }

         */
    }

    private void setConnectionOnRepositories() throws DataException {
        try {
            this.conn = createConnection();
            this.conn.setAutoCommit(false);
            ((JDBCCourseRepository) this.courseRepository).setConn(this.conn);
            ((JDBCEditionRepository) this.editionRepository).setConn(this.conn);
            ((JDBCInstructorRepository) this.instructorRepository).setConn(this.conn);
            ((JDBCSectorRepository)this.sectorRepository).setConn(this.conn);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataException("Error in creating connection!", e);
        }
    }



    /*public JDBCSchoolService(CourseRepository cr, EditionRepository er, InstructorRepository ir) throws DataException{
        this.courseRepository = cr;
        this.editionRepository = er;
        this.instructorRepository = ir;
        try {
            this.conn = createConnection();
            ((JDBCRepository) this.courseRepository).setConn(this.conn);
            ((JDBCRepository) this.editionRepository).setConn(this.conn);
            ((JDBCRepository) this.instructorRepository).setConn(this.conn);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataException("Error to create connection!", e);
        }
    }*/
    private Connection createConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
    @Override
    public EditionRepository getEditionRepository() {
        return editionRepository;
    }

    @Override
    public InstructorRepository getInstructorRepository() {
        return instructorRepository;
    }

    @Override
    public CourseRepository getCourseRepository() {
        return courseRepository;
    }

    public SectorRepository getSectorRepository() {return sectorRepository;}

    @Override
    public Optional<Instructor> findInstructorById(long id) throws DataException {
        Optional<Instructor> instructor = this.instructorRepository.findById(id);
        if(instructor.isEmpty()) {
            return Optional.empty();
        }
        Iterable<Sector> sectors = sectorRepository.getSectorsForInstructor(id);
        List<Sector> ls = StreamSupport.stream(sectors.spliterator(),false).collect(Collectors.toList());
        instructor.get().setSpecialization(ls);
        return instructor;
    }

    @Override
    public void addOrReplaceInstructorInEdition(long editionId, long instructorID) throws DataException, EntityNotFoundException {

    }

    @Override
    public void commit() throws DataException {
        try {
            this.conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataException("Error in commit", e);
        }
    }

    @Override
    public void rollBack() throws DataException {
        try {
            this.conn.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataException("Error in rollback", e);
        }
    }

    @Override
    public Iterable<Instructor> getAllInstructors() throws DataException {
        return null;
    }

    @Override
    public Edition addEdition(Edition edition) throws DataException {
        return null;
    }
}