import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class Pixelate {
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
        System.out.println("Loaded " + image.getWidth() + "x" + image.getHeight() + ", square size " + size + ", mode " + args[2]);
    }
}
