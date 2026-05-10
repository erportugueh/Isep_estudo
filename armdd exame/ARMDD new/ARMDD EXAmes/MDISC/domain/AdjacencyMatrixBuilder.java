
import java.time.LocalDate;
import java.util.*;

public class AdjacencyMatrixBuilder {

    private List<Entity> entities;
    private List<Relationship> relationships;

    // Lists sorted by type
    private List<Person> persons = new ArrayList<>();
    private List<Organization> orgs = new ArrayList<>();
    private List<Position> positions = new ArrayList<>();
    private List<Asset> assets = new ArrayList<>();

    // Table of Contents
    private Map<String, Integer> pIndex = new HashMap<>();
    private Map<String, Integer> oIndex = new HashMap<>();
    private Map<String, Integer> cIndex = new HashMap<>();
    private Map<String, Integer> aIndex = new HashMap<>();


    public AdjacencyMatrixBuilder(List<Entity> entities,
                                  List<Relationship> relationships) {

        this.entities = entities;
        this.relationships = relationships;

        separateEntities();
        buildIndexMaps();
    }

    // Sort entities by type

    private void separateEntities() {
        for (Entity e : entities) {
            switch (e.getType()) {
                case PERSON -> persons.add((Person) e);
                case ORGANIZATION -> orgs.add((Organization) e);
                case POSITION -> positions.add((Position) e);
                case ASSET -> assets.add((Asset) e);
            }
        }
    }

    // Create table of contents by type

    private void buildIndexMaps() {

        for (int i = 0; i < persons.size(); i++)
            pIndex.put(persons.get(i).getId(), i);

        for (int i = 0; i < orgs.size(); i++)
            oIndex.put(orgs.get(i).getId(), i);

        for (int i = 0; i < positions.size(); i++)
            cIndex.put(positions.get(i).getId(), i);

        for (int i = 0; i < assets.size(); i++)
            aIndex.put(assets.get(i).getId(), i);
    }

    // Snapshot

    private boolean isActive(Relationship r, String snapshot) {

        LocalDate snap = LocalDate.parse(snapshot);
        LocalDate start = r.getStartDate();
        LocalDate end = (r.getEndDate() == null) ? LocalDate.MAX : r.getEndDate();

        return !start.isAfter(snap) && !end.isBefore(snap);
    }

    // NxN GLOBAL MATRIX

    public int[][] buildGlobalMatrix(String snapshot) {

        int n = entities.size();
        int[][] M = new int[n][n];

        Map<String, Integer> index = new HashMap<>();
        for (int i = 0; i < entities.size(); i++)
            index.put(entities.get(i).getId(), i);

        for (Relationship r : relationships) {

            if (!isActive(r, snapshot)) continue;

            Integer i = index.get(r.getEntity1Id());
            Integer j = index.get(r.getEntity2Id());

            if (i == null || j == null) {
                System.out.println("WARNING: skipping global relation " + r.getId() +
                        " because entity not found: " + r.getEntity1Id() + ", " + r.getEntity2Id());
                continue;
            }

            M[i][j] = 1;

        }

        return M;
    }

    // MATRICES BY RELATIONSHIP TYPE

    public Map<String, int[][]> buildTypedMatrices(String snapshot) {

        Map<String, int[][]> matrices = new HashMap<>();

        // MAKE SURE THAT ALL THE PARENT TABLES ARE THERE, EVEN IF THERE ARE NO ACTIVE RELASHIONSHIP .

        matrices.put("relativeOf",      new int[persons.size()][persons.size()]);
        matrices.put("friendOf",        new int[persons.size()][persons.size()]);
        matrices.put("associatedWith",  new int[persons.size()][persons.size()]);
        matrices.put("appointedBy",     new int[persons.size()][persons.size()]);

        matrices.put("holdsPosition",   new int[persons.size()][positions.size()]);
        matrices.put("inOrganization",  new int[positions.size()][orgs.size()]);

        matrices.put("influences",      new int[persons.size()][orgs.size()]);
        matrices.put("memberOf",        new int[persons.size()][orgs.size()]);
        matrices.put("supports",        new int[persons.size()][orgs.size()]);
        matrices.put("opposes",         new int[persons.size()][orgs.size()]);

        matrices.put("controls",        new int[orgs.size()][orgs.size()]);
        matrices.put("partnerOf",      new int[orgs.size()][orgs.size()]);
        matrices.put("supervises",      new int[orgs.size()][orgs.size()]);

        matrices.put("ownerOf",         new int[persons.size()][assets.size()]);


        for (Relationship r : relationships) {

            if (!isActive(r, snapshot)) continue;

            String type = r.getType();

            // MAKE A MATRIX THAT IS THE RIGHT SIZE.

            matrices.putIfAbsent(type, createMatrixForType(type, r));

            int[][] M = matrices.get(type);

            int i = getRowIndex(type, r.getEntity1Id());
            int j = getColIndex(type, r.getEntity2Id());

            if (i == -1 || j == -1) {
                System.out.println("WARNING: skipping typed relation " + r.getId() +
                        " because entity not found: " + r.getEntity1Id() + ", " + r.getEntity2Id());
                continue;
            }

            M[i][j] = 1;

        }

        return matrices;
    }

    // CREATE A MATRIX WITH THE CORRECT DIMENSION FOR EACH TYPE.

    private int[][] createMatrixForType(String type, Relationship r) {

        Entity e1 = getEntityById(r.getEntity1Id());
        Entity e2 = getEntityById(r.getEntity2Id());

        return switch (type) {

            // PERSON–PERSON

            case "relativeOf", "friendOf", "associatedWith", "appointedBy" ->
                    new int[persons.size()][persons.size()];

            // PERSON–POSITION

            case "holdsPosition" ->
                    new int[persons.size()][positions.size()];

            // POSITION–ORGANIZATION

            case "inOrganization" ->
                    new int[positions.size()][orgs.size()];

            // ORGANIZATION–ORGANIZATION

            case "controls", "supervises" ->
                    new int[orgs.size()][orgs.size()];

            case "partnerOf" -> {
                if (e1 instanceof Organization && e2 instanceof Organization)
                    yield new int[orgs.size()][orgs.size()];
                if (e1 instanceof Person && e2 instanceof Asset)
                    yield new int[persons.size()][assets.size()];
                yield null;
            }

            // PERSON–ORGANIZATION

            case "influences", "memberOf", "supports", "opposes" ->
                    new int[persons.size()][orgs.size()];

            // PERSON–ASSET

            case "ownerOf" ->
                    new int[persons.size()][assets.size()];

            default -> null;
        };
    }


    // GET THE CORRECT INDICES BY TYPE

    private int getRowIndex(String type, String id) {

        Entity e1 = getEntityById(id);

        return switch (type) {

            // PERSON ROW

            case "relativeOf", "friendOf", "associatedWith", "appointedBy",
                 "holdsPosition", "influences", "memberOf", "supports", "opposes",
                 "ownerOf" ->
                    pIndex.getOrDefault(id, -1);

            // POSITION ROW

            case "inOrganization" ->
                    cIndex.getOrDefault(id, -1);

            // ORGANIZATION ROW

            case "controls", "supervises" ->
                    oIndex.getOrDefault(id, -1);

            case "partnerOf" -> {
                if (e1 instanceof Organization) yield oIndex.getOrDefault(id, -1);
                if (e1 instanceof Person)      yield pIndex.getOrDefault(id, -1);
                yield -1;
            }

            default -> -1;
        };
    }



    private int getColIndex(String type, String id) {

        Entity e2 = getEntityById(id);

        return switch (type) {

            // PERSON–PERSON

            case "relativeOf", "friendOf", "associatedWith", "appointedBy" ->
                    pIndex.getOrDefault(id, -1);

            // PERSON–POSITION

            case "holdsPosition" ->
                    cIndex.getOrDefault(id, -1);

            // POSITION–ORGANIZATION

            case "inOrganization" ->
                    oIndex.getOrDefault(id, -1);

            // PERSON–ORGANIZATION

            case "influences", "memberOf", "supports", "opposes" ->
                    oIndex.getOrDefault(id, -1);

            // ORGANIZATION–ORGANIZATION

            case "controls", "supervises" ->
                    oIndex.getOrDefault(id, -1);

            // PERSON–ASSET

            case "ownerOf" ->
                    aIndex.getOrDefault(id, -1);

            case "partnerOf" -> {
                if (e2 instanceof Organization) yield oIndex.getOrDefault(id, -1);
                if (e2 instanceof Asset)        yield aIndex.getOrDefault(id, -1);
                yield -1;
            }

            default -> -1;
        };
    }



    public List<Person> getPersons() {
        return persons;
    }

    public List<Organization> getOrganizations() {
        return orgs;
    }

    public List<Position> getPositions() {
        return positions;
    }

    public List<Asset> getAssets() {
        return assets;
    }

    private Entity getEntityById(String id) {
        for (Entity e : entities) {
            if (e.getId().equals(id)) return e;
        }
        return null;
    }


}