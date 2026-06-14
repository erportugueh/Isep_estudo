package pt.ipp.isep.dei.examples.basic.ui;

import pt.ipp.isep.dei.examples.basic.domain.Analysis.SubNetwork;
import pt.ipp.isep.dei.examples.basic.domain.Edge;
import pt.ipp.isep.dei.examples.basic.domain.Entity;
import pt.ipp.isep.dei.examples.basic.domain.repository.EdgeRepository;
import pt.ipp.isep.dei.examples.basic.domain.repository.EntityRepository;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class SubNetworkUI implements Runnable {

    private final Scanner scanner = new Scanner(System.in);
    private final EntityRepository entityRepository;
    private final EdgeRepository edgeRepository;

    public SubNetworkUI(EntityRepository entityRepository, EdgeRepository edgeRepository) {
        this.entityRepository = entityRepository;
        this.edgeRepository = edgeRepository;
    }

    @Override
public void run() {
    System.out.println("\n-------------- VISUALISE SUBNETWORK --------------");

    // 1. Fetch data once
    Entity[] entities = entityRepository.getAllEntities();
    Edge[] edges = edgeRepository.getAllEdges();

    if (entities.length == 0) {
        System.out.println("Error: No entities found. Import data first.");
        return;
    }

    while (true) {

        // 2. Get snapshot date
        System.out.print("\nEnter the snapshot date (YYYY-MM-DD) (or type 'exit'): ");
        String dateInput = scanner.nextLine().trim();

        if (dateInput.equalsIgnoreCase("exit")) {
            break;
        }

        LocalDate date;
        try {
            date = LocalDate.parse(dateInput);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Use YYYY-MM-DD.");
            continue;
        }

        // 3. Get entity ID
        listEntities(entities);
        System.out.print("Enter the Entity ID to analyse: ");
        String entityId = scanner.nextLine().trim();

        System.out.println("\nProcessing subnetwork for " + entityId + "...");

        // 4. Run analysis
        SubNetwork engine = new SubNetwork(edges, entities);
        engine.generateSubnetworkTransitive(entityId, date);

        System.out.println("\nDone.");
        System.out.println("--------------------------------------------------");
    }

    System.out.println("Returning to main menu...");
}

    private void listEntities(Entity[] entities) {
        System.out.println("\nAvailable entities:");
        for (int i = 0; i < entities.length; i++) {
            System.out.println("  " + entities[i].getId() + "  " + entities[i].getName());
        }
    }
}