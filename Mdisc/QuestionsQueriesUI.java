package pt.ipp.isep.dei.examples.basic.ui;

import pt.ipp.isep.dei.examples.basic.domain.Analysis.Queries;
import pt.ipp.isep.dei.examples.basic.domain.AssetEntity;
import pt.ipp.isep.dei.examples.basic.domain.Edge;
import pt.ipp.isep.dei.examples.basic.domain.Entity;
import pt.ipp.isep.dei.examples.basic.domain.OrganizationEntity;
import pt.ipp.isep.dei.examples.basic.domain.PersonEntity;
import pt.ipp.isep.dei.examples.basic.domain.PositionEntity;
import pt.ipp.isep.dei.examples.basic.domain.repository.EdgeRepository;
import pt.ipp.isep.dei.examples.basic.domain.repository.EntityRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class QuestionsQueriesUI implements Runnable {

    private final EntityRepository entityRepo;
    private final EdgeRepository edgeRepo;
    private final Scanner scanner = new Scanner(System.in);

    public QuestionsQueriesUI(EntityRepository entityRepo, EdgeRepository edgeRepo) {
        this.entityRepo = entityRepo;
        this.edgeRepo   = edgeRepo;
    }

    @Override
    public void run() {
        List<Edge> edges = new ArrayList<>();
        edgeRepo.findAll().forEach(edges::add);

        Queries queryEngine = new Queries(edges, buildNameMap());
        List<String> questions = queryEngine.getPredefinedQuestions();

        int choice;
        do {
            System.out.println("\n-------------- US23: ETHICS QUERIES --------------");
            for (int i = 0; i < questions.size(); i++) {
                System.out.println((i + 1) + ". " + questions.get(i));
            }
            System.out.println("0. Exit");
            System.out.print("Select Question: ");

            try {
                choice = Integer.parseInt(scanner.nextLine());
                if (choice > 0 && choice <= questions.size()) {
                    System.out.println();
                    List<String> output = queryEngine.executeQuery(choice - 1);
                    output.forEach(System.out::println);
                }
            } catch (Exception e) {
                choice = 0;
            }
        } while (choice != 0);
    }

    private Map<String, String> buildNameMap() {
        Map<String, String> map = new LinkedHashMap<>();
        for (Entity entity : entityRepo.findAll()) {
            String name;
            if (entity instanceof PersonEntity p)            name = p.getName();
            else if (entity instanceof OrganizationEntity o) name = o.getName();
            else if (entity instanceof PositionEntity pos)   name = pos.getPositionTitle();
            else if (entity instanceof AssetEntity a)        name = a.getAssetType();
            else                                             name = entity.getId();
            map.put(entity.getId(), name);
        }
        return map;
    }
}