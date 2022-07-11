package main.school.ui;

import java.time.LocalDate;
import java.util.*;
import main.school.data.DataException;
import main.school.data.abstractions.CourseRepository;
import main.school.data.abstractions.EditionRepository;
import main.school.data.abstractions.InstructorRepository;
import main.school.factory.RepositoryAbstractFactory;
import main.school.factory.ServiceAbstractFactory;
import main.school.model.*;
import main.school.services.AbstractSchoolService;
import main.school.services.InMemorySchoolService;
import main.school.services.TextFileSchoolService;


public class Console {
    private static final Scanner sc = new Scanner(System.in); //static final
    private AbstractSchoolService schoolService;

    public Console(AbstractSchoolService ss) {
        this.schoolService = ss;
    }
    public Console() {
        try {
            this.schoolService = ServiceAbstractFactory.getInstance().createSchoolService();
        } catch (DataException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    public void start (){
        try {
            run();
        } catch (DataException e) {
            System.out.println(e.getMessage());
            System.out.println("Closing program");
        }
    }
    public void run() throws DataException {
        while (true) {
            menu();
            String input = sc.nextLine();
            switch (input) {
                case "0":
                    printAllCourses();
                    break;
                case "1":
                    printAllInstructors();
                    break;
                case "2":
                    printAllEditions();
                    break;
                case "3":
                    retrieveEditionFromCourseId();
                    break;
                case "4":
                    retrieveCourseFromTitleLike();
                    break;
                case "5":
                    retrieveInstructorByAgeAndMoreThanOneSpecialization();
                    break;
                case "6":
                    getInstructorsBornAfterDateAndMultiSpecialized();
                    break;
                case "7":
                    retrieveInstructorsFromSectorAndLevel();
                    break;
                case "8":
                    assignInstructorToEdition();
                    break;
                case "9":
                    addNewCourse();
                    break;
                case "10":
                    addNewInstructor();
                    break;
                case "11":
                    addNewEdition();
                    break;
                case "12":
                    updateInstructor();
                    break;
                case "exit":
                    return;
                default:
                    System.out.println("Invalid command.");
                    break;
            }
        }
    }


    private void menu() {
        System.out.println("Type 0 to show all courses");
        System.out.println("Type 1 to show all instructors");
        System.out.println("Type 2 to show all editions");
        System.out.println("Type 3 to show all editions of a course given its id");
        System.out.println("Type 4 to show all courses that contain a given word in the title");
        System.out.println("Type 5 to show all instructors older than a given age and specialised in at least two sectors");
        System.out.println("Type 6 to show all instructors born after a certain date and specialised in at least two sectors");
        System.out.println("Type 7 to show all instructors that teach in a given sector at a given level");
        System.out.println("Type 8 to assign an instructor to an edition");
        System.out.println("Type 9 to add a new course");
        System.out.println("Type 10 to add a new instructor");
        System.out.println("Type 11 to add a new edition");
        System.out.println("Type exit to close the console");
    }

    // long idCourse, LocalDate startDate, LocalDate endDate, double cost, long idInstructor
    private void addNewEdition() throws DataException {
        long idCourse = readInt("Please insert course id: ");
        LocalDate startDate = readDate("Please insert starting date: ");
        LocalDate endDate = readDate( "Please insert ending date: ");
        double cost = readDouble("Please insert cost: ");
        long idInstructor = readInt("Please insert instructor id: ");
        Course c = new Course();
        c.setId(idCourse);
        Instructor i = new Instructor();
        i.setId(idInstructor);
        Edition e = new Edition(0L, c, startDate, endDate, cost, i);
        schoolService.addEdition(e);
    }

    private void printAllInstructors() throws DataException {
        for (Instructor i : schoolService.getInstructorRepository().getAll()
            //for (Instructor i : schoolService.getAllInstructors()){...} renderebbe il servizio + rumoroso
                //dovendo scrivere per ogni istruzione CRUD un metodo
        ) {
            System.out.println(i);
        }
    }

    private void addNewCourse() throws DataException {
        String title = readLine("Please insert course title:");
        int hours = readInt("Please insert course hours:");
        Sector sector = readSector();
        Level level = readLevel();
        Course c = new Course(title, hours, sector, level);
        this.schoolService.getCourseRepository().addCourse(c);
        this.schoolService.commit();
    }

    private double readDouble (@SuppressWarnings("all") String prompt)  {
        System.out.println(prompt);
        return Double.parseDouble(sc.nextLine());
    }
    private LocalDate readDate (String prompt) {
        System.out.println(prompt);
        return LocalDate.parse(sc.nextLine());
    }
    private String readLine (@SuppressWarnings("all") String prompt) {
        System.out.print(prompt);
        return sc.nextLine();
    }
    private int readInt (String prompt) {
        System.out.println(prompt);
        int i = sc.nextInt();
        sc.nextLine();
        return i;
    }
    private Sector readSector () {
        System.out.println("Please insert sector (GRAPHICS, OFFICE, DEVELOPMENT): ");

        String line = sc.nextLine();
        return Sector.valueOf(line);
    }
    private Level readLevel () {
        System.out.println("Please insert level (BASIC, ADVANCED, GURU): ");
        String line = sc.nextLine();
        return Level.valueOf(line);
    }
    private void assignInstructorToEdition() throws DataException {
        System.out.println("Please insert instructor id: ");
        long instructorId = sc.nextLong();
        System.out.println("Please insert edition id: ");
        long editionId = sc.nextLong();
        sc.nextLine(); //serve per eliminare il return rimasto in Input buffer
        try{
            schoolService.addOrReplaceInstructorInEdition(editionId, instructorId);
            schoolService.commit();//dopo aver fatto qualcosa vuole confermare di aver apportato la modifica
            System.out.println("Instructor assigned.");
        }catch (EntityNotFoundException e) {
            System.out.println(e.getMessage());
            System.out.println("Please try again.");
        }
    }

    //corsi ordinati per id o alfabeticamente (flag per cambiare)
    private void printAllCourses() throws DataException {
        for (Course course : schoolService.getCourseRepository().getAll(true)) {
            System.out.println(course.toString());
        }
    }

    private void printAllEditions() throws DataException {
        for (Edition e : schoolService.getEditionRepository().getAll()) {
            System.out.println(e.toString());
        }
    }

    private void retrieveEditionFromCourseId() throws DataException {
        long id = 0L; //costante long
        boolean isValidId = false;
        do {
            System.out.println("Insert course id:");
            if (sc.hasNextLong()) {
                id = sc.nextLong();
                sc.nextLine();
                isValidId = true;
            } else {
                sc.nextLine();
                System.out.println("Invalid input, try again!");
            }
        } while (!isValidId);
        for (Edition Edition : schoolService.getEditionRepository().getEditionsFromCourseId(id)) {
            System.out.println(Edition.toString());
        }

    }

    private void retrieveCourseFromTitleLike() throws DataException {
        System.out.println("Insert word contained in the course's title:");
        String keyword = sc.next();
        sc.nextLine();
        for (Course course : schoolService.getCourseRepository().getCoursesByTitleLike(keyword)) {
            System.out.println(course.toString());
        }

    }

    private void retrieveInstructorsFromSectorAndLevel() throws DataException {
        System.out.println("Insert 0 for Graphics");
        System.out.println("Insert 1 for Office");
        System.out.println("Insert 2 for Development");
        Sector sector = null;
        boolean f1 = false;
        boolean f2 = false;
        while(!f1) {
            switch (sc.nextLine()) {
                case "0" -> {
                    sector = Sector.GRAPHICS;
                    f1 = true;
                }
                case "1" -> {
                    sector = Sector.OFFICE;
                    f1 = true;
                }
                case "2" -> {
                    sector = Sector.DEVELOPMENT;
                    f1 = true;
                }
                default -> {
                    System.out.println("Invalid input, try again!");
                    System.out.println("Insert 0 for Graphics");
                    System.out.println("Insert 1 for Office");
                    System.out.println("Insert 2 for Development");
                }
            }
        }

        System.out.println("Insert 0 for Basic");
        System.out.println("Insert 1 for Advanced");
        System.out.println("Insert 2 for Guru");
        Level level = null;
        while(!f2) {
            switch (sc.nextLine()) {
                case "0" -> {
                    level = Level.BASIC;
                    f2 = true;
                }
                case "1" -> {
                    level = Level.ADVANCED;
                    f2 = true;
                }
                case "2" -> {
                    level = Level.GURU;
                    f2 = true;
                }
                default -> {
                    System.out.println("Invalid input, try again!");
                    System.out.println("Insert 0 for Basic");
                    System.out.println("Insert 1 for Advanced");
                    System.out.println("Insert 2 for Guru");
                }
            }
        }


        for (Instructor instructor : schoolService.getEditionRepository().getInstructorFromSectorAndLevel(sector, level)) {
            System.out.println(instructor);
        }
    }

    private void retrieveInstructorByAgeAndMoreThanOneSpecialization() throws DataException {
        System.out.println("How old should the instructor be, at the very least?");
        int age = sc.nextInt();
        sc.nextLine();
        for (Instructor instructor: schoolService.getInstructorRepository().findOlderThanGivenAgeAndMoreThanOneSpecialization(age)) {
            System.out.println(instructor);
        }

    }

    private void addNewInstructor() throws DataException {
        Instructor i = getInstructor();
        schoolService.getInstructorRepository().addInstructor(i);
        schoolService.commit();
    }
    private Instructor getInstructor(){
        System.out.println("Please insert instructor name: ");
        String name = sc.nextLine();
        System.out.println("Please insert instructor last name: ");
        String lastname = sc.nextLine();
        System.out.println("Please insert instructor date of birth ('yyyy-mm-dd' format): ");
        String dateString = sc.nextLine();
        LocalDate dob = LocalDate.parse(dateString);
        System.out.println("Please insert instructor email: ");
        String email = sc.nextLine();
        Set<Sector> sectorsSet = new HashSet<>();
        outer: do {
            System.out.println("Please insert specialization (g: graphics, d: development, o: office): , q: quit");
            String answer = sc.nextLine();
            Sector s;
            switch (answer) {
                case "g":
                    s = Sector.GRAPHICS;
                    break;
                case "d":
                    s = Sector.DEVELOPMENT;
                    break;
                case "o":
                    s = Sector.OFFICE;
                    break;
                case "q":
                    break outer;
                default:
                    System.out.println("Invalid input, try again!");
                    continue;
            }
            boolean wasAbsent = sectorsSet.add(s);
            if (!wasAbsent) {
                System.out.println("You cannot add the same specialization twice!");
            }
        } while (sectorsSet.size() < Sector.values().length);
        return new Instructor(name, lastname, dob, email, new ArrayList<>(sectorsSet));
    }

    public static void chooseFactories(){
        System.out.println("Press m or M to record data in memory, F or f to record on a text file, D or d to record on database");
        String response = sc.nextLine();
        while(!(response.equalsIgnoreCase("M") || response.equalsIgnoreCase("F") || response.equalsIgnoreCase("D"))) {
            System.out.println("Invalid input, try again!");
            response = sc.nextLine();
        }
        if (response.equalsIgnoreCase("M")) {
            RepositoryAbstractFactory.setType("memory");
            ServiceAbstractFactory.setType("memory");
        } else if(response.equalsIgnoreCase("F")){
            RepositoryAbstractFactory.setType("memory");
            ServiceAbstractFactory.setType("text");
        }else{
            RepositoryAbstractFactory.setType("database");
            ServiceAbstractFactory.setType("database");
        }
    }

    public void chooseService(CourseRepository cr, EditionRepository er, InstructorRepository ir) {
        try {
            System.out.println("Press m or M to record data in memory, F or f to record on a text file.");
            String response = sc.nextLine();
            while(!(response.equalsIgnoreCase("M") || response.equalsIgnoreCase("F"))) {
                System.out.println("Invalid input, try again!");
                response = sc.nextLine();
            }
            if (response.equalsIgnoreCase("M")) {
                //
                schoolService = new InMemorySchoolService(cr, er, ir);
            } else {
                schoolService = new TextFileSchoolService(cr, er, ir);
            }


        } catch (DataException e) {
            System.out.println("Unable to initialize data.");
            System.out.println(e.getMessage());
        }
    }
    private void getInstructorsBornAfterDateAndMultiSpecialized() throws DataException {
        System.out.println("Please insert instructor date of birth ('yyyy-mm-dd' format): ");
        String dateString = sc.nextLine();
        LocalDate date = LocalDate.parse(dateString);
        for (Instructor instructor: schoolService.getInstructorRepository().getInstructorsBornAfterDateAndMultiSpecialized(date)) {
            System.out.println(instructor);
        }
    }
    private void updateInstructor() throws DataException {
        Instructor i = getInstructor();
        schoolService.getInstructorRepository().updateInstructor(i);
    }

}
