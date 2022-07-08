package main.school.model;

public class SectorAssignment implements WithID{
    private long id;
    private long sectorId;
    private long instructorId;

    public SectorAssignment() {
    }

    public SectorAssignment(long sectorId, long instructorId) {
        this.sectorId = sectorId;
        this.instructorId = instructorId;
    }

    public long getSectorId() {
        return sectorId;
    }

    public void setSectorId(long sectorId) {
        this.sectorId = sectorId;
    }

    public long getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(long instructorId) {
        this.instructorId = instructorId;
    }

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }
}
