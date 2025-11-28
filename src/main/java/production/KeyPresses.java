package production;

import whitetail.utility.logging.LogLevel;

import static whitetail.utility.ErrorHandler.LogFatalAndExit;
import static whitetail.utility.logging.Logger.LogSession;

public class KeyPresses {
    private static boolean init;
    private static int cap;
    private static int head;
    private static int tail;
    private static int codeArr[];
    private static char characterArr[];
    private static boolean pressedArr[];
    private static int code;
    private static char character;
    private static boolean pressed;

    private static final int DEFAULT_CAP = 8;

    private KeyPresses() {}

    public static boolean Init(int cap){
        assert(!init);

        if (cap > 0) {
            KeyPresses.cap = cap;
        } else {
            KeyPresses.cap = DEFAULT_CAP;
            LogSession(LogLevel.DEBUG, ErrStrCapOutOfBounds(cap));
        }

        try {
            codeArr = new int[KeyPresses.cap];
            characterArr = new char[KeyPresses.cap];
            pressedArr = new boolean[KeyPresses.cap];
        } catch (OutOfMemoryError e){
            LogFatalAndExit(ERR_STR_FAILED_INIT);
            return init = false;
        }

        Reset();

        return init = true;
    }

    public static void Free() {
        init = false;
        codeArr = null;
        characterArr = null;
        pressedArr = null;
    }

    public static void Reset() {
        tail = head = 0;
    }

    public static void Add(int code, char character, boolean pressed) {
        assert(init);

        KeyPresses.codeArr[head] = code;
        KeyPresses.characterArr[head] = character;
        KeyPresses.pressedArr[head] = pressed;
        head = (head + 1) % cap;
    }

    public static boolean Pop() {
        assert(init);

        if (tail == head) return false;

        code = KeyPresses.codeArr[tail];
        character = KeyPresses.characterArr[tail];
        pressed = KeyPresses.pressedArr[tail];
        tail = (tail + 1) % cap;

        return true;
    }

    public static int GetCurrentCode() { return code; }
    public static char GetCurrentChar() { return character; }
    public static boolean GetCurrentPressed() {return pressed; }

    private static final String CLASS = KeyPresses.class.getSimpleName();
    private static final String ERR_STR_FAILED_INIT = CLASS + " failed to " +
            "initialize because an OutOfMemoryError was encountered.\n";
    private static String ErrStrCapOutOfBounds(int c) {
        return String.format("%s defaulted to capacity [%d] because an " +
                "invalid capacity [%d] was requested.\n", CLASS, DEFAULT_CAP,
                c);
    }

}
