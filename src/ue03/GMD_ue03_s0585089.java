package ue03;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Opens an image window and adds a panel below the image
 */
public class GMD_ue03_s0585089 implements PlugIn {

    ImagePlus imp; // ImagePlus object
    private int[] origPixels;
    private int width;
    private int height;

    String[] items = {"Original", "Rot-Kanal", "Graustufen", "Invertiert", "Binärbild", "Binärbild-3", "Binärbild-7",
            "vertikale Fehlerdiffusion", "Sepia-Färbung", "7-Farben"};


    public static void main(String args[]) {

        IJ.open("/Users/jonasmantay/Jonas/Studium/HTW/Semster_02/GDM/pictures/Bear.jpg");

        GMD_ue03_s0585089 pw = new GMD_ue03_s0585089();
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

            if (method.equals("Rot-Kanal")) {

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pos = y * width + x;
                        int argb = origPixels[pos];  // Lesen der Originalwerte

                        int r = (argb >> 16) & 0xff;
                        int g = (argb >> 8) & 0xff;
                        int b = argb & 0xff;

                        int rn = r;
                        int gn = 0;
                        int bn = 0;

                        // Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

                        pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
                    }
                }
            }

            if (method.equals("Invertiert")) {

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pos = y * width + x;
                        int argb = origPixels[pos];  // Lesen der Originalwerte

                        int r = (argb >> 16) & 0xff;
                        int g = (argb >> 8) & 0xff;
                        int b = argb & 0xff;

                        int rn = 255 - r;
                        int gn = 255 - g;
                        int bn = 255 - b;

                        // Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

                        pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
                    }
                }
            }

            if (method.equals("Graustufen")) {

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pos = y * width + x;
                        int argb = origPixels[pos];

                        int r = (argb >> 16) & 0xff;
                        int g = (argb >> 8) & 0xff;
                        int b = argb & 0xff;

                        int grey = (r + g + b) / 3;

                        int rn = grey;
                        int gn = grey;
                        int bn = grey;

                        pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
                    }
                }
            }

            if (method.equals("Binärbild")) {

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pos = y * width + x;
                        int argb = origPixels[pos];

                        int r = (argb >> 16) & 0xff;
                        int g = (argb >> 8) & 0xff;
                        int b = argb & 0xff;

                        int grey = (r + b + g) / 3;

                        if (grey < 127) {
                            grey = 0;
                        } else {
                            grey = 255;
                        }

                        int rn = grey;
                        int gn = grey;
                        int bn = grey;

                        pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
                    }
                }
            }

            if (method.equals("Binärbild-3")) {

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pos = y * width + x;
                        int argb = origPixels[pos];

                        int r = (argb >> 16) & 0xff;
                        int g = (argb >> 8) & 0xff;
                        int b = argb & 0xff;

                        int grey = (r + b + g) / 3;

                        if (grey < 85) {
                            grey = 0;
                        } else if (85 <= grey && grey < 170) {
                            grey = 127;
                        } else {
                            grey = 255;
                        }

                        int rn = grey;
                        int gn = grey;
                        int bn = grey;

                        pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
                    }
                }
            }

            if (method.equals("Binärbild-7")) {

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pos = y * width + x;
                        int argb = origPixels[pos];

                        int r = (argb >> 16) & 0xff;
                        int g = (argb >> 8) & 0xff;
                        int b = argb & 0xff;

                        int grey = (r + b + g) / 3;

                        if (grey < 36) {
                            grey = 0;
                        } else if (31 <= grey && grey < 62) {
                            grey = 42;
                        } else if (62 <= grey && grey < 93) {
                            grey = 84;
                        } else if (93 <= grey && grey < 124) {
                            grey = 126;
                        } else if (124 <= grey && grey < 155) {
                            grey = 168;
                        } else if (155 <= grey && grey < 186) {
                            grey = 205;
                        } else if (186 <= grey && grey < 217) {
                            grey = 210;
                        } else {
                            grey = 255;
                        }

                        int rn = grey;
                        int gn = grey;
                        int bn = grey;

                        pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
                    }
                }
            }

            if (method.equals("Sepia-Färbung")) {

                for (int y = 0; y < height; y++) {

                    for (int x = 0; x < width; x++) {
                        int pos = y * width + x;
                        int argb = origPixels[pos];

                        int r = (argb >> 16) & 0xff;
                        int g = (argb >> 8) & 0xff;
                        int b = argb & 0xff;

                        int rn = Math.min((int) (0.393 * r + 0.769 * g + 0.189 * b), 255);
                        int gn = Math.min((int) (0.349 * r + 0.686 * g + 0.168 * b), 255);
                        int bn = Math.min((int) (0.272 * r + 0.534 * g + 0.131 * b), 255);


                        pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
                    }
                }
            }

            if (method.equals("vertikale Fehlerdiffusion")) {

                int mistake = 0;

                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        int pos = y * width + x;
                        int argb = origPixels[pos];

                        int r = (argb >> 16) & 0xff;
                        int g = (argb >> 8) & 0xff;
                        int b = argb & 0xff;

                        int before = (r + g + b) / 3;
                        int nlight = before + mistake;

                        if (nlight > 127) {
                            mistake = nlight - 255;
                            before = 255;

                        } else {
                            mistake = nlight;
                            before = 0;

                        }

                        pixels[pos] = (0xFF << 24) | (before << 16) | (before << 8) | before;

                    }
                }


                if (method.equals("7-Farben")) {

                    for (int y = 0; y < height; y++) {
                        for (int x = 0; x < width; x++) {
                            int pos = y * width + x;
                            int argb = origPixels[pos];

                            int r = (argb >> 16) & 0xff;
                            int g = (argb >> 8) & 0xff;
                            int b = argb & 0xff;


                            int rn = r;
                            int gn = g;
                            int bn = b;

                            pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
                        }
                    }
                }

            }



            if (method.equals("7-Farben")) {

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pos = y * width + x;
                        int argb = origPixels[pos];  // Lesen der Originalwerte

                        int r = (argb >> 16) & 0xff;
                        int g = (argb >> 8) & 0xff;
                        int b = argb & 0xff;

                        int rn = r;
                        int gn = g;
                        int bn = b;


                        //schwarz
                        rn = 20;
                        gn = 20;
                        bn = 20;

                        //brown
                        rn = 60;
                        gn = 55;
                        bn = 50;

                        //light grey
                        rn = 80;
                        gn = 80;
                        bn = 80;

                        //light brown
                        rn = 145;
                        gn = 100;
                        bn = 70;

                        //rot
                        rn = 200;
                        gn = 100;
                        bn = 100;

                        //blau
                        rn = 55;
                        gn = 110;
                        bn = 150;

                        //snow
                        rn = 210;
                        gn = 210;
                        bn = 220;

                        pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
                    }
                }
            }

            /*if (method.equals("7-Farben")) {

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pos = y * width + x;
                        int argb = origPixels[pos];  // Lesen der Originalwerte

                        int r = (argb >> 16) & 0xff;
                        int g = (argb >> 8) & 0xff;
                        int b = argb & 0xff;

                        int rn = r;
                        int gn = g;
                        int bn = b;


                        if (r < 20 && g < 20 && b < 20) {
                            //schwarz
                            rn = 20;
                            gn = 20;
                            bn = 20;
                        } else if (r < 60 && g < 55 && b < 50) {
                            //brown
                            rn = 60;
                            gn = 55;
                            bn = 50;
                        } else if (r < 80 && g < 80 && b < 80) {
                            //light grey
                            rn = 80;
                            gn = 80;
                            bn = 80;
                        } else if (r < 145 && g < 100 && b < 70) {
                            //light brown
                            rn = 145;
                            gn = 100;
                            bn = 70;
                        } else if (r < 200 && g < 100 && b < 100) {
                            //rot
                            rn = 200;
                            gn = 100;
                            bn = 100;
                        }else if (r < 145 && g < 100 && b < 70) {
                            //blau
                            rn = 55;
                            gn = 110;
                            bn = 150;
                        } else if (r < 256 && g < 256 && b < 256) {
                            //snow
                            rn = 210;
                            gn = 210;
                            bn = 220;
                        }
                            pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
                        }
                    }
                }*/

            /* if (method.equals("Binärbild_multicolor")) {

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pos = y * width + x;
                        int argb = origPixels[pos];

                        int r = (argb >> 16) & 0xff;
                        int g = (argb >> 8) & 0xff;
                        int b = argb & 0xff;

                        Scanner sc = new Scanner(System.in);
                        System.out.println("How many different grayscales do you want to have?");
                        int in = sc.nextInt();


                        int n = Math.min(0, Math.max(in, 255));

                        int grey = greycolor(n);

                        int rn = grey;
                        int gn = grey;
                        int bn = grey;

                        pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
                    }
                }
            }
*/

            /*if (method.equals("6 Indexed Colors")) {
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pos = y * width + x;
                        int argb = origPixels[pos]; // Lesen der Originalwerte
                        int r = (argb >> 16) & 0xff;
                        int g = (argb >> 8) & 0xff;
                        int b = argb & 0xff;

                        // Wert wird Pixeln zugeordnet
                        int rn = 0;
                        int gn = 0;
                        int bn = 0;

                       //schwarz
                        if (r <= 41 || r > 41 && r <= 62 && g < 80) {
                            rn = 25;
                            gn = 29;
                            bn = 29;
                        }
                        // blau
                        else if (((r > 41 && r <= 62) && g > 80)
                                || ((r > 60 && r <= 100) && b > 100)) {
                            rn = 53;
                            gn = 105;
                            bn = 141;
                        }
                        // dunkelgrau
                        else if (r > 62 && r <= 94) {
                            rn = 62;
                            gn = 62;
                            bn = 60;
                        }
                        // braun
                        else if (r > 94 && r <= 134) {
                            rn = 108;
                            gn = 96;
                            bn = 86;
                        }
                        // mittelgrau
                        else if (r > 134 && r <= 180) {
                            rn = 152;
                            gn = 148;
                            bn = 146;
                        }
                        // hellgrau
                        else if (r > 180 && r <= 256) {
                            rn = 209;
                            gn = 207;
                            bn = 208;
                        }

                        // Hier muessen die neuen RGB-Werte wieder auf den
                        // Bereich von 0 bis 255 begrenzt werden

                        pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
                    }
                }
            }*/

        }
    }
}
