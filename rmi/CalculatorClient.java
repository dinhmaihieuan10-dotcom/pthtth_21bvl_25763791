package rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class CalculatorClient {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);

            CalculatorService service = (CalculatorService) registry.lookup("CalculatorService");

            System.out.println("==========================================");
            System.out.println("   CHƯƠNG TRÌNH TÍNH TỔNG HAI SỐ (RMI)    ");
            System.out.println("==========================================");

            System.out.print("Nhập số thứ nhất (a): ");
            int a = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Nhập số thứ hai (b): ");
            int b = Integer.parseInt(scanner.nextLine().trim());

            int result = service.add(a, b);

            System.out.println("------------------------------------------");
            System.out.println("📩 Kết quả từ Server trả về: " + a + " + " + b + " = " + result);
            System.out.println("==========================================");

        } catch (NumberFormatException e) {
            System.err.println("Lỗi: Vui lòng nhập số nguyên hợp lệ!");
        } catch (Exception e) {
            System.err.println("[CLIENT LỖI]: Không thể thực thi phương thức RMI: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}