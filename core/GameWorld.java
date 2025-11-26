package core;

import java.util.ArrayList;
import java.util.List;
import java.awt.Rectangle;

import input.InputHandler;
import entities.Boss;
import entities.ChargerEnemy;
import entities.Enemy;
import entities.HeavyEnemy;
import entities.Player;
import entities.Projectiles;
import entities.RangedEnemy;
import entities.Simple_Projectiles;
import map.Map;

import java.awt.Point;

//A new class to divide the management of the game structure and the global management done by game
public class GameWorld {
    private Player player;
    private int score=0;
    private Map map;
    private List<Enemy> enemies;
    private Game game;

    private double spawnEnemyproba=0.2;
    //private Boss currentBoss;
    private List<Projectiles> projectilesList;
    //private List<Projectile> enemyProjectiles;
    //private List<Item> items;
    //private List<Particle> particles;

    //private Map<String, Enemy> enemyMap;

    private int worldWidth;
    private int worldHeight;
    private int tileSize;
    private static final int PLAYER_SHOOT_COOLDOWN_FRAMES = 5;
    private int playerShootCooldown = 0;

    private int playerDamageCooldown = 0;
    private static final int PLAYER_DAMAGE_COOLDOWN_FRAMES = 10;

    public GameWorld(int worldWidth, int worldHeight, int tileSize,Game game) {
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.game = game;
        this.tileSize = tileSize;
        
    
        // Spawn of some Ennemy (to implement)
        //spawnEnemy(200, 200);
        //spawnEnemy(300, 250);
    }

    public void reset(){
        this.player = null;
        this.enemies = null;
        this.score = 0;
        
    }

    public void restart(){
        // Initialisation of the map ( default map for now)
        this.score = 0;
        this.playerShootCooldown = 0;
        this.playerDamageCooldown = 0;
        map = new Map(this.worldWidth / this.tileSize, this.worldHeight / this.tileSize, this.tileSize);
        map.createDefaultMap();

        // Initialisation of lists

        enemies = new ArrayList<>();
        projectilesList = new ArrayList<>();
        
        //  spawn point
        Point spawn = map.getSpawnPoint();
        player = new Player(spawn.x, spawn.y,20,20,100,1,1,100);
    }

    public void update(InputHandler input,int which_frame_in_cyle){
        this.player.update_input(this.map, input);
        //System.out.println("Player bounds: " + this.player.getBounds());
        updateEnemiesPosition(this.map,this.player);
        if (which_frame_in_cyle%5==0){ // If we are in a precise moment, the game will try to make spawn some enemies
        updateEnemiesSpawn(this.map);
        }
        if (playerShootCooldown > 0) {
            playerShootCooldown--;
        }
        
        handlePlayerShooting(input);
        updateProjectiles();

        handleCollisions();
        if (playerDamageCooldown > 0) {
            playerDamageCooldown--;
        }
        updateEnemiesDeath();
        checkPlayerStatus();

    }


    //GETTERS

    public Player getPlayer(){
        return this.player;
    }

    public List<Enemy> getEnemy(){
        return this.enemies;
    }

    public Map getMap(){
        return this.map;
    }

    public Game getGame(){
        return this.game;
    }

    public int getWidthInPixels(){
        return this.worldWidth;
    }

    public int getHeightInPixels(){
        return this.worldHeight;
    }

    public int getTileSize(){
        return this.tileSize;
    }

    public int getScore(){
        return this.score;
    }

    public List<Projectiles> getListProjectiles(){
        return this.projectilesList;
    }


    //SETTERS
    // UPDATERS OF FEATURES

    public void playerShoot(){
        Projectiles proj ;
        switch(player.getProjectilesTypes()){
            case Simple_Projectiles:
                proj = new Simple_Projectiles(player, 5, 5, 1);
                break;
            default:
                proj = new Simple_Projectiles(player, 3, 3, 5);
                break;
        }
        projectilesList.add(proj);
        
    }
    public void updateProjectiles(){
        for (int i = projectilesList.size() - 1; i >= 0; i--) {
            Projectiles proj = projectilesList.get(i);
            proj.update(map);
            //System.out.println("Projectile bounds: " + proj.getBounds());
            proj.setBounds();
            if (proj.toDestroy()) {
                projectilesList.remove(i);
            }
        }
    }
    public void updateEnemiesPosition(Map map, Player player){
        for (int i = enemies.size() - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);
            Projectiles shot = enemy.updateBehavior(map, player);
            if (shot != null) {
                projectilesList.add(shot);
            }
        }
    }
 
    public void updateEnemiesSpawn(Map map){
        for (int i = map.getEnemySpawnPoints().size() - 1; i >= 0; i--) {
            if (enemies.size()>10){
                return ;
            }
            if (Math.random()<this.spawnEnemyproba){
                Enemy n_enemy = createRandomEnemy(map.getEnemySpawnPoints().get(i));
                if (n_enemy != null) {
                    enemies.add(n_enemy);
                }
            }
            
        }
    }

    public void updateEnemiesDeath(){
        for (int i = enemies.size() - 1; i >= 0; i--) {
            if (enemies.get(i).getIsDead()){
                enemies.remove(i);
                score++;
            }
        }
    }

    public void handleCollisions(){
        Rectangle playerBounds = player.getBounds();
        for (int i = projectilesList.size() - 1; i >= 0; i--) {
            Projectiles proj = projectilesList.get(i);
            boolean hitSomething = false;
            if (proj.getBounds().intersects(playerBounds)) {
                player.take_damage(proj.getSourceEntity().getAttack());
                hitSomething = true;
            }
            for (int j = enemies.size() - 1; j >= 0; j--) {
                Enemy enemy = enemies.get(j);
                if (proj.getSourceEntity() != enemy && proj.getBounds().intersects(enemy.getBounds())) {
                    enemy.take_damage(proj.getSourceEntity().getAttack());
                    hitSomething = true;
                }
            }
            if (hitSomething) {
                projectilesList.remove(i);
            }
        }
        if (playerDamageCooldown == 0) {
        for (int j = enemies.size() - 1; j >= 0; j--) {
                Enemy enemy = enemies.get(j);
                if (enemy.getBounds().intersects(player.getBounds())) {
                    player.take_damage(enemy.getAttack());
                }
            }
            playerDamageCooldown = PLAYER_DAMAGE_COOLDOWN_FRAMES;
        }

    }

    private void checkPlayerStatus(){
        if (player == null) {
            return;
        }
        if (player.getIsDead() || player.getHP() <= 0) {
            if (projectilesList != null) {
                projectilesList.clear();
            }
            game.changeGameState(GameState.GAMEOVER);
        }
    }

    private void handlePlayerShooting(InputHandler input){
        if (player == null) {
            return;
        }
        boolean attemptedShot = false;
        Double aimAngle = null;

        Double arrowAngle = input.getShootAngleFromArrows();
        if (arrowAngle != null) {
            aimAngle = arrowAngle;
            attemptedShot = true;
        } else if (input.isMouseShootPressed()) {
            aimAngle = determineMouseAngle(input);
            attemptedShot = aimAngle != null;
        } else if (input.isShootPressed()) {
            aimAngle = player.getFacingAngle();
            attemptedShot = true;
        }

        if (attemptedShot && aimAngle != null && playerShootCooldown == 0) {
            player.setFacingAngle(aimAngle);
            playerShoot();
            playerShootCooldown = PLAYER_SHOOT_COOLDOWN_FRAMES;
        }
    }

    private Double determineMouseAngle(InputHandler input){
        if (player == null) {
            return null;
        }
        if (game == null || game.getFrame() == null) {
            return null;
        }
        java.awt.Component panel = game.getFrame().getGamePanel();
        if (panel == null) {
            return null;
        }
        java.awt.Point mouse = input.getMousePosition();
        if (mouse == null) {
            return null;
        }

        int panelWidth = panel.getWidth();
        int panelHeight = panel.getHeight();
        if (panelWidth == 0 || panelHeight == 0) {
            return null;
        }

        double playerCenterX = player.getX() + player.getWidthInPixels() / 2.0;
        double playerCenterY = player.getY() + player.getHeightInPixels() / 2.0;
        double cameraX = playerCenterX - panelWidth / 2.0;
        double cameraY = playerCenterY - panelHeight / 2.0;

        double targetX = cameraX + mouse.x;
        double targetY = cameraY + mouse.y;

        double dx = targetX - playerCenterX;
        double dy = targetY - playerCenterY;

        if (Math.abs(dx) < 0.001 && Math.abs(dy) < 0.001) {
            return null;
        }

        return Math.atan2(dy, dx);
    }

    private Enemy createRandomEnemy(Point spawnPoint){
        double roll = Math.random();
        if (roll < 0.4) {
            ChargerEnemy charger = new ChargerEnemy(spawnPoint.x, spawnPoint.y);
            return charger;
        } else if (roll < 0.75) {
            RangedEnemy ranged = new RangedEnemy(spawnPoint.x, spawnPoint.y);
            return ranged;
        } else {
            HeavyEnemy heavy = new HeavyEnemy(spawnPoint.x, spawnPoint.y);
            return heavy;
        }
    }

}

