package gamelogic.player;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.AlphaComposite;
import java.awt.Composite;

import gameengine.PhysicsObject;
import gameengine.graphics.MyGraphics;
import gameengine.hitbox.RectHitbox;
import gameengine.maths.Vector2D;
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
    private boolean isFrozen = false;
    private Vector2D frozenPosition = new Vector2D();
    private boolean isInvincible = false;
    private static final Color NORMAL_COLOR = Color.YELLOW;
    private static final Color INVINCIBLE_COLOR = Color.GRAY;

    public Player(float x, float y, Level level) {
        super(x, y, level.getLevelData().getTileSize(), level.getLevelData().getTileSize(), level);
        int offset = (int)(level.getLevelData().getTileSize()*0.1);
        this.hitbox = new RectHitbox(this, offset, offset, width - offset, height - offset);
        this.frozenPosition = new Vector2D();
    }
    //Player freeze when sheild
    @Override
    public void update(float tslf) {
        if(PlayerInput.isDownKeyDown()) {
            if(!isFrozen) {
                
                frozenPosition.x = position.x;
                frozenPosition.y = position.y;
                isFrozen = true;
            }
            isInvincible = true;
            position.x = frozenPosition.x;
            position.y = frozenPosition.y;
            movementVector.x = 0;
            movementVector.y = 0;
            return; 
        } else {
            isFrozen = false;
            isInvincible = false;
        }

        float currentSpeed = walkSpeed;
        
        if(isInContact(Water.class)) {
            currentSpeed *= 1.75f;
        }
        
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
        
        if(!isInvincible) {  
            opacity = isInContact(Gas.class) ? 0.5f : 1.0f;
        }
        
        super.update(tslf);
        
        isJumping = true;
        if(collisionMatrix[BOT] != null) isJumping = false;
    }

    @Override
    public void draw(Graphics g) {
        if(g instanceof Graphics2D) {
            Graphics2D g2d = (Graphics2D)g;
            Composite originalComposite = g2d.getComposite();
            
            if(isInvincible) {
                opacity = (System.currentTimeMillis() / 100 % 2 == 0) ? 1.0f : 0.3f;
            }
            
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
            
            g.setColor(isInvincible ? INVINCIBLE_COLOR : NORMAL_COLOR);
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

    public boolean isInvincible() {
        return isInvincible;
    }
}