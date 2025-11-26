package production.text;

public class Character {
    public final int id;
    public final double xTextureCoord;
    public final double yTextureCoord;
    public final double xMaxTextureCoord;
    public final double yMaxTextureCoord;
    public final double xOffset;
    public final double yOffset;
    public final double sizeX;
    public final double sizeY;
    public final double xAdvance;

    Character(int id, double xTextureCoord, double yTextureCoord,
            double xTexSize, double yTexSize, double xOffset, double yOffset,
            double sizeX, double sizeY, double xAdvance) {
        this.id = id;
        this.xTextureCoord = xTextureCoord;
        this.yTextureCoord = yTextureCoord;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.xMaxTextureCoord = xTexSize + xTextureCoord;
        this.yMaxTextureCoord = yTexSize + yTextureCoord;
        this.xAdvance = xAdvance;
    }
}
