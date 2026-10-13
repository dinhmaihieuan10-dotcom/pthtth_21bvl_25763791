package tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GroupChatServer {
    private static final int PORT = 7000;
    
    public static final List<String> ROOM_LIST = Arrays.asList("Study", "Game", "TraiNghiem");
    
    private static final Map<String, List<ClientHandler>> rooms = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        for (String room : ROOM_LIST) {
            rooms.put(room, new CopyOnWriteArrayList<>());
        }

        ExecutorService threadPool = Executors.newCachedThreadPool();
        System.out.println("[SERVER] Group Chat Server đang chạy tại port " + PORT + "...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket);
                threadPool.submit(clientHandler);
            }
        } catch (IOException e) {
            System.err.println("[SERVER LỖI]: " + e.getMessage());
        } finally {
            threadPool.shutdown();
        }
    }


    public static void broadcastToRoom(String roomName, String message, ClientHandler sender) {
        List<ClientHandler> clientsInRoom = rooms.get(roomName);
        if (clientsInRoom != null) {
            for (ClientHandler client : clientsInRoom) {
                if (client != sender) { 
                    client.sendMessage(message);
                }
            }
        }
    }


    public static void joinRoom(String roomName, ClientHandler client) {
        List<ClientHandler> clientsInRoom = rooms.get(roomName);
        if (clientsInRoom != null) {
            clientsInRoom.add(client);
            broadcastToRoom(roomName, "[HỆ THỐNG]: >>> " + client.getClientName() + " đã tham gia phòng [" + roomName + "] <<<", client);
        }
    }

    public static void leaveRoom(String roomName, ClientHandler client) {
        List<ClientHandler> clientsInRoom = rooms.get(roomName);
        if (clientsInRoom != null && clientsInRoom.remove(client)) {
            broadcastToRoom(roomName, "[HỆ THỐNG]: <<< " + client.getClientName() + " đã rời phòng [" + roomName + "] >>>", client);
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private String clientName;
        private String currentRoom;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public String getClientName() {
            return clientName;
        }

        public void sendMessage(String msg) {
            if (out != null) {
                out.println(msg);
            }
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);

                out.println("WELCOME! Vui lòng nhập Tên của bạn để bắt đầu:");
                clientName = in.readLine();
                if (clientName == null || clientName.trim().isEmpty()) {
                    clientName = "User_" + socket.getPort();
                }
                clientName = clientName.trim();
                System.out.println("[KẾT NỐI MỚI]: " + clientName + " (" + socket.getRemoteSocketAddress() + ")");

                while (true) {
                    out.println("\n=== DANH SÁCH PHÒNG CHAT ===");
                    for (int i = 0; i < ROOM_LIST.size(); i++) {
                        out.println((i + 1) + ". " + ROOM_LIST.get(i));
                    }
                    out.println("Vui lòng nhập TÊN PHÒNG hoặc SỐ THỨ TỰ phòng để tham gia (Hoặc nhập /exit để thoát):");

                    String choice = in.readLine();
                    if (choice == null || choice.trim().equalsIgnoreCase("/exit")) {
                        out.println("BYE");
                        break;
                    }

                    choice = choice.trim();
                    String selectedRoom = null;

                    try {
                        int index = Integer.parseInt(choice) - 1;
                        if (index >= 0 && index < ROOM_LIST.size()) {
                            selectedRoom = ROOM_LIST.get(index);
                        }
                    } catch (NumberFormatException e) {
                        for (String room : ROOM_LIST) {
                            if (room.equalsIgnoreCase(choice)) {
                                selectedRoom = room;
                                break;
                            }
                        }
                    }

                    if (selectedRoom == null) {
                        out.println("[LỖI]: Tên hoặc số phòng không hợp lệ. Vui lòng chọn lại!");
                        continue;
                    }

                    currentRoom = selectedRoom;
                    joinRoom(currentRoom, this);
                    out.println("\n--------------------------------------------------");
                    out.println("ĐÃ THAM GIA PHÒNG CHAT [" + currentRoom + "]");
                    out.println("Cú pháp:");
                    out.println(" - Gõ nội dung bất kỳ để gửi tin nhắn.");
                    out.println(" - Gõ /quit để rời phòng hiện tại.");
                    out.println(" - Gõ /exit để thoát chương trình.");
                    out.println("--------------------------------------------------");

                    String msg;
                    boolean exitServer = false;
                    while ((msg = in.readLine()) != null) {
                        msg = msg.trim();
                        if (msg.equalsIgnoreCase("/quit")) {
                            leaveRoom(currentRoom, this);
                            currentRoom = null;
                            out.println("[HỆ THỐNG]: Bạn đã rời phòng chat.");
                            break; 
                        } else if (msg.equalsIgnoreCase("/exit")) {
                            leaveRoom(currentRoom, this);
                            currentRoom = null;
                            out.println("BYE");
                            exitServer = true;
                            break; 
                        } else if (!msg.isEmpty()) {
                            String chatFormat = "[" + clientName + "]: " + msg;
                            broadcastToRoom(currentRoom, chatFormat, this);
                        }
                    }

                    if (exitServer || msg == null) {
                        break;
                    }
                }

            } catch (IOException e) {
                System.out.println("[MẤT KẾT NỐI]: " + clientName);
            } finally {
                if (currentRoom != null) {
                    leaveRoom(currentRoom, this);
                }
                try {
                    socket.close();
                } catch (IOException ignored) {}
                System.out.println("[ĐÃ THOÁT]: " + clientName);
            }
        }
    }
}