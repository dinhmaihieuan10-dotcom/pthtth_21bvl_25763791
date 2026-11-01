package rmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FlightServiceImpl extends UnicastRemoteObject implements FlightService {

    // Danh sách các chuyến bay và số ghế còn trống (Key = Mã chuyến bay, Value = Số ghế trống)
    // Sử dụng ConcurrentHashMap để đảm bảo an toàn đa luồng khi nhiều Client đặt vé đồng thời
    private final Map<String, Integer> flightMap;

    public FlightServiceImpl() throws RemoteException {
        super();
        flightMap = new ConcurrentHashMap<>();

        // Khởi tạo danh sách các chuyến bay mẫu ban đầu
        flightMap.put("VN123", 50);  // Chuyến VN123 có 50 ghế
        flightMap.put("VJ456", 20);  // Chuyến VJ456 có 20 ghế
        flightMap.put("QH789", 5);   // Chuyến QH789 có 5 ghế
    }

    @Override
    public int availableSeats(String flightNumber) throws RemoteException {
        String key = flightNumber.trim().toUpperCase();
        System.out.println("[SERVER]: Tra cứu số ghế trống chuyến " + key);
        return flightMap.getOrDefault(key, -1);
    }

    @Override
    public synchronized boolean bookTicket(String flightNumber, int seats) throws RemoteException {
        String key = flightNumber.trim().toUpperCase();
        System.out.println("[SERVER]: Yêu cầu đặt " + seats + " vé cho chuyến " + key);

        if (seats <= 0 || !flightMap.containsKey(key)) {
            return false;
        }

        int currentSeats = flightMap.get(key);
        if (currentSeats >= seats) {
            flightMap.put(key, currentSeats - seats);
            System.out.println("[SERVER]: Đặt thành công! Số ghế còn lại của " + key + ": " + (currentSeats - seats));
            return true;
        }

        System.out.println("[SERVER]: Đặt thất bại! Chuyến " + key + " không đủ ghế trống.");
        return false;
    }

    @Override
    public synchronized boolean cancelBooking(String flightNumber, int seats) throws RemoteException {
        String key = flightNumber.trim().toUpperCase();
        System.out.println("[SERVER]: Yêu cầu hủy " + seats + " vé cho chuyến " + key);

        if (seats <= 0 || !flightMap.containsKey(key)) {
            return false;
        }

        int currentSeats = flightMap.get(key);
        flightMap.put(key, currentSeats + seats);
        System.out.println("[SERVER]: Hủy thành công! Số ghế còn lại của " + key + ": " + (currentSeats + seats));
        return true;
    }

    public static void main(String[] args) {
        try {
            // 1. Khởi tạo RMI Registry tại cổng 1099
            Registry registry = LocateRegistry.createRegistry(1099);

            // 2. Tạo đối tượng dịch vụ Đặt vé
            FlightServiceImpl service = new FlightServiceImpl();

            // 3. Đăng ký dịch vụ với tên "FlightService"
            registry.rebind("FlightService", service);

            System.out.println("==========================================");
            System.out.println("🚀 RMI Flight Booking Server đang chạy tại port 1099...");
            System.out.println("Các chuyến bay hiện có: VN123 (50 ghế), VJ456 (20 ghế), QH789 (5 ghế)");
            System.out.println("==========================================");

        } catch (Exception e) {
            System.err.println("[SERVER LỖI]: " + e.getMessage());
            e.printStackTrace();
        }
    }
}