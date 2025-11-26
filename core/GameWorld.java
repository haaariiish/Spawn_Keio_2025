package core;

import java.util.ArrayList;
import java.util.List;
import java.awt.Rectangle;

import input.InputHandler;
import entities.Boss;
import entities.Enemy;
import entities.Player;
import entities.Projectiles;
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
        
    }

    public void restart(){
        // Initialisation of the map ( default map for now)
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
        if (input.isShootPressed()){
            playerShoot();
        }
        updateProjectiles();

        handleCollisions();
        updateEnemiesDeath();

 
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
            enemies.get(i).update_position(map, player);
        }
    }
 
    public void updateEnemiesSpawn(Map map){
        for (int i = map.getEnemySpawnPoints().size() - 1; i >= 0; i--) {
            if (enemies.size()>10){
                return ;
            }
            if (Math.random()<this.spawnEnemyproba){
                Enemy n_enemy = new Enemy(map.getEnemySpawnPoints().get(i).x, map.getEnemySpawnPoints().get(i).y,15,15,5,1,1,10);
                //initialisation of enemies but random
                n_enemy.setSpeed(Math.random()*5);
                enemies.add(n_enemy);
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
                if (proj.getBounds().intersects(enemy.getBounds())) {
                    enemy.take_damage(proj.getSourceEntity().getAttack());
                    hitSomething = true;
                }
            }
            if (hitSomething) {
                projectilesList.remove(i);
            }
        }

    }

}

