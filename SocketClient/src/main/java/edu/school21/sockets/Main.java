package edu.school21.sockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        try {
            int port = parsePort(args[0]);
            Socket socket = createSocket(port);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

            Thread mainThread = Thread.currentThread();
            startInputThread(in, mainThread);
            handleUserInput(userInput, out);
        } catch (IOException | NumberFormatException e) {
            System.out.println("argument \"--port=<integer>\" required");
        }
    }

    private static int parsePort(String arg) {
        return Integer.parseInt(arg.substring(arg.indexOf('=') + 1));
    }

    private static Socket createSocket(int port) throws IOException {
        return new Socket("localhost", port);
    }

    private static void startInputThread(BufferedReader in, Thread mainThread) {
        Thread inputMessageThread = new Thread(() -> {
            String msg;
            try {
                while (true) {
                    if ((msg = in.readLine()) != null) {
                        System.out.println(msg);
                        if (msg.equals("You have left the chat.") || msg.equals("Internal server error")) {
                            mainThread.interrupt();
                            return;
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        inputMessageThread.start();
    }

    private static void handleUserInput(BufferedReader userInput, PrintWriter out) throws IOException {
        String message;
        while (!Thread.currentThread().isInterrupted() && (message = userInput.readLine()) != null) {
            out.println(message);
        }
    }
}
