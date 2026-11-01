package rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class BankClient {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            // 1. Kết nối tới RMI Registry tại localhost cổng 1099
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);

            // 2. Tìm kiếm (lookup) dịch vụ "BankService"
            BankService service = (BankService) registry.lookup("BankService");

            int choice;
            do {
                System.out.println("\n================ MENU NGÂN HÀNG TỪ XA (RMI) ================");
                System.out.println("1. Kiểm tra số dư tài khoản");
                System.out.println("2. Gửi (Nạp) tiền");
                System.out.println("3. Rút tiền");
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
                        double currentBalance = service.getBalance();
                        System.out.printf("💳 Số dư hiện tại của bạn là: %,.2f VNĐ\n", currentBalance);
                        break;

                    case 2:
                        System.out.print("Nhập số tiền muốn nạp: ");
                        double depositAmount = Double.parseDouble(scanner.nextLine().trim());
                        if (depositAmount <= 0) {
                            System.out.println("⚠️ Số tiền nạp phải lớn hơn 0!");
                        } else {
                            service.deposit(depositAmount);
                            System.out.println("✅ Nạp tiền thành công!");
                        }
                        break;

                    case 3:
                        System.out.print("Nhập số tiền muốn rút: ");
                        double withdrawAmount = Double.parseDouble(scanner.nextLine().trim());
                        if (withdrawAmount <= 0) {
                            System.out.println("⚠️ Số tiền rút phải lớn hơn 0!");
                        } else {
                            boolean success = service.withdraw(withdrawAmount);
                            if (success) {
                                System.out.println("✅ Rút tiền thành công! Vui lòng nhận tiền.");
                            } else {
                                System.out.println("❌ Rút tiền thất bại! Số dư không đủ để thực hiện giao dịch.");
                            }
                        }
                        break;

                    case 0:
                        System.out.println("Cảm ơn bạn đã sử dụng dịch vụ ngân hàng!");
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