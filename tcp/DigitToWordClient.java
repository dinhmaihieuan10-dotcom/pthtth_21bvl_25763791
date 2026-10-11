package tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class DigitToWordClient {
    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 5000;

        try (Socket socket = new Socket(host, port);
             BufferedReader console = new BufferedReader(
                     new InputStreamReader(System.in, StandardCharsets.UTF_8));
             BufferedReader in = new BufferedReader(new InputStreamReader(
                     socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(
                     socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            System.out.println("Đã kết nối tới Server (" + host + ":" + port + ")");
            System.out.println("Nhập 1 chữ số (0-9) để chuyển đổi, hoặc nhập 'QUIT' để thoát:");

            String userInput;
            while ((userInput = console.readLine()) != null) {
                out.println(userInput); // Gửi câu lệnh sang Server (UTF-8, mỗi thông điệp 1 dòng)

                String response = in.readLine(); // Nhận phản hồi từ Server
                if (response == null) {
                    System.out.println("Server đã đóng kết nối.");
                    break;
                }

                System.out.println("Server trả về: " + response);

                if (userInput.equalsIgnoreCase("QUIT")) {
                    break;
                }
            }
        } catch (NumberFormatException e) {
            System.err.println("Cổng (Port) phải là số nguyên!");
        } catch (IOException e) {
            System.err.println("Lỗi kết nối tới Server: " + e.getMessage());
        }
    }
}