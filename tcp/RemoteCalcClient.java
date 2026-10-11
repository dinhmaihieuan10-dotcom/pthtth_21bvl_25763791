package tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class RemoteCalcClient {
    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = 5004;

        try (Socket socket = new Socket(host, port);
             BufferedReader console = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            System.out.println("Đã kết nối CALC Server (" + host + ":" + port + ")");
            System.out.println("Cú pháp lệnh: CALC <toán_tử> <số_1> <số_2> (Ví dụ: CALC + 100 200)");
            System.out.println("Nhập QUIT để thoát.");

            String userInput;
            while ((userInput = console.readLine()) != null) {
                out.println(userInput);

                String response = in.readLine();
                if (response == null) {
                    System.out.println("Server đã ngắt kết nối.");
                    break;
                }

                System.out.println("Server: " + response);

                if (userInput.trim().equalsIgnoreCase("QUIT")) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi kết nối: " + e.getMessage());
        }
    }
}