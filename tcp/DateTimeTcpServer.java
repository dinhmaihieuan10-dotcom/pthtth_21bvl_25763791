package tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeTcpServer {
    private static final int PORT = 5002;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MM yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH mm ss");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("dd MM yyyy HH mm ss");

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("[TCP Server] Đang chạy tại port " + PORT);
            while (true) {
                try (Socket socket = server.accept()) {
                    serve(socket);
                } catch (IOException e) {
                    System.err.println("[TCP Server] Lỗi kết nối client: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("[TCP Server] Khởi động thất bại: " + e.getMessage());
        }
    }

    private static void serve(Socket socket) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            String request;
            while ((request = in.readLine()) != null) {
                String cmd = request.trim().toUpperCase();
                if (cmd.equals("QUIT")) {
                    out.println("OK BYE");
                    break;
                }
                out.println(processCommand(cmd));
            }
        }
    }

    public static String processCommand(String cmd) {
        LocalDateTime now = LocalDateTime.now();
        switch (cmd) {
            case "DATE":
                return "OK " + now.format(DATE_FMT);
            case "TIME":
                return "OK " + now.format(TIME_FMT);
            case "DATETIME":
                return "OK " + now.format(DATETIME_FMT);
            default:
                return "ERR UNKNOWN_COMMAND";
        }
    }
}