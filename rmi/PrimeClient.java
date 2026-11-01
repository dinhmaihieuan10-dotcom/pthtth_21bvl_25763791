package rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class PrimeClient {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);

            PrimeService service = (PrimeService) registry.lookup("PrimeService");

            System.out.println("==========================================");
            System.out.println("   CHƯƠNG TRÌNH KIỂM TRA SỐ NGUYÊN TỐ (RMI)");
            System.out.println("==========================================");

            System.out.print("Nhập một số nguyên n cần kiểm tra: ");
            int n = Integer.parseInt(scanner.nextLine().trim());

            boolean result = service.isPrime(n);

            System.out.println("------------------------------------------");
            if (result) {
                System.out.println("📩 Kết quả từ Server: Số " + n + " LÀ số nguyên tố. ✅");
            } else {
                System.out.println("📩 Kết quả từ Server: Số " + n + " KHÔNG PHẢI là số nguyên tố. ❌");
            }
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