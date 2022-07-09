package main.school.model;

public class Course {
    private long id;
    private String title;
    private int duration;
    private Sector sector;
    private Level level;

    public Course(String title, int duration, Sector sector, Level level) {
        this (0L, title, duration, sector, level);
    }
    public Course (long id , String title, int duration, Sector sector, Level level) {
        this.id = id;
        this.title = title;
        this.duration = duration;
        this.sector = sector;
        this.level = level;
    }
    public Course () {

    }

    @Override
    public String toString() {
        return "Id: " + id + " Title: " + title + " Duration: " + duration + " Sector: " + sector.name() + " Level: "
                + level.name();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {

        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Sector getSector() {
        return sector;
    }

    public void setSector(Sector sector) {
        this.sector = sector;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

}