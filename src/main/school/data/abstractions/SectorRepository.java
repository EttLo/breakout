package main.school.data.abstractions;

import main.school.data.DataException;
import main.school.model.Sector;

public interface SectorRepository {

    Iterable<Sector> getSectorsForInstructor(long instructorId) throws DataException;
}
