import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

public class DataManager extends JFrame {
    private final JTextArea data;
    private final List<DictionaryWord> allWords;

    public DataManager() {
        setTitle("Data Manager");
        setSize(750, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Menu
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        JMenuItem homeMenuItem = new JMenuItem("Home");
        JMenuItem favoritesMenuItem = new JMenuItem("Favorites");
        JMenuItem dataManagerMenuItem = new JMenuItem("Data Manager");
        menu.add(homeMenuItem);
        menu.add(favoritesMenuItem);
        menu.add(dataManagerMenuItem);
        menuBar.add(menu);
        setJMenuBar(menuBar);

        JPanel upperPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        upperPanel.setBorder(BorderFactory.createEmptyBorder(10, 21, 10, 21));
        upperPanel.setBackground(Color.decode("#D4344D"));
        JLabel title = new JLabel("Data");
        title.setForeground(Color.WHITE);
        title.setFont(Util.loadPoppins(24, "regular"));
        upperPanel.add(title);
        add(upperPanel, BorderLayout.NORTH);

        allWords = new ArrayList<>();
        Util.loadWords(allWords);

        data = new JTextArea();
        data.setFont(Util.loadPoppins(24, "regular"));
        data.setForeground(Color.decode("#505562"));
        data.setLineWrap(true);
        data.setBorder(new EmptyBorder(10, 10, 10, 10));
        loadTextArea();

        JButton saveButton = new JButton("Save");
        saveButton.setBackground(Color.decode("#D4344D"));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFont(Util.loadPoppins(20, "regular"));
        saveButton.setBorder(null);
        saveButton.setFocusPainted(false);
        saveButton.setPreferredSize(new Dimension(-1, 47));
        saveButton.addActionListener(_ -> saveWords());

        JPanel lowerPanel = new JPanel();
        lowerPanel.setBackground(Color.decode("#F7F9F9"));
        lowerPanel.setBorder(BorderFactory.createEmptyBorder(27, 27, 27, 27));
        lowerPanel.setLayout(new BorderLayout(0, 27));
        lowerPanel.add(new JScrollPane(data), BorderLayout.CENTER);
        lowerPanel.add(saveButton, BorderLayout.SOUTH);

        add(lowerPanel, BorderLayout.CENTER);

        favoritesMenuItem.addActionListener(_ -> {
            // Open FavoritesGUI
            Favorites gui = new Favorites();
            gui.setVisible(true);

            // Close DictionaryApp
            dispose();
        });

        homeMenuItem.addActionListener(_ -> {
            // Open FavoritesGUI
            DictionaryApp gui = new DictionaryApp();
            gui.setVisible(true);

            // Close DictionaryApp
            dispose();
        });
    }

    private void loadTextArea() {
        StringBuilder content = new StringBuilder();
        for (DictionaryWord word : allWords) {
            content.append(word.getWord()).append("; ")
                    .append(word.getPartOfSpeech()).append("; ")
                    .append(word.getDefinition()).append("\n");
        }
        data.setText(content.toString());
    }

    private void saveWords() {
        String[] lines = data.getText().split("\\n");
        List<DictionaryWord> newWords = new ArrayList<>();
        Set<String> wordSet = new HashSet<>();

        for (String line : lines) {
            String[] parts = line.split(";", 3);
            if (parts.length == 3) {
                String word = parts[0].trim();
                if (wordSet.contains(word)) {
                    JOptionPane.showMessageDialog(this, "Duplicate word found: " + word, "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                wordSet.add(word);
                newWords.add(new DictionaryWord(word, parts[1].trim(), parts[2].trim()));
            } else {
                JOptionPane.showMessageDialog(this, "Invalid format in line: " + line, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("data.bin"))) {
            for (DictionaryWord word : newWords) {
                oos.writeObject(word.getWord() + "; " + word.getPartOfSpeech() + "; " + word.getDefinition());
            }
            allWords.clear();
            allWords.addAll(newWords);
            loadTextArea(); // Refresh the text area with updated words
            JOptionPane.showMessageDialog(this, "Data saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            Util.LOGGER.log(Level.SEVERE, "Error loading words from file", e);
            JOptionPane.showMessageDialog(this, "Error saving data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DataManager gui = new DataManager();
            gui.setVisible(true);
        });
    }
}
