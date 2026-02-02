import com.j3d.engine.interact.cmd.CommandsManager;
import com.j3d.engine.interact.cmd.base.Command;

import javax.swing.*;
import java.awt.*;

public class playground {
    public static void main(String[] args) {
//        FileDialog dialog = new FileDialog(
//                (Frame) null, "Open", FileDialog.LOAD
//        );
//        dialog.setDirectory("C:\\");
//        dialog.setVisible(true);
//
//        String dir = dialog.getDirectory();
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Folder");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        JFrame frame = new JFrame();
        int result = chooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            System.out.println(chooser.getSelectedFile().getAbsolutePath());
            frame.dispose();
        }
    }
}
