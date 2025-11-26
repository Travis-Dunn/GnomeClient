package production.text;

import java.util.ArrayList;
import java.util.List;

public class Word {
    private final List<Character> characters;
    private double width;
    public final double fontSize;

    Word(double fontSize) {
        characters = new ArrayList<>();
        this.fontSize = fontSize;
        width = 0;
    }

    public List<Character> getCharacters() { return characters; }

    public double getWidth() { return width; }

    public void addCharacter(Character character) {
        characters.add(character);
        width += character.xAdvance * fontSize;
    }
}
