package ue02;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Panel;

import javax.swing.BorderFactory;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 Opens an image window and adds a panel below the image
 */
public class GDM_ue02_s0585089 implements PlugIn {

    ImagePlus imp; // ImagePlus object
    private int[] origPixels;
    private int width;
    private int height;


    public static void main(String args[]) {
        //new ImageJ();
        //IJ.open("/users/barthel/applications/ImageJ/_images/orchid.jpg");
        IJ.open("/Users/jonasmantay/Jonas/Studium/HTW/Semster_02/GDM/pictures/orchid.jpg");

        GDM_ue02_s0585089 pw = new GDM_ue02_s0585089();
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


    class CustomWindow extends ImageWindow implements ChangeListener {

        private JSlider jSliderBrightness;

        private JSlider jSliderContrast;

        private JSlider jSliderSaturation;

        private JSlider jSliderHue;
        private double brightness;
        private double saturation = 10;
        private double hue = 0;
        private double contrast = 10;

        CustomWindow(ImagePlus imp, ImageCanvas ic) {
            super(imp, ic);
            addPanel();
        }

        void addPanel() {
            //JPanel panel = new JPanel();
            Panel panel = new Panel();

            panel.setLayout(new GridLayout(4, 1));
            jSliderBrightness = makeTitledSilder("Helligkeit", -128, 128, 0);
            jSliderContrast = makeTitledSilder("Kontrast", 0, 100, 10);
            jSliderSaturation = makeTitledSilder("Saturation", 0, 50, 10);
            jSliderHue = makeTitledSilder("Hue", 0, 360, 0);
            panel.add(jSliderBrightness);
            panel.add(jSliderContrast);
            panel.add(jSliderSaturation);
            panel.add(jSliderHue);


            add(panel);

            pack();
        }

        private JSlider makeTitledSilder(String string, int minVal, int maxVal, int val) {

            JSlider slider = new JSlider(JSlider.HORIZONTAL, minVal, maxVal, val);
            Dimension preferredSize = new Dimension(width, 50);
            slider.setPreferredSize(preferredSize);
            TitledBorder tb = new TitledBorder(BorderFactory.createEtchedBorder(),
                    string, TitledBorder.LEFT, TitledBorder.ABOVE_BOTTOM,
                    new Font("Sans", Font.PLAIN, 11));
            slider.setBorder(tb);
            slider.setMajorTickSpacing((maxVal - minVal) / 10);
            slider.setPaintTicks(true);
            slider.addChangeListener(this);

            return slider;
        }

        private void setSliderTitle(JSlider slider, String str) {
            TitledBorder tb = new TitledBorder(BorderFactory.createEtchedBorder(),
                    str, TitledBorder.LEFT, TitledBorder.ABOVE_BOTTOM,
                    new Font("Sans", Font.PLAIN, 11));
            slider.setBorder(tb);
        }

        public void stateChanged(ChangeEvent e) {
            JSlider slider = (JSlider) e.getSource();

            if (slider == jSliderBrightness) {
                brightness = slider.getValue();
                String str = "Helligkeit " + brightness;
                setSliderTitle(jSliderBrightness, str);
            }


            if (slider == jSliderContrast){
                contrast = slider.getValue();
                String str = "Kontrast " + contrast;
                setSliderTitle(jSliderContrast, str);
            }

            if (slider == jSliderSaturation){
                saturation = slider.getValue();
                String str = "Saturarion " + saturation;
                setSliderTitle(jSliderSaturation, str);
            }



            if (slider == jSliderHue){
                hue = slider.getValue();
                String str = "Hue " + hue;
                setSliderTitle(jSliderSaturation, str);
            }


            changePixelValues(imp.getProcessor());

            imp.updateAndDraw();
        }


        private void changePixelValues(ImageProcessor ip) {

            // Array fuer den Zugriff auf die Pixelwerte
            int[] pixels = (int[]) ip.getPixels();

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int pos = y * width + x;
                    int argb = origPixels[pos];  // Lesen der Originalwerte

                    int r = (argb >> 16) & 0xff;
                    int g = (argb >> 8) & 0xff;
                    int b = argb & 0xff;

                    //System.out.print("before: r:" + r + ", g:"+ g +", b"+ b);
                    //System.out.println("");


                    double yn = r * 0.299 + g * 0.587 + b * 0.114;
                    double un = (b - yn) * 0.493;
                    double vn = (r - yn) * 0.877;

                    //brightness
                    yn = yn + brightness;

                    //saturation
                    un = un * saturation/10;
                    vn = vn * saturation/10;

                    //contrast
                    un = un * contrast/10;
                    vn = vn * contrast/10;
                    yn = (yn  - 127.5) * contrast/10 + 127.5;

                    double huerad = Math.toRadians(hue);
                    //hue
                    un = (un * Math.cos(huerad)) - (vn * Math.sin(huerad));
                    vn = (vn * Math.cos(huerad)) - (un * Math.sin(huerad));


                    int rn = Math.max(0, Math.min((int) (yn + vn/0.877), 255));
                    int bn = Math.max(0, Math.min((int) (yn + un/0.493), 255));
                    int gn = Math.max(0, Math.min((int) (1/0.587 * yn - 0.299/0.587*rn - 0.114/0.587 * bn), 255));

                    //System.out.print("after: r:" + rn + ", g:"+ gn +", b:"+ bn);
                    //System.out.println("");

                    pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
                }
            }
        }

    }



}