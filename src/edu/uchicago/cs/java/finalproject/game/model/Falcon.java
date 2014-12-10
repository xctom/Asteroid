package edu.uchicago.cs.java.finalproject.game.model;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.uchicago.cs.java.finalproject.controller.Game;
import edu.uchicago.cs.java.finalproject.sounds.Sound;


public class Falcon extends Sprite {

	// ==============================================================
	// FIELDS 
	// ==============================================================
	
	private final double THRUST = .65;
    public final int UP = 1;//0001
    public final int DOWN = 2;//0010
    public final int LEFT = 4;//0100
    public final int RIGHT = 8;//1000
    public final int STOP = 0;
    private int moveOri;

    public final int fireUp = 1;//0001
    public final int fireDown = 2;//0010
    public final int fireLeft = 4;//0100
    public final int fireRight = 8;//1000
    public final int fireNone = 0;//0000
    private int fireOri;

	final int DEGREE_STEP = 15;
	
	private boolean bShield = false;
	private boolean bFlame = false;
	private boolean bProtected; //for fade in and out
	private boolean bPowerUp;

	private boolean bThrusting = false;
	private boolean bTurningRight = false;
	private boolean bTurningLeft = false;
	private int bTurningBound;

	private int nShield;//shield time
	private int nPowerUp;//power up time

	private final double[] FLAME = { 22 * Math.PI / 24 + Math.PI / 2,
			Math.PI + Math.PI / 2, 26 * Math.PI / 24 + Math.PI / 2 };

	private int[] nXFlames = new int[FLAME.length];
	private int[] nYFlames = new int[FLAME.length];

	private Point[] pntFlames = new Point[FLAME.length];

	
	// ==============================================================
	// CONSTRUCTOR 
	// ==============================================================
	
	public Falcon() {
		super();

		ArrayList<Point> pntCs = new ArrayList<Point>();
		
		// top of ship
		//pntCs.add(new Point(0, 18));

        //right part
        pntCs.add(new Point(0,-3));
        pntCs.add(new Point(6,0));
        pntCs.add(new Point(3,12));
        pntCs.add(new Point(12,0));
        pntCs.add(new Point(0,-10));

        //left part
        pntCs.add(new Point(0,-3));
        pntCs.add(new Point(-6,0));
        pntCs.add(new Point(-3,12));
        pntCs.add(new Point(-12,0));
        pntCs.add(new Point(0,-10));

		assignPolarPoints(pntCs);

		setColor(Color.yellow);
		
		//put falcon in the middle.
		setCenter(new Point(Game.DIM.width / 2, Game.DIM.height / 2));
		
		//with random orientation
		//setOrientation(Game.R.nextInt(360));
        setOrientation(0);

		//this is the size of the falcon
		setRadius(35);

		//these are falcon specific
		setProtected(true);
        setShield(0);
        setPowerUp(0);
		setFadeValue(0);

        //Keep still
        addMoveOri(STOP);
        addFireOri(fireNone);
	}
	
	
	// ==============================================================
	// METHODS 
	// ==============================================================

	public void move() {
		super.move();

        fire();

        if (moveOri != STOP) {
            bFlame = true;
            if ((moveOri & UP) == UP) {
                setDeltaY(-THRUST*25);
            }

            if ((moveOri & DOWN) == DOWN) {
                setDeltaY(THRUST*25);
            }

            if ((moveOri & LEFT) == LEFT) {
                setDeltaX(-THRUST*25);
            }

            if ((moveOri & RIGHT) == RIGHT) {
                setDeltaX(THRUST*25);
            }

            setbTurningBound();
            rotate();

        }else {
            bFlame = false;
        }

	} //end move

	public void rotateLeft() {
		bTurningLeft = true;

	}

	public void rotateRight() {
		bTurningRight = true;
	}

    public void rotateClockWise(){
        setOrientation(getOrientation() + DEGREE_STEP);
    }

    public void rotateCounterClockWise(){
        setOrientation(getOrientation() - DEGREE_STEP);
    }

	public void stopRotating() {
		bTurningRight = false;
		bTurningLeft = false;
	}

    public void rotate(){
        if (getOrientation() != bTurningBound) {

            if (((getOrientation() >= 270 || getOrientation() == 0) && bTurningBound == 270)){
                if (getOrientation() == 0) setOrientation(360);
                rotateCounterClockWise();
            }else if (getOrientation() >= 270 && bTurningBound == 0) {
                rotateClockWise();
                if (getOrientation() == 360) setOrientation(0);
            }else if (getOrientation() >= 0 && getOrientation() <= 135 && bTurningBound == 315){
                if (getOrientation() == 0) setOrientation(360);
                rotateCounterClockWise();
            }else if (getOrientation() - bTurningBound > 0) {
                rotateCounterClockWise();
            }else if (getOrientation() - bTurningBound < 0) {
                rotateClockWise();
            }

        }

    }


    public void setbTurningBound() {

        switch (moveOri){
            case 1:bTurningBound = 270;break;//0001 up
            case 2:bTurningBound = 90;break;//0010 down
            case 4:bTurningBound = 180;break;//0100 left
            case 8:bTurningBound = 0;break;//1000 right
            case 5:bTurningBound = 225;break;//0101 up left
            case 9:bTurningBound = 315;break;//1001 up right
            case 6:bTurningBound = 135;break;//0110 down left
            case 10:bTurningBound = 45;break;//1010 down right
            default:break;
        }

    }

    public void thrustOn() {
		bThrusting = true;
	}

	public void thrustOff() {
		bThrusting = false;
		bFlame = false;
	}

    /**
     * add one orientation for moving
     * @param moveOri
     */
    public void addMoveOri(int moveOri) {
        this.moveOri |= moveOri;
    }

    /**
     * remove one orientation for moving
     * @param moveOri
     */
    public void removeMoveOri(int moveOri){
        this.moveOri &= ~moveOri;

        if (moveOri == LEFT || moveOri == RIGHT){
            setDeltaX(0);
        }

        if (moveOri == UP || moveOri == DOWN){
            setDeltaY(0);
        }
    }

    /**
     * add fire direction
     * @param fireOri
     */
    public void addFireOri(int fireOri) {
        this.fireOri |= fireOri;
    }

    /**
     * remove fire direction
     * @param fireOri
     */
    public void removeFireOri(int fireOri){
        this.fireOri &= ~fireOri;
    }

    public int getFireOri(){
        switch (fireOri){
            case 1:return 270;
            case 2:return 90;
            case 4:return 180;
            case 8:return 0;
            case 5:return 225;
            case 9:return 315;
            case 6:return 135;
            case 10:return 45;
            default:return -1;
        }
    }

    public void fire(){

        int ori = getFireOri();

        if (ori != -1){
            if (bPowerUp){
                //reduce cruise or game will got stuck
                if (nPowerUp % 2 == 0)
                    CommandCenter.movFriends.add(new Cruise(this));
            }else {
                CommandCenter.movFriends.add(new Bullet(this));
            }
        }

    }


    private int adjustColor(int nCol, int nAdj) {
		if (nCol - nAdj <= 0) {
			return 0;
		} else {
			return nCol - nAdj;
		}
	}

	public void draw(Graphics g) {

		//does the fading at the beginning or after hyperspace
		Color colShip;
		if (getFadeValue() == 255) {
			colShip = Color.orange;
		} else {
			//colShip = new Color(adjustColor(getFadeValue(), 200), adjustColor(
			//		getFadeValue(), 175), getFadeValue());
            colShip = Color.blue;
		}

		//shield on
		if (bShield && nShield > 0) {
			setShield(getShield() - 1);
			g.setColor(Color.green);
			g.drawOval(getCenter().x - (int)(getRadius() * 1.5),
                    (int)(getCenter().y - getRadius() * 1.5), getRadius() * 3,
					getRadius() * 3);

		} //end if shield


        //power up on
        if (bPowerUp && nPowerUp > 0) {
            setPowerUp(getPowerUp() - 1);
        } //end if power up

		//thrusting
		if (bFlame) {
			g.setColor(colShip);
			//the flame
			for (int nC = 0; nC < FLAME.length; nC++) {
				if (nC % 2 != 0) //odd
				{
					pntFlames[nC] = new Point((int) (getCenter().x + 2.5
							* getRadius()
							* Math.sin(Math.toRadians(getOrientation())
									+ FLAME[nC])), (int) (getCenter().y - 2.5
							* getRadius()
							* Math.cos(Math.toRadians(getOrientation())
									+ FLAME[nC])));

				} else //even
				{
					pntFlames[nC] = new Point((int) (getCenter().x + getRadius()
							* 1.1
							* Math.sin(Math.toRadians(getOrientation())
									+ FLAME[nC])),
							(int) (getCenter().y - getRadius()
									* 1.1
									* Math.cos(Math.toRadians(getOrientation())
											+ FLAME[nC])));

				} //end even/odd else

			} //end for loop

			for (int nC = 0; nC < FLAME.length; nC++) {
				nXFlames[nC] = pntFlames[nC].x;
				nYFlames[nC] = pntFlames[nC].y;

			} //end assign flame points

			//g.setColor( Color.white );
            ((Graphics2D)g).setPaint(new GradientPaint(0,0,Color.RED,50,50,Color.yellow,true));
			g.fillPolygon(nXFlames, nYFlames, FLAME.length);

		} //end if flame

		drawShipWithColor(g, colShip);

	} //end draw()

	public void drawShipWithColor(Graphics g, Color col) {
		super.draw(g);
		g.setColor(col);
		g.drawPolygon(getXcoords(), getYcoords(), dDegrees.length);
	}

	public void fadeInOut() {
		if (getProtected()) {
			setFadeValue(getFadeValue() + 3);
		}
		if (getFadeValue() == 255) {
			setProtected(false);
		}
	}
	
	public void setProtected(boolean bParam) {
		if (bParam) {
			setFadeValue(0);
		}
		bProtected = bParam;
	}

	public void setProtected(boolean bParam, int n) {
		if (bParam && n % 3 == 0) {
			setFadeValue(n);
		} else if (bParam) {
			setFadeValue(0);
		}
		bProtected = bParam;
	}	

	public boolean getProtected() {return bProtected;}

	public void setShield(int n) {

        if (n > 0) {
            nShield = n;
            bShield = true;
        }else {
            nShield = 0;
            bShield = false;
        }

    }

	public int getShield() {return nShield;}

    public int getPowerUp() {
        return nPowerUp;
    }

    public void setPowerUp(int n) {
        if (n > 0) {
            nPowerUp = n;
            bPowerUp = true;
        }else {
            nPowerUp = 0;
            bPowerUp = false;
        }
    }

    public void setBomb(int bNum){

    }
} //end class
