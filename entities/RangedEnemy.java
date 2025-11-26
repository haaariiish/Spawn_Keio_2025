package entities;

public class RangedEnemy extends Enemy {
    private final double preferredRange;
    private final double fireRange;
    private final double retreatPadding;

    public RangedEnemy(double x, double y) {
        super(x, y, 14, 14, 20, 2, 1, 120,(int) Math.round(20+2*Math.random()));
        this.setSpeed(4 + Math.random());
        this.setShootCooldownFrames(60);
        this.preferredRange = 220;
        this.fireRange = 280;
        this.retreatPadding = 60;
        this.setBulletFast(12);
    }

    @Override
    protected void updateMovement(Player player) {
        facePlayer(player);
        double dx = player.getX() - this.getX();
        double dy = player.getY() - this.getY();
        double distance = Math.hypot(dx, dy);
        double angle = this.getFacingAngle();
        double speed = this.getSpeed();

        if (distance > preferredRange + retreatPadding) {
            setVelocityX(Math.cos(angle) * speed);
            setVelocityY(Math.sin(angle) * speed);
        } else if (distance < preferredRange - retreatPadding) {
            setVelocityX(-Math.cos(angle) * speed);
            setVelocityY(-Math.sin(angle) * speed);
        } else {
            setVelocityX(0);
            setVelocityY(0);
        }
    }

    @Override
    protected Projectiles attemptSpecialAction(Player player) {
        double distance = Math.hypot(player.getX() - this.getX(), player.getY() - this.getY());
        if (distance <= fireRange && isShootReady()) {
            resetShootCooldown();
            return new Simple_Projectiles(this, 6, 6, 0.98);
        }
        return null;
    }
}

