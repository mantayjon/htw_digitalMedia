package ue05;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

import java.awt.Panel;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;

/**
 * Opens an image window and adds a panel below the image
 */
public class GMD_ue05_s0585089 implements PlugIn {

    ImagePlus imp; // ImagePlus object
    private int[] origPixels;
    private int width;
    private int height;

    String[] items = {"Original", "Weichzeichner", "Hochpassfilter (Katendetektion)",
            "verstärkte Kanten (kontrast höher)"};


    public static void main(String args[]) {

        IJ.open("/Users/jonasmantay/Jonas/Studium/HTW/Semster_02/GDM/pictures/sail.jpg");

        GMD_ue05_s0585089 pw = new GMD_ue05_s0585089();
        pw.imp = IJ.getImage();
        pw.run("");
    }

    public void run(String arg) {
        if (imp == null)
            imp = WindowManager.getCurrentImage();
        if (imp == null) {
            return;
        }
        CustomCanvas cc = new CustomCanvas(imp);

        storePixelValues(imp.getProcessor());

        new CustomWindow(imp, cc);
    }


    private void storePixelValues(ImageProcessor ip) {
        width = ip.getWidth();
        height = ip.getHeight();

        origPixels = ((int[]) ip.getPixels()).clone();
    }


    class CustomCanvas extends ImageCanvas {

        CustomCanvas(ImagePlus imp) {
            super(imp);
        }

    } // CustomCanvas inner class


    class CustomWindow extends ImageWindow implements ItemListener {

        private String method;

        CustomWindow(ImagePlus imp, ImageCanvas ic) {
            super(imp, ic);
            addPanel();
        }

        void addPanel() {
            //JPanel panel = new JPanel();
            Panel panel = new Panel();

            JComboBox cb = new JComboBox(items);
            panel.add(cb);
            cb.addItemListener(this);

            add(panel);
            pack();
        }

        public void itemStateChanged(ItemEvent evt) {

            // Get the affected item
            Object item = evt.getItem();

            if (evt.getStateChange() == ItemEvent.SELECTED) {
                System.out.println("Selected: " + item.toString());
                method = item.toString();
                changePixelValues(imp.getProcessor());
                imp.updateAndDraw();
            }

        }


        private void changePixelValues(ImageProcessor ip) {

            // Array zum Zurückschreiben der Pixelwerte
            int[] pixels = (int[]) ip.getPixels();

            if (method.equals("Original")) {

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pos = y * width + x;

                        pixels[pos] = origPixels[pos];
                    }
                }
            }

            if (method.equals("Weichzeichner")) {

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pos = y * width + x;

                        double[] filter = {
                                1d / 9, 1d / 9, 1d / 9,
                                1d / 9, 1d / 9, 1d / 9,
                                1d / 9, 1d / 9, 1d / 9,
                        };

                        int[] r = pixelValue(x, y, "r");
                        int rn = applyFilter(r, filter);

                        int[] g = pixelValue(x, y, "g");
                        int gn = applyFilter(g, filter);

                        int[] b = pixelValue(x, y, "b");
                        int bn = applyFilter(b, filter);

                        pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
                    }
                }

            }

            if (method.equals("Hochpassfilter (Katendetektion)")) {

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pos = y * width + x;

                        double[] filter = {
                                -1 / 9f, -1 / 9f, -1 / 9f,
                                -1 / 9f, 8 / 9f, -1 / 9f,
                                -1 / 9f, -1 / 9f, -1 / 9f,
                        };

                        int[] red = pixelValue(x, y, "r");
                        int rn = applyFilter(red, filter);
                        rn += 50;
                        rn = limitBrightness(rn);

                        int[] green = pixelValue(x, y, "g");
                        int gn = applyFilter(green, filter);
                        gn += 50;
                        gn = limitBrightness(gn);

                        int[] blue = pixelValue(x, y, "b");
                        int bn = applyFilter(blue, filter);
                        bn += 50;
                        bn = limitBrightness(bn);

                        pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
                    }
                }
            }

            if (method.equals("verstärkte Kanten (kontrast höher)")) {

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pos = y * width + x;

                        double[] filter = new double[]{
                                -1d / 9, -1d / 9, -1d / 9,
                                -1d / 9, 17d / 9, -1d / 9,
                                -1d / 9, -1d / 9, -1d / 9,
                        };

                        int[] red = pixelValue(x, y, "r");
                        int rn = applyFilter(red, filter);
                        rn = limitBrightness(rn);

                        int[] green = pixelValue(x, y, "g");
                        int gn = applyFilter(green, filter);

                        gn = limitBrightness(gn);

                        int[] blue = pixelValue(x, y, "b");
                        int bn = applyFilter(blue, filter);
                        bn = limitBrightness(bn);

                        pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
                    }
                }


            }
        }

        public int[] pixelValue(int x, int y, String color) {
            int[] arr = new int[9];
            int i = 0;

            for (int col = -1; col < 2; col++) {
                for (int row = -1; row < 2; row++, i++) {

                    int argb = pixelToFilter(x + row, y + col);

                    if (color.equals("r")) {
                        arr[i] = (argb >> 16) & 0xff;
                    }
                    if (color.equals("g")) {
                        arr[i] = (argb >> 8) & 0xff;
                    }
                    if (color.equals("b")) {
                        arr[i] = argb & 0xff;
                    }

                }
            }
            return arr;
        }

        public int pixelToFilter(int x, int y) {
            if (y < 0) {
                y = 0;
            } else if (y >= height) {
                y = height - 1;
            }
            if (x < 0) {
                x = 0;
            } else if (x >= width) {
                x = width - 1;
            }
            int pos = y * width + x;
            return origPixels[pos];
        }

        public int applyFilter(int[] arr, double[] filter) {
            double newValue = 0;
            for (int i = 0; i < 9; i++) {
                newValue += arr[i] * filter[i];
            }
            return (int) newValue;
        }

        public int limitBrightness(int pixel) {

            if (pixel > 255) {
                pixel = 255;
            } else if (pixel < 0) {
                pixel = 0;
            }

            return pixel;
        }

    } // CustomWindow inner class
}