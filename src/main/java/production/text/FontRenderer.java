package production.text;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class FontRenderer {
    int shaderID;
    int vertexShaderID;
    int fragmentShaderID;

    public FontRenderer() {
        shaderID = GL20.glCreateProgram();
        vertexShaderID = loadShader("src/main/resources/shaders/fontVertex.txt", GL20.GL_VERTEX_SHADER);
        fragmentShaderID = loadShader("src/main/resources/shaders/fontFragment.txt", GL20.GL_FRAGMENT_SHADER);

        GL20.glAttachShader(shaderID, vertexShaderID);
        GL20.glAttachShader(shaderID, fragmentShaderID);

        GL20.glBindAttribLocation(shaderID, 0, "position");
        GL20.glBindAttribLocation(shaderID, 1, "textureCoords");

        GL20.glLinkProgram(shaderID);
        GL20.glValidateProgram(shaderID);
    }

    private static int loadShader(String file, int type) {
        StringBuilder shaderSource = new StringBuilder();
        try{
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while((line = reader.readLine())!=null){
                shaderSource.append(line).append("//\n");
            }
            reader.close();
        }catch(IOException e){
            e.printStackTrace();
            System.exit(-1);
        }
        int shaderID = GL20.glCreateShader(type);
        GL20.glShaderSource(shaderID, shaderSource);
        GL20.glCompileShader(shaderID);
        if(GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS )== GL11.GL_FALSE){
            System.out.println(GL20.glGetShaderInfoLog(shaderID, 500));
            System.err.println("Could not compile shader!");
            System.exit(-1);
        }
        return shaderID;
    }

    public void render(Map<FontType, List<GUIText>> texts){
        prepare();
        for(FontType font : texts.keySet()){
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, font.getTextureAtlas());
            for(GUIText text : texts.get(font)){
                renderText(text);
            }
        }
        endRendering();
    }

    public void cleanUp(){
        GL20.glUseProgram(0);

        GL20.glDetachShader(shaderID, vertexShaderID);
        GL20.glDetachShader(shaderID, fragmentShaderID);
        GL20.glDeleteShader(vertexShaderID);
        GL20.glDeleteShader(fragmentShaderID);
        GL20.glDeleteProgram(shaderID);
    }

    private void prepare(){
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL20.glUseProgram(shaderID);
    }

    private void renderText(GUIText text){
        GL30.glBindVertexArray(text.getMesh());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);

        int loc = GL20.glGetUniformLocation(shaderID, "colour");
        Vector3f v = text.getColour();
        GL20.glUniform3f(loc,v.x,v.y,v.z);
        int loc1 = GL20.glGetUniformLocation(shaderID, "translation");
        Vector2f v1 = text.getPosition();
        GL20.glUniform2f(loc1, v1.x, v1.y);

        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, text.getVertexCount());
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL30.glBindVertexArray(0);
    }

    private void endRendering(){
        GL20.glUseProgram(0);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

}
