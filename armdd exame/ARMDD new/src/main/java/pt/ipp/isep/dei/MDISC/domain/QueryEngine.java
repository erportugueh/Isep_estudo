package pt.ipp.isep.dei.MDISC.domain;

import java.util.*;

public class QueryEngine {

    private final Map<String, int[][]> M;   // matrices by type
    private final List<Person> persons;
    private final List<Organization> orgs;
    private final List<Position> positions;
    private final List<Asset> assets;

    private int[][] witness; // testimonial matrices

    public QueryEngine(AdjacencyMatrixBuilder builder, String snapshot) {

        this.M = builder.buildTypedMatrices(snapshot);

        this.persons = builder.getPersons();
        this.orgs = builder.getOrganizations();
        this.positions = builder.getPositions();
        this.assets = builder.getAssets();
    }


    // AC1 — Predefined questions

    public List<String> getQuestions() {
        return List.of(
                "Q1: Which individuals have relatives who hold prominent positions?",
                "Q2: Which individuals have relatives who hold prominent positions in an organization?",
                "Q3: Which individuals holding positions in public organizations influence companies?",
                "Q4: Which individuals influence organizations that control companies?",
                "Q5: Which individuals have associates who own assets in companies they regulate?"
        );
    }

    // Translate question → matrix chain
    private List<String> chainFor(String q) {

        if (q.startsWith("Q1")) return List.of("relativeOf", "holdsPosition");
        if (q.startsWith("Q2")) return List.of("relativeOf", "holdsPosition", "inOrganization");
        if (q.startsWith("Q3")) return List.of("holdsPosition", "inOrganization", "influences");
        if (q.startsWith("Q4")) return List.of("influences", "controls", "partnerOf", "supervises");
        if (q.startsWith("Q5")) return List.of("associatedWith", "ownerOf");

        return List.of();
    }


    // Boolean multiplication with flags

    private int[][] booleanMultiply(int[][] A, int[][] B) {

        if (A == null || B == null) return null;

        int rows = A.length;
        int cols = B[0].length;
        int mid  = A[0].length;

        int[][] R = new int[rows][cols];
        witness = new int[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {

                for (int k = 0; k < mid; k++) {
                    if (A[i][k] == 1 && B[k][j] == 1) {
                        R[i][j] = 1;
                        witness[i][j] = k; // save as a draft
                        break;
                    }
                }
            }
        }

        return R;
    }


    // AC2 — Execute query and display the full query string

    public List<String> execute(String question) {

        List<String> chain = chainFor(question);
        if (chain.isEmpty()) return List.of("Invalid question");

        int questionNumber = getQuestions().indexOf(question) + 1;

        int[][] result = M.get(chain.get(0));

        for (int i = 1; i < chain.size(); i++) {
            result = booleanMultiply(result, M.get(chain.get(i)));
        }

        List<String> output = new ArrayList<>();
        output.add("US23 – Q" + questionNumber);

        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[0].length; j++) {

                if (result[i][j] == 1) {

                    List<String> steps = reconstructChain(chain, i, j);

                    String from = resolveEntity(chain.get(0), i, true);
                    String to   = resolveEntity(chain.get(chain.size()-1), j, false);

                    // In the end
                    output.add("(" + from + ", " + to + ")");

                    // Natural textual explanation
                    StringBuilder explanation = new StringBuilder("because ");

                    for (int s = 0; s < steps.size(); s++) {

                        String[] parts = steps.get(s).split(" —\\(");
                        String left = parts[0].trim();
                        String rel = parts[1].split("\\)→")[0];
                        String right = parts[1].split("\\)→")[1].trim();

                        explanation.append(naturalLanguage(rel, left, right));

                        if (s < steps.size() - 1)
                            explanation.append(", who ");
                    }

                    output.add(explanation.toString());
                    output.add(""); // blank line
                }
            }
        }

        boolean foundAny = false;

        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[0].length; j++) {

                if (result[i][j] == 1) {

                    foundAny = true;
                    List<String> steps = reconstructChain(chain, i, j);

                    String from = resolveEntity(chain.get(0), i, true);
                    String to   = resolveEntity(chain.get(chain.size()-1), j, false);

                    output.add("(" + from + ", " + to + ")");

                    StringBuilder explanation = new StringBuilder("because ");

                    for (int s = 0; s < steps.size(); s++) {
                        String[] parts = steps.get(s).split(" —\\(");
                        String left = parts[0].trim();
                        String rel = parts[1].split("\\)→")[0];
                        String right = parts[1].split("\\)→")[1].trim();

                        explanation.append(naturalLanguage(rel, left, right));

                        if (s < steps.size() - 1)
                            explanation.append(", who ");
                    }

                    output.add(explanation.toString());
                    output.add("");
                }
            }
        }

        if (!foundAny) {
            output.add("No results found.");
        }


        return output;
    }


    // Reconstruction of the entire chain

    private List<String> reconstructChain(List<String> chain, int startIndex, int endIndex) {

        List<String> steps = new ArrayList<>();

        int currentStart = startIndex;
        int currentEnd = endIndex;

        for (int c = 0; c < chain.size() - 1; c++) {

            int mid = witness[currentStart][currentEnd];

            String from = resolveEntity(chain.get(c), currentStart, true);
            String to   = resolveEntity(chain.get(c), mid, false);

            steps.add(from + " —(" + chain.get(c) + ")→ " + to);

            currentStart = mid;
        }

        // Final jump
        String lastFrom = resolveEntity(chain.get(chain.size()-2), currentStart, true);
        String lastTo   = resolveEntity(chain.get(chain.size()-1), endIndex, false);

        steps.add(lastFrom + " —(" + chain.get(chain.size()-1) + ")→ " + lastTo);

        return steps;
    }


    // Resolve index → correct entity

    private String resolveEntity(String relationType, int index, boolean isRow) {

        return switch (relationType) {

            case "relativeOf", "friendOf", "associatedWith", "appointedBy" ->
                    persons.get(index).getName();

            case "holdsPosition" ->
                    isRow ? persons.get(index).getName()
                            : positions.get(index).getPositionTitle();

            case "inOrganization" ->
                    isRow ? positions.get(index).getPositionTitle()
                            : orgs.get(index).getName();

            case "influences", "memberOf", "supports", "opposes" ->
                    isRow ? persons.get(index).getName()
                            : orgs.get(index).getName();

            case "controls", "partnerOf", "supervises" ->
                    orgs.get(index).getName();

            case "ownerOf" ->
                    isRow ? persons.get(index).getName()
                            : assets.get(index).getAssetType();

            default -> "UNKNOWN";
        };
    }


    // Natural language for explanations

    private String naturalLanguage(String relation, String from, String to) {
        return switch (relation) {
            case "relativeOf" -> from + " is relative of " + to;
            case "friendOf" -> from + " is friend of " + to;
            case "associatedWith" -> from + " is associated with " + to;
            case "appointedBy" -> from + " was appointed by " + to;
            case "holdsPosition" -> from + " holds a position in " + to;
            case "inOrganization" -> from + " works in " + to;
            case "influences" -> from + " influences " + to;
            case "memberOf" -> from + " is member of " + to;
            case "supports" -> from + " supports " + to;
            case "opposes" -> from + " opposes " + to;
            case "controls" -> from + " controls " + to;
            case "ownerOf" -> from + " owns " + to;
            case "partnerOf" -> from + " is a partner of " + to;
            case "supervises" -> from + " supervises " + to;
            default -> from + " relates to " + to;
        };
    }
}


