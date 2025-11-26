package production.text;

import java.util.ArrayList;
import java.util.List;

public class Line {
    public final double maxLength;
    public final double spaceSize;
    private final List<Word> words;
    private double currentLineLength;

    Line(double spaceWidth, double fontSize, double maxLength) {
        this.spaceSize = fontSize * spaceWidth;
        this.maxLength = maxLength;
        words = new ArrayList<>();
    }

    public boolean attemptToAddWord(Word word) {
        double additionalLength = word.getWidth();
        additionalLength += !words.isEmpty() ? spaceSize : 0;
        if (!(currentLineLength + additionalLength <= maxLength)) return false;

        words.add(word);
        currentLineLength += additionalLength;
        return true;
    }

    public double getMaxLength() { return maxLength; }

    public double getLineLength() { return currentLineLength; }

    public List<Word> getWords() { return words; }
}
