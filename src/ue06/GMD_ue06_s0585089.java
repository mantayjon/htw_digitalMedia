package ue06;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.NewImage;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;


public class GMD_ue06_s0585089 implements PlugInFilter {

    ImagePlus imp; // ImagePlus object


    public static void main(String args[]) {
        GMD_ue06_s0585089 scale = new GMD_ue06_s0585089();
        IJ.open("/Users/jonasmantay/Jonas/Studium/HTW/Semster_02/GDM/pictures/component.jpg");

        scale.imp = IJ.getImage();
        ImageProcessor ip = scale.imp.getProcessor();
        scale.run(ip);
    }


    public int setup(String arg, ImagePlus imp) {
        if (arg.equals("about")) {
            showAbout();
            return DONE;
        }
        return DOES_RGB + NO_CHANGES;
        // kann RGB-Bilder und veraendert das Original nicht
    }

    public void run(ImageProcessor ip) {
        int width = ip.getWidth();  // Breite bestimmen
        int height = ip.getHeight(); // Hoehe bestimmen

        String[] dropDownMenu = {"Kopie", "Pixelwiederholung", "Bilinear"};
        String dropDown = "";

        GenericDialog gd = new GenericDialog("scale");
        gd.addChoice("Methode", dropDownMenu, dropDownMenu[0]);
        gd.addNumericField("Hoehe:", 500, 0);
        gd.addNumericField("Breite:", 600, 0);
        gd.showDialog();
        dropDown = gd.getNextChoice();

        int height_n = (int) gd.getNextNumber(); // _n fuer das neue skalierte Bild
        int width_n = (int) gd.getNextNumber();


        //height_n = height;
        //width_n  = width;

        ImagePlus neu = NewImage.createRGBImage("Skaliertes Bild",
                width_n, height_n, 1, NewImage.FILL_BLACK);

        ImageProcessor ip_n = neu.getProcessor();


        int[] pix = (int[]) ip.getPixels();
        int[] pix_n = (int[]) ip_n.getPixels();

        if (dropDown.equals("Kopie")) {
            // Schleife ueber das neue Bild
            for (int y_n = 0; y_n < height_n; y_n++) {
                for (int x_n = 0; x_n < width_n; x_n++) {
                    int y = y_n;
                    int x = x_n;

                    if (y < height && x < width) {
                        int pos_n = y_n * width_n + x_n;
                        int pos = y * width + x;

                        pix_n[pos_n] = pix[pos];
                    }
                }
            }
        }

        if (dropDown.equals("Pixelwiederholung")) {

            double x_factor = (width-1)/(double)width_n;
            double y_factor = (height-1)/(double)height_n;

            for (int y_n = 0; y_n < height_n; y_n++) {
                for (int x_n = 0; x_n < width_n; x_n++) {

                    int y = y_n;
                    int x = x_n;

                    int pos_n = y_n * width_n + x_n;
                    int pos = (int)(Math.round(y_n * y_factor) * width + Math.round(x_n * x_factor));

                    pix_n[pos_n] = pix[pos];


                }
            }
        }

        if (dropDown.equals("Bilinear")) {

            int A;
            int B;
            int C;
            int D;
            int x;
            int y;
            int index;

            float v;
            float h;
            float b;
            float r;
            float g;

            float x_faktor = ((float) (width - 1)) / width_n;
            float y_faktor = ((float) (height - 1)) / height_n;

            int i = 0;

            for (int y_n = 0; y_n < height_n; y_n++) {
                for (int x_n = 0; x_n < width_n; x_n++) {

                    x = (int) (x_faktor * x_n);
                    y = (int) (y_faktor * y_n);

                    v = (x_faktor * x_n) - x;
                    h = (y_faktor * y_n) - y;

                    index = y * width + x;

                    A = pix[index];
                    B = pix[index + 1];
                    C = pix[index + width];
                    D = pix[index + width + 1];

                    b = (A & 0xff) * (1 - v) * (1 - h) + (B & 0xff) * v * (1 - h) + (C & 0xff) * h * (1 - v) +
                            (D & 0xff) * (v * h);

                    g = ((A >> 8) & 0xff) * (1 - v) * (1 - h) + ((B >> 8) & 0xff) * v * (1 - h) + ((C >> 8) & 0xff)
                            * h * (1 - v) + ((D >> 8) & 0xff) * (v * h);

                    r = ((A >> 16) & 0xff) * (1 - v) * (1 - h) + ((B >> 16) & 0xff) * v * (1 - h) + ((C >> 16) & 0xff)
                            * h * (1 - v) + ((D >> 16) & 0xff) * (v * h);

                    pix_n[i++] = (0xFF << 24) | ((((int) r) << 16) & 0xff0000) | ((((int) g) << 8) & 0xff00) | ((int) b);

                }
            }
        }
        // neues Bild anzeigen
        neu.show();
        neu.updateAndDraw();
    }

    void showAbout() {
        IJ.showMessage("");
    }
}

