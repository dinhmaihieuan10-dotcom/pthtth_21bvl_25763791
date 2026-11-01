package rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class HelloClient {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);

            HelloService service = (HelloService) registry.lookup("HelloService");

            String response = service.sayHello();

            System.out.println("==========================================");
            System.out.println("📩 Kết quả nhận được từ RMI Server:");
            System.out.println("👉 \"" + response + "\"");
            System.out.println("==========================================");

        } catch (Exception e) {
            System.err.println("[CLIENT LỖI]: Không thể gọi phương thức RMI: " + e.getMessage());
            e.printStackTrace();
        }
    }
}