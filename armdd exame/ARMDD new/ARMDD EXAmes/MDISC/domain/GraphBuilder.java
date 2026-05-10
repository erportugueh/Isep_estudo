
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;
import java.time.LocalDate;
import java.util.List;

public class GraphBuilder {

    private Graph graph;

    public -GraphBuilder() {
        System.setProperty("org.graphstream.ui", "swing");
        graph = new MultiGraph("Grafo");
        graph.setAttribute("ui.stylesheet", buildStyleSheet());
    }

    // ─── Adiciona entidades ao grafo ──────────────────────────

    public void addEntities(List<Entity> entities) {
        for (Entity e : entities) {
            if (graph.getNode(e.getId()) == null) {
                graph.addNode(e.getId());
                graph.getNode(e.getId()).setAttribute("ui.label", e.getId());
                String cssClass = getCssClass(e.getType());
                graph.getNode(e.getId()).setAttribute("ui.class", cssClass);
            }
        }
    }

    // ─── Devolve a classe CSS baseada no tipo de entidade ─────

    private String getCssClass(Type type) {
        if (type == Type.PERSON)       return "person";
        if (type == Type.ORGANIZATION) return "organization";
        if (type == Type.ASSET)        return "asset";
        if (type == Type.POSITION)     return "position";
        return "default";
    }

    // ─── Adiciona relações ativas no snapshot ─────────────────

    public void addRelationshipsForSnapshot(List<Relationship> relationships,
                                            String snapshot) {
        for (Relationship r : relationships) {
            if (isActive(r, snapshot)) {
                if (graph.getEdge(r.getId()) == null) {

                    if (graph.getNode(r.getEntity1Id()) == null ||
                            graph.getNode(r.getEntity2Id()) == null) {
                        System.out.println("WARNING: skipping relationship " + r.getId() +
                                " because node(s) not found: " + r.getEntity1Id() +
                                ", " + r.getEntity2Id());
                        continue;
                    }

                    graph.addEdge(r.getId(), r.getEntity1Id(), r.getEntity2Id(), true);
                    graph.getEdge(r.getId()).setAttribute("ui.label", r.getType());
                    graph.getEdge(r.getId()).setAttribute("ui.style",
                            "fill-color: " + getColor(r.getType()) + ";");
                }
            }
        }
    }

    // ─── Verifica se a relação estava ativa na data ───────────

    private boolean isActive(Relationship r, String snapshot) {
        LocalDate snap  = LocalDate.parse(snapshot);
        LocalDate start = r.getStartDate();
        LocalDate end   = r.getEndDate() == null
                ? LocalDate.MAX
                : r.getEndDate();
        return !start.isAfter(snap) && !end.isBefore(snap);
    }

    // ─── Cor por tipo de relação (AC1 US20) ───────────────────

    private String getColor(String type) {
        if (type.equals("relativeOf"))      return "blue";
        if (type.equals("friendOf"))        return "cyan";
        if (type.equals("associatedWith"))  return "grey";
        if (type.equals("appointedBy"))     return "red";
        if (type.equals("holdsPosition"))   return "green";
        if (type.equals("inOrganization"))  return "orange";
        if (type.equals("controls"))        return "darkred";
        if (type.equals("partnerOf"))       return "purple";
        if (type.equals("supervises"))      return "brown";
        if (type.equals("influences"))      return "magenta";
        if (type.equals("memberOf"))        return "yellow";
        if (type.equals("supports"))        return "lightgreen";
        if (type.equals("opposes"))         return "salmon";
        if (type.equals("ownerOf"))         return "gold";
        return "black";
    }

    // ─── Stylesheet com imagens PNG por tipo de entidade ──────

    private String buildStyleSheet() {

        // Caminho dinâmico — funciona em qualquer computador da equipa
        String base = "file:///" + System.getProperty("user.dir")
                .replace("\\", "/")
                + "/src/resources/icons/";

        return
                // PERSON → stick man
                "node.person {" +
                        "   shape: box;"  +
                        "   fill-mode: image-scaled;" +
                        "   fill-image: url('" + base + "person.png');" +
                        "   size: 40px;" +
                        "   stroke-mode: plain;" +
                        "   stroke-color: #4A90E2;" +
                        "   stroke-width: 2px;" +
                        "   text-alignment: under;" +
                        "   text-size: 12;" +
                        "}" +

                        // ORGANIZATION → edifício
                        "node.organization {" +
                        "   shape: box;"  +
                        "   fill-mode: image-scaled;" +
                        "   fill-image: url('" + base + "organization.png');" +
                        "   size: 40px;" +
                        "   stroke-mode: plain;" +
                        "   stroke-color: #E94E77;" +
                        "   stroke-width: 2px;" +
                        "   text-alignment: under;" +
                        "   text-size: 12;" +
                        "}" +

                        // ASSET → dinheiro
                        "node.asset {" +
                        "   shape: box;"  +
                        "   fill-mode: image-scaled;" +
                        "   fill-image: url('" + base + "asset.png');" +
                        "   size: 40px;" +
                        "   stroke-mode: plain;" +
                        "   stroke-color: #F5A623;" +
                        "   stroke-width: 2px;" +
                        "   text-alignment: under;" +
                        "   text-size: 12;" +
                        "}" +

                        // POSITION → estrela
                        "node.position {" +
                        "   shape: box;"  +
                        "   fill-mode: image-scaled;" +
                        "   fill-image: url('" + base + "position.png');" +
                        "   size: 40px;" +
                        "   stroke-mode: plain;" +
                        "   stroke-color: #7ED321;" +
                        "   stroke-width: 2px;" +
                        "   text-alignment: under;" +
                        "   text-size: 12;" +
                        "}" +

                        // DEFAULT
                        "node {" +
                        "   shape: box;"  +
                        "   size: 30px;" +
                        "   text-size: 12;" +
                        "   text-alignment: under;" +
                        "}" +

                        // ARESTAS
                        "edge {" +
                        "   text-size: 11;" +
                        "   text-alignment: along;" +
                        "   arrow-shape: arrow;" +
                        "   arrow-size: 10px, 5px;" +
                        "}";
    }

    // ─── Mostrar o grafo numa janela ──────────────────────────

    public void display(List<Entity> entities, List<Relationship> relationships) {
        var viewer = graph.display();
        var pipe = viewer.newViewerPipe();

        pipe.addViewerListener(new GraphClickListener(entities, relationships));
        pipe.addSink(graph);

        new Thread(() -> {
            while (true) {
                pipe.pump();
                try { Thread.sleep(20); } catch (Exception ignored) {}
            }
        }).start();

    }



    public Graph getGraph() {
        return graph;
    }
}