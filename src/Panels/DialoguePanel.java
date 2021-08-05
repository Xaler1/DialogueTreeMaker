package Panels;

import Frames.MainWindow;
import Helpers.ComponentListener;
import Helpers.InConnector;
import Helpers.OutConnector;
import Managers.Person;
import Nodes.DialogueNode;
import Nodes.Node;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class DialoguePanel extends NodePanel{

    private final JTextArea text_entry;
    private final JScrollPane pane;
    private final JComboBox<String> person_choice;
    private DialogueNode node;

    private int person_id = -1;
    private boolean refreshing = false;

    //TODO: Find a way so that the connectors are the same size as they are visually.
    public DialoguePanel(MainWindow window, Point start) {
        super(window);
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.9;
        constraints.weighty = 0.1;
        constraints.anchor = GridBagConstraints.CENTER;

        setLocation(start.x, start.y);
        setBorder(BorderFactory.createMatteBorder(5, 2, 2, 2, Color.yellow));
        addMouseListener(new ComponentListener(window, this));

        person_choice = new JComboBox<>();
        person_choice.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (refreshing) return;
                Person person = keeper.getPersonByName((String)(person_choice.getSelectedItem()));
                node.setPerson(person);
                if (person == null) return;
                person_id = person.id;
            }
        });
        person_choice.setPreferredSize(new Dimension(190, 10));
        add(person_choice, constraints);

        constraints.gridy = 1;
        constraints.gridheight = 3;
        constraints.weighty = 0.5;
        text_entry = new JTextArea("Hello world!");
        text_entry.setLineWrap(true);
        text_entry.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                node.setDialogueText(text_entry.getText());
            }
        });
        pane = new JScrollPane(text_entry);
        pane.setPreferredSize(new Dimension(170, 50));
        add(pane, constraints);

        constraints.gridx = 0;
        constraints.weightx = 0.3;
        constraints.weighty = 0.3;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.BOTH;
        in_connector = new InConnector(this, window);
        add(in_connector, constraints);

        constraints.gridx = 2;
        out_connector = new OutConnector(this, window);
        add(out_connector, constraints);

        rescale(1, new Point(0, 0));
    }

    @Override
    public void setNode(Node node) {
        this.node = (DialogueNode) node;
        refresh();
    }

    @Override
    public void refresh() {
        refreshing = true;
        boolean contains_old = false;
        person_choice.removeAllItems();
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

    @Override
    public void rescale(float mod, Point source) {
        super.rescale(mod, source);
        setSize((int)(300 * canvas.scale.getX()), (int)(150 * canvas.scale.getY()));
        person_choice.setFont(window.main_font.deriveFont((float)(20 * canvas.scale.getX())));
        //person_choice.setBounds((int)(40 * canvas.scale.getX()), (int)(10 * canvas.scale.getY()), (int)(210 * canvas.scale.getX()), (int)(30 * canvas.scale.getY()));
        text_entry.setFont(window.main_font.deriveFont((float)(20 * canvas.scale.getX())));
        //pane.setBounds((int)(40 * canvas.scale.getX()), (int)(40 * canvas.scale.getY()), (int)(210 * canvas.scale.getX()), (int)(100 * canvas.scale.getY()));
        //in_connector.setLocation((int)(5 * canvas.scale.getX()), (int)(45 * canvas.scale.getY()));
        in_connector.rescale();
        //out_connector.setLocation((int)(255 * canvas.scale.getX()), (int)(45 * canvas.scale.getY()));
        out_connector.rescale();
    }
}
