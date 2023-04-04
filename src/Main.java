//TODO: 1. Finish and polish up MovementPattern
//TODO: 2. Make a spell system
//TODO: 3. Исправить конструктор MPTarget (просит не список существ и id, а ссылку на существо) - спросить у ДМ
//TODO: 4. Front-end design
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

public class Main extends JFrame{
    private static Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    private static final int SCREEN_WIDTH = (int)dim.getWidth();
    private static final int SCREEN_HEIGHT = (int)dim.getHeight();
    boolean debug = false;
    private MyPanel panel;


    //Типа глобальные-неглобальные списки:
    public Creature[] creatures = new Creature[1000];
    public int kCreatures = 0;
    public GameObject[] gameObjects = new GameObject[1000];
    public int kGameObjects = 0;

    //Игровые константы:
    public static final int CREATURE_MAX_VELOCITY = 20;
    public static final Force GRAVITY = new Force(0, 0.5, -1);
    private int playerControlledCreatureId = 0;

    //Системные константы:
    public static final int KEYBOARD_INDEX_0 = 96;
    public static final int KEYBOARD_INDEX_9 = 105;

    //Системные неконстанты:
    public static int PLAYER_CREATURE_MOVE_LEFT = 37; //Код стрелки влево на клавиатуре
    public static int PLAYER_CREATURE_MOVE_RIGHT = 39; //Код стрелки вправо на клавиатуре
    public static int PLAYER_CREATURE_JUMP = 32; //Код пробела
    double cameraX = 0;
    double cameraY = 0;
    Spell[] spells;
    int selectedSpell;
    public Main(String title){
        super(title);
        setBounds(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        setUndecorated(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        panel = new MyPanel(true, this);
        add(panel);
        panel.setFocusable(true);
        panel.setRequestFocusEnabled(true);
        panel.requestFocus();
        setVisible(true);

        gameObjects[0] = new GameObject(0, 300, 300, 73);
        gameObjects[1] = new GameObject(100, 100, 30, 120);
        kGameObjects = 2;
        creatures[0] = new Creature(200, 250, 8, 32,1);
        creatures[0].applyForce(GRAVITY);
        creatures[0].loadSprite("playerCharacterIdle.png");
        creatures[0].setMaxHealth(2000);
        creatures[0].setHealth(2000);

        creatures[1] = new Creature(1000, 500, 64, 64, 1);
        creatures[1].loadSprite("fireball.png");
        creatures[1].setMaxHealth(20);
        kCreatures = 2;

        spells = new Spell[10];
        spells[0] = new Spell(1);
        setSelectedSpell(0);


        while (true){
            panel.repaint();
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            }
            catch (java.lang.InterruptedException e){
                e.printStackTrace();
            }
            System.out.println(creatures[1].getHealth());
//            System.out.println(creatures[0]+" "+creatures[0].checkCollision(gameObjects, 0));
        }
    }
    public static void main(String[] args) {
        Main main = new Main("Gerania");
    }
    public void setSelectedSpell(int selectedSpell){
        this.selectedSpell=selectedSpell;
    }
    class MyPanel extends JPanel implements MouseListener, MouseMotionListener, KeyListener{
        Main main;
        public MyPanel(boolean isDoubleBuffered, Main main) {
            super(isDoubleBuffered);
            addMouseListener(this);
            addMouseMotionListener(this);
            addKeyListener(this);
            this.main = main;
        }
        public void paint(Graphics g){
            super.paint(g);
            g.setColor(Color.GREEN);
            for (int i=0; i<kGameObjects; i++){
                g.drawRect((int)(gameObjects[i].getX()-cameraX), (int)(gameObjects[i].getY()-cameraY), gameObjects[i].getWidth(), gameObjects[i].getHeight());
            }
            g.setColor(Color.BLACK);
            for (int i = 0; i< kCreatures; i++){
                creatures[i].update(main);
                g.drawImage(creatures[i].getSprite(), (int)(creatures[i].getX()-cameraX), (int)(creatures[i].getY()-cameraY), creatures[i].getWidth(), creatures[i].getHeight(), panel);
            }
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
                else if (keyCode == PLAYER_CREATURE_JUMP&&creatures[playerControlledCreatureId].hasVerticalCollision())
                    creatures[playerControlledCreatureId].move(new Vector(0, -4));
                else if (keyCode>=KEYBOARD_INDEX_0&&keyCode<=KEYBOARD_INDEX_9){
                    main.setSelectedSpell(keyCode-KEYBOARD_INDEX_0);
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
            main.spells[main.selectedSpell].cast(0,new Point(e.getX(), e.getY()), main);
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
    }
}