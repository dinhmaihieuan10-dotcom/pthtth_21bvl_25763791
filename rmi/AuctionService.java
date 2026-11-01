package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface AuctionService extends Remote {
    // 1. Dành cho Seller: Thêm sản phẩm vào hệ thống
    boolean addProduct(String id, String name, double startPrice) throws RemoteException;

    // 2. Dành cho Bidder: Lấy danh sách sản phẩm đang đấu giá
    List<Product> getProductList() throws RemoteException;

    // 3. Dành cho Bidder: Đưa ra giá thầu
    boolean placeBid(String productId, double amount, String bidderName) throws RemoteException;
}