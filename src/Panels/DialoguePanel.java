package Panels;

import Frames.MainWindow;
import Helpers.ComponentListener;
import Helpers.InConnector;
import Helpers.OutConnector;
import Managers.Conditional;
import Managers.Person;
import Nodes.DialogueNode;
import Nodes.Node;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.LinkedList;
import java.util.List;


/*
    This is a visual representation of a dialogue node. A character can be assigned so that it's properties can be
    used in conditionals.
 */
public class DialoguePanel extends NodePanel{

    private final JTextArea text_entry;
    private final JScrollPane pane;
    private final JComboBox<String> person_choice;
    private final JButton conditional_btn;
    private DialogueNode node;
    private List<ConditionalBlock> conditional_panels;

    private int person_id = -1;
    private boolean refreshing = false;

    /*
        This assembles the panel using a gridbag layout.
     */
    //TODO: Find a way so that the connectors are the same size physically as they are visually.
    public DialoguePanel(MainWindow window, Point start) {
        super(window);
        conditional_panels = new LinkedList<>();

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
        constraints.gridheight = 2;
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
        pane.setPreferredSize(new Dimension(170, 50));
        add(pane, constraints);

        constraints.gridx = 0;
        constraints.weightx = 0.3;
        constraints.weighty = 0.3;
        constraints.gridheight = 2;
        constraints.fill = GridBagConstraints.BOTH;
        in_connector = new InConnector(this, window);
        add(in_connector, constraints);

        constraints.gridx = 2;
        out_connector = new OutConnector(this, window);
        add(out_connector, constraints);

        constraints.gridy = 3;
        constraints.gridx = 1;
        conditional_btn = new JButton("Add conditional");
        conditional_btn.setPreferredSize(new Dimension(30, 30));
        conditional_btn.addActionListener(e -> createConditional(null));
        add(conditional_btn, constraints);

        rescale();
    }

    private void createConditional(Conditional conditional) {
        if (conditional == null) {
            conditional = node.addConditional();
        }
        ConditionalBlock block = new ConditionalBlock(window, conditional, this);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 5 + conditional_panels.size();
        constraints.weightx = 1;
        constraints.weighty = 0.3;
        constraints.gridwidth = 3;
        add(block, constraints);
        rescale();
        conditional_panels.add(block);
    }

    @Override
    public void setNode(Node node) {
        this.node = (DialogueNode) node;
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
        This resizes the font and the panel to fit under the new zoom level.
        //TODO: add border resizing.
     */
    @Override
    public void rescale(float mod, Point source) {
        super.rescale(mod, source);
        setSize((int)(350 * canvas.scale.getX()), (int)(200 * canvas.scale.getY() + 30 * conditional_panels.size()));
        person_choice.setFont(window.main_font.deriveFont((float)(20 * canvas.scale.getX())));
        text_entry.setFont(window.main_font.deriveFont((float)(20 * canvas.scale.getX())));
        conditional_btn.setFont(window.main_font.deriveFont((float)(20 * canvas.scale.getX())));
        in_connector.rescale();
        out_connector.rescale();
        for (ConditionalBlock block : conditional_panels) {
            block.rescale();
        }
    }
}
