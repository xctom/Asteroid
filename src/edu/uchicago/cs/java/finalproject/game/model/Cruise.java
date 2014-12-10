package edu.uchicago.cs.java.finalproject.game.model;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

import edu.uchicago.cs.java.finalproject.controller.Game;

public class Cruise extends Sprite {

	private final double FIRE_POWER = 15.0;
	private final int MAX_EXPIRE = 50;
	
	//for drawing alternative shapes
	//you could have more than one of these sets so that your sprite morphs into various shapes
	//throughout its life
		public double[] dLengthsAlts;
		public double[] dDegreesAlts;

	public Cruise(Falcon fal) {

		super();

		//defined the points on a cartesean grid
		ArrayList<Point> pntCs = new ArrayList<Point>();

        pntCs.add(new Point(0, 10));
        pntCs.add(new Point(5, 7));
        pntCs.add(new Point(6, 4));
        pntCs.add(new Point(6, 0));
        pntCs.add(new Point(4, 4));
        pntCs.add(new Point(3, 5));

        pntCs.add(new Point(0, 5));

        pntCs.add(new Point(-3, 5));
        pntCs.add(new Point(-4, 4));
        pntCs.add(new Point(-6, 0));
        pntCs.add(new Point(-6, 4));
        pntCs.add(new Point(-5, 7));
        pntCs.add(new Point(0, 10));


		assignPolarPoints(pntCs);

		//a cruis missile expires after 25 frames
		setExpire(MAX_EXPIRE);
		setRadius(20);

        int newOri = fal.getFireOri() - 50 + Game.R.nextInt(100);

		//everything is relative to the falcon ship that fired the bullet
		setDeltaX(fal.getDeltaX()
				+ Math.cos(Math.toRadians(newOri)) * FIRE_POWER);
		setDeltaY(fal.getDeltaY()
				+ Math.sin(Math.toRadians(newOri)) * FIRE_POWER);
		setCenter(fal.getCenter());

		//set the bullet orientation to the falcon (ship) orientation
		setOrientation(newOri);
		setColor(Color.WHITE);

	}

	
	//assign for alt imag
	protected void assignPolorPointsAlts(ArrayList<Point> pntCs) {
		 dDegreesAlts = convertToPolarDegs(pntCs);
		 dLengthsAlts = convertToPolarLens(pntCs);

	}
	
	@Override
	public void move() {

//		super.move();
//
//		if (getExpire() < MAX_EXPIRE -5){
//			setDeltaX(getDeltaX() * 1.07);
//			setDeltaY(getDeltaY() * 1.07);
//		}
        Point pnt = getCenter();

        if (pnt.x > getDim().width || pnt.x < 0) {
            setDeltaX(-getDeltaX());
            setOrientation(180 - getOrientation());
        } else if(pnt.y > getDim().height || pnt.y < 0){
            setDeltaY(-getDeltaY());
            setOrientation(-getOrientation());
        }

        double dX = pnt.x + getDeltaX();
        double dY = pnt.y + getDeltaY();

        setCenter(new Point((int) dX, (int) dY));

	}
	
	@Override
	public void draw(Graphics g) {

        super.draw(g);
        g.setColor(Color.RED);
        g.fillPolygon(getXcoords(), getYcoords(), dDegrees.length);

    }

//    public void drawAlt(Graphics g) {
//    	setXcoords( new int[dDegreesAlts.length]);
//    	setYcoords( new int[dDegreesAlts.length]);
//        setObjectPoints( new Point[dDegrees.length]);
//
//        for (int nC = 0; nC < dDegreesAlts.length; nC++) {
//
//        	setXcoord((int) (getCenter().x + getRadius()
//                    * dLengthsAlts[nC]
//                    * Math.sin(Math.toRadians(getOrientation()) + dDegreesAlts[nC])), nC);
//
//
//        	setYcoord((int) (getCenter().y - getRadius()
//                            * dLengthsAlts[nC]
//                            * Math.cos(Math.toRadians(getOrientation()) + dDegreesAlts[nC])), nC);
//            //need this line of code to create the points which we will need for debris
//        	setObjectPoint( new Point(getXcoord(nC), getYcoord(nC)), nC);
//        }
//
//        g.setColor(Color.DARK_GRAY);
//        g.drawPolygon(getXcoords(), getYcoords(), dDegreesAlts.length);
//    }


	//override the expire method - once an object expires, then remove it from the arrayList.
	@Override
	public void expire() {
		if (getExpire() == 0)
			CommandCenter.movFriends.remove(this);
		else
			setExpire(getExpire() - 1);
	}

}
