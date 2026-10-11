package tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class DigitToWordServer {
    private static final int PORT = 5000;

    private static final String[] DIGIT_WORDS = {
        "Không", "Một", "Hai", "Ba", "Bốn", 
        "Năm", "Sáu", "Bảy", "Tám", "Chín"
    };

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("TCP Digit Server đang lắng nghe tại port " + PORT);

            while (true) {
                try (Socket socket = server.accept()) {
                    System.out.println("Client kết nối từ: " + socket.getRemoteSocketAddress());
                    serve(socket);
                } catch (IOException e) {
                    System.err.println("Lỗi phiên làm việc với client: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Không thể khởi động Server: " + e.getMessage());
        }
    }

    static void serve(Socket socket) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(
                socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(
                socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            String request;
            while ((request = in.readLine()) != null) {
                if (request.equalsIgnoreCase("QUIT")) {
                    out.println("OK BYE");
                    break;
                }

                String response = processDigit(request);
                out.println(response);
            }
        }
    }


    static String processDigit(String input) {
        if (input == null || input.length() != 1 || !Character.isDigit(input.charAt(0))) {
            return "ERR INVALID_DIGIT";
        }

        int digit = input.charAt(0) - '0';
        return "OK " + DIGIT_WORDS[digit];
    }
}