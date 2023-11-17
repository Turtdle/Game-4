package items;

import game.Enemy;

import java.util.ArrayList;
public class ArrowProjectile extends Projectile{

    public ArrowProjectile(double x, double y, boolean isRight, ArrayList<Enemy> enemies) {
        super(x, y, isRight , enemies);

        image.scale(5);
        speed = 10;
    }
    @Override
    public void animate(){
        if(animationState >= 3){
            animationState = 1;
        }
        else{
            animationState++;
        }
        if(isRight){
            image.setImage("media/Projectiles/Arrow.png");
        }else{
            image.setImage("media/Projectiles/Arrow.png");
        }

    }
    @Override
    public void tick(){
        super.tick();
        if(loaded){
            for(Enemy e : enemies){
                //if fireball is a certain distance from enemies center
                if(!e.isDead()){
                    if(Math.abs(x - e.getX()) < 50 && Math.abs(y - e.getY()) < 50){
                        e.setHealth(e.getHealth() - 1);

                        e.knockback(-5);
                        image.setVisible(false);
                        loaded = false;
                }
                }
                
            }
        }

    }
}