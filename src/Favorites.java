import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

public class Favorites extends JFrame {
    private final JTextField searchField;
    private final Set<DictionaryWord> favoriteWords;
    private final DefaultListModel<DictionaryWord> listModel;

    private static final String FAVORITES_FILE = "favorites.bin";

    public Favorites() {
        listModel = new DefaultListModel<>();
        favoriteWords = loadFavorites();

        setTitle("Favorites");
        setSize(750, 750);
        setResizable(false);
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

        // Upper Panel
        JPanel upperPanel = new JPanel();
        GridLayout upperPanelLayout = new GridLayout(2, 1);
        upperPanel.setLayout(upperPanelLayout);
        upperPanel.setBorder(BorderFactory.createEmptyBorder(5, 27, 27, 27));
        upperPanel.setBackground(Color.decode("#D4344D"));
        JLabel title = new JLabel("Favorites");
        title.setForeground(Color.WHITE);
        title.setFont(Util.loadPoppins(24, "regular"));
        upperPanel.add(title);
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(11, 0, 11, 0));
        JLabel searchLabel = new JLabel();
        searchLabel.setIcon(Util.loadImage("search-normal.png"));
        searchPanel.add(searchLabel);
        searchField = new JTextField();
        searchField.setFont(Util.loadPoppins(24, "regular"));
        searchField.setPreferredSize(new Dimension(600, 35));
        searchField.setMargin(new Insets(0, 0, 0, 0));
        searchField.setBorder(null);
        searchPanel.add(searchField);
        upperPanel.add(searchPanel);
        add(upperPanel, BorderLayout.NORTH);

        // Lower Panel
        JPanel lowerPanel = new JPanel(new GridLayout(2, 1, 27, 27));
        lowerPanel.setBackground(Color.decode("#F7F9F9"));
        lowerPanel.setBorder(BorderFactory.createEmptyBorder(27, 27, 27, 27));
        JPanel operationPanel = new JPanel(new BorderLayout(27, 0));
        operationPanel.setBackground(null);
        loadFavoriteWords();
        JList<DictionaryWord> wordList = new JList<>(listModel);
        wordList.setCellRenderer(new CustomListCellRenderer());
        JScrollPane words = new JScrollPane(wordList);
        words.setBorder(null);
        words.setBackground(Color.decode("#E8EBF1"));
        operationPanel.add(words, BorderLayout.CENTER);
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 28));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(-28, 0, 0, 0));
        buttonsPanel.setPreferredSize(new Dimension(132, -1));
        buttonsPanel.setBackground(null);
        JButton favoriteButton = new JButton();
        favoriteButton.setPreferredSize(new Dimension(132, 47));
        favoriteButton.setBackground(Color.WHITE);
        favoriteButton.setBorder(null);
        favoriteButton.setIcon(Util.loadImage("heart-slash.png"));
        favoriteButton.setFocusable(false);
        favoriteButton.addActionListener(_ -> removeFavorite(wordList.getSelectedValue()));
        buttonsPanel.add(favoriteButton);
        operationPanel.add(buttonsPanel, BorderLayout.EAST);
        lowerPanel.add(operationPanel);
        JPanel outputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        outputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        outputPanel.setBackground(Color.WHITE);
        JLabel word = new JLabel();
        word.setPreferredSize(new Dimension(630, 35));
        word.setFont(Util.loadPoppins(32, "semibold"));
        word.setForeground(Color.decode("#505562"));
        outputPanel.add(word);
        JLabel partOfSpeech = new JLabel();
        partOfSpeech.setPreferredSize(new Dimension(630, 35));
        partOfSpeech.setFont(Util.loadPoppins(16, "italic"));
        partOfSpeech.setForeground(Color.decode("#D4344D"));
        outputPanel.add(partOfSpeech);
        JTextArea definition = new JTextArea();
        definition.setPreferredSize(new Dimension(630, 100));
        definition.setEditable(false);
        definition.setForeground(Color.decode("#7D828B"));
        definition.setFont(Util.loadPoppins(16, "regular"));
        definition.setLineWrap(true);
        outputPanel.add(definition);

        lowerPanel.add(outputPanel);

        add(lowerPanel, BorderLayout.CENTER);

        homeMenuItem.addActionListener(_ -> {
            // Open DictionaryApp
            DictionaryApp gui = new DictionaryApp();
            gui.setVisible(true);

            // Close Favorites
            dispose();
        });

        dataManagerMenuItem.addActionListener(_ -> {
            // Open DataManagerGUI
            DataManager gui = new DataManager();
            gui.setVisible(true);

            // Close Favorites
            dispose();
        });

        wordList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                DictionaryWord selectedWord = wordList.getSelectedValue();
                if (selectedWord != null) {
                    word.setText(selectedWord.getWord());
                    partOfSpeech.setText("(" + selectedWord.getPartOfSpeech() + ")");
                    definition.setText(selectedWord.getDefinition());
                } else {
                    word.setText("");
                    partOfSpeech.setText("");
                    definition.setText("");
                }
            }
        });

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterList();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterList();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterList();
            }
        });
    }

    private void filterList() {
        String searchTerm = searchField.getText().trim().toLowerCase();
        listModel.clear();

        for (DictionaryWord word : favoriteWords) {
            if (word.getWord().toLowerCase().contains(searchTerm)) {
                listModel.addElement(word);
            }
        }
    }

    private void loadFavoriteWords() {
        for (DictionaryWord word : favoriteWords) {
            listModel.addElement(word);
        }
    }

    private Set<DictionaryWord> loadFavorites() {
        Set<DictionaryWord> favorites = new HashSet<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FAVORITES_FILE))) {
            favorites = (Set<DictionaryWord>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            // No favorites loaded or file not found, start with an empty set
        }
        return favorites;
    }

    private void saveFavorites() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FAVORITES_FILE))) {
            oos.writeObject(favoriteWords);
        } catch (IOException e) {
            Util.LOGGER.log(Level.SEVERE, "Error loading words from file", e);
        }
    }

    private void removeFavorite(DictionaryWord word) {
        if (word != null) {
            int response = JOptionPane.showConfirmDialog(this, "Are you sure you want to remove this word from favorites?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                favoriteWords.remove(word);
                listModel.removeElement(word);
                saveFavorites();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Favorites gui = new Favorites();
            gui.setVisible(true);
        });
    }
}
