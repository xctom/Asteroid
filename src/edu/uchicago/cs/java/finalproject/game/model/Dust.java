package edu.uchicago.cs.java.finalproject.game.model;

import edu.uchicago.cs.java.finalproject.controller.Game;

import java.awt.*;

/**
 * Created by xuchen on 12/1/14.
 */
public class Dust extends Sprite {

    private int nExpiry;

    public Dust(Asteroid astExploded){

        //call Sprite constructor
        super();

        //random delta-x
        int nDX = Game.R.nextInt(4);
        if(nDX %2 ==0)
            nDX = -nDX;
        setDeltaX(nDX);

        //random delta-y
        int nDY = Game.R.nextInt(4);
        if(nDY %2 ==0)
            nDY = -nDY;
        setDeltaY(nDY);

        setCenter(astExploded.getCenter());
        setnExpiry(100);

    }

    public void setnExpiry(int nExpiry) {
        this.nExpiry = nExpiry;
    }

    @Override
    public void draw(Graphics g) {

        Point pntCenter = getCenter();
        g.fillOval(pntCenter.x, pntCenter.y, nExpiry / 20 , nExpiry / 20);

    }

    @Override
    //called every 45 ms
    public void expire() {
        if(nExpiry > 0) {
            nExpiry--;
        }else {
            CommandCenter.getMovDebris().remove(this);
            System.gc();
        }
    }


}
