package remotedesktop.client;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ReceivingScreen extends JFrame {

    private DataInputStream dataInputStream;
    private Socket socket;
    private JLabel imageLabel; // JLabel để hiển thị ảnh
    private volatile boolean running = true; // Kiểm soát vòng lặp

    public ReceivingScreen(Socket socket) {
        this.socket = socket;
        
        // Thiết lập JFrame
        setTitle("Remote Desktop Viewer");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Tạo JLabel để hiển thị ảnh
        imageLabel = new JLabel();
        add(imageLabel, BorderLayout.CENTER);
        
        setVisible(true);
        
        try {
            dataInputStream = new DataInputStream(socket.getInputStream());
            startReceiving();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startReceiving() {
        // Nhận và hiển thị ảnh liên tục từ server trong luồng riêng
        new Thread(() -> {
            while (running && !socket.isClosed()) {
                try {
                    // Đọc kích thước dữ liệu ảnh
                    int length = dataInputStream.readInt();
                    if (length > 0) {
                        // Tạo mảng byte để chứa dữ liệu ảnh
                        byte[] imageBytes = new byte[length];
                        dataInputStream.readFully(imageBytes);

                        // Chuyển đổi byte array thành BufferedImage
                        BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageBytes));

                        // Hiển thị ảnh trên JLabel
                        if (img != null) {
                            ImageIcon icon = new ImageIcon(img);
                            imageLabel.setIcon(icon);
                            imageLabel.repaint(); // Cập nhật JLabel
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Error receiving data: " + e.getMessage());
                    break;
                }
            }
            closeResources();
        }).start();
    }

    // Dừng nhận dữ liệu và đóng tài nguyên
    public void stopReceiving() {
        running = false;
        closeResources();
    }

    // Đóng các tài nguyên
    private void closeResources() {
        try {
            if (dataInputStream != null) dataInputStream.close();
            if (socket != null && !socket.isClosed()) socket.close();
            System.out.println("Resources closed.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
