package edu.uchicago.cs.java.finalproject.game.model;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import edu.uchicago.cs.java.finalproject.controller.Game;

public class NewShipFloater extends Sprite {

    public static enum KIND {ADDLIFE,POWERUP,SHIELD,BOMB};

	private int nSpin;
    private Random rand;
    private KIND kind;
    private int score;

	public NewShipFloater() {

		super();

        rand = new Random();

		ArrayList<Point> pntCs = new ArrayList<Point>();
		// top of ship
		pntCs.add(new Point(4, 4));
		pntCs.add(new Point(2,0));
		pntCs.add(new Point(4, -4));
		pntCs.add(new Point(0,-2));
		pntCs.add(new Point(-4, -4));
		pntCs.add(new Point(-2,0));
		pntCs.add(new Point(-4, 4));
		pntCs.add(new Point(0,2));

		assignPolarPoints(pntCs);

		setExpire(250);
		setRadius(40);
        setKind(getRandKind());

        switch (kind){
            case ADDLIFE:setColor(Color.blue);break;
            case POWERUP:setColor(Color.yellow);break;
            case SHIELD:setColor(Color.GREEN);break;
            case BOMB:setColor(Color.red);break;
        }

		int nX = Game.R.nextInt(10);
		int nY = Game.R.nextInt(10);
		int nS = Game.R.nextInt(5);
		
		//set random DeltaX
		if (nX % 2 == 0)
			setDeltaX(nX);
		else
			setDeltaX(-nX);

		//set rnadom DeltaY
		if (nY % 2 == 0)
			setDeltaX(nY);
		else
			setDeltaX(-nY);
		
		//set random spin
		if (nS % 2 == 0)
			setSpin(nS);
		else
			setSpin(-nS);

		//random point on the screen
		setCenter(new Point(Game.R.nextInt(Game.DIM.width),
				Game.R.nextInt(Game.DIM.height)));

		//random orientation 
		setOrientation(Game.R.nextInt(360));

        //set score for new ship floater
        setScore(1000);
	}

    public NewShipFloater(Asteroid astExploded){
        this();
        setCenter(astExploded.getCenter());
    }

	public void move() {
		super.move();
		setOrientation(getOrientation() + getSpin());
	}

    /**
     * get new kind for new ship floater
     * @return
     */
    private KIND getRandKind(){
        int pick = rand.nextInt(KIND.values().length);
        return KIND.values()[pick];
    }

	public int getSpin() {
		return this.nSpin;
	}

	public void setSpin(int nSpin) {
		this.nSpin = nSpin;
	}

	//override the expire method - once an object expires, then remove it from the arrayList.
	@Override
	public void expire() {
		if (getExpire() == 0)
			CommandCenter.movFloaters.remove(this);
		else
			setExpire(getExpire() - 1);
	}

    public void setKind(KIND kind) {
        this.kind = kind;
    }

    public KIND getKind() {
        return kind;
    }

    @Override
	public void draw(Graphics g) {
		super.draw(g);
		//fill this polygon (with whatever color it has)
		g.fillPolygon(getXcoords(), getYcoords(), dDegrees.length);
		//now draw a white border
		g.setColor(Color.WHITE);
		g.drawPolygon(getXcoords(), getYcoords(), dDegrees.length);
	}

    public int getScore(){
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
