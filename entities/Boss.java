package entities;
import java.awt.Graphics;
import java.awt.Color;

public class Boss extends Enemy{
    private String name= "Boss A";
    public Boss(double x, double y, int width, int height, int hp, int attack, int defense,int range, int speed){
        super(x,y, width, height, hp, attack, defense, range,speed);
    }

    public void updateMovement(Player player){
        return;
    }
    @Override
    public void render(Graphics g,int x, int y){
        /*g.setColor(Color.PURPLE)
        g.fillOval((int) this.getX() -x,(int) this.getY() -y ,this.getWidthInPixels() ,this.getHeightInPixels() );
        g.drawRect((int) this.getX() -x,(int) this.getY() -y ,this.getWidthInPixels() ,this.getHeightInPixels() );*/
        return;
        }

}
