package tcp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class MessageLogServer {
    private static final int PORT = 5007;
    private static final String LOG_DIR = "data/logs";
    private static final Pattern VALID_CLIENT_ID = Pattern.compile("^[a-zA-Z0-9_-]+$");
    private static final DateTimeFormatter TIMESTAMP_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        File dir = new File(LOG_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        System.out.println("[Log Server] Đang lắng nghe tại port " + PORT);
        try (ServerSocket server = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = server.accept();
                new Thread(() -> handleClient(socket)).start();
            }
        } catch (IOException e) {
            System.err.println("[Log Server] Lỗi khởi động: " + e.getMessage());
        }
    }

    private static void handleClient(Socket socket) {
        String remoteAddr = socket.getRemoteSocketAddress().toString();
        System.out.println("[Kết nối mới]: " + remoteAddr);

        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            out.println("WELCOME! Vui lòng gửi lệnh HELLO <clientId> để bắt đầu.");

            String clientId = null;
            PrintWriter logWriter = null;

            String line;
            while ((line = in.readLine()) != null) {
                String trimmed = line.trim();

                if (clientId == null) {
                    if (trimmed.toUpperCase().startsWith("HELLO ")) {
                        String idInput = trimmed.substring(6).trim();
                        if (VALID_CLIENT_ID.matcher(idInput).matches()) {
                            clientId = idInput;
                            File logFile = new File(LOG_DIR, clientId + ".txt");
                            logWriter = new PrintWriter(new BufferedWriter(
                                    new OutputStreamWriter(new FileOutputStream(logFile, true), StandardCharsets.UTF_8)), true);
                            
                            out.println("OK READY " + clientId);
                        } else {
                            out.println("ERR INVALID_CLIENT_ID (Chỉ chấp nhận chữ, số, '-' và '_')");
                        }
                    } else {
                        out.println("ERR REQUIRE_HELLO (Phải gửi HELLO <clientId> trước)");
                    }
                    continue;
                }

                if (trimmed.equalsIgnoreCase("QUIT")) {
                    out.println("OK BYE");
                    break;
                }

                String timestamp = LocalDateTime.now().format(TIMESTAMP_FMT);
                logWriter.println(timestamp + " | " + remoteAddr + " | " + line);
                out.println("OK LOGGED");
            }

            if (logWriter != null) {
                logWriter.close();
            }
        } catch (IOException e) {
            System.err.println("[Lỗi Client " + remoteAddr + "]: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {}
        }
    }
}