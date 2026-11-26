
import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

class Contact {
    int id;
    String name, phone;

    Contact(int id, String name, String phone) {
        this.id = id;
        this.name = name;
        this.phone = phone;
    }
}

public class RunningModel extends JFrame {

    JTextField idField, nameField, phoneField;
    JTable table;
    DefaultTableModel model;

    LinkedList<Contact> contacts = new LinkedList<>();
    HashMap<Integer, Contact> idMap = new HashMap<>();
    HashMap<String, Contact> phoneMap = new HashMap<>();

    int nextId = 101;
    final String FILE_NAME = "contacts.txt";

    RunningModel() {

        setTitle("Contact Management");
        setSize(1100, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel main = new JPanel(new BorderLayout(30, 30));
        main.setBorder(
            BorderFactory.createEmptyBorder(30, 35, 30, 35)
        );
        main.setBackground(new Color(238, 238, 238));

        // HEADING
        JLabel heading = new JLabel("Contact Management");
        heading.setFont(new Font("Arial", Font.BOLD, 28));
        heading.setForeground(new Color(190, 25, 45));

        main.add(heading, BorderLayout.NORTH);

        // LEFT PANEL
        JPanel left = new JPanel(new BorderLayout(15, 25));
        left.setBackground(new Color(238, 238, 238));

        // FORM PANEL
        JPanel form = new JPanel(new GridLayout(3, 2, 15, 25));
        form.setBackground(new Color(238, 238, 238));

        idField = new JTextField();
        nameField = new JTextField();
        phoneField = new JTextField();

        idField.setEditable(false);

        addRow(form, "ID", idField);
        addRow(form, "Name", nameField);
        addRow(form, "Phone Number", phoneField);

        // SMALL BUTTONS
        JPanel buttons = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 12, 10)
        );
        buttons.setBackground(new Color(238, 238, 238));

        JButton save = new JButton("Save");
        JButton update = new JButton("Update");
        JButton delete = new JButton("Delete");
        JButton clear = new JButton("Clear");

        JButton[] allButtons = {
            save, update, delete, clear
        };

        Dimension buttonSize = new Dimension(115, 40);

        for (JButton b : allButtons) {
            b.setPreferredSize(buttonSize);
            b.setFont(new Font("Arial", Font.PLAIN, 15));
            b.setFocusPainted(false);
            buttons.add(b);
        }

        left.add(form, BorderLayout.NORTH);
        left.add(buttons, BorderLayout.CENTER);

        // RIGHT TABLE
        model = new DefaultTableModel(
            new String[]{"ID", "Name", "Phone Number"}, 0
        ) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(32);
        table.setFont(new Font("Arial", Font.PLAIN, 15));

        table.getTableHeader().setFont(
            new Font("Arial", Font.BOLD, 15)
        );

        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(550, 380));

        // CONTENT PANEL
        JPanel content = new JPanel(
            new GridLayout(1, 2, 40, 0)
        );

        content.setBackground(new Color(238, 238, 238));
        content.add(left);
        content.add(scroll);

        main.add(content, BorderLayout.CENTER);
        add(main);

        // BUTTON ACTIONS
        save.addActionListener(e -> saveContact());
        update.addActionListener(e -> updateContact());
        delete.addActionListener(e -> deleteContact());
        clear.addActionListener(e -> clearFields());

        // TABLE ROW SELECTION
        table.getSelectionModel().addListSelectionListener(e -> {

            if (!e.getValueIsAdjusting()) {

                int row = table.getSelectedRow();

                if (row >= 0) {
                    idField.setText(
                        model.getValueAt(row, 0).toString()
                    );

                    nameField.setText(
                        model.getValueAt(row, 1).toString()
                    );

                    phoneField.setText(
                        model.getValueAt(row, 2).toString()
                    );
                }
            }
        });

        loadContacts();
        refreshTable();
    }

    // ADD FORM ROW
    void addRow(JPanel panel, String text, JTextField field) {

        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 19));

        field.setFont(new Font("Arial", Font.PLAIN, 17));
        field.setPreferredSize(new Dimension(300, 38));

        panel.add(label);
        panel.add(field);
    }

    // SAVE CONTACT
    void saveContact() {

        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();

        if (name.isEmpty() || phone.isEmpty()) {
            message("Please enter Name and Phone Number.");
            return;
        }

        if (!phone.matches("[0-9]{10}")) {
            message("Enter exactly 10 digits.");
            return;
        }

        if (phoneMap.containsKey(phone)) {
            message("Phone number already exists!");
            return;
        }

        Contact c = new Contact(nextId++, name, phone);

        contacts.add(c);
        idMap.put(c.id, c);
        phoneMap.put(c.phone, c);

        refreshTable();
        saveToFile();
        clearFields();

        message("Contact Saved Successfully!");
    }

    // UPDATE CONTACT
    void updateContact() {

        if (idField.getText().isEmpty()) {
            message("Please select a contact from table.");
            return;
        }

        int id = Integer.parseInt(idField.getText());
        Contact c = idMap.get(id);

        if (c == null) {
            message("Contact Not Found!");
            return;
        }

        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();

        if (name.isEmpty() || !phone.matches("[0-9]{10}")) {
            message("Enter valid Name and 10 digit Phone.");
            return;
        }

        Contact existing = phoneMap.get(phone);

        if (existing != null && existing != c) {
            message("Phone number already exists!");
            return;
        }

        phoneMap.remove(c.phone);

        c.name = name;
        c.phone = phone;

        phoneMap.put(c.phone, c);

        refreshTable();
        saveToFile();
        clearFields();

        message("Contact Updated Successfully!");
    }

    // DELETE CONTACT
    void deleteContact() {

        if (idField.getText().isEmpty()) {
            message("Please select a contact from table.");
            return;
        }

        int id = Integer.parseInt(idField.getText());
        Contact c = idMap.get(id);

        if (c == null) {
            message("Contact Not Found!");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete this contact?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {

            contacts.remove(c);
            idMap.remove(c.id);
            phoneMap.remove(c.phone);

            refreshTable();
            saveToFile();
            clearFields();

            message("Contact Deleted Successfully!");
        }
    }

    // CLEAR FIELDS
    void clearFields() {

        idField.setText("");
        nameField.setText("");
        phoneField.setText("");

        table.clearSelection();
    }

    // REFRESH TABLE
    void refreshTable() {

        model.setRowCount(0);

        ArrayList<Contact> sorted = new ArrayList<>(contacts);

        sorted.sort(
            Comparator.comparing(
                c -> c.name,
                String.CASE_INSENSITIVE_ORDER
            )
        );

        for (Contact c : sorted) {
            model.addRow(
                new Object[]{c.id, c.name, c.phone}
            );
        }
    }

    // LOAD CONTACTS FROM FILE
    void loadContacts() {

        File file = new File(FILE_NAME);

        if (!file.exists()) return;

        try (BufferedReader br =
                 new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = br.readLine()) != null) {

                String[] data = line.split(",", -1);

                if (data.length != 3) continue;

                try {
                    int id = Integer.parseInt(data[0]);

                    String name = data[1];
                    String phone = data[2];

                    if (name.isEmpty()
                        || !phone.matches("[0-9]{10}")
                        || phoneMap.containsKey(phone)
                        || idMap.containsKey(id)) {
                        continue;
                    }

                    Contact c = new Contact(id, name, phone);

                    contacts.add(c);
                    idMap.put(id, c);
                    phoneMap.put(phone, c);

                    nextId = Math.max(nextId, id + 1);

                } catch (NumberFormatException ignored) {
                    // Skip invalid records
                }
            }

        } catch (IOException e) {
            message("Loading Error: " + e.getMessage());
        }
    }

    // SAVE CONTACTS TO FILE
    void saveToFile() {

        try (BufferedWriter bw =
                 new BufferedWriter(new FileWriter(FILE_NAME))) {

            for (Contact c : contacts) {

                bw.write(
                    c.id + "," + c.name + "," + c.phone
                );

                bw.newLine();
            }

        } catch (IOException e) {
            message("Saving Error: " + e.getMessage());
        }
    }

    // MESSAGE
    void message(String text) {
        JOptionPane.showMessageDialog(this, text);
    }

    // MAIN METHOD
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            RunningModel app = new RunningModel();
            app.setVisible(true);

        });
    }
}
