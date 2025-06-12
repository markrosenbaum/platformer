/*
 * 
 */
package gamelogic.player;

import java.awt.event.KeyEvent;

import gameengine.input.KeyboardInputManager;

/**
 * 
 * @author Paul
 *
 */
class PlayerInput {
	/**
	 * 
	 * @return true if the walk-up-key is down
	 */
	//I got annoyed so I made the up key also jump
	 public static boolean isJumpKeyDown() {
		return KeyboardInputManager.isKeyDown(KeyEvent.VK_SPACE) || KeyboardInputManager.isKeyDown(KeyEvent.VK_W) || KeyboardInputManager.isKeyDown(KeyEvent.VK_UP);
	}
	/**
	 * 
	 * @return true if the walk-left-key is down
	 */
	public static boolean isLeftKeyDown() {
		return KeyboardInputManager.isKeyDown(KeyEvent.VK_A) || KeyboardInputManager.isKeyDown(KeyEvent.VK_LEFT);
	}
	/**
	 * 
	 * @return true if the walk-right-key is down
	 */
	public static boolean isRightKeyDown() {
		return KeyboardInputManager.isKeyDown(KeyEvent.VK_D) || KeyboardInputManager.isKeyDown(KeyEvent.VK_RIGHT);
	}
	//Checks if my down key is down
	public static boolean isDownKeyDown() {
        return KeyboardInputManager.isKeyDown(KeyEvent.VK_S) || KeyboardInputManager.isKeyDown(KeyEvent.VK_DOWN);
    }
}
