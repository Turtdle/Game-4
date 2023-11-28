package items;

import java.util.ArrayList;

import acm.graphics.*;
import game.Enemy;
import game.Item;

public class Sword7 extends Item implements Melee{
	public Sword7() {

		GImage image = new GImage("media/Items/Weapons/Sword/Sword_Left7.png");
		GImage imageRight = new GImage("media/Items/Weapons/Sword/Sword_Right7.png");

		image.setSize(75,75);
		imageRight.setSize(75,75);
		this.getItemBody().add(image);
		this.getItemBodyRight().add(imageRight);
	}

	@Override
	public void attackEvent(ArrayList<Enemy> enemies) {
		
	}

	@Override
	public int getKnockback() {
		return 5;
	}
	@Override
	public int getDamage() {
		return 20;
	}
}