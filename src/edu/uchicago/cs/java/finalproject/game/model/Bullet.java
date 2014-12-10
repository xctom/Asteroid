package edu.uchicago.cs.java.finalproject.game.model;

import java.awt.*;
import java.util.ArrayList;

import edu.uchicago.cs.java.finalproject.controller.Game;


public class Bullet extends Sprite {

	private final double FIRE_POWER = 50.0;
	
    public Bullet(Falcon fal){
		
		super();

		//defined the points on a cartesean grid
		ArrayList<Point> pntCs = new ArrayList<Point>();
		
		pntCs.add(new Point(0,8)); //top point

        pntCs.add(new Point(2,2));
        pntCs.add(new Point(8,0));
        pntCs.add(new Point(2,-2));
        pntCs.add(new Point(0,-8));
        pntCs.add(new Point(-2,-2));
        pntCs.add(new Point(-8,0));
        pntCs.add(new Point(-2,2));
        pntCs.add(new Point(0,8));

		assignPolarPoints(pntCs);

		//a bullet expires after 20 frames
	    setExpire(20);
	    setRadius(6);
	    

	    //everything is relative to the falcon ship that fired the bullet
	    setDeltaX( fal.getDeltaX() +
	               Math.cos( Math.toRadians( fal.getFireOri() ) ) * FIRE_POWER) ;
	    setDeltaY( fal.getDeltaY() +
	               Math.sin( Math.toRadians( fal.getFireOri() ) ) * FIRE_POWER);
	    setCenter( fal.getCenter() );

	    //set the bullet orientation to the falcon (ship) orientation
	    setOrientation(fal.getOrientation());

	}

    public void move() {

        Point pnt = getCenter();
        double dX = pnt.x + getDeltaX();
        double dY = pnt.y + getDeltaY();

        if (pnt.x > getDim().width || pnt.x < 0 || pnt.y > getDim().height || pnt.y < 0) {
            setExpire(0);
        } else {
            setCenter(new Point((int) dX, (int) dY));
        }

    }

    //override the expire method - once an object expires, then remove it from the arrayList. 
	public void expire(){
 		if (getExpire() == 0)
 			CommandCenter.movFriends.remove(this);
	}

    @Override
    public void draw(Graphics g) {
        super.draw(g);
        g.setColor(Color.RED);
        ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.fillPolygon(getXcoords(),getYcoords(),getDegrees().length);
    }
}
