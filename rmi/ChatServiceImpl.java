package rmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ChatServiceImpl extends UnicastRemoteObject implements ChatService {

    public ChatServiceImpl() throws RemoteException {
        super();
    }

    // Triển khai phương thức nhận tin nhắn và trả về phản hồi
    @Override
    public String sendMessage(String message) throws RemoteException {
        System.out.println("\n📩 [CLIENT]: " + message);
        
        // Tạo phản hồi từ phía Server
        String reply = "Server đã nhận được tin nhắn: \"" + message + "\"";
        System.out.println("📤 [SERVER RESPOND]: " + reply);
        
        return reply;
    }

    public static void main(String[] args) {
        try {
            // 1. Khởi tạo RMI Registry tại cổng 1099
            Registry registry = LocateRegistry.createRegistry(1099);

            // 2. Tạo đối tượng dịch vụ Chat
            ChatServiceImpl service = new ChatServiceImpl();

            // 3. Đăng ký dịch vụ với tên "ChatService"
            registry.rebind("ChatService", service);

            System.out.println("==========================================");
            System.out.println("🚀 RMI Chat Server đang chạy tại port 1099...");
            System.out.println("Sẵn sàng lắng nghe tin nhắn từ Client!");
            System.out.println("==========================================");

        } catch (Exception e) {
            System.err.println("[SERVER LỖI]: " + e.getMessage());
            e.printStackTrace();
        }
    }
}