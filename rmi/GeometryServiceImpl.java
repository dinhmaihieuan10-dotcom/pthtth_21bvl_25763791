package rmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class GeometryServiceImpl extends UnicastRemoteObject implements GeometryService {

    public GeometryServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public double rectangleArea(double width, double height) throws RemoteException {
        double area = width * height;
        System.out.println("[SERVER]: Tính diện tích hình chữ nhật (w=" + width + ", h=" + height + ") -> Result: " + area);
        return area;
    }

    @Override
    public double circleArea(double radius) throws RemoteException {
        double area = Math.PI * radius * radius;
        System.out.println("[SERVER]: Tính diện tích hình tròn (r=" + radius + ") -> Result: " + area);
        return area;
    }

    @Override
    public double triangleArea(double base, double height) throws RemoteException {
        double area = 0.5 * base * height;
        System.out.println("[SERVER]: Tính diện tích hình tam giác (b=" + base + ", h=" + height + ") -> Result: " + area);
        return area;
    }

    public static void main(String[] args) {
        try {
            // 1. Khởi tạo RMI Registry tại cổng 1099
            Registry registry = LocateRegistry.createRegistry(1099);

            // 2. Tạo đối tượng dịch vụ Tính diện tích
            GeometryServiceImpl service = new GeometryServiceImpl();

            // 3. Đăng ký dịch vụ với tên "GeometryService"
            registry.rebind("GeometryService", service);

            System.out.println("==========================================");
            System.out.println("🚀 RMI Geometry Server đang chạy tại port 1099...");
            System.out.println("Sẵn sàng tính toán diện tích hình học!");
            System.out.println("==========================================");

        } catch (Exception e) {
            System.err.println("[SERVER LỖI]: " + e.getMessage());
            e.printStackTrace();
        }
    }
}