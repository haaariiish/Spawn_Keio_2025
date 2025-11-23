package entities;

public class Player extends Moving_Entity{
    private String name = "Test_H";

    public Player(double x, double y, int width, int height, int hp, int attack, int defense,int range){
        super();
        this.setHP(hp);
        this.setAttack(attack);
        this.setDefense(defense);
        this.setX(x);
        this.setY(y);
        this.setWidthinPixel(width);
        this.setHeightinPixel(height);
    }

    


}
