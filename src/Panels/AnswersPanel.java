package Panels;

import Frames.MainWindow;
import Helpers.OutConnector;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AnswersPanel extends NodePanel {
    private List<NodePanel> answers;

    public AnswersPanel(MainWindow window) {
        super(window);
        answers = new ArrayList<>();
    }

    public NodePanel createAnswer() {
        NodePanel answer_panel = new NodePanel(window, this);
        answer_panel.setBounds(10, this.getHeight() - 10, 280, 50);
        answer_panel.setBackground(Color.orange);

        JTextArea text_entry = new JTextArea("Hello world");
        text_entry.setLineWrap(true);
        text_entry.setBounds(35, 5, 210, 40);
        answer_panel.add(text_entry);
        OutConnector out_connector = new OutConnector(answer_panel, window);
        out_connector.setBounds(250, 10, 30, 30);
        answer_panel.setOutConnector(out_connector);
        answer_panel.add(out_connector);

        this.setBounds(this.getX(), this.getY(), this.getWidth(), this.getHeight() + 50);
        add(answer_panel);

        return answer_panel;
    }
}
