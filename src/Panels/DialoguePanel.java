package Panels;

import Frames.MainWindow;
import Helpers.ComponentListener;
import Helpers.InConnector;
import Helpers.OutConnector;

import javax.swing.*;
import java.awt.*;

public class DialoguePanel extends NodePanel{

    private final JTextArea text_entry;
    private final JScrollPane pane;

    public DialoguePanel(MainWindow window, Point start) {
        super(window);
        setLocation(start.x, start.y);
        setBorder(BorderFactory.createMatteBorder(5, 2, 2, 2, Color.yellow));
        addMouseListener(new ComponentListener(window, this));

        text_entry = new JTextArea("Hello world!");
        text_entry.setLineWrap(true);
        pane = new JScrollPane(text_entry);
        add(pane);

        in_connector = new InConnector(this, window);
        add(in_connector);

        out_connector = new OutConnector(this, window);
        add(out_connector);

        rescale(1, new Point(0, 0));
    }

    @Override
    public void rescale(float mod, Point source) {
        super.rescale(mod, source);
        setSize((int)(300 * canvas.scale.getX()), (int)(120 * canvas.scale.getY()));
        text_entry.setFont(window.main_font.deriveFont((float)(20 * canvas.scale.getX())));
        pane.setBounds((int)(40 * canvas.scale.getX()), (int)(10 * canvas.scale.getY()), (int)(210 * canvas.scale.getX()), (int)(100 * canvas.scale.getY()));
        in_connector.setLocation((int)(5 * canvas.scale.getX()), (int)(45 * canvas.scale.getY()));
        in_connector.rescale();
        out_connector.setLocation((int)(255 * canvas.scale.getX()), (int)(45 * canvas.scale.getY()));
        out_connector.rescale();
    }
}
