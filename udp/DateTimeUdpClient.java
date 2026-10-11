package udp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

public class DateTimeUdpClient {
    public static void main(String[] args) throws Exception {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = 5003;
        InetAddress serverAddr = InetAddress.getByName(host);

        try (DatagramSocket socket = new DatagramSocket();
             BufferedReader console = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            
            socket.setSoTimeout(3000);
            System.out.println("[UDP Client] Sẵn sàng. Nhập lệnh: DATE, TIME, DATETIME (Gõ exit để đóng client local)");

            String userInput;
            while ((userInput = console.readLine()) != null) {
                if (userInput.trim().equalsIgnoreCase("exit")) break;

                byte[] sendData = userInput.getBytes(StandardCharsets.UTF_8);
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddr, port);
                socket.send(sendPacket);

                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                try {
                    socket.receive(receivePacket);
                    String response = new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength(), StandardCharsets.UTF_8);
                    System.out.println("Server trả về: " + response);
                } catch (SocketTimeoutException e) {
                    System.err.println("[UDP Client] Hết thời gian chờ (Timeout) - Server không phản hồi!");
                }
            }
        }
    }
}