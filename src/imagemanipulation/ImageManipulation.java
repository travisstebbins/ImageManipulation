package imagemanipulation;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.imageio.ImageIO;

/**
 * Class that allows loading of an image and then manipulation
 * using various methods
 * 
 * @author Travis
 */
public class ImageManipulation {
    
    //variables
    static BufferedImage img = null;
    static BufferedImage postImg = null;
    static int width;
    static int height;
    static double kernel[][];
    
    //helper objects
    static Scanner kb = new Scanner(System.in);

    /**
     * Main method loads image, calls a manipulation function,
     * and exports the manipulated image
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        try {
            img = ImageIO.read(new File("pre_images/avocados.png"));
            width = img.getWidth();
            height = img.getHeight();
            postImg = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        } catch (IOException e) {
        }
        kernel = generateGaussianKernel(0.84089642, 10);
        convolute(kernel);
        export();
    }
    
    /**
     * Generates a 2D kernel array based on a given standard
     * deviation and radius
     * 
     * @param stdDev Gaussian function standard deviation
     * @param radius the blur radius
     * @return 2D kernel array
     */
    public static double[][] generateGaussianKernel(double stdDev, int radius) {
        double[][] kernel = new double[(2 * radius) + 1][(2 * radius) + 1];
        int origin = (int) (kernel.length / 2.0);
        for(int i = 0; i < kernel.length; i++) {
            for(int j = 0; j < kernel[0].length; j++) {
                int x = Math.abs(i - origin);
                int y = Math.abs(j - origin);
                kernel[i][j] = gaussian(x, y, stdDev);
            }
        }
        return kernel;
    }
    
    /**
     * Calculates the Gaussian filter kernel value at a specific pixel
     * based on the pixel's position and the Gaussian function's
     * standard deviation
     * 
     * @param x the pixel's horizontal position within the kernel
     * @param y the pixel's vertical position within the kernel
     * @param stdDev the Gaussian function's standard deviation
     * @return double value for the pixel's Gaussian kernel value
     */
    private static double gaussian(int x, int y, double stdDev) {
        return (1 / (2 * Math.PI * Math.pow(stdDev, 2))) * 
                Math.pow(Math.E, -((Math.pow(x, 2) + Math.pow(y, 2)) / (2 * Math.pow(stdDev, 2))));
    }
    
    /**
     * Convolutes a kernel on an image
     * 
     * @param kernel the kernel that is convoluted over the image; different kernels
     * produce different effects (i.e. gaussian blur, sharpen, etc.)
     */
    public static void convolute(double[][] kernel)
    {
        for(int i = (int) Math.floor(kernel.length / 2); i < width - (int) Math.floor(kernel.length / 2); i++) {
            for(int j = (int) Math.floor(kernel[0].length / 2); j < height - (int) Math.floor(kernel[0].length / 2); j++) {
                int rAccum = 0;
                int gAccum = 0;
                int bAccum = 0;
                for(int x = -(int) Math.floor(kernel.length / 2); x < (int) Math.floor(kernel.length / 2); x++) {
                    for(int y = 0 - (int) Math.floor(kernel[0].length / 2); y < (int) Math.floor(kernel[0].length / 2); y++) {
                        Color color = new Color(img.getRGB(i + x, j + y));
                        rAccum += kernel[x + (int) Math.floor(kernel.length / 2)][y + (int) Math.floor(kernel[0].length / 2)] * color.getRed();
                        gAccum += kernel[x + (int) Math.floor(kernel.length / 2)][y + (int) Math.floor(kernel[0].length / 2)] * color.getGreen();
                        bAccum += kernel[x + (int) Math.floor(kernel.length / 2)][y + (int) Math.floor(kernel[0].length / 2)] * color.getBlue();
                    }
                }
                postImg.setRGB(i, j, toRGB(rAccum, gAccum, bAccum));
            }
        }
    }
    
    /**
     * Converts r,g,b values to a usable int value
     * 
     * @param r input red value
     * @param g input green value
     * @param b input blue value
     * @return usable int representation of color
     */
    public static int toRGB(int r, int g, int b) {
        int red = r&255;
        int green = g&255;
        int blue = b&255;
        return (red << 16) | (green << 8) | blue;
    }
    
    /**
     * Exports the image to a file with a name given by the user
     * 
     * @throws IOException if the file location can't be found
     */
    public static void export() throws IOException {
        System.out.print("File name: ");
        String fileNameInput = kb.nextLine();
        String fileName = "post_images/" + fileNameInput + ".png";
        File f = new File(fileName);
        ImageIO.write(postImg,"PNG",f);
    }
    
}
