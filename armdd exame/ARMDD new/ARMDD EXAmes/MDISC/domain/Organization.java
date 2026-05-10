
import java.time.LocalDate;

public class Organization extends Entity{

    private String name;
    private String organizationType;
    private String country;

    public Organization(String id, LocalDate startDate, LocalDate endDate, String name, String organizationType, String country) {
        super(id, Type.ORGANIZATION, startDate, endDate);
        this.name = name;
        this.organizationType = organizationType;
        this.country = country;
    }

    public String getName() {
        return name;
    }

}
