package com.j3d.engine.geometry.geo3d.matrix;

/**
 * Defines the core contract for a matrix implementation. This interface provides
 * methods for accessing dimensions, getting and setting individual elements,
 * retrieving the underlying data array, and creating a copy of the matrix.
 * It also includes a default method for generating a formatted string representation.
 * <p>
 *     A huge thank you to Mindbourne for their AP Maths videos on matrices.
 * </p>
 * @author Lehlogonolo Poole
 * @see MatrixMath
 * @see Vector3
 * @see Matrix3
 */
public interface MatrixInterface {
    /**
     * Returns the number of rows in the matrix.
     *
     * @return The number of rows.
     */
    int rows();

    /**
     * Returns the number of columns in the matrix.
     *
     * @return The number of columns.
     */
    int cols();

    /**
     * Retrieves the value of a specific element in the matrix.
     *
     * @param row The row index of the element (0-based).
     * @param col The column index of the element (0-based).
     * @return The value at the specified position.
     */
    double get(int row, int col);

    /**
     * Returns the underlying 2D array that represents the matrix data.
     *
     * @return A 2D double array containing the matrix values.
     */
    double[][] get();

    /**
     * Creates and returns a deep copy of this matrix.
     *
     * @return A new {@link MatrixInterface} instance with the same values as the original.
     */
    MatrixInterface copy();

    /**
     * Generates a formatted string representation of the matrix, suitable for console output.
     * The matrix is enclosed in box-drawing characters, and columns are aligned for readability.
     * Values are formatted to four decimal places.
     *
     * @return A string representing the formatted matrix.
     */
    default String toMatrixString() {
        // First, compute max width per column
        int[] colWidths = new int[cols()];
        for (int j = 0; j < cols(); j++) {
            int max = 0;
            for (int i = 0; i < rows(); i++) {
                String val = String.format("%.4f", get(i, j));
                max = Math.max(max, val.length());
            }
            colWidths[j] = max + 2; // add padding
        }

        StringBuilder sb = new StringBuilder();

        // Top row: ┍ ... ┑
        sb.append("┍");
        for (int j = 0; j < cols(); j++) {
            sb.append(" ".repeat(Math.max(0, colWidths[j])));
            if (j < cols() - 1) sb.append(" ");
        }
        sb.append("┑\n");

        // Matrix rows
        for (int i = 0; i < rows(); i++) {
            sb.append("│ ");
            for (int j = 0; j < cols(); j++) {
                String val = String.format("%.4f", get(i, j));
                sb.append(String.format("%-" + colWidths[j] + "s", val));
                if (j < cols() - 2) sb.append(" ");
            }
            sb.append("│\n");
        }

        // Bottom row: ┕ ... ┙
        sb.append("┕");
        for (int j = 0; j < cols(); j++) {
            sb.append(" ".repeat(Math.max(0, colWidths[j])));
            if (j < cols() - 1) sb.append(" ");
        }
        sb.append("┙");

        return sb.toString();
    }

}
