package combined;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ServiceServer {
    private static final int UDP_DISCOVERY_PORT = 5010;
    private static final int TCP_SERVICE_PORT = 6010;
    private static final String SERVICE_NAME = "HostInspectorService";
    private static final String VERSION = "v2.0";

    public static void main(String[] args) {
        new Thread(ServiceServer::startTcpService).start();

        startUdpDiscovery();
    }

    private static void startUdpDiscovery() {
        byte[] buffer = new byte[1024];
        try (DatagramSocket socket = new DatagramSocket(UDP_DISCOVERY_PORT)) {
            socket.setBroadcast(true);
            System.out.println("[UDP Discovery] Lắng nghe yêu cầu DISCOVER_SERVICE tại port " + UDP_DISCOVERY_PORT);

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String request = new String(packet.getData(), packet.getOffset(), packet.getLength(), StandardCharsets.UTF_8).trim();

                if (request.equalsIgnoreCase("DISCOVER_SERVICE")) {
                    String response = String.format("SERVICE %s %d %s", SERVICE_NAME, TCP_SERVICE_PORT, VERSION);
                    byte[] sendData = response.getBytes(StandardCharsets.UTF_8);

                    DatagramPacket responsePacket = new DatagramPacket(
                            sendData, sendData.length, packet.getAddress(), packet.getPort());
                    socket.send(responsePacket);

                    System.out.println("[UDP Discovery] Đã trả lời cho Client " + packet.getAddress().getHostAddress() + ":" + packet.getPort());
                }
            }
        } catch (Exception e) {
            System.err.println("[UDP Discovery Error]: " + e.getMessage());
        }
    }

    private static void startTcpService() {
        try (ServerSocket server = new ServerSocket(TCP_SERVICE_PORT)) {
            System.out.println("[TCP Service] Đang chạy tại port " + TCP_SERVICE_PORT);
            while (true) {
                Socket socket = server.accept();
                new Thread(() -> {
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                         PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {
                        
                        out.println("WELCOME TO " + SERVICE_NAME + " (" + VERSION + ")");
                        out.println("Trạng thái: Kết nối TCP thành công!");
                    } catch (Exception ignored) {}
                }).start();
            }
        } catch (Exception e) {
            System.err.println("[TCP Service Error]: " + e.getMessage());
        }
    }
}