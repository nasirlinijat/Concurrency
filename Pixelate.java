import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class Pixelate {
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
        averageBlock(image, 0, 0, size);
        System.out.println("First block color: " + Integer.toHexString(image.getRGB(0, 0) & 0xFFFFFF));
    }
}
