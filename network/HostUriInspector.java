package network;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

public class HostUriInspector {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Lỗi: Thiếu tham số truyền vào!");
            System.out.println("Cú pháp: java network.HostUriInspector <hostname> <URI>");
            return;
        }

        String hostnameArg = args[0];
        String uriArg = args[1];

        System.out.println("==================================================");
        System.out.println("          KẾT QUẢ KIỂM TRA HOST & URI             ");
        System.out.println("==================================================");

        System.out.println("---> 1. THÔNG TIN HOST: " + hostnameArg);
        try {
            InetAddress[] addresses = InetAddress.getAllByName(hostnameArg);
            for (InetAddress addr : addresses) {
                String ipType = "Chưa xác định";
                if (addr instanceof Inet4Address) {
                    ipType = "IPv4";
                } else if (addr instanceof Inet6Address) {
                    ipType = "IPv6";
                }

                System.out.println("  - IP: " + addr.getHostAddress());
                System.out.println("    + Loại IP:    " + ipType);
                System.out.println("    + Loopback:   " + addr.isLoopbackAddress());
                System.out.println("    + Site local: " + addr.isSiteLocalAddress());
            }
        } catch (UnknownHostException e) {
            System.err.println("  [LỖI HOST]: Không phân giải được hostname '" + hostnameArg + "'");
        }

        System.out.println("\n--------------------------------------------------");

        System.out.println("---> 2. PHÂN TÍCH URI: " + uriArg);
        try {
            URI uri = new URI(uriArg);

            if (uri.getScheme() == null) {
                throw new URISyntaxException(uriArg, "Cú pháp URI không hợp lệ (Thiếu Scheme)");
            }

            System.out.println("  - Scheme:   " + (uri.getScheme() != null ? uri.getScheme() : "Không có"));
            System.out.println("  - Host:     " + (uri.getHost() != null ? uri.getHost() : "Không có"));
            System.out.println("  - Port:     " + (uri.getPort() != -1 ? uri.getPort() : "Mặc định / Không có"));
            System.out.println("  - Path:     " + (uri.getPath() != null && !uri.getPath().isEmpty() ? uri.getPath() : "Không có"));
            System.out.println("  - Query:    " + (uri.getQuery() != null ? uri.getQuery() : "Không có"));
            System.out.println("  - Fragment: " + (uri.getFragment() != null ? uri.getFragment() : "Không có"));

        } catch (URISyntaxException e) {
            System.err.println("  [LỖI URI]: Chuỗi URI không đúng định dạng. " + e.getReason());
        }

        System.out.println("==================================================\n");
    }
}