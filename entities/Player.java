package entities;

import java.awt.Graphics;
import java.awt.Color;

import input.InputHandler;
import map.Map;

public class Player extends Moving_Entity{
    private static final Color COLOR_PLAYER = Color.BLACK;
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
        boolean moved = false;
        if (input.isMovingLeft()) {
            this.moveLeft();
            moved = true;
        } 
        if (input.isMovingRight()) {
            this.moveRight();
            moved = true;
        }
        if (input.isMovingUp()) {
            this.moveUp();
            moved = true;
        }
        if (input.isMovingDown()) {
            this.moveDown();
            moved = true;
        }

        int horizontal = input.getHorizontalDirection();
        int vertical = input.getVerticalDirection();
        if (moved && (horizontal != 0 || vertical != 0)) {
            this.setFacingAngle(Math.atan2(vertical, horizontal));
        }
    }

    public void render(Graphics g, int x, int y){
        g.setColor(COLOR_PLAYER);
        g.fillOval(x, y, this.getWidthInPixels(), this.getHeightInPixels());
        g.drawRect(x, y, this.getWidthInPixels(), this.getHeightInPixels());
    }

    
    

    }
    





