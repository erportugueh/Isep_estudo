package pt.ipp.isep.dei.MDISC.domain;

import java.time.LocalDate;

public abstract class Entity {

    private String id;
    private Type type;
    private LocalDate startDate;
    private LocalDate endDate;

    public Entity(String id, Type type, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    //lc
    public String getId() {
        return id;
    }

    public Type getType() {
        return type;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
}
