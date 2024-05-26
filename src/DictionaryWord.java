import java.io.Serializable;
import java.util.Objects;

public class DictionaryWord implements Serializable {
    private final String word;
    private final String partOfSpeech;
    private final String definition;

    // Constructor
    public DictionaryWord(String word, String partOfSpeech, String definition) {
        this.word = word;
        this.partOfSpeech = partOfSpeech;
        this.definition = definition;
    }

    // Getters and Setters
    public String getWord() {
        return word;
    }

    public String getPartOfSpeech() {
        return partOfSpeech;
    }

    public String getDefinition() {
        return definition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DictionaryWord that = (DictionaryWord) o;
        return word.equals(that.word) &&
                partOfSpeech.equals(that.partOfSpeech) &&
                definition.equals(that.definition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(word, partOfSpeech, definition);
    }

    @Override
    public String toString() {
        return word;
    }
}