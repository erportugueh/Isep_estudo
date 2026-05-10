
import java.time.LocalDate;

public class Position extends Entity{

    private String positionTitle;
    private String positionType;
    private String organizationId;

    public Position(String id, LocalDate startDate, LocalDate endDate, String positionTitle, String positionType, String organizationId) {
        super(id, Type.POSITION, startDate, endDate);
        this.positionTitle = positionTitle;
        this.positionType = positionType;
        this.organizationId = organizationId;
    }

    public String getPositionTitle() {
        return positionTitle;
    }
}
