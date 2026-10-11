package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import tcp.DateTimeTcpServer; 

public class DateTimeUdpServer {
    private static final int PORT = 5003;

    public static void main(String[] args) {
        byte[] buffer = new byte[1024];
        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            System.out.println("[UDP Server] Đang chạy tại port " + PORT);

            while (true) {
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                socket.receive(request);

                String cmd = new String(request.getData(), request.getOffset(), request.getLength(), StandardCharsets.UTF_8).trim().toUpperCase();
                
                String responseStr = DateTimeTcpServer.processCommand(cmd);

                byte[] responseData = responseStr.getBytes(StandardCharsets.UTF_8);
                DatagramPacket response = new DatagramPacket(responseData, responseData.length, request.getAddress(), request.getPort());
                socket.send(response);
            }
        } catch (IOException e) {
            System.err.println("[UDP Server] Lỗi: " + e.getMessage());
        }
    }
}