package pt.ipp.isep.dei.examples.basic.ui;

import pt.ipp.isep.dei.examples.basic.domain.Analysis.NepotismDetector;
import pt.ipp.isep.dei.examples.basic.domain.repository.EdgeRepository;
import pt.ipp.isep.dei.examples.basic.domain.repository.EntityRepository;
import java.util.List;
import java.util.Scanner;

public class NepotismDetectorUI implements Runnable {

    private final EdgeRepository edgeRepo;
    private final NepotismDetector detector = new NepotismDetector();
    private final Scanner scanner = new Scanner(System.in);

    public NepotismDetectorUI(EntityRepository entityRepo, EdgeRepository edgeRepo) {
        this.edgeRepo = edgeRepo;
    }

    @Override
    public void run() {
        int option;
        do {
            System.out.println("\n============== NEPOTISM ANALYSIS ==============");
            System.out.println("1 - List Direct Nepotism (Immediate Contact)");
            System.out.println("2 - List Indirect Nepotism (Via Intermediary)");
            System.out.println("0 - Return to Main Menu");
            System.out.print("Select an option: ");

            option = readOption();
            var allEdges = edgeRepo.findAll(); 

            switch (option) {
                case 1 -> displayResults(detector.detectDirect(allEdges), "Direct Nepotism");
                case 2 -> displayResults(detector.detectIndirect(allEdges), "Indirect Nepotism");
                case 0 -> System.out.println("Returning to Main Menu...");
                default -> System.out.println("Invalid selection.");
            }
        } while (option != 0);
    }

    private void displayResults(List<String> cases, String title) {
        System.out.println("\n--- " + title + " ---");
        if (cases.isEmpty()) {
            System.out.println("No matching records found.");
        } else {
            cases.forEach(System.out::println);
            System.out.println("Total cases identified: " + cases.size());
        }
    }

    private int readOption() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (Exception e) {
            return -1;
        }
    }
}