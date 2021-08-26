package Panels;

import Frames.MainWindow;
import Helpers.ComponentListener;
import Helpers.InConnector;
import Managers.Graph;
import Managers.Person;
import Nodes.AnswerNode;
import Nodes.DialogueNode;
import Nodes.Node;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
    This is a visual representation of a choice node. Conceptually it is no different from the dialogue node, however
    visually it does not have an out connector. Instead, it can store multiple answer nodes inside.
 */
public class ChoicePanel extends NodePanel {
    private List<AnswerPanel> answers;
    private final ChoicePanel self;

    private final JTextArea text_entry;
    private final JComboBox<String> person_choice;
    private final JScrollPane pane;
    private final JLabel add_btn;
    private DialogueNode node;

    private int person_id = -1;
    private boolean refreshing = false;

    /*
        This assembles the panel.
     */
    public ChoicePanel(MainWindow window, Graph graph, Point start) {
        super(window, graph);
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.9;
        constraints.weighty = 0.1;
        answers = new ArrayList<>();
        self = this;

        setLocation(start);
        loadBackground("purple");
        addMouseListener(new ComponentListener(window, this));

        person_choice = new JComboBox<>();
        person_choice.addItem("None");
        person_choice.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (refreshing) return;
                Person person = keeper.getPersonByName((String)(person_choice.getSelectedItem()));
                node.setPerson(person);
                for (AnswerPanel answer : answers) {
                    answer.setPerson(person);
                }
                if (person == null) return;
                person_id = person.id;
            }
        });
        person_choice.setPreferredSize(new Dimension(190, 10));
        add(person_choice, constraints);

        constraints.gridy = 1;
        constraints.weighty = 0.5;
        text_entry = new JTextArea("Hello world!");
        text_entry.setLineWrap(true);
        text_entry.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                node.setDialogueText(text_entry.getText());
            }
        });
        pane = new JScrollPane(text_entry);
        pane.setPreferredSize(new Dimension(190, 50));
        add(pane, constraints);

        constraints.gridx = 0;
        constraints.weightx = 0.3;
        constraints.weighty = 0.3;
        in_connector = new InConnector(this, window);
        add(in_connector, constraints);

        constraints.gridx = 2;
        add_btn = new JLabel();
        add_btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                createAnswer(null);
            }
        });
        add_btn.setPreferredSize(new Dimension(30, 30));
        add_btn.setHorizontalAlignment(SwingConstants.CENTER);
        add(add_btn, constraints);
        rescale(1, new Point(0, 0));
    }


    @Override
    public void setNode(Node node) {
        this.node = (DialogueNode) node;
        for (Node child : node.getChildren()) {
            createAnswer((AnswerNode) child);
        }
        text_entry.setText(this.node.getDialogueText());
        if (this.node.getPerson() != null) {
            person_choice.setSelectedItem(this.node.getPerson().name);
        }
        refresh();
    }

    /*
        This reloads the current list of people from the project and adds the to the combo box. Updating the current
        person if necessary, e.g. if it has been removed.
     */
    @Override
    public void refresh() {
        refreshing = true;
        boolean contains_old = false;
        person_choice.removeAllItems();
        person_choice.addItem("None");
        for (Person person : keeper.getPeople()) {
            person_choice.addItem(person.name);
            if (person.id == person_id) {
                person_choice.setSelectedItem(person.name);
                contains_old = true;
            }
        }
        if (!contains_old) {
            node.removeCharacter();
            person_id = -1;
        }
        refreshing = false;
    }

    /*
        This adds a new answer panel visually and connects this to a new dialogue node conceptually.
     */
    public NodePanel createAnswer(AnswerNode node) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridwidth = 3;
        constraints.gridy = answers.size() + 2;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.9;
        constraints.weighty = 0.4;

        AnswerPanel answer_panel = null;
        if (node == null) {
            answer_panel = new AnswerPanel(window, this, graph);
            graph.addAnswerNode(answer_panel, "Hello World!");
            graph.createRelation(this, answer_panel);
        } else {
            answer_panel = new AnswerPanel(window, this, graph);
            graph.assignNodePanel(answer_panel, node.getId());
        }
        answer_panel.setPreferredSize(new Dimension(190, 50));
        answers.add(answer_panel);
        answer_panel.setNode(graph.getNode(answer_panel));
        answer_panel.setPerson(this.node.getPerson());
        add(answer_panel, constraints);
        rescale(1, new Point(0, 0));
        window.current_canvas.components.add(answer_panel);
        return answer_panel;
    }

    /*
        This rescales the font as well as the panel to fit in all the answer panels under the new zoom level.
        //TODO: add border resizing.
     */
    @Override
    public void rescale(float mod, Point source) {
        super.rescale(mod, source);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 3;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.BOTH;
        int size = 150;
        for (AnswerPanel panel : answers) {
            constraints.weighty = 0.4 + 0.2 * panel.conditional_panels.size();
            size += 90;
            size += 40 * panel.conditional_panels.size();
            remove(panel);
            panel.setPreferredSize(new Dimension(190, (int) ((40 + 20 * panel.conditional_panels.size()) * canvas.scale.getY())));
            add(panel, constraints);
            constraints.gridy++;
        }
        this.setSize((int)(350 * canvas.scale.getX()), (int)(size * canvas.scale.getY()));
        person_choice.setFont(window.main_font.deriveFont((float)(20 * canvas.scale.getX())));
        text_entry.setFont(window.main_font.deriveFont((float)(20 * canvas.scale.getX())));
        in_connector.rescale();
        Image img = null;
        try {
            img = ImageIO.read(new File("imgs/plus.png"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        Image scaled = img.getScaledInstance((int)(30 * canvas.scale.getX()), (int)(30 * canvas.scale.getY()), Image.SCALE_FAST);
        add_btn.setIcon(new ImageIcon(scaled));
    }

    @Override
    public void removeAllInNodeChildren() {
        for (AnswerPanel child : answers) {
            child.removeAllInNodeChildren();
            canvas.removeOutConnections(child);
            graph.removeNode(child);
        }
    }
}
