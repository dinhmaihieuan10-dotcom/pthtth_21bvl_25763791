package rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class GeometryClient {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            // 1. Kết nối tới RMI Registry tại localhost cổng 1099
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);

            // 2. Tìm kiếm (lookup) dịch vụ "GeometryService"
            GeometryService service = (GeometryService) registry.lookup("GeometryService");

            int choice;
            do {
                System.out.println("\n================ MENU TÍNH DIỆN TÍCH HÌNH HỌC (RMI) ================");
                System.out.println("1. Tính diện tích Hình Chữ Nhật");
                System.out.println("2. Tính diện tích Hình Tròn");
                System.out.println("3. Tính diện tích Hình Tam Giác");
                System.out.println("0. Thoát chương trình");
                System.out.println("===================================================================");
                System.out.print("Chọn chức năng (0-3): ");

                try {
                    choice = Integer.parseInt(scanner.nextLine().trim());
                } catch (NumberFormatException e) {
                    choice = -1;
                }

                switch (choice) {
                    case 1:
                        System.out.print("Nhập chiều rộng (width): ");
                        double width = Double.parseDouble(scanner.nextLine().trim());
                        System.out.print("Nhập chiều cao (height): ");
                        double height = Double.parseDouble(scanner.nextLine().trim());

                        if (width <= 0 || height <= 0) {
                            System.out.println("⚠️ Kích thước phải lớn hơn 0!");
                        } else {
                            double rectArea = service.rectangleArea(width, height);
                            System.out.printf("📐 Diện tích Hình Chữ Nhật: %,.2f\n", rectArea);
                        }
                        break;

                    case 2:
                        System.out.print("Nhập bán kính (radius): ");
                        double radius = Double.parseDouble(scanner.nextLine().trim());

                        if (radius <= 0) {
                            System.out.println("⚠️ Bán kính phải lớn hơn 0!");
                        } else {
                            double circArea = service.circleArea(radius);
                            System.out.printf("🔴 Diện tích Hình Tròn: %,.2f\n", circArea);
                        }
                        break;

                    case 3:
                        System.out.print("Nhập độ dài đáy (base): ");
                        double base = Double.parseDouble(scanner.nextLine().trim());
                        System.out.print("Nhập chiều cao (height): ");
                        double triHeight = Double.parseDouble(scanner.nextLine().trim());

                        if (base <= 0 || triHeight <= 0) {
                            System.out.println("⚠️ Kích thước phải lớn hơn 0!");
                        } else {
                            double triArea = service.triangleArea(base, triHeight);
                            System.out.printf("🔺 Diện tích Hình Tam Giác: %,.2f\n", triArea);
                        }
                        break;

                    case 0:
                        System.out.println("Đã thoát chương trình Client.");
                        break;

                    default:
                        System.out.println("⚠️ Lựa chọn không hợp lệ, vui lòng thử lại!");
                }

            } while (choice != 0);

        } catch (Exception e) {
            System.err.println("[CLIENT LỖI]: Không thể kết nối hoặc thực thi dịch vụ RMI: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}