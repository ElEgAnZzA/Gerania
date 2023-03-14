public class Main {
    public Creature[] creatures = new Creature[1000];
    public GameObject[] gameObjects = new GameObject[1000];
    public static void main(String[] args) {
        Creature creature = new Creature(0, 0, 1, 1, 1);
        creature.setVelocity(new Vector(2, 15));
        Point[] a = creature.getBounds();
        System.out.println(a[0]+" "+a[1]);
//        Force gravity = new Force(0, -5, -1);
//        Force force1 = new Force(3, 2, 10);
//        Force force2 = new Force(-3, 1, 5);
//        creature.applyForce(gravity);
//        creature.applyForce(force1);
//        creature.applyForce(force2);
//
//        while (true){
//            System.out.println("x:"+creature.getX()+" y:"+creature.getY()+" velocity:"+creature.getVelocity()+" forces:");
//            Force[] forces = creature.forces;
//            for (int i = 0; i< creature.kForces; i++){
//                System.out.println(forces[i]);
//            }
//            creature.update();
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
    }
}