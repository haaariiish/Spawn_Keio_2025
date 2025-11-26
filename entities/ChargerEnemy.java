package entities;

public class ChargerEnemy extends Enemy {

    public ChargerEnemy(double x, double y) {
        super(x, y, 15, 15, 25, 2, 1, 30,(int) Math.round(12+2*Math.random()));
        this.setSpeed(8 + Math.random() * 4);
    }

    @Override
    protected void updateMovement(Player player) {
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

        facePlayer(player);
    }
}

