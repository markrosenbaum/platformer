package gameengine;

import gameengine.GameObject;
import gameengine.hitbox.RectHitbox;
import gameengine.maths.Vector2D;
import gamelogic.level.Level;
import gamelogic.tiles.Tile;

public class PhysicsObject extends GameObject {
    //Used for collision
    public static final int BOT = 0;
    public static final int TOP = 1;
    public static final int LEF = 2;
    public static final int RIG = 3;

    protected Vector2D movementVector;
    protected RectHitbox hitbox;
    protected Tile[] collisionMatrix;
    protected Tile[] closestMatrix;
    private Level level;

    protected boolean isInContact(Class<? extends Tile> tileType) {
        for(int i = 0; i < level.getMap().getWidth(); i++) {
            for(int j = 0; j < level.getMap().getHeight(); j++) {
                Tile tile = level.getMap().getTiles()[i][j];
                if(tile != null && tileType.isInstance(tile)) {
                    if(tile.getHitbox() != null && tile.getHitbox().isIntersecting(this.hitbox)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public PhysicsObject(float x, float y, int width, int height, Level level) {
        super(x, y, width, height);
        this.level = level;
        this.movementVector = new Vector2D();
        this.hitbox = new RectHitbox(this, 0, 0, width, height);
        this.collisionMatrix = new Tile[4];
        this.closestMatrix = new Tile[4];
    }

    @Override
    public void update(float tslf) {
        movementVector.y += (Level.GRAVITY * Level.GRAVITY) * tslf;

        updateCollisionMatrix(tslf); // checking collision based on the new position -> current movement Vector

        //Collision-handling 
        Tile bot = collisionMatrix[BOT];
        if(bot != null) {
            position.y = bot.getHitbox().getY() - (hitbox.getOffsetY() + hitbox.getHeight());
            movementVector.y = 0;
        }
        Tile top = collisionMatrix[TOP];
        if(top != null) {
            position.y = (top.getHitbox().getY() + top.getHitbox().getHeight()) - hitbox.getOffsetY();
            movementVector.y = 0;
        }
        Tile lef = collisionMatrix[LEF];
        if(lef != null) {
            position.x = (lef.getHitbox().getX() + lef.getHitbox().getWidth()) - hitbox.getOffsetX();
            movementVector.x = 0;
        }
        Tile rig = collisionMatrix[RIG];
        if(rig != null) {
            position.x = rig.getHitbox().getX() - (hitbox.getOffsetX() + hitbox.getWidth());
            movementVector.x = 0;
        }

        position.x += movementVector.x * tslf;
        position.y += movementVector.y * tslf;
        
        hitbox.update(); // -> saving old position
    }

    public void updateCollisionMatrix(float tslf) {
        Vector2D newPositon = new Vector2D(getX(), getY());
        newPositon.x += movementVector.x * tslf;
        newPositon.y += movementVector.y * tslf;

        //Finding the closest obstacles to the player in all 4 directions;
        float closestBot = Float.MAX_VALUE, closestTop = Float.MAX_VALUE, closestLef = Float.MAX_VALUE, closestRig = Float.MAX_VALUE;
        Tile bot = null, top = null, lef = null, rig = null;

        //old Position of Physics Object
        float leftSide = hitbox.getX();
        float rightSide = hitbox.getX() + hitbox.getWidth();
        float topSide = hitbox.getY();
        float botSide = hitbox.getY() + hitbox.getHeight();
        
        for (int i = 0; i < level.getMap().getWidth(); i++) {
            for (int j = 0; j < level.getMap().getHeight(); j++) {
                Tile tile = level.getMap().getTiles()[i][j];
                if(!tile.isSolid()) continue;
                RectHitbox obstacle = tile.getHitbox();
                if(obstacle == null) continue;
                
                //Position of tile
                float tileLeftSide = obstacle.getX();
                float tileRightSide = obstacle.getX() + obstacle.getWidth();
                float tileTopSide = obstacle.getY();
                float tileBotSide = obstacle.getY() + obstacle.getHeight();
                
                //Find closest obstacle below player
                if(leftSide < tileRightSide && rightSide > tileLeftSide && botSide <= tileTopSide) {
                    if(tileTopSide - (botSide) < closestBot) {
                        bot = tile;
                        closestBot = tileTopSide - (botSide);
                    }
                }
                //Find closest obstacle above player
                if(leftSide < tileRightSide && rightSide > tileLeftSide && topSide >= tileBotSide) {
                    if(topSide - (tileBotSide) < closestTop) {
                        top = tile;
                        closestTop = topSide - (tileBotSide);
                    }
                }
                //Find closest obstacle right to the player
                if(topSide < tileBotSide && botSide > tileTopSide && rightSide <= tileLeftSide) {
                    if(tileLeftSide - (rightSide) < closestRig) {
                        rig = tile;
                        closestRig = tileLeftSide - (rightSide);
                    }
                }
                //Find closest obstacle left to the player
                if(topSide < tileBotSide && botSide > tileTopSide && leftSide >= tileRightSide) {
                    if(leftSide - (tileRightSide) < closestLef) {
                        lef = tile;
                        closestLef = leftSide - (tileRightSide);
                    }
                }
            }
        }
        
        //Save closest as options
        closestMatrix[BOT] = bot;
        closestMatrix[TOP] = top;
        closestMatrix[LEF] = lef;
        closestMatrix[RIG] = rig;
        
        //Fill Matrix when collision really is detected
        Tile[] matrix = new Tile[4];
        if(bot != null) {
            if(newPositon.y + (hitbox.getOffsetY() + hitbox.getHeight()) > bot.getHitbox().getY()) {
                matrix[BOT] = bot;
            }
        }
        if(top != null) {
            if(newPositon.y + hitbox.getOffsetY() < top.getHitbox().getY() + top.getHitbox().getHeight()) {
                matrix[TOP] = top;
            }
        }
        if(lef != null) {
            if(newPositon.x + hitbox.getOffsetX() < lef.getHitbox().getX() + lef.getHitbox().getWidth()) {
                matrix[LEF] = lef;
            }
        }
        if(rig != null) {
            if(newPositon.x + (hitbox.getOffsetX() + hitbox.getWidth()) > rig.getHitbox().getX()) {
                matrix[RIG] = rig;
            }
        }   
        this.collisionMatrix = matrix;
    }

    public Tile[] getCollisionMatrix() {
        return collisionMatrix;
    }
    
    public float getMovementX() {
        return movementVector.x;
    }
    
    public float getMovementY() {
        return movementVector.y;
    }
    
    public RectHitbox getHitbox() {
        return hitbox;
    }
    
    public Level getLevel() {
        return level;
    }
}