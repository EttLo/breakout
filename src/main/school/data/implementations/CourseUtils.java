package main.school.data.implementations;

import main.school.model.Course;
import main.school.model.Level;
import main.school.model.Sector;

public class CourseUtils {
    public static String toTextLine(Course c) {
        return String.format("%d,%s,%d,%s,%s",c.getId(), c.getTitle(),c.getDuration(),c.getSector().
                                              name(), c.getLevel().name());
    }
    public static Course fromTextLine (String line) {
        var sArray = line.split(",");
        return new Course(Long.parseLong(sArray[0]), sArray[1], Integer.parseInt(sArray[2]), Sector.valueOf(sArray[3]),
                Level.valueOf(sArray[4]));
    }
}
