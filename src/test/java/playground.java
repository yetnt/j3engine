import com.j3d.engine.geometry.geo3d.matrix.Matrix4;
import com.j3d.engine.geometry.geo3d.matrix.MatrixInterface;
import com.j3d.engine.geometry.geo3d.matrix.MatrixMath;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;

public class playground {
    public static void main(String[] args) {
        Matrix4 m = new Matrix4(
                new double[][]{
                        {1, 0, 0, 0},
                        {0, 1, 0, 0},
                        {0, 0, 1, 0},
                        {0, 0, 0, 1}
                }
        );

        Vector3 v = new Vector3(10, 40, 2);
        MatrixInterface m2 = MatrixMath.matrixOf(
                new double[][]{
                        {-4, 2, 3},
                        {3, 4, 3},
                }
        );
        MatrixInterface f = MatrixMath.mult(v, m2);
        System.out.println(MatrixMath.mult(
                m,
                MatrixMath.matrixOf(
                        new double[][]{
                                {12},
                                {34},
                                {4},
                                {0}
                        }
                )
        ).toMatrixString());
        System.out.println(f.toMatrixString());
    }
}
