package rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class ChatClient {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            // 1. Kết nối tới RMI Registry tại localhost cổng 1099
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);

            // 2. Tìm kiếm (lookup) dịch vụ "ChatService"
            ChatService service = (ChatService) registry.lookup("ChatService");

            System.out.println("==========================================");
            System.out.println("   ỨNG DỤNG CHAT ĐƠN GIẢN QUA RMI (1-1)   ");
            System.out.println("   (Gõ '/exit' hoặc 'quit' để thoát)     ");
            System.out.println("==========================================");

            while (true) {
                System.out.print("\n💬 [BẠN]: ");
                String message = scanner.nextLine().trim();

                if (message.equalsIgnoreCase("/exit") || message.equalsIgnoreCase("quit")) {
                    System.out.println("Đã ngắt kết nối chat.");
                    break;
                }

                if (!message.isEmpty()) {
                    // Gọi phương thức sendMessage() từ xa
                    String response = service.sendMessage(message);
                    System.out.println("🤖 [SERVER PHẢN HỒI]: " + response);
                }
            }

        } catch (Exception e) {
            System.err.println("[CLIENT LỖI]: Không thể kết nối hoặc gửi tin nhắn RMI: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}