package udp;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.MembershipKey;
import java.nio.charset.StandardCharsets;

public class MulticastReceiver {
    private static final String MULTICAST_IP = "239.255.0.1";
    private static final int PORT = 5009;

    public static void main(String[] args) {
        try {
            InetAddress group = InetAddress.getByName(MULTICAST_IP);
            NetworkInterface netIf = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
            if (netIf == null) {
                netIf = NetworkInterface.getNetworkInterfaces().nextElement();
            }

            System.out.println("[Receiver] Sử dụng giao diện mạng: " + netIf.getDisplayName());

            try (DatagramChannel channel = DatagramChannel.open(StandardProtocolFamily.INET)) {
                channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
                channel.bind(new InetSocketAddress(PORT));
                channel.setOption(StandardSocketOptions.IP_MULTICAST_IF, netIf);

                MembershipKey key = channel.join(group, netIf);
                System.out.println("[Receiver] Đã join group " + MULTICAST_IP + ":" + PORT + ". Đang chờ thông báo...");

                ByteBuffer buffer = ByteBuffer.allocate(1024);
                int count = 0;

                while (count < 5) {
                    buffer.clear();
                    InetSocketAddress sender = (InetSocketAddress) channel.receive(buffer);
                    buffer.flip();

                    String msg = StandardCharsets.UTF_8.decode(buffer).toString();
                    System.out.println("[Nhận từ " + sender.getAddress().getHostAddress() + "]: " + msg);
                    count++;
                }

                key.drop();
                System.out.println("[Receiver] Đã rời khỏi Multicast Group.");
            }
        } catch (Exception e) {
            System.err.println("[LỖI RECEIVER]: " + e.getMessage());
            System.err.println("-> Gợi ý: Nếu bị ngắt kết nối, kiểm tra Firewall / VPN hoặc dùng Loopback!");
        }
    }
}