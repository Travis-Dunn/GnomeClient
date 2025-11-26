package production;

import java.io.*;
import java.net.*;
import java.util.Scanner;

import static whitetail.utility.ErrorHandler.LogFatalAndExit;

public class GnomeClient {
    private static final String SERVER_ADDRESS = "192.168.1.94";
    private static final int SERVER_PORT = 7777;

    public static void main(String[] args) {
        System.out.println("=== Gnome Client - Phase 1: Interactive Client ===");
        System.out.println("Connecting to server at " + SERVER_ADDRESS + ":" + SERVER_PORT + "...");

        try {
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            System.out.println("Connected to server!");

            // Set up streams
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );
            PrintWriter out = new PrintWriter(
                    socket.getOutputStream(),
                    true
            );


            // Main thread: read user input and send to server

            GnomeGameEngine engine = new GnomeGameEngine();
            if (!engine.initFirstHalf()) {
                System.err.println("Engine failed to init first half");
                return;
            }
            if (!engine.initSecondHalf("Massively Multiplayer Online Role-Playing Game", 800, 600,
                    0, true, false)) {
                LogFatalAndExit("Engine failed to init second half");
                return;
            }

            engine.setNetwork(out);


            // Thread to read messages from server continuously
            Thread readerThread = new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        System.out.println("[SERVER] " + message);
                        if (message.startsWith("PLAYERS:")) {
                            engine.enQueueServerMsg(message);
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Connection lost: " + e.getMessage());
                }
            });
            readerThread.start();


            engine.run();

            /*
            Scanner scanner = new Scanner(System.in);
            System.out.println("Type messages (or 'QUIT' to exit):");

            String userInput;
            while (scanner.hasNextLine()) {
                userInput = scanner.nextLine();
                out.println(userInput);

                if (userInput.equalsIgnoreCase("QUIT")) {
                    break;
                }
            }
             */

            socket.close();
            System.out.println("Disconnected from server");

        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}