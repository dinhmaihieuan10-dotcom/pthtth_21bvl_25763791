package tcp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class ServerDashboard implements Runnable {
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss - dd/MM/yyyy");
    private boolean running = true;

    public static void startAutoRefresh(int intervalSeconds) {
        ServerDashboard dashboard = new ServerDashboard();
        Thread thread = new Thread(() -> {
            while (dashboard.running) {
                try {
                    Thread.sleep(intervalSeconds * 1000L);
                    dashboard.printDashboard();
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }


    public synchronized void printDashboard() {
        System.out.println("\n=====================================================================");
        System.out.println("            📊 HỆ THỐNG DASHBOARD QUẢN LÝ CHAT SERVER               ");
        System.out.println("=====================================================================");
        System.out.println(" 🕒 Thời gian kiểm tra : " + LocalDateTime.now().format(TIME_FMT));
        System.out.println(" 🚪 Cổng Server (PORT) : 7000");
        System.out.println(" 👥 Cấu hình Phòng Chat: " + GroupChatServer.ROOM_LIST);
        System.out.println("---------------------------------------------------------------------");
        System.out.printf("| %-5s | %-20s | %-32s |\n", "STT", "Tên Phòng Chat", "Trạng Thái Phòng");
        System.out.println("---------------------------------------------------------------------");

        for (int i = 0; i < GroupChatServer.ROOM_LIST.size(); i++) {
            String roomName = GroupChatServer.ROOM_LIST.get(i);
            System.out.printf("| %-5d | %-20s | %-32s |\n", (i + 1), roomName, "Đang hoạt động (Ready)");
        }
        System.out.println("=====================================================================\n");
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (running) {
            System.out.println("\n[ADMIN DASHBOARD]: Gõ 'status' để xem trạng thái phòng, 'exit' để đóng Dashboard.");
            String cmd = scanner.nextLine().trim();
            if (cmd.equalsIgnoreCase("status")) {
                printDashboard();
            } else if (cmd.equalsIgnoreCase("exit")) {
                running = false;
                System.out.println("Đã đóng giao diện Admin Dashboard.");
                break;
            }
        } scanner.close();
    }

    public static void main(String[] args) {
        ServerDashboard dashboard = new ServerDashboard();
        dashboard.printDashboard();
        dashboard.run();
    }
}