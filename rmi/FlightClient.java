package rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class FlightClient {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            // 1. Kết nối tới RMI Registry tại localhost cổng 1099
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);

            // 2. Tìm kiếm (lookup) dịch vụ "FlightService"
            FlightService service = (FlightService) registry.lookup("FlightService");

            int choice;
            do {
                System.out.println("\n================ MENU ĐẶT VÉ MÁY BAY TỪ XA (RMI) ================");
                System.out.println("1. Kiểm tra số ghế còn trống");
                System.out.println("2. Đặt vé máy bay");
                System.out.println("3. Hủy vé đã đặt");
                System.out.println("0. Thoát chương trình");
                System.out.println("=================================================================");
                System.out.print("Chọn chức năng (0-3): ");

                try {
                    choice = Integer.parseInt(scanner.nextLine().trim());
                } catch (NumberFormatException e) {
                    choice = -1;
                }

                switch (choice) {
                    case 1:
                        System.out.print("Nhập mã chuyến bay (ví dụ: VN123, VJ456, QH789): ");
                        String flightCheck = scanner.nextLine();
                        int seats = service.availableSeats(flightCheck);
                        if (seats >= 0) {
                            System.out.println("✈️ Chuyến bay [" + flightCheck.toUpperCase() + "] còn trống: " + seats + " ghế.");
                        } else {
                            System.out.println("❌ Không tìm thấy thông tin chuyến bay: " + flightCheck);
                        }
                        break;

                    case 2:
                        System.out.print("Nhập mã chuyến bay muốn đặt vé: ");
                        String flightBook = scanner.nextLine();
                        System.out.print("Nhập số lượng vé muốn đặt: ");
                        int bookCount = Integer.parseInt(scanner.nextLine().trim());

                        boolean isBooked = service.bookTicket(flightBook, bookCount);
                        if (isBooked) {
                            System.out.println("✅ Đặt vé thành công " + bookCount + " chỗ cho chuyến bay " + flightBook.toUpperCase() + "!");
                        } else {
                            System.out.println("❌ Đặt vé thất bại! Chuyến bay không tồn tại hoặc không đủ số ghế trống.");
                        }
                        break;

                    case 3:
                        System.out.print("Nhập mã chuyến bay muốn hủy vé: ");
                        String flightCancel = scanner.nextLine();
                        System.out.print("Nhập số lượng vé muốn hủy: ");
                        int cancelCount = Integer.parseInt(scanner.nextLine().trim());

                        boolean isCancelled = service.cancelBooking(flightCancel, cancelCount);
                        if (isCancelled) {
                            System.out.println("✅ Đã hủy thành công " + cancelCount + " vé của chuyến bay " + flightCancel.toUpperCase() + ".");
                        } else {
                            System.out.println("❌ Hủy vé thất bại! Mã chuyến bay hoặc số lượng hủy không hợp lệ.");
                        }
                        break;

                    case 0:
                        System.out.println("Cảm ơn bạn đã sử dụng dịch vụ đặt vé!");
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