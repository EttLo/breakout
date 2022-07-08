package main.school.model;

public enum Sector {

    GRAPHICS(1),
    OFFICE(2),
    DEVELOPMENT(3);
    private int id;
    Sector(int i){
        this.id = i;
    }
    public int getId() {
        return id;
    }
}
