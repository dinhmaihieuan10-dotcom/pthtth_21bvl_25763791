package rmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AuctionServiceImpl extends UnicastRemoteObject implements AuctionService {

    // Danh sách các sản phẩm đang đấu giá (Key = Mã SP, Value = Product)
    private final Map<String, Product> productMap;

    public AuctionServiceImpl() throws RemoteException {
        super();
        productMap = new ConcurrentHashMap<>();

        // Thêm một số sản phẩm mẫu ban đầu
        Product p1 = new Product("SP01", "Laptop Dell XPS 15", 15000000);
        Product p2 = new Product("SP02", "iPhone 15 Pro Max", 20000000);
        productMap.put(p1.getId(), p1);
        productMap.put(p2.getId(), p2);
    }

    @Override
    public synchronized boolean addProduct(String id, String name, double startPrice) throws RemoteException {
        String key = id.trim().toUpperCase();
        if (productMap.containsKey(key) || startPrice <= 0) {
            System.out.println("[SERVER]: Thêm sản phẩm thất bại (Mã SP " + key + " đã tồn tại hoặc giá không hợp lệ).");
            return false;
        }

        Product newProduct = new Product(key, name.trim(), startPrice);
        productMap.put(key, newProduct);
        System.out.println("[SERVER]: ➕ Seller đã thêm sản phẩm mới: " + newProduct);
        return true;
    }

    @Override
    public List<Product> getProductList() throws RemoteException {
        System.out.println("[SERVER]: Tra cứu danh sách sản phẩm đấu giá.");
        return new ArrayList<>(productMap.values());
    }

    @Override
    public synchronized boolean placeBid(String productId, double amount, String bidderName) throws RemoteException {
        String key = productId.trim().toUpperCase();
        Product product = productMap.get(key);

        if (product == null) {
            System.out.println("[SERVER]: Đặt giá thầu thất bại (Không tìm thấy mã SP: " + key + ").");
            return false;
        }

        // Đấu giá hợp lệ phải cao hơn giá cao nhất hiện tại
        if (amount > product.getHighestBid()) {
            double oldBid = product.getHighestBid();
            String oldBidder = product.getHighestBidder();

            // Cập nhật giá mới
            product.setHighestBid(amount);
            product.setHighestBidder(bidderName.trim());

            // Thông báo trên Server khi có người đặt giá cao hơn
            System.out.println("==================================================================================");
            System.out.println("🔔 [THÔNG BÁO ĐẤU GIÁ MỚI]:");
            System.out.println("👉 Sản phẩm: " + product.getName() + " (" + key + ")");
            System.out.println("👉 Người đặt giá mới: " + bidderName + " với số tiền: " + String.format("%,.0f VNĐ", amount));
            System.out.println("👉 (Giá cũ: " + String.format("%,.0f VNĐ", oldBid) + " của " + oldBidder + ")");
            System.out.println("==================================================================================");

            return true;
        }

        System.out.println("[SERVER]: Đặt giá thầu thất bại do " + bidderName + " đặt giá " + String.format("%,.0f", amount) + " VNĐ <= " + String.format("%,.0f", product.getHighestBid()) + " VNĐ.");
        return false;
    }

    public static void main(String[] args) {
        try {
            // 1. Khởi tạo RMI Registry tại cổng 1099
            Registry registry = LocateRegistry.createRegistry(1099);

            // 2. Tạo đối tượng dịch vụ Đấu giá
            AuctionServiceImpl service = new AuctionServiceImpl();

            // 3. Đăng ký dịch vụ với tên "AuctionService"
            registry.rebind("AuctionService", service);

            System.out.println("==========================================");
            System.out.println("🚀 RMI Auction Server đang chạy tại port 1099...");
            System.out.println("Sẵn sàng nhận giao dịch đấu giá trực tuyến!");
            System.out.println("==========================================");

        } catch (Exception e) {
            System.err.println("[SERVER LỖI]: " + e.getMessage());
            e.printStackTrace();
        }
    }
}