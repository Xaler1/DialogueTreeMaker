package Panels;

import Frames.MainWindow;
import Helpers.ComponentListener;
import Helpers.InConnector;
import Helpers.OutConnector;
import Managers.Character;
import Nodes.DialogueNode;
import Nodes.Node;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DialoguePanel extends NodePanel{

    private final JTextArea text_entry;
    private final JScrollPane pane;
    private final JComboBox<String> character_choice;
    private DialogueNode node;

    public DialoguePanel(MainWindow window, Point start) {
        super(window);

        setLocation(start.x, start.y);
        setBorder(BorderFactory.createMatteBorder(5, 2, 2, 2, Color.yellow));
        addMouseListener(new ComponentListener(window, this));

        character_choice = new JComboBox<>();
        character_choice.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                character_choice.removeAllItems();
                for (Character character : keeper.getCharacters()) {
                    character_choice.addItem(character.name);
                }
            }
        });
        add(character_choice);

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
    }

    @Override
    public void rescale(float mod, Point source) {
        super.rescale(mod, source);
        setSize((int)(300 * canvas.scale.getX()), (int)(150 * canvas.scale.getY()));
        character_choice.setFont(window.main_font.deriveFont((float)(20 * canvas.scale.getX())));
        character_choice.setBounds((int)(40 * canvas.scale.getX()), (int)(10 * canvas.scale.getY()), (int)(210 * canvas.scale.getX()), (int)(30 * canvas.scale.getY()));
        text_entry.setFont(window.main_font.deriveFont((float)(20 * canvas.scale.getX())));
        pane.setBounds((int)(40 * canvas.scale.getX()), (int)(40 * canvas.scale.getY()), (int)(210 * canvas.scale.getX()), (int)(100 * canvas.scale.getY()));
        in_connector.setLocation((int)(5 * canvas.scale.getX()), (int)(45 * canvas.scale.getY()));
        in_connector.rescale();
        out_connector.setLocation((int)(255 * canvas.scale.getX()), (int)(45 * canvas.scale.getY()));
        out_connector.rescale();
    }
}
