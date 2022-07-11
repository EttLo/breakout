package main.school.model;

import main.school.data.abstractions.WithId;

public enum Sector implements WithId {
    GRAPHICS(1),
    OFFICE(2),
    DEVELOPMENT(3);

    private long id;

    Sector(long id) {
        this.id = id;
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
