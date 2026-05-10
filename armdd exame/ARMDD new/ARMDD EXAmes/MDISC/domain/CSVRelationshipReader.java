
import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CSVRelationshipReader {

    public List<Relationship> readFromFile(String filePath) {

        List<Relationship> list = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {

                // Skip the header
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] parts = line.split(",");

                String id        = parts[0];
                String type      = parts[1];

                // Convert String to LocalDate
                LocalDate startDate = LocalDate.parse(parts[2].trim());

                // endDate may be empty → nulll
                LocalDate endDate = parts[3].trim().isEmpty()
                        ? null
                        : LocalDate.parse(parts[3].trim());

                String entity1Id = parts[4];
                String entity2Id = parts[5];
                double weight    = Double.parseDouble(parts[6].trim());

                Relationship r = new Relationship(id, type, startDate, endDate,
                        entity1Id, entity2Id, weight);
                list.add(r);
            }

            reader.close();

        } catch (Exception e) {
            System.out.println("Error reading the file: " + e.getMessage());
        }

        return list;
    }
}