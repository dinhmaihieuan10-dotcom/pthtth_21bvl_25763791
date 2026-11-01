package rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class MainServer {
    public static void main(String[] args) {
        try {
            System.out.println("==================================================================");
            System.out.println("🚀 ĐANG KHỞI CHẠY TỰ ĐỘNG TOÀN BỘ 9 DỊCH VỤ RMI SERVER...");
            System.out.println("==================================================================");

            // 1. Khởi tạo 1 RMI Registry duy nhất tại port 1099 cho tất cả dịch vụ
            Registry registry = LocateRegistry.createRegistry(1099);

            // 2. Tự động đăng ký Bài 1: Hello World
            HelloServiceImpl helloService = new HelloServiceImpl();
            registry.rebind("HelloService", helloService);
            System.out.println("✅ [1/9] HelloService đã sẵn sàng.");

            // 3. Tự động đăng ký Bài 2: Máy tính
            CalculatorServiceImpl calculatorService = new CalculatorServiceImpl();
            registry.rebind("CalculatorService", calculatorService);
            System.out.println("✅ [2/9] CalculatorService đã sẵn sàng.");

            // 4. Tự động đăng ký Bài 3: Kiểm tra Số Nguyên Tố
            PrimeServiceImpl primeService = new PrimeServiceImpl();
            registry.rebind("PrimeService", primeService);
            System.out.println("✅ [3/9] PrimeService đã sẵn sàng.");

            // 5. Tự động đăng ký Bài 4: Quản lý Danh Bạ
            ContactServiceImpl contactService = new ContactServiceImpl();
            registry.rebind("ContactService", contactService);
            System.out.println("✅ [4/9] ContactService đã sẵn sàng.");

            // 6. Tự động đăng ký Bài 5: Chat
            ChatServiceImpl chatService = new ChatServiceImpl();
            registry.rebind("ChatService", chatService);
            System.out.println("✅ [5/9] ChatService đã sẵn sàng.");

            // 7. Tự động đăng ký Bài 6: Ngân hàng
            BankServiceImpl bankService = new BankServiceImpl();
            registry.rebind("BankService", bankService);
            System.out.println("✅ [6/9] BankService đã sẵn sàng.");

            // 8. Tự động đăng ký Bài 7: Tính Diện tích
            GeometryServiceImpl geometryService = new GeometryServiceImpl();
            registry.rebind("GeometryService", geometryService);
            System.out.println("✅ [7/9] GeometryService đã sẵn sàng.");

            // 9. Tự động đăng ký Bài 8: Đặt vé máy bay
            FlightServiceImpl flightService = new FlightServiceImpl();
            registry.rebind("FlightService", flightService);
            System.out.println("✅ [8/9] FlightService đã sẵn sàng.");

            // 10. Tự động đăng ký Bài 9: Đấu giá trực tuyến
            AuctionServiceImpl auctionService = new AuctionServiceImpl();
            registry.rebind("AuctionService", auctionService);
            System.out.println("✅ [9/9] AuctionService đã sẵn sàng.");

            System.out.println("==================================================================");
            System.out.println("🎉 TẤT CẢ 9 RMI SERVICE ĐÃ ĐƯỢC KÍCH HOẠT VÀ ĐANG CHẠY TẠI PORT 1099!");
            System.out.println("Bây giờ bạn có thể bật MainClient.java để kiểm tra bất kỳ bài nào.");
            System.out.println("==================================================================");

        } catch (Exception e) {
            System.err.println("❌ LỖI KHI TỰ ĐỘNG KHỞI CHẠY SERVER: " + e.getMessage());
            e.printStackTrace();
        }
    }
}