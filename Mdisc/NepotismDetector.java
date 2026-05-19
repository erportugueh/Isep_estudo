package pt.ipp.isep.dei.examples.basic.domain.Analysis;

import pt.ipp.isep.dei.examples.basic.domain.Edge;
import java.util.*;

public class NepotismDetector {

    private static final Set<String> PERSONAL_TYPES = Set.of("relativeOf", "friendOf", "associatedWith", "partnerOf");
//Direct detecting of nepotism
    public List<String> detectDirect(List<Edge> edges) {
        Map<String, Set<String>> graph = buildSymmetricGraph(edges);
        List<String> results = new ArrayList<>();

        for (Edge e : edges) {
            // Case: Direct Nepotism (A appoints B, and A is connected to B)
            if (e.getType().equalsIgnoreCase("appointedBy")) {
                String appointee = e.getEntity1Id(); 
                String appointor = e.getEntity2Id();

                if (graph.getOrDefault(appointor, Set.of()).contains(appointee)) {
                    results.add(String.format("[DIRECT] %s was appointed by their contact %s", appointee, appointor));
                }
            }
        }
        return results;
    }
//Indirect detection of nepotism  // Seach for intermediary 

    public List<String> detectIndirect(List<Edge> edges) {
        Map<String, Set<String>> graph = buildSymmetricGraph(edges);
        List<String> results = new ArrayList<>();

        for (Edge e : edges) {
            if (e.getType().equalsIgnoreCase("appointedBy")) {
                String appointee = e.getEntity1Id(); 
                String appointor = e.getEntity2Id();

                if (!graph.getOrDefault(appointor, Set.of()).contains(appointee)) {
                    Set<String> appointorContacts = graph.getOrDefault(appointor, Set.of());
                    
                    for (String intermediary : appointorContacts) {
                        if (graph.getOrDefault(intermediary, Set.of()).contains(appointee)) {
                            results.add(String.format("[INDIRECT] %s appointed by %s via shared contact %s", 
                                        appointee, appointor, intermediary));
                            break; 
                        }
                    }
                }
            }
        }
        return results;
    }

    private Map<String, Set<String>> buildSymmetricGraph(List<Edge> edges) {
        Map<String, Set<String>> adj = new HashMap<>();
        for (Edge e : edges) {
            if (PERSONAL_TYPES.contains(e.getType())) {
                String v1 = e.getEntity1Id();
                String v2 = e.getEntity2Id();
                adj.computeIfAbsent(v1, k -> new HashSet<>()).add(v2);
                adj.computeIfAbsent(v2, k -> new HashSet<>()).add(v1);
            }
        }
        return adj;
    }
}