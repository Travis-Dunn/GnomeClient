package production;

import whitetail.graphics.Shader;
import whitetail.graphics.Sprite;
import whitetail.graphics.Texture;
import whitetail.graphics.cameras.Camera;
import whitetail.graphics.materials.MaterialD;

import java.util.HashMap;
import java.util.Map;

public class Data {
    public static Texture characterTex;

    public static Shader shader;

    public static MaterialD characterMaterial;

    // NEW: Map of clientId -> Sprite
    public static Map<Integer, Sprite> playerSprites = new HashMap<>();

    public static int myClientId = -1;  // Track which player is "us"

    public static Camera cam;

    public static int fontAtlasID;
}
