import org.w3c.dom.xpath.XPathResult;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

public class TemporaryArt {
    double x;
    double y;
    int width;
    int height;
    boolean shrinking;
    int lifeTime;
    long birthTime;

    Image image;

    int index;

    double origX;
    double origY;
    int origWidth;
    int origHeight;


    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
    public int getIndex() {
        return index;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
    public Image getImage(){
        return image;
    }


    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }
    public void setIndex(int index) {
        this.index = index;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }
    public void loadImage (File file){
        try{
            this.image = ImageIO.read(file);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }


    public TemporaryArt(double x, double y, int width, int height, boolean shrinking, int lifeTime, long birthTime, int index, File imageFile){
        this.x = x;
        this.origX = x;
        this.y = y;
        this.origY = y;
        this.width = width;
        this.origWidth = width;
        this.height = height;
        this.origHeight = height;
        this.shrinking = shrinking;
        this.lifeTime = lifeTime;
        this.birthTime = birthTime;
        this.index = index;
        this.loadImage(imageFile);
    }

    public void update (Main main, long currentTime){
        double timePassedCoeff = (currentTime-birthTime)/lifeTime;
        if (timePassedCoeff>=1)
            this.kill(main);
        else if (shrinking){
            this.setWidth((int)(origWidth*(1-timePassedCoeff)));
            this.setHeight((int)(origHeight*(1-timePassedCoeff)));
            this.setX(this.origX+(origWidth-this.getWidth())/2);
            this.setY(this.origY+(origHeight-this.getHeight())/2);
        }

    }

    public void kill(Main main){
        System.out.println("Killing "+this+" index: "+this.index);
        for (int i =this.index; i<main.kTemporaryArts-1;i++){
            main.temporaryArts[i] = main.temporaryArts[i+1];
            main.temporaryArts[i].setIndex(main.temporaryArts[i].getIndex()-1);
        }
        if (main.kTemporaryArts<100)
            main.temporaryArts[main.kTemporaryArts-1] = main.temporaryArts[main.kTemporaryArts];
        else
            main.temporaryArts[main.kTemporaryArts-1]=null;
        main.kTemporaryArts--;
    }
    @Override
    public String toString(){
        return "TemporaryArt at ("+this.getX()+", "+this.getY()+"); width: "+this.getWidth()+"; height: "+this.getHeight()+" "+image;
    }
    public static TemporaryArt stringToTemporaryArt(Main main, String string, long time){
        String[] parameters = string.split(" ");
        boolean shrinking;
        if (parameters[4] == "0")
            shrinking = false;
        else
            shrinking = true;
        return new TemporaryArt(Integer.valueOf(parameters[0]), Integer.valueOf(parameters[1]), Integer.valueOf(parameters[2]), Integer.valueOf(parameters[3]),
                shrinking, Integer.valueOf(parameters[5]), time, main.kTemporaryArts, new File(parameters[6]));
        //double x, double y, int width, int height, boolean shrinking, int lifeTime, long birthTime, int index, File imageFile
    }
}
