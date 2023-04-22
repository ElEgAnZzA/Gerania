//TODO: 1. Изменить систему координат: а. изменить как рисуется, б. изменить, как считается столкновение, в. изменить гравитацию
//TODO: 2. Front-end design
//TODO: 3. Level save
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class MainGame extends JFrame{
    private static final Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    public static final int SCREEN_WIDTH = (int)dim.getWidth();
    public static final int SCREEN_HEIGHT = (int)dim.getHeight();
    public MyGamePanel panel;


    //Типа глобальные-неглобальные списки:
    public Creature[] creatures = new Creature[1000];
    public short kCreatures = 0;
    public GameObject[] gameObjects = new GameObject[1000];
    public short kGameObjects = 0;
    public TemporaryArt[] temporaryArts = new TemporaryArt[100];
    public short kTemporaryArts = 0;

    //Игровые константы:
    public static final int CREATURE_MAX_VELOCITY = 20;
    public static final Force GRAVITY = new Force(0, -1, -1);
    public int playerControlledCreatureId = 0;
    public Background background;

    //Системные константы:
    public static final int KEYBOARD_INDEX_1 = 49;
    public static final int KEYBOARD_INDEX_9 = 58;


    //Системно-игровые неконстанты:
    long endTime = System.currentTimeMillis();
    long beginningTime = System.currentTimeMillis();
    public static int PLAYER_CREATURE_MOVE_LEFT = 65; //Код A на клавиатуре
    public static int PLAYER_CREATURE_MOVE_RIGHT = 68; //Код D на клавиатуре
    public static int PLAYER_CREATURE_JUMP = 32; //Код пробела
    public static int PAUSE = 27; //Escape
    double cameraX = 0;
    double cameraY = 0;
    double maxX;
    boolean hasBoss = false;
    boolean happyEnd = false;
    Spell[] spells;
    int selectedSpell;
    boolean isPaused = false;
    boolean gameOver = false;
    static final int MAX_PLAYER_MANA = 150;
    double playerMana = MAX_PLAYER_MANA;
    public PrintStream log;
    public MainGame(String title, String level){
        super(title);
        try{
            background = new Background(0, 0, ImageIO.read(new File("./src/art/background.png")));
        }
        catch (IOException e){
            e.printStackTrace();
        }

        try{
            log = new PrintStream(new File("./src/logs/"+endTime+".txt"));
        }
        catch (java.io.FileNotFoundException e){
            e.printStackTrace();
        }

        setBounds(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        setUndecorated(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        panel = new MyGamePanel(true, this);
        add(panel);
        panel.setFocusable(true);
        panel.setRequestFocusEnabled(true);
        panel.requestFocus();
        loadLevel(level);

        spells = new Spell[10];
        spells[0] = new Spell(1);
        spells[1] = new Spell(2);
        spells[2] = new Spell(3);
        spells[3] = new Spell(4);
        spells[4] = new Spell(5);
        spells[5] = new Spell(-3);
        setSelectedSpell(0);



        endTime = System.currentTimeMillis();

        setVisible(true);//Должно быть в самом конце, т.к. вызывает repaint(), а значит, начинает игровой цикл
    }
    public static void main(String[] args) {
        MainGame mainGame = new MainGame("Gerania", args[0]+".txt");
    }
    public void setSelectedSpell(int selectedSpell){
        this.selectedSpell=selectedSpell;
    }
    public void clearGameObjects(){
        for (int i = 0; i<kGameObjects; i++){
            gameObjects[i]=null;
        }
        kGameObjects = 0;
    }
    public void clearCreatures(){
        for (int i = 0; i<kCreatures; i++){
            creatures[i]=null;
        }
        kCreatures = 0;
    }
    public void clearTemporaryArts(){
        for (int i = 0; i<kTemporaryArts; i++){
            temporaryArts[i]=null;
        }
        kTemporaryArts = 0;
    }
    public void loadLevel(String fileName){
        log.println("loading level "+fileName+":");
        try{
            clearGameObjects();
            log.println("GameObjects cleared");
            clearCreatures();
            log.println("Creatures cleared");
            clearTemporaryArts();
            log.println("TemporaryArts cleared");
            File levelFile = new File("./src/levels/"+fileName);
            Scanner levelRead = new Scanner(levelFile);
            kGameObjects = (short)levelRead.nextInt();
            levelRead.nextLine();
            maxX = 0;
            for (int i = 0; i<kGameObjects; i++){
                gameObjects[i] = GameObject.stringToGameObject(levelRead.nextLine());
                log.println("GameObject "+gameObjects[i]+" loaded");
                if (gameObjects[i].getX()+gameObjects[i].getWidth()>maxX)
                    maxX = gameObjects[i].getX()+gameObjects[i].getWidth();
            }

            creatures[0] = Creature.stringToCreature(this, levelRead.nextLine(), 0);
            log.println("Player character "+creatures[0]+" loaded");

            kCreatures = (short)(levelRead.nextInt()+1);
            levelRead.nextLine();
            for (int i = 1; i<kCreatures; i++){
                creatures[i] = Creature.stringToCreature(this, levelRead.nextLine(), i);
                log.println("Creature "+creatures[i]+" loaded");
            }
            int bossId = levelRead.nextInt();
            if (bossId == 1) {
                levelRead.nextLine();
                String bossString = levelRead.nextLine();
                String[] parameters = bossString.split(" ");
                creatures[kCreatures] = new CreatureBossScholar(Integer.valueOf(parameters[0]), Integer.valueOf(parameters[1]), endTime, kCreatures);
                creatures[kCreatures].applyGravity(GRAVITY);
                kCreatures++;
                hasBoss = true;
            }

            levelRead.close();
            log.println("Level "+fileName+" loaded");
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }
    public int getPlayerControlledCreatureId(){
        return playerControlledCreatureId;
    }
//    public void saveLevel(String fileName)
    class MyGamePanel extends JPanel implements MouseListener, MouseMotionListener, KeyListener{

        int a = 0;
        MainGame mainGame;

        public MyGamePanel(boolean isDoubleBuffered, MainGame mainGame) {
            super(isDoubleBuffered);
            addMouseListener(this);
            addMouseMotionListener(this);
            addKeyListener(this);
            this.mainGame = mainGame;
        }
        public void paint(Graphics g){
            mainGame.beginningTime = endTime;
            mainGame.endTime = System.currentTimeMillis();
            super.paint(g);

            if (gameOver||happyEnd){
                try{
                    TimeUnit.SECONDS.sleep(5);
                    this.mainGame.setVisible(false);
                    ChooseLevel.main(null);
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }
            }

            mainGame.background.move(mainGame);
            g.drawImage(mainGame.background.getImage(), (int)(mainGame.background.getX()-cameraX), SCREEN_HEIGHT- mainGame.background.getHeight(), panel);

            g.setColor(Color.BLACK);
            for (int i=0; i<kGameObjects; i++) {
                if (gameObjects[i].getX() + gameObjects[i].getWidth() > cameraX && gameObjects[i].getX() < cameraX + SCREEN_WIDTH) {
                    g.fillRect((int) (gameObjects[i].getX() - cameraX), SCREEN_HEIGHT-(int)(gameObjects[i].getY() - cameraY+gameObjects[i].getHeight()), gameObjects[i].getWidth(), gameObjects[i].getHeight());
                }
            }
            for (int i = 0; i< kCreatures; i++){
                if(creatures[i].getX()+creatures[i].getWidth()>cameraX&&creatures[i].getX()<cameraX+SCREEN_WIDTH) {
                    if(!creatures[i].flip)
                        g.drawImage(creatures[i].getSprite(), (int) (creatures[i].getX() - cameraX), SCREEN_HEIGHT-(int)(creatures[i].getY() - cameraY+creatures[i].getHeight()), creatures[i].getWidth(), creatures[i].getHeight(), panel);
                    else
                        g.drawImage(creatures[i].getSprite(), (int) (creatures[i].getX() - cameraX + creatures[i].getWidth()), SCREEN_HEIGHT-(int)(creatures[i].getY() - cameraY+creatures[i].getHeight()), -creatures[i].getWidth(), creatures[i].getHeight(), panel);
                    if (isPaused||gameOver)
                        creatures[i].setLastInteractionTime(endTime);
                    else
                        creatures[i].update(mainGame);
                }
            }

            for (int i = 0; i<kTemporaryArts; i++){
                if(temporaryArts[i].getX()+temporaryArts[i].getWidth()>cameraX&&temporaryArts[i].getX()<cameraX+SCREEN_WIDTH){
                    g.drawImage(temporaryArts[i].getImage(), (int) (temporaryArts[i].getX() - cameraX), SCREEN_HEIGHT-(int)(temporaryArts[i].getY() - cameraY+temporaryArts[i].getHeight()), temporaryArts[i].getWidth(), temporaryArts[i].getHeight(), panel);
                    temporaryArts[i].update(mainGame, endTime);
                }
            }
            if ((creatures[playerControlledCreatureId].getX()-cameraX>3*SCREEN_WIDTH/5)&&!(isPaused||gameOver)){
                cameraX+=5;
            }
            if (cameraX>0&&(creatures[playerControlledCreatureId].getX()-cameraX<2*SCREEN_WIDTH/5)&&!(isPaused||gameOver)){
                cameraX-=5;
            }

            if(playerMana<MAX_PLAYER_MANA&&!(isPaused||gameOver)){
                double addMana = (endTime-beginningTime)/150.0;
                if (playerMana+addMana>MAX_PLAYER_MANA){
                    playerMana=MAX_PLAYER_MANA;
                }
                else
                    playerMana+=addMana;
            }
            g.setColor(Color.RED);
            g.fillRect(SCREEN_WIDTH-250, 25, 200*creatures[playerControlledCreatureId].getHealth()/creatures[playerControlledCreatureId].getMaxHealth(), 20);
            g.setColor(Color.CYAN);
            g.fillRect(SCREEN_WIDTH-250, 50, (int)(200*playerMana/MAX_PLAYER_MANA), 20);
            g.setColor(Color.BLACK);
            g.drawRect(SCREEN_WIDTH-250, 25, 200, 20);
            g.drawRect(SCREEN_WIDTH-250, 50, 200, 20);

            if (creatures[playerControlledCreatureId].getX()>maxX&& !hasBoss)
                happyEnd();

            if(mainGame.isPaused)
                try{
                    g.drawImage(ImageIO.read(new File("./src/art/paused.png")), (SCREEN_WIDTH-800)/2, (SCREEN_HEIGHT-600)/2, panel);
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            if (happyEnd){
                g.setColor(Color.WHITE);
                g.fillRect(0,0, SCREEN_WIDTH, SCREEN_HEIGHT);
                g.setColor(Color.BLACK);
                try {
                    g.drawImage(ImageIO.read(new File("./src/art/youWon.png")), (SCREEN_WIDTH-800)/2, (SCREEN_HEIGHT-600)/2, panel);
                }
                catch(IOException e){
                    e.printStackTrace();
                }
            }
            else if(gameOver) {
                g.setColor(Color.WHITE);
                g.fillRect(0,0, SCREEN_WIDTH, SCREEN_HEIGHT);
                g.setColor(Color.BLACK);
                try {
                    g.drawImage(ImageIO.read(new File("./src/art/gameOver.png")), (SCREEN_WIDTH-800)/2, (SCREEN_HEIGHT-600)/2, panel);
                }
                catch(IOException e){
                    e.printStackTrace();
                }
            }



            try{
                TimeUnit.MILLISECONDS.sleep(50);
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
            repaint();
        }

        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (playerControlledCreatureId>=0&&playerControlledCreatureId<kCreatures){
                int keyCode = e.getKeyCode();
                if(keyCode == PLAYER_CREATURE_MOVE_LEFT&&Math.abs(creatures[playerControlledCreatureId].getVelocity().getX())<10&&!(gameOver||happyEnd||isPaused))
                    creatures[playerControlledCreatureId].move(new Vector(-1, 0));
                else if (keyCode == PLAYER_CREATURE_MOVE_RIGHT&&Math.abs(creatures[playerControlledCreatureId].getVelocity().getX())<10&&!(gameOver||happyEnd||isPaused))
                    creatures[playerControlledCreatureId].move(new Vector(1,0));
                else if (keyCode == PLAYER_CREATURE_JUMP&&creatures[playerControlledCreatureId].hasVerticalCollision()&&!(gameOver||happyEnd||isPaused)) {
                    creatures[playerControlledCreatureId].move(new Vector(0, 15));
                    System.out.println("JUMPING");
                    System.out.println(creatures[playerControlledCreatureId].hasVerticalCollision());
                }
                else if (keyCode == PAUSE)
                    isPaused = !isPaused;
                else if (keyCode>=KEYBOARD_INDEX_1&&keyCode<=KEYBOARD_INDEX_9&&!(gameOver||happyEnd||isPaused)){
                    int spellId = keyCode-KEYBOARD_INDEX_1;
                    if (spells[spellId]!=null)
                        mainGame.setSelectedSpell(spellId);
                }

            }
        }

        @Override
        public void keyReleased(KeyEvent e) {

        }

        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if(mainGame.spells[mainGame.selectedSpell].spellCost()<=playerMana&&!(gameOver||happyEnd||isPaused)) {
                mainGame.spells[mainGame.selectedSpell].cast(playerControlledCreatureId, new Point(e.getX(), e.getY()), mainGame);
                playerMana-= mainGame.spells[mainGame.selectedSpell].spellCost();
                System.out.println(playerMana);
            }
            else
                System.out.println("not enough mana");
        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }

        @Override
        public void mouseDragged(MouseEvent e) {

        }

        @Override
        public void mouseMoved(MouseEvent e) {

        }
        public void gameOver(){
            gameOver = true;
            repaint();
        }
        public void happyEnd(){
            happyEnd = true;
            gameOver();
        }
    }
}