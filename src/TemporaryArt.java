import javax.imageio.ImageIO;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

public class TemporaryArt {
    //Характеристики:
    double x;
    double y;
    int width;
    int height;
    boolean shrinking;
    int lifeTime;
    long birthTime;

    Image image;



    //Прочая информация:
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

    public void update (MainGame mainGame, long currentTime){ //Обновляем временное изображение
        double timePassedCoeff = (currentTime-birthTime)/lifeTime; //Коэффициент прошедшего времени
        if (timePassedCoeff>=1)//Если прошло больше, чем изображение существует - удаляем
            this.delete(mainGame);

        else if (shrinking){//Если нужно, сжимаем
            this.setWidth((int)(origWidth*(1-timePassedCoeff)));
            this.setHeight((int)(origHeight*(1-timePassedCoeff)));
            this.setX(this.origX+(origWidth-this.getWidth())/2);
            this.setY(this.origY+(origHeight-this.getHeight())/2);
        }

    }

    public void delete(MainGame mainGame){

        for (int i = this.index; i< mainGame.kTemporaryArts-1; i++){ //Вырезаем из списка в mainGame
            mainGame.temporaryArts[i] = mainGame.temporaryArts[i+1];
            mainGame.temporaryArts[i].setIndex(mainGame.temporaryArts[i].getIndex()-1);
        }
        //Сдвигаем список влево:
        if (mainGame.kTemporaryArts<100)
            mainGame.temporaryArts[mainGame.kTemporaryArts-1] = mainGame.temporaryArts[mainGame.kTemporaryArts];
        else
            mainGame.temporaryArts[mainGame.kTemporaryArts-1]=null;
        mainGame.kTemporaryArts--;
    }
    @Override
    public String toString(){
        return "TemporaryArt at ("+this.getX()+", "+this.getY()+"); width: "+this.getWidth()+"; height: "+this.getHeight()+" "+image;
    }
    public static TemporaryArt stringToTemporaryArt(MainGame mainGame, String string, long time){ //Получение временного изображения из строки
        String[] parameters = string.split(" ");
        boolean shrinking;
        shrinking = parameters[4] != "0";
        return new TemporaryArt(Integer.valueOf(parameters[0]), Integer.valueOf(parameters[1]), Integer.valueOf(parameters[2]), Integer.valueOf(parameters[3]),
                shrinking, Integer.valueOf(parameters[5]), time, mainGame.kTemporaryArts, new File(parameters[6]));
        //double x, double y, int width, int height, boolean shrinking, int lifeTime, long birthTime, int index, File imageFile
    }
}
