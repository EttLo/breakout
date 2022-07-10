package main.school.model;

import java.util.HashMap;
import java.util.Map;

public enum Sector implements WithID {

    GRAPHICS(1),
    OFFICE(2),
    DEVELOPMENT(3);
    private long id;
    private static Map map = new HashMap<>();
    Sector(int i){
        this.id = i;
    }
    public long getId() {
        return id;
    }

    static {
        for (Sector sector : Sector.values()) {
            map.put(sector.id, sector);
        }
    }


    public static Sector valueOf(long id) {
        return (Sector) map.get(id);
    }

    @Override
    public void setId(long id) {

    }
}
