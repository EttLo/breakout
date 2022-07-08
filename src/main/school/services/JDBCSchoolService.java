package main.school.services;

import main.school.data.DataException;
import main.school.data.abstractions.CourseRepository;
import main.school.data.abstractions.EditionRepository;
import main.school.data.abstractions.InstructorRepository;
import main.school.data.abstractions.JDBCRepository;
import main.school.factory.RepositoryAbstractFactory;
import main.school.model.Edition;
import main.school.model.EntityNotFoundException;
import main.school.model.Instructor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCSchoolService implements AbstractSchoolService {
    public static final String URL = "jdbc:oracle:thin:SCHOOL/school@localhost:1521/XEPDB1";
    private CourseRepository courseRepository;
    private EditionRepository editionRepository;
    private InstructorRepository instructorRepository;

    private Connection conn;

    public JDBCSchoolService() throws DataException {
        var factory = RepositoryAbstractFactory.getInstance();
        this.courseRepository = factory.createCourseRepository();
        this.editionRepository = factory.createEditionRepository();
        this.instructorRepository = factory.createInstructorRepository();
        try {
            this.conn = createConnection();
            ((JDBCRepository) this.courseRepository).setConn(this.conn);
            ((JDBCRepository) this.editionRepository).setConn(this.conn);
            ((JDBCRepository) this.instructorRepository).setConn(this.conn);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataException("Error to create connection!", e);
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

    @Override
    public void addOrReplaceInstructorInEdition(long editionId, long instructorID) throws DataException, EntityNotFoundException {

    }

    @Override
    public void commit() throws DataException {
        try {
            this.conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataException("Error to commit!", e);
        }
    }

    @Override
    public void rollBack() throws DataException {
        try {
            this.conn.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataException("Error to rollback!", e);
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