package items;

import java.util.ArrayList;

import game.Enemy;

public interface Melee {
	public void attackEvent(ArrayList<Enemy> enemies);
}