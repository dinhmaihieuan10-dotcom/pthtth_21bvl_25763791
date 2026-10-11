package combined;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public class ServiceClient {
    private static final int DISCOVERY_PORT = 5010;
    private static final int TIMEOUT_MS = 3000;

    public static void main(String[] args) {
        Set<String> discoveredServers = new HashSet<>();

        System.out.println("[Client] Đang gửi UDP Broadcast tìm kiếm dịch vụ...");

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setBroadcast(true);
            socket.setSoTimeout(TIMEOUT_MS);

            byte[] sendData = "DISCOVER_SERVICE".getBytes(StandardCharsets.UTF_8);
            DatagramPacket sendPacket = new DatagramPacket(
                    sendData, sendData.length, InetAddress.getByName("255.255.255.255"), DISCOVERY_PORT);
            socket.send(sendPacket);

            byte[] receiveBuffer = new byte[1024];

            while (true) {
                try {
                    DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                    socket.receive(receivePacket);

                    String response = new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength(), StandardCharsets.UTF_8).trim();
                    InetAddress serverIp = receivePacket.getAddress();

                    String serverKey = serverIp.getHostAddress() + ":" + response;

                    if (!discoveredServers.contains(serverKey)) {
                        discoveredServers.add(serverKey);
                        System.out.println("\n[PHÁT HIỆN SERVER MỚI]:");
                        System.out.println(" - IP Nguồn: " + serverIp.getHostAddress());
                        System.out.println(" - Phản hồi: " + response);

                        String[] parts = response.split("\\s+");
                        if (parts.length >= 4 && parts[0].equals("SERVICE")) {
                            int tcpPort = Integer.parseInt(parts[2]);
                            connectToTcpService(serverIp, tcpPort);
                        }
                    }
                } catch (SocketTimeoutException e) {
                    break;
                }
            }

            if (discoveredServers.isEmpty()) {
                System.out.println("[Client] Không tìm thấy Server dịch vụ nào trong mạng.");
            }

        } catch (Exception e) {
            System.err.println("[Client Error]: " + e.getMessage());
        }
    }

    private static void connectToTcpService(InetAddress serverIp, int tcpPort) {
        System.out.printf(" -> Đang kết nối TCP tới %s:%d...\n", serverIp.getHostAddress(), tcpPort);
        try (Socket socket = new Socket(serverIp, tcpPort);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            while ((line = in.readLine()) != null) {
                System.out.println("    [TCP Response]: " + line);
            }
        } catch (Exception e) {
            System.err.println("    [Lỗi kết nối TCP]: " + e.getMessage());
        }
    }
}