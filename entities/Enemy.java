package entities;

import map.Map;
import java.awt.Graphics;


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

    public void update_velocity(Player player){
        double xplayer = player.getX();
        double yplayer = player.getY();

        double vx_with_noise= (Math.random()+0.5)*this.getSpeed();
        double vy_with_noise = (Math.random()+0.5)*this.getSpeed();

        if(this.getY()-yplayer>this.getRange()){
                vy_with_noise = -vy_with_noise;
            }
            
        else if (Math.abs(this.getY()-yplayer)<=this.getRange()){
               vy_with_noise -=  this.getSpeed();
            }

        if(this.getX()-xplayer>this.getRange()){
                vx_with_noise = -vx_with_noise;
            }
            
        else if (Math.abs(this.getX()-xplayer)<=this.getRange()){
               vx_with_noise -=  this.getSpeed();
            }
        setVelocityX(vx_with_noise);
        setVelocityY(vy_with_noise);
    }

    public void update_position(Map map, Player player){
        update_velocity(player);
        this.take_damage(1);
        update(map);
    }
    public void render(Graphics g,int x, int y){
        g.fillOval((int) this.getX() -x,(int) this.getY() -y ,this.getWidthInPixels() ,this.getHeightInPixels() );
    }

}
