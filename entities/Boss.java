package entities;

public class Boss extends Enemy{
    private String name= "Boss A";
    public Boss(double x, double y, int width, int height, int hp, int attack, int defense,int range, int speed){
        super(x,y, width, height, hp, attack, defense, range,speed);
    }

    public void updateMovement(Player player){
        return;
    }
}
