import java.io.*;
import java.util.*;

// Contact class
class Contact {
    int id;
    String name;
    String phone;
    String email;

    Contact(int id, String name, String phone, String email) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    public String toString() {
        return "C" + id + " | " + name + " | " + phone + " | " + email;
    }
}

// Linked List Node
class Node {
    Contact data;
    Node next;

    Node(Contact data) {
        this.data = data;
        this.next = null;
    }
}

// Custom Linked List
class ContactList {
    Node head, tail;

    void append(Contact c) {
        Node newNode = new Node(c);

        if (head == null) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
    }

    void remove(Contact c) {
        Node current = head;
        Node previous = null;

        while (current != null) {
            if (current.data == c) {
                if (previous == null) {
                    head = current.next;
                } else {
                    previous.next = current.next;
                }

                if (current == tail) {
                    tail = previous;
                }
                return;
            }

            previous = current;
            current = current.next;
        }
    }

    ArrayList<Contact> getAll() {
        ArrayList<Contact> contacts = new ArrayList<>();
        Node current = head;

        while (current != null) {
            contacts.add(current.data);
            current = current.next;
        }

        return contacts;
    }
}

// BST Node
class BSTNode {
    String key;
    ArrayList<Contact> contacts;
    BSTNode left, right;

    BSTNode(Contact c) {
        key = c.name;
        contacts = new ArrayList<>();
        contacts.add(c);
    }
}

// Binary Search Tree
class NameBST {
    BSTNode root;

    void insert(Contact c) {
        root = insertRec(root, c);
    }

    BSTNode insertRec(BSTNode node, Contact c) {
        if (node == null) {
            return new BSTNode(c);
        }

        int compare = c.name.compareToIgnoreCase(node.key);

        if (compare < 0) {
            node.left = insertRec(node.left, c);
        } else if (compare > 0) {
            node.right = insertRec(node.right, c);
        } else {
            node.contacts.add(c);
        }

        return node;
    }

    Contact search(String name) {
        BSTNode current = root;

        while (current != null) {
            int compare = name.compareToIgnoreCase(current.key);

            if (compare == 0) {
                return current.contacts.get(0);
            } else if (compare < 0) {
                current = current.left;
            } else {
                current = current.right;
            }
        }

        return null;
    }

    void delete(Contact c) {
        root = deleteRec(root, c.name, c);
    }

    BSTNode deleteRec(BSTNode node, String name, Contact c) {
        if (node == null) {
            return null;
        }

        int compare = name.compareToIgnoreCase(node.key);

        if (compare < 0) {
            node.left = deleteRec(node.left, name, c);
        } else if (compare > 0) {
            node.right = deleteRec(node.right, name, c);
        } else {
            node.contacts.remove(c);

            if (!node.contacts.isEmpty()) {
                return node;
            }

            if (node.left == null) return node.right;
            if (node.right == null) return node.left;

            BSTNode successor = findMin(node.right);
            node.key = successor.key;
            node.contacts = successor.contacts;
            node.right = deleteMin(node.right);
        }

        return node;
    }

    BSTNode findMin(BSTNode node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    BSTNode deleteMin(BSTNode node) {
        if (node.left == null) {
            return node.right;
        }

        node.left = deleteMin(node.left);
        return node;
    }
}

// Main Contact Manager
public class ContactManager {

    static Scanner sc = new Scanner(System.in);

    static ContactList list = new ContactList();
    static NameBST nameIndex = new NameBST();

    static HashMap<String, Contact> phoneMap = new HashMap<>();

    static int nextId = 101;

    static final String FILE_NAME = "contacts.txt";

    // Validate phone number
    static boolean isValidPhone(String phone) {
        return phone.matches("[0-9]{10}");
    }

    // Validate text for file storage
    static boolean isValidText(String text) {
        return !text.contains(",") &&
               !text.contains("\n") &&
               !text.contains("\r");
    }

    // Add contact
    static void addContact() {
        System.out.print("Enter Name: ");
        String name = sc.nextLine().trim();

        System.out.print("Enter Phone: ");
        String phone = sc.nextLine().trim();

        System.out.print("Enter Email: ");
        String email = sc.nextLine().trim();

        if (name.isEmpty() || email.isEmpty() ||
            !isValidText(name) || !isValidText(email)) {
            System.out.println("Invalid contact details.");
            return;
        }

        if (!isValidPhone(phone)) {
            System.out.println("Invalid phone number. Enter exactly 10 digits.");
            return;
        }

        if (phoneMap.containsKey(phone)) {
            System.out.println("Duplicate phone number. Contact not added.");
            return;
        }

        Contact c = new Contact(nextId++, name, phone, email);

        list.append(c);
        phoneMap.put(phone, c);
        nameIndex.insert(c);

        System.out.println("Contact added successfully!");
        System.out.println(c);
    }

    // Search contact
    static void searchContact() {
        System.out.println("1. Search by Name");
        System.out.println("2. Search by Phone");
        System.out.print("Enter choice: ");

        String choice = sc.nextLine();

        Contact c = null;

        if (choice.equals("1")) {
            System.out.print("Enter Name: ");
            String name = sc.nextLine().trim();

            c = nameIndex.search(name);

        } else if (choice.equals("2")) {
            System.out.print("Enter Phone: ");
            String phone = sc.nextLine().trim();

            c = phoneMap.get(phone);

        } else {
            System.out.println("Invalid choice!");
            return;
        }

        if (c != null) {
            System.out.println("Contact Found:");
            System.out.println(c);
        } else {
            System.out.println("Contact Not Found!");
        }
    }

    // Update contact
    static void updateContact() {
        System.out.print("Enter current phone number: ");
        String phone = sc.nextLine().trim();

        Contact c = phoneMap.get(phone);

        if (c == null) {
            System.out.println("Contact Not Found!");
            return;
        }

        System.out.print("Enter New Name: ");
        String newName = sc.nextLine().trim();

        System.out.print("Enter New Phone: ");
        String newPhone = sc.nextLine().trim();

        System.out.print("Enter New Email: ");
        String newEmail = sc.nextLine().trim();

        if (newName.isEmpty() || newEmail.isEmpty() ||
            !isValidText(newName) || !isValidText(newEmail)) {
            System.out.println("Invalid contact details.");
            return;
        }

        if (!isValidPhone(newPhone)) {
            System.out.println("Invalid phone number!");
            return;
        }

        if (!newPhone.equals(phone) &&
            phoneMap.containsKey(newPhone)) {
            System.out.println("Duplicate phone number!");
            return;
        }

        // Update BST before changing the name
        nameIndex.delete(c);

        // Update HashMap if phone changes
        if (!newPhone.equals(phone)) {
            phoneMap.remove(phone);
            phoneMap.put(newPhone, c);
        }

        c.name = newName;
        c.phone = newPhone;
        c.email = newEmail;

        // Reinsert contact in BST
        nameIndex.insert(c);

        System.out.println("Contact Updated Successfully!");
        System.out.println(c);
    }

    // Delete contact
    static void deleteContact() {
        System.out.print("Enter Phone Number: ");
        String phone = sc.nextLine().trim();

        Contact c = phoneMap.get(phone);

        if (c == null) {
            System.out.println("Contact Not Found!");
            return;
        }

        list.remove(c);
        phoneMap.remove(phone);
        nameIndex.delete(c);

        System.out.println("Contact Deleted Successfully!");
    }

    // Display all contacts with sorting
    static void displayContacts() {
        ArrayList<Contact> contacts = list.getAll();

        if (contacts.isEmpty()) {
            System.out.println("No Contacts Available!");
            return;
        }

        System.out.println("1. Ascending Order");
        System.out.println("2. Descending Order");
        System.out.print("Enter choice: ");

        String choice = sc.nextLine();

        if (choice.equals("1")) {
            contacts.sort(
                Comparator.comparing(
                    c -> c.name,
                    String.CASE_INSENSITIVE_ORDER
                )
            );
        } else if (choice.equals("2")) {
            contacts.sort(
                Comparator.comparing(
                    (Contact c) -> c.name,
                    String.CASE_INSENSITIVE_ORDER
                ).reversed()
            );
        } else {
            System.out.println("Invalid choice!");
            return;
        }

        System.out.println("\n===== CONTACT LIST =====");

        for (Contact c : contacts) {
            System.out.println(c);
        }
    }

    // Load contacts from file
    static void loadFromFile() {
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            return;
        }

        try (BufferedReader br =
                 new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",", -1);

                if (data.length != 4) continue;

                try {
                    int id = Integer.parseInt(data[0]);

                    String name = data[1];
                    String phone = data[2];
                    String email = data[3];

                    if (name.isEmpty() || email.isEmpty() ||
                        !isValidPhone(phone) ||
                        phoneMap.containsKey(phone)) {
                        continue;
                    }

                    Contact c = new Contact(id, name, phone, email);

                    list.append(c);
                    phoneMap.put(phone, c);
                    nameIndex.insert(c);

                    nextId = Math.max(nextId, id + 1);

                } catch (NumberFormatException e) {
                    System.out.println("Skipping invalid contact record.");
                }
            }

            System.out.println("Contacts loaded successfully.");

        } catch (IOException e) {
            System.out.println("Error loading contacts: " + e.getMessage());
        }
    }

    // Save contacts to file
    static void saveToFile() {
        try (BufferedWriter bw =
                 new BufferedWriter(new FileWriter(FILE_NAME))) {

            Node current = list.head;

            while (current != null) {
                Contact c = current.data;

                bw.write(c.id + "," + c.name + "," +
                         c.phone + "," + c.email);

                bw.newLine();
                current = current.next;
            }

            System.out.println("Contacts saved to contacts.txt.");

        } catch (IOException e) {
            System.out.println("Error saving contacts: " + e.getMessage());
        }
    }

    // Main method
    public static void main(String[] args) {

        loadFromFile();

        while (true) {

            System.out.println("\n================================");
            System.out.println("     CONTACT MANAGER");
            System.out.println("       WITH FAST SEARCH");
            System.out.println("================================");

            System.out.println("1. Add Contact");
            System.out.println("2. Search Contact");
            System.out.println("3. Update Contact");
            System.out.println("4. Delete Contact");
            System.out.println("5. View All Contacts");
            System.out.println("6. Exit");

            System.out.print("Enter Your Choice: ");

            String choice = sc.nextLine();

            switch (choice) {

                case "1":
                    addContact();
                    break;

                case "2":
                    searchContact();
                    break;

                case "3":
                    updateContact();
                    break;

                case "4":
                    deleteContact();
                    break;

                case "5":
                    displayContacts();
                    break;

                case "6":
                    saveToFile();
                    System.out.println("Thank You! Goodbye.");
                    sc.close();
                    return;

                default:
                    System.out.println("Invalid Choice! Try Again.");
            }
        }
    }
}
