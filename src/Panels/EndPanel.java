package Panels;

import Frames.MainWindow;
import Helpers.ComponentListener;
import Helpers.InConnector;
import Helpers.OutConnector;

import javax.swing.*;
import java.awt.*;

public class EndPanel extends NodePanel{

    private final JLabel end_label;

    public EndPanel(MainWindow window, Point start) {
        super(window);
        setLocation(start);

        setBorder(BorderFactory.createMatteBorder(2, 2, 2, 5, Color.red));
        setBackground(Color.lightGray);
        addMouseListener(new ComponentListener(window, this));

        end_label = new JLabel("End");
        add(end_label);

        in_connector = new InConnector(this, window);
        add(in_connector);

        rescale(1, new Point(0, 0));
    }

    @Override
    public void rescale(float mod, Point source) {
        super.rescale(mod, source);
        setSize((int)(120 * canvas.scale.getX()), (int)(50 * canvas.scale.getY()));
        end_label.setFont(window.main_font.deriveFont((float) (28.0f * canvas.scale.getX())));
        end_label.setBounds((int)(40 * canvas.scale.getX()), (int)(5 * canvas.scale.getY()), (int)(70 * canvas.scale.getX()), (int)(40 * canvas.scale.getY()));
        in_connector.setLocation((int)(5 * canvas.scale.getX()), (int)(10 * canvas.scale.getY()));
        in_connector.rescale();
    }
}
