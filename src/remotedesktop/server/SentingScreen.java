package remotedesktop.server;

import java.net.Socket;
import javax.imageio.ImageIO;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.SocketException;

public class SentingScreen extends Thread {
    private Socket socket;
    private Robot robot;
    private Rectangle rectangle;
    private DataOutputStream dos;

    public SentingScreen(Socket socket, Robot robot, Rectangle rectangle) {
        this.socket = socket;
        this.robot = robot;
        this.rectangle = rectangle;
        start();
    }

    public void run() {
        try {
            dos = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        while (true) {
            try {
                // Capture the screen within the specified rectangle
                BufferedImage image = robot.createScreenCapture(rectangle);
                
                // Convert the image to a byte array
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(image, "png", baos);
                byte[] imageBytes = baos.toByteArray();

                // Send the length of the byte array first
                dos.writeInt(imageBytes.length);
                // Send the actual image data
                dos.write(imageBytes);
                dos.flush();

            } catch (SocketException e) {
                System.out.println("Client đã ngắt kết nối. Dừng truyền màn hình.");
                break;
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }

            // Sleep to limit the frame rate (e.g., ~30 FPS)
            try {
                Thread.sleep(34);
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
