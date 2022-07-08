package main.school.data.implementations;

import main.school.data.DataException;
import main.school.data.abstractions.InstructorRepository;
import main.school.model.Instructor;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Optional;

public class InMemoryInstructorRepository implements InstructorRepository {

    private static HashMap<Long, Instructor> repoInstructors = new HashMap<>();
    private static long instructorId;

    @Override
    public boolean instructorExists(long idInstructor) {
        Instructor instructor = repoInstructors.get(idInstructor);
        return instructor!=null;
    }

    @Override
    public Iterable<Instructor> getInstructorsBornAfterDateAndMultiSpecialized(LocalDate date) throws DataException {
        return repoInstructors.values().stream()
                                .filter(i -> i.isBornAfter(date) && i.isSpecializedInMultipleSectors())
                                .toList();
        /*
        List<Instructor> instructors = new ArrayList<>();
        for (Instructor instructor : repoInstructors.values()) {
            if (instructor.isBornAfter(date) && instructor.isSpecializedInMultipleSectors())
                instructors.add(instructor);
        }
        return instructors;

         */
    }
    @Override
    public void addInstructor(Instructor instructor) throws DataException {
        instructor.setId(++instructorId);
        repoInstructors.put(instructor.getId(), instructor);

    }

    @Override
    public Iterable<Instructor> getAll() throws DataException {
        return repoInstructors.values();
    }

    @Override
    public Optional<Instructor> findById(long instructorId) {
        Instructor i = repoInstructors.get(instructorId);
        return i!=null?Optional.of(i):Optional.empty();
    }
    @Override //nell'override possiamo cambiare il tipo di ritorno per via della covarianza
    public Iterable<Instructor> findOlderThanGivenAgeAndMoreThanOneSpecialization(int age) {
        return repoInstructors.values().stream()
                                        .filter(i -> i.isOlderThan(age)
                                                && i.isSpecializedInMultipleSectors())
                                        .toList();

        /*
        List<Instructor> listInstructor = new ArrayList<>();
        for(Instructor i : repoInstructors.values()) {
            if(i.isMajorThan(age)&&i.isSpecializedInMultipleSectors()) {
                listInstructor.add(i);
            }
        }
        return listInstructor;

         */
    }
    @Override
    public void clear() {
        this.repoInstructors.clear();
    }

    @Override
    public boolean updateInstructor(Instructor instructor) {
        //va implementato
        return false;
    }
}
