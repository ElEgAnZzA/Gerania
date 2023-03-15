public class Main {

    public static void main(String[] args) {
        Creature[] creatures = new Creature[1000];
        GameObject[] gameObjects = new GameObject[1000];
        creatures[0] = new Creature(0, 0, 1, 1);
        creatures[0].setVelocity(new Vector(1, 0));
        gameObjects[0] = new GameObject(5, -2, 10, 4);
        for(int i=0; i<7; i++){
            System.out.println(""+creatures[0]+" "+creatures[0].getVelocity());
            System.out.println(""+gameObjects[0]);
            creatures[0].update(gameObjects);

        }
    }
}