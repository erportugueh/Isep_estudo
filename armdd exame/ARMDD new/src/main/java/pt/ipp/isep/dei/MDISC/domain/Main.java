package pt.ipp.isep.dei.MDISC.domain;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        try {

            // US19 — Read entities and relationships

            CsvEntityReader entityReader = new CsvEntityReader();
            CSVRelationshipReader relReader = new CSVRelationshipReader();

            System.out.println("Add the Vertices (entities): ");
            List<Entity> entities = entityReader.readEntities(sc.nextLine());

            System.out.println("Add the Edges (Relationship): ");
            List<Relationship> relationships = relReader.readFromFile(sc.nextLine());

            System.out.println("US19: Loaded " + entities.size() + " entities");
            System.out.println("US19: Loaded " + relationships.size() + " relationships\n");



            // US20 — Build graph and display snapshot

            String snapshot = "2024-01-01";

            GraphBuilder graphBuilder = new GraphBuilder();
            graphBuilder.addEntities(entities);
            graphBuilder.addRelationshipsForSnapshot(relationships, snapshot);

            System.out.println("US20: Graph built for snapshot " + snapshot);
            graphBuilder.display(entities, relationships);




            // US21 — Create matrices

            AdjacencyMatrixBuilder matrixBuilder =
                    new AdjacencyMatrixBuilder(entities, relationships);

            Map<String, int[][]> typedMatrices =
                    matrixBuilder.buildTypedMatrices(snapshot);

            int[][] globalMatrix =
                    matrixBuilder.buildGlobalMatrix(snapshot);

            System.out.println("\nUS21: Typed matrices created:");
            typedMatrices.forEach((type, matrix) ->
                    System.out.println(" - " + type + " (" + matrix.length + "x" + matrix[0].length + ")"));

            System.out.println("US21: Global matrix size = "
                    + globalMatrix.length + "x" + globalMatrix[0].length);

            System.out.println("\nGlobal adjacency matrix:");
            MatrixPrinter.print(globalMatrix);

            System.out.println("\nGlobal adjacency matrix (Conceptual):");
            RelationLegendPrinter.printLegend();
            RelationTypeMatrixBuilder rmb = new RelationTypeMatrixBuilder(relationships);
            String[][] conceptual = rmb.buildMatrix();

            MatrixPrinterRelationTypes.print(conceptual);


            for (String type : typedMatrices.keySet()) {
                System.out.println("\nMatrix for relation: " + type);
                MatrixPrinter.print(typedMatrices.get(type));
            }



            // US22 — Direct nepotism

            NepotismDetector nepotism = new NepotismDetector();
            List<String> nepotismCases = nepotism.detectNepotism(relationships);

            System.out.println("\nUS22: Direct nepotism cases:");
            if (nepotismCases.isEmpty()) {
                System.out.println(" - No direct nepotism detected.");
            } else {
                nepotismCases.forEach(c -> System.out.println(" - " + c));
            }



            // US23 — Loop of questions

            QueryEngine engine = new QueryEngine(matrixBuilder, snapshot);

            new Thread(() -> {
                while (true) {
                    System.out.println("\nUS23: Available questions:");
                    List<String> questions = engine.getQuestions();
                    for (int i = 0; i < questions.size(); i++) {
                        System.out.println((i + 1) + ". " + questions.get(i));
                    }
                    System.out.println("0. Exit");

                    System.out.print("\nSelect a question (0-" + questions.size() + "): ");
                    int choice = sc.nextInt();
                    sc.nextLine();

                    if (choice == 0) {
                        System.out.println("Exiting program. Goodbye!");
                        break;
                    }

                    if (choice < 1 || choice > questions.size()) {
                        System.out.println("Invalid choice.");
                        continue;
                    }

                    String q = questions.get(choice - 1);
                    System.out.println("\nExecuting query: " + q);

                    List<String> results = engine.execute(q);

                    System.out.println("US23 Results:");
                    if (results.isEmpty()) {
                        System.out.println(" - No indirect chains found.");
                    } else {
                        results.forEach(r -> System.out.println(" - " + r));
                    }
                }
            }).start();


        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}


