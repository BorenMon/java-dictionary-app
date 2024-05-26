import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Util {
    public static final Logger LOGGER = Logger.getLogger(Util.class.getName());

    public static Font loadPoppins(float size, String weight) {
        String pathName = switch (weight) {
            case "regular" -> "/resources/fonts/Poppins/Poppins-Regular.ttf";
            case "semibold" -> "/resources/fonts/Poppins/Poppins-SemiBold.ttf";
            case "italic" -> "/resources/fonts/Poppins/Poppins-Italic.ttf";
            default -> null;
        };

        if (pathName == null) {
            return new Font("Sans-Serif", Font.PLAIN, 14);
        }

        try (InputStream is = DictionaryApp.class.getResourceAsStream(pathName)) {
            if (is != null) {
                return Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(size);
            } else {
                throw new IOException("Font resource not found: " + pathName);
            }
        } catch (FontFormatException | IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading font", e);
            return new Font("Sans-Serif", Font.PLAIN, 14);
        }
    }

    static void loadWords(List<DictionaryWord> allWords) {
        File file = new File("data.bin");
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                while (true) {
                    try {
                        String line = (String) ois.readObject();
                        String[] parts = line.split(";", 3);
                        if (parts.length == 3) {
                            allWords.add(new DictionaryWord(parts[0].trim(), parts[1].trim(), parts[2].trim()));
                        }
                    } catch (EOFException eofException) {
                        // End of file reached
                        break;
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                LOGGER.log(Level.SEVERE, "Error loading words from file", e);
            }
        } else {
            LOGGER.log(Level.WARNING, "The file data.bin does not exist.");
        }
    }

    public static ImageIcon loadImage(String imageName) {
        return new ImageIcon(Objects.requireNonNull(Util.class.getResource("/resources/images/" + imageName)));
    }
}
