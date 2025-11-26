package entities;

public class HeavyEnemy extends Enemy {

    public HeavyEnemy(double x, double y) {
        super(x, y, 22, 22, 80, 6, 4, 40,(int) Math.round(8+2*Math.random()));
        this.setSpeed(3);
    }

    @Override
    protected void updateMovement(Player player) {
        facePlayer(player);
        double angle = this.getFacingAngle();
        double speed = this.getSpeed();
        setVelocityX(Math.cos(angle) * speed);
        setVelocityY(Math.sin(angle) * speed);
    }
}

