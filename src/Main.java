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
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main extends JFrame{
    private static Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    public static final int SCREEN_WIDTH = (int)dim.getWidth();
    public static final int SCREEN_HEIGHT = (int)dim.getHeight();
    boolean debug = false;
    public MyPanel panel;


    //Типа глобальные-неглобальные списки:
    public Creature[] creatures = new Creature[1000];
    public short kCreatures = 0;
    public GameObject[] gameObjects = new GameObject[1000];
    public short kGameObjects = 0;
    public TemporaryArt[] temporaryArts = new TemporaryArt[100];
    public short kTemporaryArts = 0;

    //Игровые константы:
    public static final int CREATURE_MAX_VELOCITY = 10;
    public static final Force GRAVITY = new Force(0, 1, -1);
    private int playerControlledCreatureId = 0;

    //Системные константы:
    public static final int KEYBOARD_INDEX_1 = 49;
    public static final int KEYBOARD_INDEX_9 = 58;


    //Системно-игровые неконстанты:
    long endTime = System.currentTimeMillis();
    long beginningTime = System.currentTimeMillis();
    public static int PLAYER_CREATURE_MOVE_LEFT = 37; //Код стрелки влево на клавиатуре
    public static int PLAYER_CREATURE_MOVE_RIGHT = 39; //Код стрелки вправо на клавиатуре
    public static int PLAYER_CREATURE_JUMP = 32; //Код пробела
    public static int PAUSE = 27; //Escape
    double cameraX = 0;
    double cameraY = 0;
    Spell[] spells;
    int selectedSpell;
    boolean isPaused = false;
    boolean gameOver = false;
    static final int MAX_PLAYER_MANA = 150;
    double playerMana = MAX_PLAYER_MANA;
    public PrintStream log;
    public Main(String title){
        super(title);

        try{
            log = new PrintStream(new File("./src/logs/"+endTime+".txt"));
        }
        catch (java.io.FileNotFoundException e){
            e.printStackTrace();
        }

        setBounds(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        setUndecorated(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        panel = new MyPanel(true, this);
        add(panel);
        panel.setFocusable(true);
        panel.setRequestFocusEnabled(true);
        panel.requestFocus();
        loadLevel("devTest.txt");

        spells = new Spell[10];
        spells[0] = new Spell(1);
        spells[1] = new Spell(2);
        spells[2] = new Spell(3);
        setSelectedSpell(0);



        endTime = System.currentTimeMillis();

        setVisible(true);//Должно быть в самом конце, т.к. вызывает repaint(), а значит, начинает игровой цикл
    }
    public static void main(String[] args) {
        Main main = new Main("Gerania");
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
            for (int i = 0; i<kGameObjects; i++){
                gameObjects[i] = GameObject.stringToGameObject(levelRead.nextLine());
                log.println("GameObject "+gameObjects[i]+" loaded");
            }

            creatures[0] = Creature.stringToCreature(levelRead.nextLine(), this);
            log.println("Player character "+creatures[0]+" loaded");

            kCreatures = (short)(levelRead.nextInt()+1);
            levelRead.nextLine();
            for (int i = 1; i<kCreatures; i++){
                creatures[i] = Creature.stringToCreature(levelRead.nextLine(), this);
                log.println("Creature "+creatures[i]+" loaded");
            }
            int bossId = levelRead.nextInt();
            levelRead.nextLine();
            switch (bossId){
                case(1):
                    String bossString = levelRead.nextLine();
                    String[] parameters = bossString.split(" ");
                    creatures[kCreatures] = new CreatureBossScholar(Integer.valueOf(parameters[0]), Integer.valueOf(parameters[1]), endTime, kCreatures);
                    creatures[kCreatures].applyGravity(GRAVITY);
                    kCreatures++;
                    break;
                default:
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
    class MyPanel extends JPanel implements MouseListener, MouseMotionListener, KeyListener{

        int a = 0;
        Main main;

        public MyPanel(boolean isDoubleBuffered, Main main) {
            super(isDoubleBuffered);
            addMouseListener(this);
            addMouseMotionListener(this);
            addKeyListener(this);
            this.main = main;
        }
        public void paint(Graphics g){
            main.beginningTime = endTime;
            main.endTime = System.currentTimeMillis();
            if (creatures[playerControlledCreatureId].getX()-cameraX>3*SCREEN_WIDTH/5){
                cameraX+=5;
            }
            if (cameraX>0&&(creatures[playerControlledCreatureId].getX()-cameraX<2*SCREEN_WIDTH/5)){
                cameraX-=5;
            }
            super.paint(g);
            g.setColor(Color.RED);
            g.fillRect(SCREEN_WIDTH-250, 25, 200*creatures[playerControlledCreatureId].getHealth()/creatures[playerControlledCreatureId].getMaxHealth(), 20);
            g.setColor(Color.CYAN);
            g.fillRect(SCREEN_WIDTH-250, 50, (int)(200*playerMana/MAX_PLAYER_MANA), 20);
            g.setColor(Color.BLACK);
            g.drawRect(SCREEN_WIDTH-250, 25, 200, 20);
            g.drawRect(SCREEN_WIDTH-250, 50, 200, 20);

            g.setColor(Color.GREEN);
            for (int i=0; i<kGameObjects; i++) {
                if (gameObjects[i].getX() + gameObjects[i].getWidth() > cameraX && gameObjects[i].getX() < cameraX + SCREEN_WIDTH) {
                    g.drawRect((int) (gameObjects[i].getX() - cameraX), (int) (gameObjects[i].getY() - cameraY), gameObjects[i].getWidth(), gameObjects[i].getHeight());
                }
            }
            g.setColor(Color.BLACK);
            for (int i = 0; i< kCreatures; i++){
                if(creatures[i].getX()+creatures[i].getWidth()>cameraX&&creatures[i].getX()<cameraX+SCREEN_WIDTH) {
                    g.drawImage(creatures[i].getSprite(), (int) (creatures[i].getX() - cameraX), (int) (creatures[i].getY() - cameraY), creatures[i].getWidth(), creatures[i].getHeight(), panel);
                    creatures[i].update(main);
                }
            }

            for (int i = 0; i<kTemporaryArts; i++){
                if(temporaryArts[i].getX()+temporaryArts[i].getWidth()>cameraX&&temporaryArts[i].getX()<cameraX+SCREEN_WIDTH){
                    g.drawImage(temporaryArts[i].getImage(), (int) (temporaryArts[i].getX() - cameraX), (int) (temporaryArts[i].getY() - cameraY), temporaryArts[i].getWidth(), temporaryArts[i].getHeight(), panel);
                    temporaryArts[i].update(main, endTime);
                }
            }

            if(playerMana<MAX_PLAYER_MANA){
                double addMana = (endTime-beginningTime)/150.0;
                if (playerMana+addMana>MAX_PLAYER_MANA){
                    playerMana=MAX_PLAYER_MANA;
                }
                else
                    playerMana+=addMana;
            }

            if(main.isPaused==true)
                g.drawString("PAUSED", 10, 10);
            if(gameOver) {
                g.setColor(Color.white);
                g.fillRect(0,0,main.SCREEN_WIDTH, main.SCREEN_HEIGHT);
                g.setColor(Color.black);
                try {
                    g.drawImage(ImageIO.read(new File("./src/art/gameOver.png")), (main.SCREEN_WIDTH-800)/2, (main.SCREEN_HEIGHT-600)/2, panel);
                }
                catch(IOException e){

                }
            }
            while(isPaused||gameOver){
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                }
                catch (java.lang.InterruptedException e){
                    e.printStackTrace();
                }
            }
            try{
                TimeUnit.MILLISECONDS.sleep(100);
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
                if(keyCode == PLAYER_CREATURE_MOVE_LEFT) {
                    creatures[playerControlledCreatureId].move(new Vector(-1, 0));
                }
                else if (keyCode == PLAYER_CREATURE_MOVE_RIGHT)
                    creatures[playerControlledCreatureId].move(new Vector(1,0));
                else if (keyCode == PLAYER_CREATURE_JUMP&&creatures[playerControlledCreatureId].hasVerticalCollision()) {
                    creatures[playerControlledCreatureId].move(new Vector(0, -15));
                    System.out.println("JUMPING");
                    System.out.println(creatures[playerControlledCreatureId].hasVerticalCollision());
                }
                else if (keyCode == PAUSE)
                    isPaused = !isPaused;
                else if (keyCode>=KEYBOARD_INDEX_1&&keyCode<=KEYBOARD_INDEX_9){
                    int spellId = keyCode-KEYBOARD_INDEX_1;
                    if (spells[spellId]!=null)
                        main.setSelectedSpell(spellId);
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
            if(main.spells[main.selectedSpell].spellCost()<=playerMana) {
                main.spells[main.selectedSpell].cast(0, new Point(e.getX(), e.getY()), main);
                playerMana-=main.spells[main.selectedSpell].spellCost();
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
    }
}