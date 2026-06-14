package pt.ipp.isep.dei.examples.basic.domain.Analysis;

import pt.ipp.isep.dei.examples.basic.domain.Edge;
import java.util.*;

public class Queries {

    private final List<Edge> allEdges;
    private final List<String> entityIndex; 

    public Queries(List<Edge> edges) {
        this.allEdges = edges;
        this.entityIndex = extractEntities(edges);
    }

    public List<String> getPredefinedQuestions() {
        return List.of(
            "Q1: Which individuals have relatives who hold prominent positions?",
            "Q2: Which individuals have relatives who hold prominent positions in an organization?",
            "Q3: Which individuals holding positions in public organizations influence companies?",
            "Q4: Which individuals hold positions in public organizations and own assets connected to companies they influence?",
            "Q5: Which individuals influence organizations where their relatives, friends, or associates have political connections?",
            "Q6: Which individuals are members of organizations that are opposed by people connected to them?",
            "Q7: Which individuals influence organizations that are supervised or controlled by organizations where their relatives, friends, or associates hold positions?"
        );
    }
    
    private List<String> getChainFor(int index) {
        return switch (index) {
            case 0 -> List.of("relativeOf", "holdsPosition");
            case 1 -> List.of("relativeOf", "holdsPosition", "inOrganization");
            case 2 -> List.of("rev_inOrganization", "rev_holdsPosition", "influences");
            case 3 -> List.of("holdsPosition", "inOrganization", "rev_inOrganization", "rev_holdsPosition", "influences", "rev_influences", "anyEconomicLink");
            case 4 -> List.of("influences", "rev_influences", "anySocialLink", "anyPoliticalLink");   
            case 5 -> List.of("memberOf", "rev_opposes");
            case 6 -> List.of("anySocialLink", "anySocialLink", "anySocialLink", "holdsPosition", "inOrganization", "anyStructuralLink");
            default -> List.of();
        };
    }

    public List<String> executeQuery(int questionIndex) {
        List<String> relationshipChain = getChainFor(questionIndex);
        if (relationshipChain.isEmpty()) return List.of("Invalid Question");

        int n = entityIndex.size();
        int[][] resultMatrix = buildMatrix(relationshipChain.get(0));
        List<int[][]> stepWitnesses = new ArrayList<>();

        for (int i = 1; i < relationshipChain.size(); i++) {
            int[][] nextMatrix = buildMatrix(relationshipChain.get(i));
            int[][] witness = new int[n][n];
            for (int[] row : witness) Arrays.fill(row, -1);
            
            resultMatrix = booleanMultiplyWithWitness(resultMatrix, nextMatrix, witness);
            stepWitnesses.add(witness);
        }

        if (questionIndex == 5) { 
            int[][] social1 = buildMatrix("anySocialLink");
            int[][] social2 = booleanMultiplyWithWitness(social1, social1, new int[n][n]);
            int[][] social3 = booleanMultiplyWithWitness(social2, social1, new int[n][n]);

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    boolean connectedInReach = (social1[i][j] == 1 || social2[i][j] == 1 || social3[i][j] == 1);
                    if (resultMatrix[i][j] == 1 && !connectedInReach) {
                        resultMatrix[i][j] = 0;
                    }
                }
            }
        } 
        else if (questionIndex == 6) { 
            int[][] influences = buildMatrix("influences");
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (resultMatrix[i][j] == 1 && influences[i][j] != 1) {
                        resultMatrix[i][j] = 0;
                    }
                }
            }
        }

        return formatResults(resultMatrix, stepWitnesses, relationshipChain, questionIndex + 1);
    }

    private int[][] buildMatrix(String type) {
        int n = entityIndex.size();
        int[][] matrix = new int[n][n];

        if (type.equals("relativeOf") || type.equals("anySocialLink")) {
            List<String> types = type.equals("anySocialLink") 
                ? List.of("relativeOf", "friendOf", "associatedWith") 
                : List.of("relativeOf");
            for (Edge e : allEdges) {
                if (types.stream().anyMatch(t -> t.equalsIgnoreCase(e.getType()))) {
                    int i = entityIndex.indexOf(e.getEntity1Id());
                    int j = entityIndex.indexOf(e.getEntity2Id());
                    if (i != -1 && j != -1) {
                        matrix[i][j] = 1;
                        matrix[j][i] = 1; 
                    }
                }
            }
            return matrix;
        }

        if (type.equals("anyPoliticalLink")) {
            List<String> types = List.of("memberOf", "supports", "opposes");
            for (Edge e : allEdges) {
                if (types.stream().anyMatch(t -> t.equalsIgnoreCase(e.getType()))) {
                    int i = entityIndex.indexOf(e.getEntity1Id());
                    int j = entityIndex.indexOf(e.getEntity2Id());
                    if (i != -1 && j != -1) matrix[i][j] = 1;
                }
            }
            return matrix;
        }

        if (type.equals("anyEconomicLink")) {
            List<String> types = List.of("ownerOf", "partnerOf");
            for (Edge e : allEdges) {
                if (types.stream().anyMatch(t -> t.equalsIgnoreCase(e.getType()))) {
                    int i = entityIndex.indexOf(e.getEntity1Id());
                    int j = entityIndex.indexOf(e.getEntity2Id());
                    if (i != -1 && j != -1) matrix[i][j] = 1;
                }
            }
            return matrix;
        }

        if (type.equals("anyStructuralLink")) {
            List<String> types = List.of("supervises", "controls");
            for (Edge e : allEdges) {
                if (types.stream().anyMatch(t -> t.equalsIgnoreCase(e.getType()))) {
                    int i = entityIndex.indexOf(e.getEntity1Id());
                    int j = entityIndex.indexOf(e.getEntity2Id());
                    if (i != -1 && j != -1) matrix[i][j] = 1;
                }
            }
            return matrix;
        }

        boolean reverse = type.startsWith("rev_");
        String actualType = reverse ? type.substring(4) : type;
        for (Edge e : allEdges) {
            if (e.getType().equalsIgnoreCase(actualType)) {
                int i = entityIndex.indexOf(reverse ? e.getEntity2Id() : e.getEntity1Id());
                int j = entityIndex.indexOf(reverse ? e.getEntity1Id() : e.getEntity2Id());
                if (i != -1 && j != -1) matrix[i][j] = 1;
            }
        }
        return matrix;
    }

    private int[][] booleanMultiplyWithWitness(int[][] A, int[][] B, int[][] witness) {
        int n = A.length;
        int[][] result = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < n; k++) {
                    if (A[i][k] == 1 && B[k][j] == 1) {
                        result[i][j] = 1;
                        witness[i][j] = k; 
                        break;
                    }
                }
            }
        }
        return result;
    }

    private List<String> formatResults(int[][] resultMatrix, List<int[][]> witnesses, List<String> chain, int qNum) {
        List<String> output = new ArrayList<>();
        output.add("US23 – Q" + qNum);
        boolean found = false;

        for (int i = 0; i < resultMatrix.length; i++) {
            for (int j = 0; j < resultMatrix.length; j++) {
                if (resultMatrix[i][j] == 1) {
                    found = true;
                    output.add("(" + entityIndex.get(i) + ", " + entityIndex.get(j) + ")");
                    output.add("Full Path: " + reconstructPathString(i, j, witnesses, chain));
                    output.add("");
                }
            }
        }
        if (!found) output.add("No results found.");
        return output;
    }

    private String reconstructPathString(int startIdx, int endIdx, List<int[][]> witnesses, List<String> chainTypes) {
        if (witnesses.isEmpty()) {
            return entityIndex.get(startIdx) + " --(" + chainTypes.get(0) + ")--> " + entityIndex.get(endIdx);
        }

        int lastWitnessIdx = witnesses.size() - 1;
        int midIdx = witnesses.get(lastWitnessIdx)[startIdx][endIdx];
        
        List<String> subTypes = chainTypes.subList(0, chainTypes.size() - 1);
        List<int[][]> subWitnesses = witnesses.subList(0, lastWitnessIdx);
        
        return reconstructPathString(startIdx, midIdx, subWitnesses, subTypes) 
               + " --(" + chainTypes.get(chainTypes.size() - 1) + ")--> " + entityIndex.get(endIdx);
    }

    private List<String> extractEntities(List<Edge> edges) {
        Set<String> set = new TreeSet<>();
        for (Edge e : edges) {
            set.add(e.getEntity1Id());
            set.add(e.getEntity2Id());
        }
        return new ArrayList<>(set);
    }
}