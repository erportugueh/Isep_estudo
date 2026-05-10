
import java.util.*;

public class RelationTypeMatrixBuilder {

    private final List<Relationship> relationships;

    public RelationTypeMatrixBuilder(List<Relationship> relationships) {
        this.relationships = relationships;
    }

    public String[][] buildMatrix() {

        String[][] M = new String[4][4];

        // Start with empty lists
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                M[i][j] = "";

        for (Relationship r : relationships) {

            String rel = r.getType();

            String from = entityTypeOf(r.getEntity1Id());
            String to   = entityTypeOf(r.getEntity2Id());

            int i = indexOf(from);
            int j = indexOf(to);

            if (i == -1 || j == -1) {
                System.out.println("WARNING: Invalid entity type in relation: "
                        + r.getEntity1Id() + " -> " + r.getEntity2Id() + " (" + rel + ")");
                continue;
            }

            String symbol = symbolFor(rel);

            // Add multiple relationships
            if (M[i][j].isEmpty())
                M[i][j] = symbol;
            else
                M[i][j] += "," + symbol;
        }

        // Replace blanks with "0"
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                if (M[i][j].isEmpty())
                    M[i][j] = "0";

        return M;
    }

    private String entityTypeOf(String id) {
        if (id == null || id.isBlank()) return "?";
        return switch (Character.toUpperCase(id.charAt(0))) {
            case 'P' -> "P";
            case 'O' -> "O";
            case 'C' -> "C";
            case 'A' -> "A";
            default -> "?";
        };
    }

    private int indexOf(String t) {
        return switch (t) {
            case "P" -> 0;
            case "O" -> 1;
            case "C" -> 2;
            case "A" -> 3;
            default -> -1;
        };
    }

    private String symbolFor(String rel) {
        return switch (rel) {

            // Person → Person
            case "relativeOf" -> "Rof";
            case "friendOf" -> "Fof";
            case "associatedWith" -> "AW";
            case "appointedBy" -> "AB";

            // Person → Position
            case "holdsPosition" -> "HP";

            // Position → Organization
            case "inOrganization" -> "IO";

            // Person → Organization
            case "influences" -> "I";
            case "memberOf" -> "Mof";
            case "supports" -> "SP";
            case "opposes" -> "O";

            // Organization → Organization
            case "controls" -> "C";
            case "partnerOf" -> "Pof";
            case "supervises" -> "SV";

            // Person → Asset
            case "ownerOf" -> "Oof";

            default -> "?";
        };
    }
}



