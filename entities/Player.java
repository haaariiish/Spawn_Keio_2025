package entities;

import java.awt.Graphics;

import input.InputHandler;
import map.Map;

public class Player extends Moving_Entity{
    private String name = "Test_H";

    public Player(double x, double y, int width, int height, int hp, int attack, int defense,int range){
        super();
        this.setHP(hp);
        this.setAttack(attack);
        this.setDefense(defense);
        this.setX(x);
        this.setY(y);
        this.setWidthinPixel(width);
        this.setHeightinPixel(height);
        this.setBounds();
    }

    public void update_input(Map map, InputHandler input) {
        // 1. Gérer les inputs
        handleInput(input);
        
        // 2. Appliquer le mouvement avec collision
        this.update(map);
    }



    // Input managing

    public void handleInput(InputHandler input){
         if (input.isMovingLeft()) {
            this.moveLeft();
        } 
        if (input.isMovingRight()) {
            this.moveRight();
        }
        if (input.isMovingUp()) {
            this.moveUp();
        }
        if (input.isMovingDown()) {
            this.moveDown();
        }
        if (input.getHorizontalDirection()==1){ 
            if (input.getVerticalDirection()==-1){
            this.setFacing(Direction.UP_RIGHT);
            }
            else if (input.getVerticalDirection()==1){
            this.setFacing(Direction.DOWN_RIGHT);
            }
            else if (input.getVerticalDirection()==0){
            this.setFacing(Direction.RIGHT);
            }
    }
        else if (input.getHorizontalDirection()==0){
            if (input.getVerticalDirection()==-1){
            this.setFacing(Direction.UP);
            }
            else if (input.getVerticalDirection()==1){
            this.setFacing(Direction.DOWN);
            }
            
        }
        else if (input.getHorizontalDirection()==-1){
            if (input.getVerticalDirection()==-1){
            this.setFacing(Direction.UP_LEFT);
            }
            else if (input.getVerticalDirection()==1){
            this.setFacing(Direction.DOWN_LEFT);
            }
            else if (input.getVerticalDirection()==0){
            this.setFacing(Direction.LEFT);
            }
        }

        }

    public void render(Graphics g, int x, int y){
        g.fillOval(x, y, this.getWidthInPixels(), this.getHeightInPixels());
        g.drawRect(x, y, this.getWidthInPixels(), this.getHeightInPixels());
    }

    }
    





