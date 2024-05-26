import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.logging.Level;

public class DataFileGenerator {
    public static void main(String[] args) {
        String[] data = {
                "world; noun; the earth, together with all of its countries and peoples.",
                "world-beater; noun; a person or thing that is better than all others in their field.",
                "world-class; adjective; of or among the best in the world.",
        };

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("data.bin"))) {
            for (String line : data) {
                oos.writeObject(line);
            }
            System.out.println("data.bin file created successfully.");
        } catch (IOException e) {
            Util.LOGGER.log(Level.SEVERE, "Error loading words from file", e);
        }
    }
}
