package pt.ipp.isep.dei.MDISC.domain;

import org.graphstream.ui.view.ViewerListener;
import java.util.List;

public class GraphClickListener implements ViewerListener {

    private final List<Entity> entities;
    private final List<Relationship> relationships;

    public GraphClickListener(List<Entity> entities, List<Relationship> relationships) {
        this.entities = entities;
        this.relationships = relationships;
    }

    @Override
    public void buttonReleased(String id) {
        if (id == null) return;


        id = id.split("[^A-Za-z0-9]")[0];

        // Nó → entidade
        if (!id.startsWith("R")) {
            Entity e = findEntity(id);
            if (e != null) {
                String msg = switch (e.getType()) {
                    case PERSON -> {
                        Person ps = (Person) e;
                        yield  "ID: " + ps.getId() + "\nName: " + ps.getName();
                    }
                    case ORGANIZATION -> {
                        Organization o = (Organization) e;
                        yield  "ID: " + o.getId() + "\nName: " + o.getName();
                    }
                    case POSITION -> {
                        Position pt = (Position) e;
                        yield "ID: " + pt.getId() + "\nPosition Title: " + pt.getPositionTitle();
                    }
                    case ASSET -> {
                        Asset a = (Asset) e;
                        yield "ID: " + a.getId() + "\nAsset Type: " + a.getAssetType();
                    }
                };

                javax.swing.JOptionPane.showMessageDialog(
                        null,
                        msg,
                        "Entity Details",
                        javax.swing.JOptionPane.INFORMATION_MESSAGE
                );
            }
            return;
        }

        // Aresta → relação
        Relationship r = findRelationship(id);
        if (r != null) {
            String msg =
                    "ID: " + r.getId() +
                            "\nType: " + r.getType() +
                            "\nFrom: " + r.getEntity1Id() +
                            "\nTo: " + r.getEntity2Id() +
                            "\nStart: " + r.getStartDate() +
                            "\nEnd: " + r.getEndDate();

            javax.swing.JOptionPane.showMessageDialog(
                    null,
                    msg,
                    "Relationship Details",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE
            );
        }
    }



    @Override
    public void mouseOver(String id) {

    }

    @Override
    public void mouseLeft(String id) {

    }

    private Entity findEntity(String id) {
        for (Entity e : entities)
            if (e.getId().equals(id))
                return e;
        return null;
    }

    private Relationship findRelationship(String id) {
        for (Relationship r : relationships)
            if (r.getId().equals(id))
                return r;
        return null;
    }

    @Override public void buttonPushed(String id) {}
    @Override public void viewClosed(String id) {}
}


