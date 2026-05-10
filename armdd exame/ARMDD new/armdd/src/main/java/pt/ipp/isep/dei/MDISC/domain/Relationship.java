package pt.ipp.isep.dei.MDISC.domain;

import java.time.LocalDate;

public class Relationship {

    private String id;
    private String type;
    private LocalDate startDate;
    private LocalDate endDate;
    private String entity1Id;
    private String entity2Id;
    private double weight;

    public Relationship(String id, String type, LocalDate startDate, LocalDate endDate,
                        String entity1Id, String entity2Id, double weight) {
        this.id = id;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.entity1Id = entity1Id;
        this.entity2Id = entity2Id;
        this.weight = weight;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getEntity1Id() {
        return entity1Id;
    }

    public String getEntity2Id() {
        return entity2Id;
    }

    public double getWeight() {
        return weight;
    }
}