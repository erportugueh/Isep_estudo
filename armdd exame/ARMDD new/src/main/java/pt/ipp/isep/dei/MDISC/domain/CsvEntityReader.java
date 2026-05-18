package pt.ipp.isep.dei.MDISC.domain;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CsvEntityReader {

    public List<Entity> readEntities(String filePath) {
        List<Entity> entities = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String line = br.readLine(); // ignore header

            while ((line = br.readLine()) != null) {

                if (line.trim().isEmpty()) continue;

                String[] fields = line.split(",");

                String id = fields[0];
                String type = fields[1].trim().toLowerCase();

                String start = fields[2].trim();
                String end = fields[3].trim();

                LocalDate startDate = LocalDate.parse(start);
                LocalDate endDate = null;

                if (!end.isEmpty()) {
                    endDate = LocalDate.parse(end);
                }

                Entity entity = null;

                if (type.equals("person")) {
                    entity = new Person(
                            id,
                            startDate,
                            endDate,
                            fields[4],
                            fields[5],
                            fields[6]
                    );
                }
                else if (type.equals("organization")) {
                    entity = new Organization(
                            id,
                            startDate,
                            endDate,
                            fields[4],
                            fields[5],
                            fields[6]
                    );
                }
                else if (type.equals("position")) {
                    entity = new Position(
                            id,
                            startDate,
                            endDate,
                            fields[4],
                            fields[5],
                            fields[6]
                    );
                }
                else if (type.equals("asset")) {
                    entity = new Asset(
                            id,
                            startDate,
                            endDate,
                            fields[4],
                            fields[5],
                            Double.parseDouble(fields[6])
                    );
                }

                entities.add(entity);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return entities;
    }

}


