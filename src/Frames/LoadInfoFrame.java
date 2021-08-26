package Frames;

import javax.swing.*;
import java.awt.*;

public class LoadInfoFrame extends JFrame {

    public LoadInfoFrame(String name) {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();

        setTitle("Loading project");
        JLabel label = new JLabel("Loading " + name);
        label.setFont(label.getFont().deriveFont(30f));
        add(label);
        setSize(400, 100);
        setVisible(true);

        setLocationRelativeTo(null);
    }

}
