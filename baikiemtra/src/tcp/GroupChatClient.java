package tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class GroupChatClient {
    private static final String HOST = "localhost";
    private static final int PORT = 7000;

    public static void main(String[] args) {
        try {
            Socket socket = new Socket(HOST, PORT);
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);

            Thread receiveThread = new Thread(() -> {
                try {
                    String serverMsg;
                    while ((serverMsg = in.readLine()) != null) {
                        if (serverMsg.equalsIgnoreCase("BYE")) {
                            System.out.println("\n[HỆ THỐNG]: Đã ngắt kết nối tới Server.");
                            System.exit(0);
                        }
                        System.out.println(serverMsg);
                    }
                } catch (IOException e) {
                    System.out.println("\n[HỆ THỐNG]: Kết nối tới Server đã bị đóng.");
                }
            });
            receiveThread.setDaemon(true);
            receiveThread.start();

            String userInput;
            while ((userInput = console.readLine()) != null) {
                out.println(userInput);
                if (userInput.trim().equalsIgnoreCase("/exit")) {
                    break;
                }
            }

            socket.close();
        } catch (IOException e) {
            System.err.println("Lỗi kết nối tới Server: " + e.getMessage());
        }
    }
}