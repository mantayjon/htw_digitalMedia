package ue01;

import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.NewImage;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

//erste Uebung (elementare Bilderzeugung)

public class GDM_ue01_s0585089 implements PlugIn {

    final static String[] choices = {
            "Schwarzes Bild",
            "Niederländische Fahne",
            "Gelb Tuerkis horizonaler Verlauf",
            "Vier Farben Verlauf",
            "Tschechische Fahne",
            "Japanische Fahne",
    };

    private String choice;

    public static void main(String args[]) {
        ImageJ ij = new ImageJ(); // neue ImageJ Instanz starten und anzeigen
        ij.exitWhenQuitting(true);

        GDM_ue01_s0585089 imageGeneration = new GDM_ue01_s0585089();
        imageGeneration.run("");
    }

    public void run(String arg) {

        int width  = 566;  // Breite
        int height = 400;  // Hoehe

        // RGB-Bild erzeugen
        ImagePlus imagePlus = NewImage.createRGBImage("ue01.GLDM_U1", width, height, 1, NewImage.FILL_BLACK);
        ImageProcessor ip = imagePlus.getProcessor();

        // Arrays fuer den Zugriff auf die Pixelwerte
        int[] pixels = (int[])ip.getPixels();

        dialog();

        ////////////////////////////////////////////////////////////////
        // Hier bitte Ihre Aenderungen / Erweiterungen

        if ( choice.equals("Schwarzes Bild") ) {
            generateBlackImage(width, height, pixels);
        }

        if ( choice.equals("Niederländische Fahne") ) {
            generateDutchFlag(width, height, pixels);
        }

        if ( choice.equals("Gelb Tuerkis horizonaler Verlauf") ) {
            generateYellowTurquoiseImage(width, height, pixels);
        }


        if ( choice.equals("Vier Farben Verlauf") ) {
            generateFourColorImage(width, height, pixels);
        }

        if ( choice.equals("Tschechische Fahne") ) {
            generateCzechFlag(width, height, pixels);
        }

        if ( choice.equals("Japanische Fahne") ) {
            generateJapanFlag(width, height, pixels);
        }

        ////////////////////////////////////////////////////////////////////

        // neues Bild anzeigen
        imagePlus.show();
        imagePlus.updateAndDraw();
    }

    private void generateBlackImage(int width, int height, int[] pixels) {
        // Schleife ueber die y-Werte
        for (int y=0; y<height; y++) {
            // Schleife ueber die x-Werte
            for (int x=0; x<width; x++) {
                int pos = y*width + x; // Arrayposition bestimmen

                int r = 0;
                int g = 0;
                int b = 0;

                // Werte zurueckschreiben
                pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
            }
        }
    }

    private void generateDutchFlag(int width, int height, int[] pixels) {
        // Schleife ueber die y-Werte
        for (int y=0; y<height; y++) {
            // Schleife ueber die x-Werte
            for (int x=0; x<width; x++) {
                int pos = y*width + x; // Arrayposition bestimmen

                //red
                int r = 255;
                int g = 0;
                int b = 0;

                //white
                if(y>=(height/3)) {
                    g = 255;
                    b = 255;
                }

                //blue
                if(y>=((height/3)*2)){
                    r = 0;
                    g = 0;
                    b = 255;
                }

                // Werte zurueckschreiben
                pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
            }
        }
    }

    private void generateYellowTurquoiseImage(int width, int height, int[] pixels) {
        // Schleife ueber die y-Werte
        for (int y=0; y<height; y++) {
            // Schleife ueber die x-Werte
            for (int x=0; x<width; x++) {
                int pos = y*width + x; // Arrayposition bestimmen

                //colors

                double XAchses = 255 * (double) x / (double) width;

                //red
                int r = 255 - (int) XAchses;
                //blue
                int b = (int) XAchses;
                //green
                int g = 255;

                // Werte zurueckschreiben
                pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
            }
        }
    }

    private void generateFourColorImage(int width, int height, int[] pixels) {
        // Schleife ueber die y-Werte
        for (int y=0; y<height; y++) {
            // Schleife ueber die x-Werte
            for (int x=0; x<width; x++) {
                int pos = y*width + x; // Arrayposition bestimmen


                double XAchses = 255 * (double) x / (double) width;
                double YAchses = 255 * (double) y / (double) height;

                //colors
                int g = 0;
                int r = (int) XAchses;
                int b = (int) YAchses;


                // Werte zurueckschreiben
                pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
            }
        }
    }

    private void generateCzechFlag(int width, int height, int[] pixels) {
        // Schleife ueber die y-Werte
        for (int y=0; y<height; y++) {
            // Schleife ueber die x-Werte
            for (int x=0; x<width; x++) {
                int pos = y*width + x; // Arrayposition bestimmen

                //White
                int r = 150;
                int g = 255;
                int b = 255;

                //red
                if(y >= height/2){
                    g = 0;
                    b = 0;
                }

                //blue
                if(y >= height/2 && x <= height-y ){
                    r = 0;
                    g = 0;
                    b = 255;
                }
                if(y < height/2 && x <= y ){
                    r = 0;
                    g = 0;
                    b = 255;
                }

                // Werte zurueckschreiben
                pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
            }
        }
    }

    private boolean inRange(int px, int py, int cx, int cy, int radius){
        double a = Math.abs(px - cx);
        double b = Math.abs(py - cy);
        double c = Math.abs(Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2)));

        if(c <= (double)radius){
            return true;
        }else{
            return false;
        }

    }

    private void generateJapanFlag(int width, int height, int[] pixels) {
        // Schleife ueber die y-Werte
        for (int y=0; y<height; y++) {
            // Schleife ueber die x-Werte
            for (int x=0; x<width; x++) {
                int pos = y*width + x; // Arrayposition bestimmen

                int r = 255;
                int g = 255;
                int b = 255;
                int radius = width/6;

                //System.out.println("inRange:"+inRange(x,y,width/2,height/2, radius));


                if(inRange(x,y,width/2,height/2, radius)){
                    g = 0;
                    b = 0;
                    r = 200;
                }

                // Werte zurueckschreiben
                pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
            }
        }
    }

    private void dialog() {
        // Dialog fuer Auswahl der Bilderzeugung
        GenericDialog gd = new GenericDialog("Bildart");

        gd.addChoice("Bildtyp", choices, choices[0]);


        gd.showDialog();	// generiere Eingabefenster

        choice = gd.getNextChoice(); // Auswahl uebernehmen

        if (gd.wasCanceled())
            System.exit(0);
    }
}
