import java.awt.*;

public class Background {
    double x;
    double y;
    Image image;
    int width;
    int height;
    public Background(double x, double y, Image image){
        this.x = x;
        this.y = y;
        this.image = image;
        width = image.getWidth(null);
        height = image.getHeight(null);
    }
    public Image getImage(){
        return image;
    }
    public double getX(){
        return x;
    }
    public double getY(){
        return y;
    }
    public int getWidth(){
        return width;
    }
    public int getHeight(){
        return height;
    }

    public void move(MainGame mainGame){
        double coefficient = mainGame.cameraX/(mainGame.maxX- MainGame.SCREEN_WIDTH);
        this.x = mainGame.cameraX-(this.width- MainGame.SCREEN_WIDTH)*coefficient;
    }
}
