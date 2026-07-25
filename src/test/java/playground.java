import com.j3d.gen.docs.reader.J3DocsReader;

import java.io.File;
import java.io.FileNotFoundException;

public class playground {
    public static void main(String[] args) throws FileNotFoundException {
        File file = new File("C:\\Users\\ACER\\Documents\\code\\Jaiva3dEngine\\src\\main\\resources\\docs\\intro.j3.md");
        J3DocsReader.parseFile(file);
    }
}
