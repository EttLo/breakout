package main.school.services;

import main.school.data.DataException;
import main.school.data.abstractions.*;
import main.school.model.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JdbcSchoolService implements AbstractSchoolService {
    public static final String URL = "jdbc:oracle:thin:SCHOOL/school@localhost:1521/XEPDB1";
    private CourseRepository courseRepository;
    private EditionRepository editionRepository;
    private InstructorRepository instructorRepository;

    private SectorRepository sectorRepository;

    private Connection conn;

    public JdbcSchoolService (CourseRepository cr, EditionRepository er,
                              InstructorRepository ir, SectorRepository sr)
            throws DataException{
        this.courseRepository = cr;
        this.editionRepository = er;
        this.instructorRepository = ir;
        this.sectorRepository = sr;
        try {
            this.conn = createConnection();
            this.conn.setAutoCommit(false);
            ((JdbcRepository) this.courseRepository).setConn(this.conn);
            ((JdbcRepository) this.editionRepository).setConn(this.conn);
            ((JdbcRepository) this.instructorRepository).setConn(this.conn);
            ((JdbcRepository) this.sectorRepository).setConn(this.conn);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataException("Error to create connection!", e);
        }
    }
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

    @Override
    public Instructor addInstructor(Instructor instructor) throws DataException {
        this.instructorRepository.addInstructor(instructor);
        for (Sector s : instructor.getSpecialization()){
            SectorAssignment sa = new SectorAssignment(s.getId(), instructor.getId());
            this.sectorRepository.insertSectorForInstructor(sa);
        }
        return instructor;
    }

}
