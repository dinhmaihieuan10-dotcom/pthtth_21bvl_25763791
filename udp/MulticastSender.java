package udp;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.StandardCharsets;

public class MulticastSender {
    private static final String MULTICAST_IP = "239.255.0.1";
    private static final int PORT = 5009;

    public static void main(String[] args) {
        try {
            InetAddress group = InetAddress.getByName(MULTICAST_IP);
            NetworkInterface netIf = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
            if (netIf == null) {
                netIf = NetworkInterface.getNetworkInterfaces().nextElement();
            }

            try (DatagramChannel channel = DatagramChannel.open(StandardProtocolFamily.INET)) {
                channel.setOption(StandardSocketOptions.IP_MULTICAST_IF, netIf);
                InetSocketAddress target = new InetSocketAddress(group, PORT);

                String[] messages = {
                    "THÔNG BÁO 1: Hệ thống bảo trì lúc 22h00.",
                    "THÔNG BÁO 2: Cập nhật phiên bản v2026.",
                    "THÔNG BÁO 3: Kết thúc buổi làm việc."
                };

                for (String msg : messages) {
                    ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8));
                    channel.send(buffer, target);
                    System.out.println("[Sender đã phát]: " + msg);
                    Thread.sleep(1000);
                }
            }
        } catch (Exception e) {
            System.err.println("[LỖI SENDER]: " + e.getMessage());
        }
    }
}