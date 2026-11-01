package rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Scanner;

public class AuctionClient {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            // 1. Kết nối tới RMI Registry tại localhost cổng 1099
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);

            // 2. Tìm kiếm (lookup) dịch vụ "AuctionService"
            AuctionService service = (AuctionService) registry.lookup("AuctionService");

            System.out.println("==========================================================");
            System.out.println("   HỆ THỐNG ĐẤU GIÁ TRỰC TUYẾN TỪ XA (RMI AUCTION)        ");
            System.out.println("==========================================================");
            System.out.print("Nhập tên của bạn: ");
            String userName = scanner.nextLine().trim();

            int choice;
            do {
                System.out.println("\n---------------- MENU THAO TÁC (" + userName + ") ----------------");
                System.out.println("1. [Bidder] Xem danh sách sản phẩm đang đấu giá");
                System.out.println("2. [Bidder] Đặt giá thầu (Bid)");
                System.out.println("3. [Seller] Đăng sản phẩm mới để đấu giá");
                System.out.println("0. Thoát");
                System.out.println("----------------------------------------------------------");
                System.out.print("Chọn chức năng (0-3): ");

                try {
                    choice = Integer.parseInt(scanner.nextLine().trim());
                } catch (NumberFormatException e) {
                    choice = -1;
                }

                switch (choice) {
                    case 1:
                        List<Product> products = service.getProductList();
                        System.out.println("\n--- DANH SÁCH SẢN PHẨM ĐẦU GIÁ ---");
                        if (products.isEmpty()) {
                            System.out.println("Chưa có sản phẩm nào trong hệ thống.");
                        } else {
                            for (Product p : products) {
                                System.out.println(p);
                            }
                        }
                        break;

                    case 2:
                        System.out.print("Nhập Mã sản phẩm muốn đặt giá: ");
                        String pId = scanner.nextLine();
                        System.out.print("Nhập số tiền bạn muốn đấu giá (VNĐ): ");
                        double bidAmount = Double.parseDouble(scanner.nextLine().trim());

                        boolean isBidSuccess = service.placeBid(pId, bidAmount, userName);
                        if (isBidSuccess) {
                            System.out.println("🎉 Đặt giá thầu THÀNH CÔNG! Bạn hiện là người giữ giá cao nhất.");
                        } else {
                            System.out.println("❌ Đặt giá thầu THẤT BẠI! Giá thầu của bạn phải lớn hơn giá hiện tại.");
                        }
                        break;

                    case 3:
                        System.out.print("Nhập Mã sản phẩm mới (ví dụ: SP03): ");
                        String newId = scanner.nextLine();
                        System.out.print("Nhập Tên sản phẩm: ");
                        String newName = scanner.nextLine();
                        System.out.print("Nhập Giá khởi điểm (VNĐ): ");
                        double startPrice = Double.parseDouble(scanner.nextLine().trim());

                        boolean isAddSuccess = service.addProduct(newId, newName, startPrice);
                        if (isAddSuccess) {
                            System.out.println("✅ Thêm sản phẩm đăng đấu giá THÀNH CÔNG!");
                        } else {
                            System.out.println("❌ Thêm thất bại! Mã sản phẩm đã tồn tại hoặc giá không hợp lệ.");
                        }
                        break;

                    case 0:
                        System.out.println("Đã thoát ứng dụng đấu giá.");
                        break;

                    default:
                        System.out.println("⚠️ Lựa chọn không hợp lệ, vui lòng thử lại!");
                }

            } while (choice != 0);

        } catch (Exception e) {
            System.err.println("[CLIENT LỖI]: Không thể kết nối dịch vụ RMI: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}