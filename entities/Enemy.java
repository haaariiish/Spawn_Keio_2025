package entities;

import java.awt.Graphics;
import java.util.List;
import map.Map;

public abstract class Enemy extends Moving_Entity{
    private int shootCooldownFrames;
    private int shootCooldown;

    public Enemy(double x, double y, int width, int height, int hp, int attack, int defense,int range,int speed){
        super();
        this.setHP(hp);
        this.setAttack(attack);
        this.setDefense(defense);
        this.setRange(range);
        this.setX(x);
        this.setY(y);
        this.setWidthinPixel(width);
        this.setHeightinPixel(height);
        this.setBounds();
        this.setSpeed(speed);
        this.shootCooldownFrames = 0;
        this.shootCooldown = 0;
    }

    protected void setShootCooldownFrames(int frames){
        this.shootCooldownFrames = Math.max(0, frames);
    }

    protected boolean isShootReady(){
        return shootCooldown == 0;
    }

    protected void resetShootCooldown(){
        if (shootCooldownFrames > 0){
            shootCooldown = shootCooldownFrames;
        }
    }

    protected void tickCooldowns(){
        if (shootCooldown > 0){
            shootCooldown--;
        }
    }

    protected void facePlayer(Player player){
        double dx = player.getX() - this.getX();
        double dy = player.getY() - this.getY();
        if (Math.abs(dx) > 0.001 || Math.abs(dy) > 0.001) {
            this.setFacingAngle(Math.atan2(dy, dx));
        }
    }

    public Projectiles updateBehavior(Map map, Player player, List<Moving_Entity> movingEntity){
        tickCooldowns();
        TimerUpdate();
        gotKnockback();
        updateMovement(player);
        movingEntity.add(player) ;
        update_collision_withEntities(movingEntity, map);
        update(map);
        return attemptSpecialAction(player);
    }

    

    protected abstract void updateMovement(Player player);

    protected Projectiles attemptSpecialAction(Player player){
        return null;
    }

    public abstract void render(Graphics g,int x, int y );

    

}
