
import java.time.LocalDate;

public class Asset  extends Entity{

    private String assetType;
    private String country;
    private double estimatedValue;

    public Asset(String id, LocalDate startDate, LocalDate endDate, String assetType, String country, double estimatedValue) {
        super(id, Type.ASSET, startDate, endDate);
        this.assetType = assetType;
        this.country = country;
        this.estimatedValue = estimatedValue;
    }

    public String getAssetType() {
        return assetType;
    }
}
