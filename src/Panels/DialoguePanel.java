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

    public DialoguePanel(MainWindow window, Point start) {
        super(window);

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
        add(person_choice);

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
        add(pane);

        in_connector = new InConnector(this, window);
        add(in_connector);

        out_connector = new OutConnector(this, window);
        add(out_connector);

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
        person_choice.setBounds((int)(40 * canvas.scale.getX()), (int)(10 * canvas.scale.getY()), (int)(210 * canvas.scale.getX()), (int)(30 * canvas.scale.getY()));
        text_entry.setFont(window.main_font.deriveFont((float)(20 * canvas.scale.getX())));
        pane.setBounds((int)(40 * canvas.scale.getX()), (int)(40 * canvas.scale.getY()), (int)(210 * canvas.scale.getX()), (int)(100 * canvas.scale.getY()));
        in_connector.setLocation((int)(5 * canvas.scale.getX()), (int)(45 * canvas.scale.getY()));
        in_connector.rescale();
        out_connector.setLocation((int)(255 * canvas.scale.getX()), (int)(45 * canvas.scale.getY()));
        out_connector.rescale();
    }
}
