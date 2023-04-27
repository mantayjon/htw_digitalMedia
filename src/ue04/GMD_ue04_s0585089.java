package ue04;

import ij.*;
import ij.io.*;
import ij.process.*;
import ij.gui.*;
import ij.plugin.filter.*;


public class GMD_ue04_s0585089 implements PlugInFilter {

    protected ImagePlus imp;
    final static String[] choices = {"Wischen", "Weiche Blende", "Overlay(A Vordergrund)", "Overlay(B Vordergrund)",
            "Schieb-Blende", "Chroma-Keying", "eigene Überblendung"};

    public int setup(String arg, ImagePlus imp) {
        this.imp = imp;
        return DOES_RGB + STACK_REQUIRED;
    }

    public static void main(String args[]) {
        ImageJ ij = new ImageJ(); // neue ImageJ Instanz starten und anzeigen
        ij.exitWhenQuitting(true);

        IJ.open("/Users/jonasmantay/Jonas/Studium/HTW/Semster_02/GDM/pictures/StackB.tif");

        GMD_ue04_s0585089 sd = new GMD_ue04_s0585089();
        sd.imp = IJ.getImage();
        ImageProcessor B_ip = sd.imp.getProcessor();
        sd.run(B_ip);
    }

    public void run(ImageProcessor B_ip) {
        // Film B wird uebergeben
        ImageStack stack_B = imp.getStack();

        int length = stack_B.getSize();
        int width = B_ip.getWidth();
        int height = B_ip.getHeight();

        // ermoeglicht das Laden eines Bildes / Films
        Opener o = new Opener();
        /* OpenDialog od_A = new OpenDialog("Auswählen des 2. Filmes ...",  "");

        // Film A wird dazugeladen
        String dateiA = od_A.getFileName();
        if (dateiA == null) return; // Abbruch
        String pfadA = od_A.getDirectory();*/
        ImagePlus A = o.openImage("/Users/jonasmantay/Jonas/Studium/HTW/Semster_02/GDM/pictures/Stacka.tif");
        if (A == null) return; // Abbruch

        ImageProcessor A_ip = A.getProcessor();
        ImageStack stack_A = A.getStack();

        if (A_ip.getWidth() != width || A_ip.getHeight() != height) {
            IJ.showMessage("Fehler", "Bildgrößen passen nicht zusammen");
            return;
        }

        // Neuen Film (Stack) "Erg" mit der kleineren Laenge von beiden erzeugen
        length = Math.min(length, stack_A.getSize());

        ImagePlus Erg = NewImage.createRGBImage("Ergebnis", width, height, length, NewImage.FILL_BLACK);
        ImageStack stack_Erg = Erg.getStack();

        // Dialog fuer Auswahl des Ueberlagerungsmodus
        GenericDialog gd = new GenericDialog("Überlagerung");
        gd.addChoice("Methode", choices, "");
        gd.showDialog();

        int methode = 0;
        String s = gd.getNextChoice();
        if (s.equals("Wischen")) methode = 1;
        if (s.equals("Weiche Blende")) methode = 2;
        if (s.equals("Overlay(A Vordergrund)")) methode = 3;
        if (s.equals("Overlay(B Vordergrund)")) methode = 4;
        if (s.equals("Schieb-Blende")) methode = 5;
        if (s.equals("Chroma-Keying")) methode = 6;
        if (s.equals("eigene Überblendung")) methode = 7;

        // Arrays fuer die einzelnen Bilder
        int[] pixels_B;
        int[] pixels_A;
        int[] pixels_Erg;


        // Schleife ueber alle Bilder
        for (int z = 1; z <= length; z++) {
            pixels_B = (int[]) stack_B.getPixels(z);
            pixels_A = (int[]) stack_A.getPixels(z);
            pixels_Erg = (int[]) stack_Erg.getPixels(z);

            int pos = 0;
            for (int y = 0; y < height; y++)
                for (int x = 0; x < width; x++, pos++) {
                    int cA = pixels_A[pos];
                    int rA = (cA & 0xff0000) >> 16;
                    int gA = (cA & 0x00ff00) >> 8;
                    int bA = (cA & 0x0000ff);

                    int cB = pixels_B[pos];
                    int rB = (cB & 0xff0000) >> 16;
                    int gB = (cB & 0x00ff00) >> 8;
                    int bB = (cB & 0x0000ff);

                    if (methode == 1) { //Wischen

                        if (y + 1 > (z - 1) * (double) width / (length - 1))
                            pixels_Erg[pos] = pixels_B[pos];
                        else
                            pixels_Erg[pos] = pixels_A[pos];
                    }

                    if (methode == 2) {//Weiche Blende

                        double opacity = ((1d / (length-1)) * (z-1)) ;

                        int r = (int) (rA * opacity + rB * (1 - opacity));
                        int g = (int) (gA * opacity + gB * (1 - opacity));
                        int b = (int) (bA * opacity + bB * (1 - opacity));

                        pixels_Erg[pos] = 0xFF000000 + ((r & 0xff) << 16) + ((g & 0xff) << 8) + (b & 0xff);
                    }

                    if (methode == 3) {//Overlay A Vordergrund

                        int r;
                        int g;
                        int b;

                        if (rA <= 128) {

                            r = (rB * rA) / 128;

                        } else {
                            r = 255 - ((255 - rB) * (255 - rA) / 128);
                        }

                        if (gA <= 128) {

                            g = (gB * gA) / 128;

                        } else {
                            g = 255 - ((255 - gB) * (255 - gA) / 128);
                        }

                        if (bA <= 128) {

                            b = (bB * bA) / 128;

                        } else {
                            b = 255 - ((255 - bB) * (255 - bA) / 128);
                        }
                        pixels_Erg[pos] = 0xFF000000 + ((r & 0xff) << 16) + ((g & 0xff) << 8) + (b & 0xff);

                    }

                    if (methode == 4) {//Overlay B Vordergrund

                        int r;
                        int g;
                        int b;

                        if (rB <= 128) {

                            r = (rA * rB) / 128;

                        } else {
                            r = 255 - ((255 - rA) * (255 - rB) / 128);
                        }

                        if (gB <= 128) {

                            g = (gA * gB) / 128;

                        } else {
                            g = 255 - ((255 - gA) * (255 - gB) / 128);
                        }

                        if (bB <= 128) {

                            b = (bA * bB) / 128;

                        } else {
                            b = 255 - ((255 - bA) * (255 - bB) / 128);
                        }
                        pixels_Erg[pos] = 0xFF000000 + ((r & 0xff) << 16) + ((g & 0xff) << 8) + (b & 0xff);

                    }

                    if (methode == 5) { //Schieb-blende

                        int reglerPixel = (z - 1) * width / (length - 1);
                        int posNew = pos - reglerPixel;

                        if (x + 1 > reglerPixel) {
                            pixels_Erg[pos] = pixels_B[posNew];

                        } else {
                            if (posNew < 0) {
                                posNew = 0;
                            }
                            pixels_Erg[pos] = pixels_A[posNew];
                        }

                    }

                    if (methode == 6) { // Chroma-Keying

                        int rOrange = 220;
                        int gOrange = 170;
                        int bOrange = 65;

                        double distance = Math.sqrt(Math.pow(rOrange - rA, 2) + Math.pow(gOrange - gA, 2) +
                                Math.pow(bOrange - bA, 2));

                        if (distance < 90) {
                            pixels_Erg[pos] = pixels_B[pos];
                        } else {
                            pixels_Erg[pos] = pixels_A[pos];
                        }
                    }

                    if (methode == 7) { //eigene Überblendung

                        int reglerPixel = (z - 1) * width / (length - 1);
                        int distance;
                        double mittelPunktX = width / 2;
                        double mittelPunktY = height / 2;
                        double radius = reglerPixel/ 1.3;


                        distance = (int) (Math.sqrt(Math.pow(mittelPunktX - x, 2)
                                + Math.pow(mittelPunktY - y, 2)));

                        if (distance < radius) {
                            pos = (y * width + x);
                            pixels_Erg[pos] = pixels_A[pos];
                        } else {
                            pixels_Erg[pos] = pixels_B[pos];
                        }
                    }
                }
        }

        // neues Bild anzeigen
        Erg.show();
        Erg.updateAndDraw();

    }

}



