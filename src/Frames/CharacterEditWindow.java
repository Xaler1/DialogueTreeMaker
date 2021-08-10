package Frames;

import Helpers.Property;
import Managers.Person;
import Managers.TreeKeeper;
import Panels.PropertyBlock;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

/*
    This is a window for editing a specific character. The user can choose a picture, change the name and also add
    custom properties to the character.
 */
public class CharacterEditWindow extends JDialog {

    private final TreeKeeper keeper;
    private final int id;
    private final Person person;

    private JLabel image;
    private JTextField name_entry;
    private final GridBagConstraints constraints = new GridBagConstraints();

    /*
        This assembles the initial window - with a picture that can be clicked for changing the image and a text field
        for editing the name. Also, if the character has any properties then they are added below in sequence.
        All the changes to the name, picture and properties are in real time.
     */
    public CharacterEditWindow(int id, TreeKeeper keeper) {
        this.keeper = keeper;
        this.id = id;
        this.person = keeper.getPerson(id);
        setModal (true);
        setAlwaysOnTop (true);
        setModalityType (ModalityType.APPLICATION_MODAL);
        setLayout(new GridBagLayout());
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;
        constraints.gridx = 1;
        constraints.gridy = 0;

        Image img = person.image;
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
        name_entry.setText(person.name);
        name_entry.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                checkName();
            }
        });
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

        for (Property property : person.properties.values()) {
            addProperty(property);
        }
    }

    /*
        This processes the selection of a new image for the character. It opens a file chooser with a filter for images
        only and then attempts to set the selected image - resizing it to it in the window without stretching it.
     */
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
            person.image = img;
            person.img_name = file.getName();
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

    /*
        This adds a new property to the character and a visual property block to the window.
        It prompts the user to enter a default name for the property (it only allows non-empty names with no spaces and
        which are not duplicates)
     */
    private void addProperty(Property property) {
        if (property != null) {
            constraints.gridy++;
            add(new PropertyBlock(property, person, this), constraints);
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
                property = person.addProperty(name);
                if (property == null) {
                    JOptionPane.showMessageDialog(this, "Invalid name", "Cannot have duplicate property names", JOptionPane.ERROR_MESSAGE);
                    continue;
                }
                add(new PropertyBlock(property, person, this), constraints);
                break;
            }
        }
        pack();
    }

    /*
        This removes the visual property block from the window and asks it to resize to remove any empty space.
     */
    public void remove_property(PropertyBlock block) {
        remove(block);
        revalidate();
        repaint();
        pack();
    }

    /*
        This checks that he name entered is valid i.e. - that it is not empty and not a duplicate. If it is valid
        then the person's name is updated. Otherwise, the name entered is reverted.
     */
    private void checkName() {
        String new_name = name_entry.getText().strip();
        if (new_name.equals(person.name)) return;
        if (new_name.length() == 0) {
            JOptionPane.showMessageDialog(this, "The name cannot be empty", "Invalid name", JOptionPane.ERROR_MESSAGE);
            name_entry.setText(person.name);
        } else if (!keeper.isPersonNameValid(new_name)) {
            JOptionPane.showMessageDialog(this, "People cannot have duplicate names", "Invalid name", JOptionPane.ERROR_MESSAGE);
            name_entry.setText(person.name);
        } else {
            person.setName(new_name);
        }
    }
}
