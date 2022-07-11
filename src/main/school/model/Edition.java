package main.school.model;

import main.school.data.abstractions.WithId;

import java.time.LocalDate;

public class Edition implements WithId {
    private long id;
    private Course course;
    private LocalDate startDate;
    private LocalDate endDate;
    private double cost;
    private Instructor instructor;

    public Edition(long id, Course course, LocalDate startDate, LocalDate endDate, double cost,
                   Instructor instructor) {
        this.id = id;
        this.course = course;
        this.startDate = startDate;
        this.endDate = endDate;
        this.cost = cost;
        this.instructor = instructor;
    }

    public String getData () {
        return  id + ", " + course.getTitle() + ", " + startDate.toString() + ", " + endDate.toString() + ", " +
                cost+ ", " + instructor.getName();
    }
    @Override
    public String toString() {
        return "Edition id: " + id + " Course title: " + course.getTitle() + " Instructor: " + instructor.getId() + " "
                + instructor.getName() + " " + instructor.getLastname() + " From: " + startDate.toString() + " To: "
                + endDate.toString() + " Cost: " + cost;
    }

    public Level getLevel() {
        return course.getLevel();
    }

    public Sector getSector() {
        return course.getSector();
    }

    public Instructor getInstructor() {
        return instructor;
    }

    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
