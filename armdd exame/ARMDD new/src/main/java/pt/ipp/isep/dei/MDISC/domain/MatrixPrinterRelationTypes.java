package pt.ipp.isep.dei.MDISC.domain;

public class MatrixPrinterRelationTypes {

    public static void print(String[][] M) {

        System.out.println("\nRelation Type Matrix (Dynamic):\n");

        int rows = M.length;
        int cols = M[0].length;

        String[] rowLabels = {"P (Person)", "O (Org)", "C (Pos)", "A (Asset)"};

        // Calculate the maximum width of each column
        int[] colWidths = new int[cols];

        for (int j = 0; j < cols; j++) {
            int max = 3; // minimum width
            for (int i = 0; i < rows; i++) {
                if (M[i][j] != null) {
                    max = Math.max(max, M[i][j].length());
                }
            }
            colWidths[j] = max + 3; // +3 for spacing
        }

        // Print dynamic header
        System.out.print("              ");
        for (int j = 0; j < cols; j++) {
            System.out.printf("%-" + colWidths[j] + "s", getColumnLabel(j));
        }
        System.out.println();

        // Dash
        System.out.print("          ");
        int totalWidth = 0;
        for (int w : colWidths) totalWidth += w;
        for (int k = 0; k < totalWidth + 14; k++) System.out.print("-");
        System.out.println();

        // Print rows of the matrix
        for (int i = 0; i < rows; i++) {
            System.out.printf("%-10s | ", rowLabels[i]);
            for (int j = 0; j < cols; j++) {
                System.out.printf("%-" + colWidths[j] + "s", M[i][j]);
            }
            System.out.println();
        }
    }

    private static String getColumnLabel(int index) {
        return switch (index) {
            case 0 -> "P";
            case 1 -> "O";
            case 2 -> "C";
            case 3 -> "A";
            default -> "T" + index;
        };
    }
}



