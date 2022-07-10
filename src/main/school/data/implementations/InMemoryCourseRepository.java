package main.school.data.implementations;

import main.school.data.DataException;
import main.school.data.abstractions.CourseRepository;
import main.school.model.Course;
import main.school.model.Edition;
import main.school.model.Level;
import main.school.model.Sector;

import java.util.*;

public class InMemoryCourseRepository implements CourseRepository {

    protected static long courseId;

    protected static Map<Long, Course> repoCourses = new TreeMap<>();

    @Override
    public void addCourse(Course course) throws DataException {
        course.setId(++courseId);
        repoCourses.put(course.getId(), course);
    }


    @Override
    public Iterable<Course> getCoursesByTitleLike(String title) throws DataException {
        return repoCourses.values().stream()
                .filter((c) -> c.getTitle().toLowerCase().contains(title.toLowerCase()))
                .toList();
        /*
        List<Course> courses = new ArrayList<>();
        for (Course aCourse : repoCourses.values()) {
            if (aCourse.getTitle().toLowerCase().contains(title.toLowerCase())) {
                courses.add(aCourse);
            }
        }
        return courses;
         */
    }

    @Override
    public Iterable<Course> getAll(boolean orderByTitle) throws DataException { //refactor comments later
        Collection<Course> cs = repoCourses.values();
        if(!orderByTitle) {
            return cs;
        }
        List<Course> ls = new ArrayList<>(cs);
        /*
        Comparator<Course> cc = new Comparator<Course>() {
            @Override
            public int compare(Course o1, Course o2) {
                return o1.getTitle().compareTo(o2.getTitle());
            }
        };
         */
        //Comparator<Course> ccc = (c1, c2) -> c1.getTitle().compareTo(c2.getTitle()); //lambda v1
        Collections.sort(ls, Comparator.comparing(Course::getTitle)); //method reference
        //Collections.sort(ls, Comparator.comparing(c -> c.getTitle())); //lambda v2 uses Comparator.comparing()
        return ls;
    }

    @Override
    public Optional<Course> findByID(long courseId) {
        Course found = repoCourses.get(courseId);
        return found == null ? Optional.empty() : Optional.of(found);
    }

    @Override
    public void clear() {
        this.repoCourses.clear();
    }

}
