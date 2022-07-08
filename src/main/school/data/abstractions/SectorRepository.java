package main.school.data.abstractions;

import main.school.data.DataException;
import main.school.model.SectorAssignment;

public interface SectorRepository {
    void insertSectorForInstructor(SectorAssignment sa) throws DataException;
}
