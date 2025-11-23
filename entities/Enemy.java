package entities;

public class Enemy extends Moving_Entity{
    private String name= "Ennemy A";
    public Enemy(double x, double y, int width, int height, int hp, int attack, int defense,int range){
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
