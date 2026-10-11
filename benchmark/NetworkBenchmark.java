package benchmark;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class NetworkBenchmark {
    private static final int TCP_PORT = 6001;
    private static final int UDP_PORT = 6002;
    private static final int TOTAL_MESSAGES = 1000;
    private static final int TEST_RUNS = 5;
    private static final int TIMEOUT_MS = 1000;
    
    private static final String PAYLOAD;
    static {
        char[] chars = new char[256];
        Arrays.fill(chars, 'A');
        PAYLOAD = new String(chars);
    }

    public static void main(String[] args) throws Exception {
        System.out.println("==================================================");
        System.out.println("   BẮT ĐẦU THỰC NGHIỆM SO SÁNH TCP VÀ UDP         ");
        System.out.println("==================================================");

        startTcpEchoServer();
        startUdpEchoServer();
        Thread.sleep(500); 

        long[] tcpTimes = new long[TEST_RUNS];
        long[] udpTimes = new long[TEST_RUNS];
        int[] tcpResponses = new int[TEST_RUNS];
        int[] udpResponses = new int[TEST_RUNS];

        for (int i = 0; i < TEST_RUNS; i++) {
            System.out.println("\n--- VÒNG THỬ NGHIỆM " + (i + 1) + " ---");
            
            long startTcp = System.currentTimeMillis();
            tcpResponses[i] = runTcpBenchmark();
            tcpTimes[i] = System.currentTimeMillis() - startTcp;
            System.out.printf("TCP: %d/1000 phản hồi | Thời gian: %d ms\n", tcpResponses[i], tcpTimes[i]);

            Thread.sleep(200);

            long startUdp = System.currentTimeMillis();
            udpResponses[i] = runUdpBenchmark();
            udpTimes[i] = System.currentTimeMillis() - startUdp;
            System.out.printf("UDP: %d/1000 phản hồi | Thời gian: %d ms\n", udpResponses[i], udpTimes[i]);
        }

        System.out.println("\n==================================================");
        System.out.println("                 KẾT QUẢ TỔNG HỢP                 ");
        System.out.println("==================================================");
        System.out.println("Lần\tTCP Time (ms)\tTCP Resp\tUDP Time (ms)\tUDP Resp");
        for (int i = 0; i < TEST_RUNS; i++) {
            System.out.printf("%d\t%d\t\t%d/1000\t\t%d\t\t%d/1000\n", (i + 1), tcpTimes[i], tcpResponses[i], udpTimes[i], udpResponses[i]);
        }
        System.exit(0);
    }

    private static int runTcpBenchmark() {
        int successCount = 0;
        try (Socket socket = new Socket("localhost", TCP_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {
            
            socket.setSoTimeout(TIMEOUT_MS);
            for (int i = 0; i < TOTAL_MESSAGES; i++) {
                out.println(PAYLOAD);
                String resp = in.readLine();
                if (resp != null) successCount++;
            }
        } catch (Exception e) {
            System.err.println("Lỗi TCP Benchmark: " + e.getMessage());
        }
        return successCount;
    }

    private static int runUdpBenchmark() {
        int successCount = 0;
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(TIMEOUT_MS);
            InetAddress serverAddr = InetAddress.getByName("localhost");
            byte[] sendData = PAYLOAD.getBytes(StandardCharsets.UTF_8);

            for (int i = 0; i < TOTAL_MESSAGES; i++) {
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddr, UDP_PORT);
                socket.send(sendPacket);

                byte[] receiveBuffer = new byte[512];
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                try {
                    socket.receive(receivePacket);
                    successCount++;
                } catch (SocketTimeoutException e) {
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi UDP Benchmark: " + e.getMessage());
        }
        return successCount;
    }

    private static void startTcpEchoServer() {
        new Thread(() -> {
            try (ServerSocket server = new ServerSocket(TCP_PORT)) {
                while (true) {
                    Socket client = server.accept();
                    new Thread(() -> {
                        try (BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));
                             PrintWriter out = new PrintWriter(new OutputStreamWriter(client.getOutputStream(), StandardCharsets.UTF_8), true)) {
                            String line;
                            while ((line = in.readLine()) != null) {
                                out.println(line);
                            }
                        } catch (Exception ignored) {}
                    }).start();
                }
            } catch (Exception ignored) {}
        }).start();
    }

    private static void startUdpEchoServer() {
        new Thread(() -> {
            try (DatagramSocket socket = new DatagramSocket(UDP_PORT)) {
                byte[] buffer = new byte[512];
                while (true) {
                    DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                    socket.receive(request);
                    DatagramPacket response = new DatagramPacket(request.getData(), request.getLength(), request.getAddress(), request.getPort());
                    socket.send(response);
                }
            } catch (Exception ignored) {}
        }).start();
    }
}