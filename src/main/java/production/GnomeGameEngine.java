package production;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import production.text.FontType;
import production.text.GUIText;
import production.text.TextMaster;
import whitetail.core.GameEngine;
import whitetail.event.Event;
import whitetail.event.EventListener;
import whitetail.event.EventType;
import whitetail.graphics.*;
import whitetail.graphics.cameras.Camera;
import whitetail.graphics.materials.MaterialD;
import whitetail.loaders.TextureFileParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

public class GnomeGameEngine extends GameEngine implements EventListener {

    private Queue<String> serverMsgs;
    private PrintWriter toServer;
    private String input = "0000";

    public GnomeGameEngine() { super(); }

    public void setNetwork(PrintWriter out) {
        assert(out != null);

        toServer = out;
    }

    public void enQueueServerMsg(String msg) {
        assert(init);

        serverMsgs.add(msg);
    }

    @Override
    protected void onProcessInput() {
        boolean weDidSomething = false;

        if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
            StringBuilder sb = new StringBuilder(input);
            sb.setCharAt(0, '1');
            input = sb.toString();
            weDidSomething = true;
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
            StringBuilder sb = new StringBuilder(input);
            sb.setCharAt(1, '1');
            input = sb.toString();
            weDidSomething = true;
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
            StringBuilder sb = new StringBuilder(input);
            sb.setCharAt(2, '1');
            input = sb.toString();
            weDidSomething = true;
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
            StringBuilder sb = new StringBuilder(input);
            sb.setCharAt(3, '1');
            input = sb.toString();
            weDidSomething = true;
        }

        if (weDidSomething) {
            toServer.println(input);
        }
        input = "0000";
    }

    @Override
    protected boolean onInit() {
        RenderContext.ActivateSpriteRenderer();

        Data.characterTex = TextureFileParser.FromFile("character.png");
        Data.characterTex.upload();
        Data.characterTex.freeData();

        Texture fontAtlasTexture = TextureFileParser.FromFile("mono.png");
        fontAtlasTexture.upload();
        fontAtlasTexture.freeData();
        Data.fontAtlasID = fontAtlasTexture.getID();

        Shader.AttribLoc locs[] = {
                new Shader.AttribLoc(0, "position"),
                new Shader.AttribLoc(1, "texCoord"),
                new Shader.AttribLoc(2, "normal"),
        };

        Data.shader = new Shader("example_vert.glsl",
                "example_frag.glsl", locs);

        Data.characterMaterial = new MaterialD(Data.shader, Data.characterTex);

        /*
        Data.characterSprite = new Sprite(new Vector3f(16, 1, 16),
                new Vector3f(-90, 0, 0), new Vector3f(400, 300, -1),
                Data.characterMaterial);
        Data.characterSprite.setRenderStatus(true);
        SpriteRenderer.Add(Data.characterSprite);
         */

        Data.cam = Camera.MakeMenu(800.0f, 600.0f, 0.1f, 10f);

        SpriteRenderer.SetCamera(Data.cam);

        TextMaster.init();

        FontType font = new FontType(Data.fontAtlasID, new File("src/main/resources/textures/mono.fnt"));
        GUIText text = new GUIText("Welcome to Lumbridge!", 3f, font, new Vector2f(0f, 0f), 1f, true);
        text.setColour(1, 1, 0);

        serverMsgs = new LinkedList<>();

        return init = true;
    }

    @Override
    protected void onUpdate(double delta) {
        String msg;
        /*
        while ((msg = serverMsgs.poll()) != null) {
            String[] parts = msg.split(",");
            if ("GNOME".equals(parts[0])) {
                float newX = Float.parseFloat(parts[1]);
                float newY = Float.parseFloat(parts[2]);
                Data.characterSprite.pos.x = newX;
                Data.characterSprite.pos.y = newY;
            }
        }
        */
        while ((msg = serverMsgs.poll()) != null) {
            if (msg.startsWith("PLAYERS:")) {
                // Parse: "PLAYERS:1,100,200;2,150,250"
                String playerData = msg.substring("PLAYERS:".length());

                if (playerData.isEmpty()) {
                    // No players, clear all sprites
                    for (Sprite s : Data.playerSprites.values()) {
                        s.setRenderStatus(false);
                        SpriteRenderer.Remove(s);
                    }
                    Data.playerSprites.clear();
                    continue;
                }

                String[] players = playerData.split(";");
                Set<Integer> activePlayerIds = new HashSet<>();

                for (String playerStr : players) {
                    String[] parts = playerStr.split(",");
                    int playerId = Integer.parseInt(parts[0]);
                    float x = Float.parseFloat(parts[1]);
                    float z = Float.parseFloat(parts[2]);

                    activePlayerIds.add(playerId);

                    // Get or create sprite for this player
                    Sprite sprite = Data.playerSprites.get(playerId);
                    if (sprite == null) {
                        // Create new sprite for this player
                        sprite = new Sprite(
                                new Vector3f(16, 1, 16),
                                new Vector3f(-90, 0, 0),
                                new Vector3f(x, z, -1),
                                Data.characterMaterial
                        );
                        sprite.setRenderStatus(true);
                        SpriteRenderer.Add(sprite);
                        Data.playerSprites.put(playerId, sprite);

                        System.out.println("Created sprite for player " + playerId);
                    } else {
                        // Update existing sprite position
                        sprite.pos.x = x;
                        sprite.pos.y = z;
                    }
                }

                // Remove sprites for players that disconnected
                Iterator<Integer> it = Data.playerSprites.keySet().iterator();
                while (it.hasNext()) {
                    int playerId = it.next();
                    if (!activePlayerIds.contains(playerId)) {
                        Sprite sprite = Data.playerSprites.get(playerId);
                        sprite.setRenderStatus(false);
                        SpriteRenderer.Remove(sprite);
                        it.remove();
                        System.out.println("Removed sprite for player " + playerId);
                    }
                }
            }
        }
    }

    @Override
    protected void onRender() {

        TextMaster.render();
        window.swapBuffers();
    }

    @Override
    protected void onShutdown() {
        TextMaster.cleanUp();
    }

    @Override
    public boolean handleEvent(Event event) {
        return false;
    }

    @Override
    public EventType[] getInterestedEventTypes() {
        return new EventType[0];
    }
}
