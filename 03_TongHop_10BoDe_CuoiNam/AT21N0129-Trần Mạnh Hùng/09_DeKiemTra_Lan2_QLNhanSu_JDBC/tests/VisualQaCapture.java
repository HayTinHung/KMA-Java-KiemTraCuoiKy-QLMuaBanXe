import de2.QuanLyNhanSuGUI;

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

public final class VisualQaCapture {
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeLater(() -> {
            QuanLyNhanSuGUI gui = new QuanLyNhanSuGUI();
            gui.setVisible(true);
            new Thread(() -> {
                try {
                    Thread.sleep(1200); // Đợi SwingWorker load CSDL xong
                    SwingUtilities.invokeAndWait(() -> {
                        try {
                            Component content = gui.getContentPane();
                            BufferedImage image = new BufferedImage(content.getWidth(), content.getHeight(), BufferedImage.TYPE_INT_RGB);
                            Graphics2D g2 = image.createGraphics();
                            content.paintAll(g2);
                            g2.dispose();
                            ImageIO.write(image, "PNG", new File("preview.png"));
                            System.out.println("Preview captured successfully: " + content.getWidth() + "x" + content.getHeight());
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        } finally {
                            gui.dispose();
                            System.exit(0);
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.exit(1);
                }
            }).start();
        });
    }
}
