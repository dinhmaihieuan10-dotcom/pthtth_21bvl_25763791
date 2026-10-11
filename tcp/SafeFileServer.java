package tcp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;

public class SafeFileServer {
    private static final int PORT = 5008;
    private static final String UPLOAD_DIR = "upload";

    public static void main(String[] args) {
        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        System.out.println("[File Server] Đang lắng nghe tại port " + PORT);
        try (ServerSocket server = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = server.accept();
                new Thread(() -> handleFileUpload(socket)).start();
            }
        } catch (IOException e) {
            System.err.println("[File Server] Lỗi: " + e.getMessage());
        }
    }

    private static void handleFileUpload(Socket socket) {
        try (DataInputStream dis = new DataInputStream(socket.getInputStream());
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {

            String header = dis.readUTF();
            String[] parts = header.split("\\|");
            if (parts.length != 3) {
                dos.writeUTF("ERR INVALID_HEADER");
                return;
            }

            String rawFileName = parts[0];
            long fileSize = Long.parseLong(parts[1]);
            String expectedHash = parts[2];

            String safeFileName = new File(rawFileName).getName();
            File saveFile = new File(UPLOAD_DIR, safeFileName);

            System.out.printf("[Đang nhận file]: %s (Kích thước: %d bytes)\n", safeFileName, fileSize);

            MessageDigest md = MessageDigest.getInstance("SHA-256");

            try (FileOutputStream fos = new FileOutputStream(saveFile)) {
                byte[] buffer = new byte[4096];
                long bytesRemaining = fileSize;

                while (bytesRemaining > 0) {
                    int readLen = (int) Math.min(buffer.length, bytesRemaining);
                    int read = dis.read(buffer, 0, readLen); // Đọc trực tiếp từ dis
                    if (read == -1) break;

                    fos.write(buffer, 0, read);
                    md.update(buffer, 0, read); // Cập nhật mã băm SHA-256 cho chuỗi byte vừa đọc
                    bytesRemaining -= read;
                }
            }

            String calculatedHash = bytesToHex(md.digest());

            if (calculatedHash.equalsIgnoreCase(expectedHash)) {
                System.out.println("[Thành công]: File " + safeFileName + " hợp lệ (SHA-256 Match).");
                dos.writeUTF("OK FILE_RECEIVED_SUCCESSFULLY");
            } else {
                System.err.println("[Thất bại]: File " + safeFileName + " bị hỏng hoặc sai mã Hash!");
                dos.writeUTF("ERR HASH_MISMATCH");
            }

        } catch (Exception e) {
            System.err.println("[Lỗi truyền file]: " + e.getMessage());
        } finally {
            try {
                if (!socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException ignored) {}
        }
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}