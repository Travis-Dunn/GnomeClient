package production.chat;

import org.lwjgl.input.Keyboard;
import production.KeyPresses;

import static org.lwjgl.input.Keyboard.KEY_RETURN;
import static whitetail.utility.ErrorHandler.LogFatalAndExit;

public class ChatManager {
    private static boolean init;
    private static StringBuilder sb;

    private ChatManager() {}

    public static boolean Init(){
        assert(!init);

        try {
            sb = new StringBuilder();
        } catch (OutOfMemoryError e) {
            LogFatalAndExit(ERR_STR_FAILED_INIT);
            return init = false;
        }
        return init = true;
    }

    public static void Update() {
        assert(init);

        while (KeyPresses.Pop()) {
            int code = KeyPresses.GetCurrentCode();
            char character = KeyPresses.GetCurrentChar();
            boolean pressed = KeyPresses.GetCurrentPressed();

            if (!pressed) continue;

            if (code == Keyboard.KEY_BACK && sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            } else if (!Character.isISOControl(character) && character != 0) {
                sb.append(character);
            } else if (code == KEY_RETURN) {
                /* print to console for now, later render on screen */
                System.out.println(GetCurrentMessage());
            }
        }
    }

    public static String GetCurrentMessage() {
        final String s = sb.toString();
        /* clear isn't a real method, now sure how best to 0 the buffer */
        sb.setLength(0);
        return s;
    }

    private static final String CLASS = ChatManager.class.getSimpleName();
    private static final String ERR_STR_FAILED_INIT = CLASS + " failed to " +
            "initialize because an OutOfMemoryError was encountered.\n";
}
