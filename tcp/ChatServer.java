package tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {
    private static final int PORT = 5005;
    private static final int MAX_CLIENTS = 50;

    private static final Map<String, ClientHandler> clients = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(MAX_CLIENTS);
        System.out.println("[Chat Server] Đang khởi chạy tại port " + PORT);

        try (ServerSocket server = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = server.accept();
                pool.submit(new ClientHandler(socket));
            }
        } catch (IOException e) {
            System.err.println("[Chat Server] Lỗi ServerSocket: " + e.getMessage());
        } finally {
            pool.shutdown();
        }
    }


    public static void broadcast(String message, String senderNickname) {
        for (Map.Entry<String, ClientHandler> entry : clients.entrySet()) {
            if (!entry.getKey().equalsIgnoreCase(senderNickname)) {
                entry.getValue().sendMessage(message);
            }
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket socket;
        private String nickname;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void sendMessage(String msg) {
            if (out != null) {
                out.println(msg);
            }
        }

        @Override
        public void run() {
            String clientAddr = socket.getRemoteSocketAddress().toString();
            System.out.println("[Kết nối mới]: " + clientAddr);

            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                 PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {
                
                this.out = writer;
                out.println("WELCOME! Vui lòng chọn Nickname bằng lệnh: NICK <tên_của_bạn>");

                String request;
                while ((request = in.readLine()) != null) {
                    String trimmed = request.trim();
                    if (trimmed.isEmpty()) continue;

                    if (nickname == null) {
                        if (trimmed.toUpperCase().startsWith("NICK ")) {
                            String chosenNick = trimmed.substring(5).trim();
                            if (chosenNick.isEmpty() || chosenNick.contains(" ")) {
                                out.println("ERR INVALID_NICKNAME (Không được rỗng hoặc chứa khoảng trắng)");
                            } else if (clients.putIfAbsent(chosenNick.toLowerCase(), this) == null) {
                                this.nickname = chosenNick;
                                out.println("OK NICKNAME_SET " + this.nickname);
                                out.println("Cú pháp hỗ trợ: MSG <nội_dung>, USERS, QUIT");
                                broadcast("[Hệ thống]: " + this.nickname + " đã tham gia phòng chat!", this.nickname);
                            } else {
                                out.println("ERR NICKNAME_TAKEN (Nickname đã được sử dụng, vui lòng chọn tên khác)");
                            }
                        } else {
                            out.println("ERR REQUIRE_NICKNAME (Bạn phải nhập NICK <tên> trước)");
                        }
                        continue;
                    }

                    if (trimmed.equalsIgnoreCase("QUIT")) {
                        out.println("OK BYE");
                        break;
                    } else if (trimmed.equalsIgnoreCase("USERS")) {
                        out.println("ONLINE USERS: " + String.join(", ", clients.keySet()));
                    } else if (trimmed.toUpperCase().startsWith("MSG ")) {
                        String msgContent = trimmed.substring(4);
                        broadcast("[" + this.nickname + "]: " + msgContent, this.nickname);
                        out.println("OK SENT");
                    } else {
                        out.println("ERR UNKNOWN_COMMAND");
                    }
                }
            } catch (IOException e) {
                System.out.println("[Ngắt kết nối đột ngột]: " + (nickname != null ? nickname : clientAddr));
            } finally {
                if (nickname != null) {
                    clients.remove(nickname.toLowerCase());
                    broadcast("[Hệ thống]: " + nickname + " đã rời phòng chat.", nickname);
                    System.out.println("[Đã loại bỏ Client]: " + nickname);
                }
                try {
                    socket.close();
                } catch (IOException ignored) {}
            }
        }
    }
}