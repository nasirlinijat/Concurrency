import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;

public class Pixelate {
    static class ImagePanel extends JPanel{
        BufferedImage image;

        ImagePanel(BufferedImage image){
            this.image = image;
        }

        public void paintComponent(Graphics g){
            super.paintComponent(g);
            double scaleX = (double) getWidth() / image.getWidth();
            double scaleY = (double) getHeight() / image.getHeight();
            double scale = Math.min(scaleX, scaleY);
            int newWidth = (int) (image.getWidth() * scale);
            int newHeight = (int) (image.getHeight() * scale);
            g.drawImage(image, 0, 0, newWidth, newHeight, null);
        }
    }

    static class Worker extends Thread{
        BufferedImage image;
        int size;
        int startY;
        int endY;

        Worker(BufferedImage image, int size, int startY, int endY){
            this.image = image;
            this.size = size;
            this.startY = startY;
            this.endY = endY;
        }

        public void run(){
            for(int y = startY; y < endY; y += size){
                for(int x = 0; x < image.getWidth(); x += size){
                    averageBlock(image, x, y, size);
                }
            }
        }
    }

    static void averageBlock(BufferedImage image, int x, int y, int size){
        int width = Math.min(size, image.getWidth() - x);
        int height = Math.min(size, image.getHeight() - y);
        long sumR = 0;
        long sumG = 0;
        long sumB = 0;

        for(int j = y; j < y + height; j++){
            for(int i = x; i < x + width; i++){
                int pixel = image.getRGB(i, j);
                sumR += (pixel >> 16) & 255;
                sumG += (pixel >> 8) & 255;
                sumB += pixel & 255;
            }
        }

        int count = width * height;
        int r = (int) (sumR / count);
        int g = (int) (sumG / count);
        int b = (int) (sumB / count);
        int average = (r << 16) | (g << 8) | b;

        for(int j = y; j < y + height; j++){
            for(int i = x; i < x + width; i++){
                image.setRGB(i, j, average);
            }
        }
    }

    static boolean anyAlive(Worker[] workers){
        for(Worker worker : workers){
            if(worker.isAlive()){
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) throws Exception{
        if(args.length != 3 || !(args[2].equals("S") || args[2].equals("M"))) {
            System.out.println("Usage: java Pixelate.java <file.jpg> <square_size> <S|M>");
            return;
        }

        BufferedImage original =ImageIO.read(new File(args[0]));
        int size = Integer.parseInt(args[1]);
        if(original == null || size < 1) {
            System.out.println("Could not read the image or invalid square size.");
            return;
        }

        BufferedImage image =  new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_INT_RGB);
        image.getGraphics().drawImage(original, 0, 0, null);

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        double scale = Math.min(1.0, Math.min(screen.width * 0.9 / image.getWidth(), screen.height * 0.85 / image.getHeight()));

        ImagePanel panel = new ImagePanel(image);
        panel.setPreferredSize(new Dimension((int) (image.getWidth() * scale), (int) (image.getHeight() * scale)));

        JFrame frame = new JFrame("Averaging");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);

        int threadCount = 1;
        if(args[2].equals("M")){
            threadCount = Runtime.getRuntime().availableProcessors();
        }

        int blockRows = (image.getHeight() + size - 1) / size;
        int rowsPerThread = (blockRows + threadCount - 1) / threadCount;

        Worker[] workers = new Worker[threadCount];
        for(int i = 0; i < threadCount; i++){
            int startY = i * rowsPerThread * size;
            int endY = Math.min((i + 1) * rowsPerThread * size, image.getHeight());
            workers[i] = new Worker(image, size, startY, endY);
            workers[i].start();
        }

        while(anyAlive(workers)){
            panel.repaint();
            Thread.sleep(30);
        }
        panel.repaint();

        ImageIO.write(image, "jpg", new File("result.jpg"));
        System.out.println("Saved result.jpg");
    }
}
