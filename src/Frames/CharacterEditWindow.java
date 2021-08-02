package Frames;

import Helpers.Property;
import Managers.Character;
import Managers.Project;
import Managers.TreeKeeper;
import Panels.PropertyBlock;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.ImageFilter;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class CharacterEditWindow extends JDialog {

    private final TreeKeeper keeper;
    private final int id;
    private final Character character;

    private JLabel image;
    private JTextField name_entry;
    private String current_name;
    private final GridBagConstraints constraints = new GridBagConstraints();

    public CharacterEditWindow(int id, TreeKeeper keeper) {
        this.keeper = keeper;
        this.id = id;
        this.character = keeper.getCharacter(id);
        setModal (true);
        setAlwaysOnTop (true);
        setModalityType (ModalityType.APPLICATION_MODAL);
        setLayout(new GridBagLayout());
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;
        constraints.gridx = 1;
        constraints.gridy = 0;

        Image img = character.image;
        if (img == null) {
            try {
                img = ImageIO.read(new File("imgs/default_char.png"));
            } catch (IOException ex) {}
        }
        float height = img.getHeight(this);
        float width = img.getWidth(this);
        if (width > height) {
            height = height/width * 400f;
            width = 400;
        } else {
            width = width/height * 400f;
            height = 400;
        }
        Image scaled = img.getScaledInstance((int)(width), (int)(height), Image.SCALE_SMOOTH);
        ImageIcon icon = new ImageIcon(scaled);
        image = new JLabel(icon);
        image.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectNewIcon();
            }
        });
        add(image, constraints);

        constraints.gridy = 1;
        name_entry = new JTextField();
        name_entry.setFont(name_entry.getFont().deriveFont(24f));
        name_entry.setText(character.name);
        name_entry.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                checkName();
            }
        });
        current_name = character.name;
        add(name_entry, constraints);

        constraints.gridy = 2;
        JButton property_btn = new JButton("Add Property");
        property_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addProperty(null);
            }
        });
        property_btn.setFont(property_btn.getFont().deriveFont(20f));
        add(property_btn, constraints);

        for (Property property : character.properties.values()) {
            addProperty(property);
        }
    }

    private void selectNewIcon() {
        final JFileChooser chooser = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("Images", "jpg", "png", "jpeg");
        chooser.setFileFilter(filter);
        int result = chooser.showOpenDialog(this);
        File file = null;
        if (result == JFileChooser.APPROVE_OPTION) {
            file = chooser.getSelectedFile();
        } else {
            return;
        }
        try {
            Image img = ImageIO.read(file);
            character.image = img;
            character.img_name = file.getName();
            float height = img.getHeight(this);
            float width = img.getWidth(this);
            if (width > height) {
                height = height/width * 400f;
                width = 400;
            } else {
                width = width/height * 400f;
                height = 400;
            }
            Image scaled = img.getScaledInstance((int)(width), (int)(height), Image.SCALE_SMOOTH);
            ImageIcon icon = new ImageIcon(scaled);
            image.setIcon(icon);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error setting image", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addProperty(Property property) {
        if (property != null) {
            constraints.gridy++;
            add(new PropertyBlock(property, character, this), constraints);
            return;
        }
        while (true) {
            String name = (String) JOptionPane.showInputDialog(
                   this,
                   "Enter the name of the property.",
                   "Property name",
                   JOptionPane.PLAIN_MESSAGE,
                   null,
                   null,
                   ""
            );
            if (name == null) {
                return;
            }
            name = name.trim();
            if (name.length() == 0) {
                JOptionPane.showMessageDialog(this, "Invalid name", "The name you have entered is too short", JOptionPane.ERROR_MESSAGE);
            } else if (name.contains(" ")) {
                JOptionPane.showMessageDialog(this, "Invalid name", "The name cannot contain spaces", JOptionPane.ERROR_MESSAGE);
            } else {
                constraints.gridy++;
                property = character.addProperty(name);
                if (property == null) {
                    JOptionPane.showMessageDialog(this, "Invalid name", "Cannot have duplicate property names", JOptionPane.ERROR_MESSAGE);
                    continue;
                }
                add(new PropertyBlock(property, character, this), constraints);
                break;
            }
        }
        pack();
    }

    public void remove_property(PropertyBlock block) {
        remove(block);
        revalidate();
        repaint();
        pack();
    }

    private void checkName() {
        String new_name = name_entry.getText();
        if (new_name.length() != 0) {
            current_name = new_name;
        }
        character.name = current_name;
        name_entry.setText(current_name);
    }
}
