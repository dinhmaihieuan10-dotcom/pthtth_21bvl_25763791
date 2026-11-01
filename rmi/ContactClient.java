package rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class ContactClient {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            // 1. Kết nối tới RMI Registry tại localhost cổng 1099
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);

            // 2. Tìm kiếm (lookup) dịch vụ "ContactService"
            ContactService service = (ContactService) registry.lookup("ContactService");

            int choice;
            do {
                System.out.println("\n================ MENU QUẢN LÝ DANH BẠ (RMI) ================");
                System.out.println("1. Thêm / Sửa liên hệ");
                System.out.println("2. Tìm kiếm liên hệ theo tên");
                System.out.println("3. Xóa liên hệ theo tên");
                System.out.println("0. Thoát chương trình");
                System.out.println("=============================================================");
                System.out.print("Chọn chức năng (0-3): ");

                try {
                    choice = Integer.parseInt(scanner.nextLine().trim());
                } catch (NumberFormatException e) {
                    choice = -1;
                }

                switch (choice) {
                    case 1:
                        System.out.print("Nhập tên liên hệ: ");
                        String addName = scanner.nextLine();
                        System.out.print("Nhập số điện thoại: ");
                        String addPhone = scanner.nextLine();
                        
                        service.addContact(addName, addPhone);
                        System.out.println("-> Đã gửi yêu cầu lưu thành công!");
                        break;

                    case 2:
                        System.out.print("Nhập tên liên hệ cần tìm: ");
                        String searchName = scanner.nextLine();
                        
                        String phone = service.findContact(searchName);
                        if (phone != null) {
                            System.out.println("📩 Kết quả tìm kiếm: [" + searchName + "] - SĐT: " + phone);
                        } else {
                            System.out.println("❌ Không tìm thấy liên hệ nào có tên: " + searchName);
                        }
                        break;

                    case 3:
                        System.out.print("Nhập tên liên hệ cần xóa: ");
                        String deleteName = scanner.nextLine();
                        
                        boolean isDeleted = service.deleteContact(deleteName);
                        if (isDeleted) {
                            System.out.println("✅ Đã xóa thành công liên hệ: " + deleteName);
                        } else {
                            System.out.println("❌ Không thể xóa! Không tìm thấy liên hệ: " + deleteName);
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
            System.err.println("[CLIENT LỖI]: Không thể thực thi phương thức RMI: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}