package pt.ipp.isep.dei.examples.basic.domain.Analysis;

import pt.ipp.isep.dei.examples.basic.domain.Edge;
import pt.ipp.isep.dei.examples.basic.domain.Entity;

import java.time.LocalDate;

public class SubNetwork {

    private final Edge[] allEdges;
    private final Entity[] allEntities;


    public SubNetwork(Edge[] edges, Entity[] entities) {
        this.allEdges = edges;
        this.allEntities = entities;

    }

public void generateSubnetworkTransitive(String startEntityId, LocalDate snapshotDate) {
    int n = allEntities.length;
    boolean[][] reach = new boolean[n][n];

    for (int i = 0; i < allEdges.length; i++) {
        Edge e = allEdges[i];
        
        if (isInfluenceRelation(e.getType()) && isEdgeActive(e, snapshotDate)) {
            int u = findEntityIndex(e.getEntity1Id());
            int v = findEntityIndex(e.getEntity2Id());
            
            if (u != -1 && v != -1) {
                reach[u][v] = true; 
                
                if (isSocialRelation(e.getType())) {
                    reach[v][u] = true;
                }
            }
        }
    }

    //Warshall's Algorithm
    for (int k = 0; k < n; k++) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                reach[i][j] = reach[i][j] || (reach[i][k] && reach[k][j]);
            }
        }
    }

    int startIndex = findEntityIndex(startEntityId);
    if (startIndex == -1) {
        System.out.println("Entity ID '" + startEntityId + "' not found in snapshot.");
        return;
    }

    System.out.println("\nEntities integrated with " + startEntityId + " - " + getEntityName(startEntityId) + " (Power & Social relations):");
    boolean found = false;
    for (int j = 0; j < n; j++) {
        // Only proceed if reachable
        if (reach[startIndex][j] && startIndex != j) {
            String targetId = allEntities[j].getId();
            
            // Check for direct connections
            String directType = null;
            for (int k = 0; k < allEdges.length; k++) {
                Edge e = allEdges[k];
                if (isInfluenceRelation(e.getType()) && isEdgeActive(e, snapshotDate)) {
                    boolean forward = e.getEntity1Id().equalsIgnoreCase(startEntityId) && e.getEntity2Id().equalsIgnoreCase(targetId);
                    boolean backward = isSocialRelation(e.getType()) && e.getEntity1Id().equalsIgnoreCase(targetId) && e.getEntity2Id().equalsIgnoreCase(startEntityId);
                    
                    if (forward || backward) {
                        directType = e.getType();
                        break;
                    }
                }
            }

            // ONLY print if there is a direct connection
            if (directType != null) {
                System.out.println("-> " + targetId + " - " + getEntityName(targetId));
                System.out.println("   (Direct connection: " + directType + ")");
                found = true;
            }
        }
    }
    if (!found) {
        System.out.println("No direct connections found for this entity.");
    }
}

    private boolean isEdgeActive(Edge e, LocalDate date) {
        boolean startOk = !date.isBefore(e.getStartDate());
        boolean endOk = (e.getEndDate() == null) || !date.isAfter(e.getEndDate());
        return startOk && endOk;
    }

    private int findEntityIndex(String id) {
    for (int i = 0; i < allEntities.length; i++) {
        if (allEntities[i].getId().equalsIgnoreCase(id)) {
            return i;
        }
    }
    return -1;
    }

    private boolean isInfluenceRelation(String type) {
    return isPowerRelation(type) || isSocialRelation(type);
    }

    private boolean isPowerRelation(String type) {
        return type.equalsIgnoreCase("appointedBy") ||
            type.equalsIgnoreCase("holdsPosition") ||
            type.equalsIgnoreCase("inOrganization") ||
            type.equalsIgnoreCase("influences") ||
            type.equalsIgnoreCase("ownerOf") ||
            type.equalsIgnoreCase("controls") ||
            type.equalsIgnoreCase("memberOf") ||
            type.equalsIgnoreCase("opposes") ||
            type.equalsIgnoreCase("supervises");
    }

    private boolean isSocialRelation(String type) {
        return type.equalsIgnoreCase("friendOf") ||
            type.equalsIgnoreCase("relativeOf") ||
            type.equalsIgnoreCase("associatedWith");
    }

    private String getEntityName(String id) {
        for (int i = 0; i < allEntities.length; i++) {
            if (allEntities[i].getId().equalsIgnoreCase(id)) {
                return allEntities[i].getName();
            }
        }
        return id;
    }

}
        

            
            
