package pt.ipp.isep.dei.MDISC.domain;
import java.util.*;

public class NepotismDetector {

    public List<String> detectNepotism(List<Relationship> relationships) {

        // Affinity maps
        Map<String, Set<String>> relatives = new HashMap<>();
        Map<String, Set<String>> friends = new HashMap<>();
        Map<String, Set<String>> associates = new HashMap<>();

        // Assignment map: appointedBy (appointee) = appointor
        Map<String, String> appointedBy = new HashMap<>();

        // Load maps
        for (Relationship r : relationships) {
            relatives.putIfAbsent(r.getEntity1Id(), new HashSet<>());
            friends.putIfAbsent(r.getEntity1Id(), new HashSet<>());
            associates.putIfAbsent(r.getEntity1Id(), new HashSet<>());
        }

        // Fill out forms
        for (Relationship r : relationships) {

            switch (r.getType()) {

                case "relativeOf" -> {
                    relatives.putIfAbsent(r.getEntity1Id(), new HashSet<>());
                    relatives.get(r.getEntity1Id()).add(r.getEntity2Id());
                }

                case "friendOf" -> {
                    friends.putIfAbsent(r.getEntity1Id(), new HashSet<>());
                    friends.get(r.getEntity1Id()).add(r.getEntity2Id());
                }

                case "associatedWith" -> {
                    associates.putIfAbsent(r.getEntity1Id(), new HashSet<>());
                    associates.get(r.getEntity1Id()).add(r.getEntity2Id());
                }

                case "appointedBy" -> {
                    // appointedBy(appointee) = appointor
                    appointedBy.put(r.getEntity1Id(), r.getEntity2Id());
                }
            }
        }

        // Final list of nepotism
        List<String> nepotismCases = new ArrayList<>();

        // Check for direct nepotism
        for (String nomeado : appointedBy.keySet()) {

            String nomeador = appointedBy.get(nomeado);

            if (relatives.getOrDefault(nomeado, Set.of()).contains(nomeador)) {
                nepotismCases.add(nomeado + " was appointed by " + nomeador + " (kinship)");
            }

            if (friends.getOrDefault(nomeado, Set.of()).contains(nomeador)) {
                nepotismCases.add(nomeado + " was appointed by " + nomeador + " (friendship)");
            }

            if (associates.getOrDefault(nomeado, Set.of()).contains(nomeador)) {
                nepotismCases.add(nomeado + " was appointed by " + nomeador + " (association)");
            }
        }

        return nepotismCases;
    }
}

