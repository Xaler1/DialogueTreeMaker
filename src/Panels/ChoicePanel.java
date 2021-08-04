package Panels;

import Frames.MainWindow;
import Helpers.ComponentListener;
import Helpers.InConnector;
import Helpers.OutConnector;
import Managers.Graph;
import Nodes.DialogueNode;
import Nodes.Node;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChoicePanel extends NodePanel {
    private List<AnswerPanel> answers;
    private final ChoicePanel self;

    private final JTextArea text_entry;
    private final JScrollPane pane;
    private final JLabel add_btn;
    private DialogueNode node;

    public ChoicePanel(MainWindow window, Graph graph, Point start) {
        super(window, graph);
        answers = new ArrayList<>();
        self = this;

        setLocation(start);
        setBorder(BorderFactory.createMatteBorder(5, 2, 2, 2, Color.yellow));
        addMouseListener(new ComponentListener(window, this));

        text_entry = new JTextArea("Hello world!");
        text_entry.setLineWrap(true);
        pane = new JScrollPane(text_entry);
        add(pane);

        in_connector = new InConnector(this, window);
        add(in_connector);

        add_btn = new JLabel();
        add_btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                NodePanel new_ans = createAnswer();
                graph.addAnswerNode(new_ans, "");
                graph.createRelation(self, new_ans);
                window.current_canvas.components.add(new_ans);
            }
        });
        add(add_btn);

        rescale(1, new Point(0, 0));
    }

    @Override
    public void setNode(Node node) {
        this.node = (DialogueNode) node;
    }

    public NodePanel createAnswer() {
        AnswerPanel answer_panel = new AnswerPanel(window, this, graph, new Point(10, this.getHeight() - 10));
        answers.add(answer_panel);
        answer_panel.setNode(graph.getNode(answer_panel));
        add(answer_panel);
        rescale(1, new Point(0, 0));
        return answer_panel;
    }

    @Override
    public void rescale(float mod, Point source) {
        super.rescale(mod, source);
        this.setSize((int)(300 * canvas.scale.getX()), (int)((120 + 50 * answers.size()) * canvas.scale.getY()));
        text_entry.setFont(window.main_font.deriveFont((float)(20 * canvas.scale.getX())));
        pane.setBounds((int)(40 * canvas.scale.getX()), (int)(10 * canvas.scale.getY()), (int)(210 * canvas.scale.getX()), (int)(100 * canvas.scale.getY()));
        in_connector.setLocation((int)(5 * canvas.scale.getX()), (int)(45 * canvas.scale.getY()));
        in_connector.rescale();
        Image img = null;
        try {
            img = ImageIO.read(new File("imgs/plus.png"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        Image scaled = img.getScaledInstance((int)(30 * canvas.scale.getX()), (int)(30 * canvas.scale.getY()), Image.SCALE_FAST);
        add_btn.setBounds((int)(255 * canvas.scale.getX()), (int)(45 * canvas.scale.getY()), (int)(30 * canvas.scale.getX()), (int)(30 * canvas.scale.getY()));
        add_btn.setIcon(new ImageIcon(scaled));
        int i = 0;
        for (AnswerPanel panel : answers) {
            panel.rescale(mod, new Point(0, 0));
            panel.setLocation(10, (int)((110 + i * 50) * canvas.scale.getY()));
            i++;
        }
    }
}
