package production.chat;

import production.text.GUIText;
import whitetail.utility.logging.LogLevel;

import static whitetail.utility.ErrorHandler.LogFatalAndExit;
import static whitetail.utility.logging.Logger.LogSession;

public class ChatMessages {
    private static boolean init;
    private static int msgCap;
    private static int head;

    private static long     IDArr[];
    private static long     timeArr[];
    private static int      playerIDArr[];
    private static int      bufferOffsetArr[];
    private static int      lenArr[];
    private static GUIText  GUITextArr[];
    private static boolean  renderStatusArr[];

    private static int bufferCap;
    private static char buffer[];
    private static int bufferHead;

    private static final int DEFAULT_MSG_CAP = 64;
    private static final int DEFAULT_BUFFER_CAP = 8192;

    private ChatMessages(){}

    public static boolean Init(int msgCap, int bufferCap) {
        assert(!init);

        int i;

        if (msgCap > 0) {
            ChatMessages.msgCap = msgCap;
        } else {
            ChatMessages.msgCap = DEFAULT_MSG_CAP;
            LogSession(LogLevel.DEBUG, ErrStrCapOutOfBounds(msgCap, "msg",
                    DEFAULT_MSG_CAP));
        }

        if (bufferCap > 0) {
            ChatMessages.bufferCap = bufferCap;
        } else {
            ChatMessages.bufferCap = DEFAULT_BUFFER_CAP;
            LogSession(LogLevel.DEBUG, ErrStrCapOutOfBounds(bufferCap,
                    "buffer", DEFAULT_BUFFER_CAP));
        }

        try {
            IDArr = new long             [ChatMessages.msgCap];
            timeArr = new long           [ChatMessages.msgCap];
            playerIDArr = new int           [ChatMessages.msgCap];
            bufferOffsetArr = new int       [ChatMessages.msgCap];
            lenArr = new int                [ChatMessages.msgCap];
            GUITextArr = new GUIText        [ChatMessages.msgCap];
            renderStatusArr = new boolean   [ChatMessages.msgCap];
            buffer = new char               [ChatMessages.bufferCap];
        } catch (OutOfMemoryError e) {
            LogFatalAndExit(ERR_STR_FAILED_INIT);
            return init = false;
        }

        head = 0;
        bufferHead = 0;

        for (i = 0; i < ChatMessages.msgCap; ++i) {
            IDArr[i] = 0;
            timeArr[i] = 0;
            playerIDArr[i] = 0;
            bufferOffsetArr[i] = 0;
            lenArr[i] = 0;
            GUITextArr[i] = null;
            renderStatusArr[i] = false;
        }

        for (i = 0; i < ChatMessages.bufferCap; ++i) {
            buffer[i] = 0;
        }

        return init = true;
    }

    /* Only called when we get a chat msg packet from server, so we know that
        the msg complies with whatever rules we have, such as length or
        profanity filter */
    public static void Add(long msgID, long time, int playerID, String msg) {
        assert(init);

        int msgLen = msg.length();

        IDArr[head] = msgID;
        timeArr[head] = time;
        playerIDArr[head] = playerID;
        bufferOffsetArr[head] = bufferHead;
        lenArr[head] = msgLen;
        GUITextArr[head] = null;
        renderStatusArr[head] = false;

        msg.getChars(0, msgLen, buffer, bufferHead);

        /* Fairly sure there's a bug here, but not sure what solution is best */
        if (bufferHead + msgLen > bufferCap) bufferHead = 0;
        else bufferHead += msgLen;

        head = (head + 1) % msgCap;
    }

    public static String GetMsgStr(int i) {
        assert(init);

        if (!(i < msgCap) || i < 0) {
            /* assert would almost certainly be enough here... */
            LogFatalAndExit(ErrStrMsgIndexOutOfBounds(i));
            return "";
        }

        return new String(buffer, bufferOffsetArr[i], lenArr[i]);
    }

    public static long GetMsgID(int i) {
        assert(init);

        if (!(i < msgCap) || i < 0) {
            LogFatalAndExit(ErrStrMsgIndexOutOfBounds(i));
            return 0;
        }

        return IDArr[i];
    }

    public static long GetMsgTime(int i) {
        assert(init);

        if (!(i < msgCap) || i < 0) {
            LogFatalAndExit(ErrStrMsgIndexOutOfBounds(i));
            return 0;
        }

        return timeArr[i];
    }

    public static int GetMsgPlayerID(int i) {
        assert(init);

        if (!(i < msgCap) || i < 0) {
            LogFatalAndExit(ErrStrMsgIndexOutOfBounds(i));
            return 0;
        }

        return playerIDArr[i];
    }

    public static GUIText GetMsgGUIText(int i) {
        assert(init);

        if (!(i < msgCap) || i < 0) {
            LogFatalAndExit(ErrStrMsgIndexOutOfBounds(i));
            return null;
        }

        return GUITextArr[i];
    }
     public static boolean GetMsgRenderStatus(int i) {
        assert(init);

         if (!(i < msgCap) || i < 0) {
             LogFatalAndExit(ErrStrMsgIndexOutOfBounds(i));
             return false;
         }

         return renderStatusArr[i];
     }

    public static final String CLASS = ChatMessages.class.getSimpleName();
    private static String ErrStrCapOutOfBounds(int c, String s, int def) {
        return String.format("%s defaulted to [%s] capacity [%d] because an " +
                        "invalid [%s] capacity [%d] was requested.\n", CLASS, s,
                def, s, c);
    }
    private static final String ERR_STR_FAILED_INIT = CLASS + " failed to " +
            "initialize because an OutOfMemoryError was encountered.\n";
    private static String ErrStrMsgIndexOutOfBounds(int i) {
        return String.format("%s received a request for a message with an out" +
                " of bounds index [%d]. Valid indices: [0 - %d].\n", i, msgCap);
    }
}