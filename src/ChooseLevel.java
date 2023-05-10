import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ChooseLevel extends JFrame {
    //Параметры окна:
    private static final Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    public static final int SCREEN_WIDTH = (int)dim.getWidth();
    public static final int SCREEN_HEIGHT = (int)dim.getHeight();
    public static final int WIDTH = 150;
    public static final int HEIGHT = 300;
    public static final int BUTTON_WIDTH = 100;
    public static final int BUTTON_HEIGHT = 20;
    public static final int BUTTON_INTERVAL = (HEIGHT-3*BUTTON_HEIGHT)/5;

    public ChooseLevel(String title) {
        //Создаем и настраиваем окно:
        super(title);
        setBounds((SCREEN_WIDTH-WIDTH)/2, (SCREEN_HEIGHT-HEIGHT)/2, WIDTH, HEIGHT);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.getContentPane().setLayout(null);

        addButtons();

        setVisible(true);
    }

    public void addButtons(){
        //Добавляем кнопки:
        JButton levelOne = new JButton("Уровень 1");
        levelOne.setBounds((WIDTH-BUTTON_WIDTH)/2-5, BUTTON_INTERVAL, BUTTON_WIDTH, BUTTON_HEIGHT);
        JButton levelTwo = new JButton("Уровень 2");
        levelTwo.setBounds((WIDTH-BUTTON_WIDTH)/2-5, 2*BUTTON_INTERVAL+BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT);
        JButton arena = new JButton("Арена");
        arena.setBounds((WIDTH-BUTTON_WIDTH)/2-5, 3*BUTTON_INTERVAL+2*BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT);

        //Добавляем действия к кнопкам
        levelOne.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                String[] args = new String[1];
                args[0] = "1";
                MainGame.main(args);
                setVisible(false);
            }});

        levelTwo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                String[] args = new String[1];
                args[0] = "2";
                MainGame.main(args);
                setVisible(false);
            }});
        arena.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                String[] args = new String[1];
                args[0] = "arena";
                MainGame.main(args);
                setVisible(false);
            }});

        //Добавляем кнопки к окну:
        add(levelOne);
        add(levelTwo);
        add(arena);
    }
    public static void main(String[] args) {
        if (args!=null&&args.length!=0&&!args[0].equals("debug")) {
            ChooseLevel chooseLevel = new ChooseLevel("Gerania: choose the level");
        }
        else {
            String[] arguments = new String[1];
            arguments[0] = "devTest";
            MainGame.main(arguments);
        }
    }
}
