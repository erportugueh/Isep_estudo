
import java.time.LocalDate;

public class Person extends Entity{

    private String name;
    private String birthDate;
    private String nationality;

    public Person(String id, LocalDate starDate, LocalDate endDate, String name, String birthDate, String nationality) {
        super(id, Type.PERSON, starDate, endDate);
        this.name = name;
        this.birthDate = birthDate;
        this.nationality = nationality;
    }

    public String getName() {
        return name;
    }

}
