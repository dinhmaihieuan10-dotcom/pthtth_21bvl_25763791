package tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class DateTimeTcpClient {
    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = 5002;

        try (Socket socket = new Socket(host, port);
             BufferedReader console = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            System.out.println("[TCP Client] Đã kết nối. Lệnh hỗ trợ: DATE, TIME, DATETIME, QUIT");
            String userInput;
            while ((userInput = console.readLine()) != null) {
                out.println(userInput);
                
                String response = in.readLine();
                if (response == null) {
                    System.out.println("[TCP Client] Server đã đóng kết nối ngắt đột ngột (Connection closed)!");
                    break;
                }
                
                System.out.println("Server trả về: " + response);
                if (userInput.trim().equalsIgnoreCase("QUIT")) break;
            }
        } catch (IOException e) {
            System.err.println("[TCP Client] Lỗi I/O / Mất kết nối: " + e.getMessage());
        }
    }
}