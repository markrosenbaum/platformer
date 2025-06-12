package gamelogic.player;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.AlphaComposite;
import java.awt.Composite;

import gameengine.PhysicsObject;
import gameengine.graphics.MyGraphics;
import gameengine.hitbox.RectHitbox;
import gamelogic.Main;
import gamelogic.level.Level;
import gamelogic.tiles.Tile;
import gamelogic.tiles.Water;
import gamelogic.tiles.Gas;

public class Player extends PhysicsObject {
    public float walkSpeed = 400;
    public float jumpPower = 1350;
    private boolean isJumping = false;
    private float opacity = 1.0f;

    public Player(float x, float y, Level level) {
        super(x, y, level.getLevelData().getTileSize(), level.getLevelData().getTileSize(), level);
        int offset = (int)(level.getLevelData().getTileSize()*0.1);
        this.hitbox = new RectHitbox(this, offset, offset, width - offset, height - offset);
    }

    @Override
    public void update(float tslf) {
        float currentSpeed = walkSpeed;
        
        // Check if in water
        if(isInContact(Water.class)) {
            currentSpeed *= 1.5f;
        }
        
        // Apply movement
        movementVector.x = 0;
        if(PlayerInput.isLeftKeyDown()) {
            movementVector.x = -currentSpeed;
        }
        if(PlayerInput.isRightKeyDown()) {
            movementVector.x = +currentSpeed;
        }
        if(PlayerInput.isJumpKeyDown() && !isJumping) {
            movementVector.y = -jumpPower;
            isJumping = true;
        }
        
        // Update opacity based on gas contact
        opacity = isInContact(Gas.class) ? 0.5f : 1.0f;
        
        super.update(tslf);
        
        isJumping = true;
        if(collisionMatrix[BOT] != null) isJumping = false;
    }

    @Override
    public void draw(Graphics g) {
        if(g instanceof Graphics2D) {
            Graphics2D g2d = (Graphics2D)g;
            Composite originalComposite = g2d.getComposite();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
            
            g.setColor(Color.YELLOW);
            MyGraphics.fillRectWithOutline(g, (int)getX(), (int)getY(), width, height);
            
            if(Main.DEBUGGING) {
                for (int i = 0; i < closestMatrix.length; i++) {
                    Tile t = closestMatrix[i];
                    if(t != null) {
                        g.setColor(Color.RED);
                        g.drawRect((int)t.getX(), (int)t.getY(), t.getSize(), t.getSize());
                    }
                }
            }
            
            hitbox.draw(g);
            
            g2d.setComposite(originalComposite);
        }
    }
}