package ClientSide.GUI;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Class dedicated to resizing images for Java Swing
 */
public class ResizeImg {

    /**
     * Resizes images
     * @see <a href="https://stackoverflow.com/questions/244164/how-can-i-resize-an-image-using-java">From Stack Overflow</a>
     * @param originalImg the original image
     * @param width the new width
     * @param height the new height
     * @param preserveAlpha a.k.a. keep transparency
     * @return the same image but resized
     */
    public BufferedImage resizeImg(Image originalImg, int width, int height, boolean preserveAlpha) {
        //System.out.println("resizing...");
        int imageType = preserveAlpha ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage scaledBI = new BufferedImage(width, height, imageType);
        Graphics2D g = scaledBI.createGraphics();
        if (preserveAlpha) {
            g.setComposite(AlphaComposite.Src);
        }
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(originalImg, 0, 0, width, height, null);
        g.dispose();
        return scaledBI;
    }
}
