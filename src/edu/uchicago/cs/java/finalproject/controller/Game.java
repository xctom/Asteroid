package edu.uchicago.cs.java.finalproject.controller;

import sun.audio.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.sound.sampled.Clip;

import edu.uchicago.cs.java.finalproject.game.model.*;
import edu.uchicago.cs.java.finalproject.game.view.*;
import edu.uchicago.cs.java.finalproject.sounds.Sound;

// ===============================================
// == This Game class is the CONTROLLER
// ===============================================

public class Game implements Runnable, KeyListener {

	// ===============================================
	// FIELDS
	// ===============================================

	public static final Dimension DIM = new Dimension(1300, 700); //the dimension of the game.
	private GamePanel gmpPanel;
	public static Random R = new Random();
	public final static int ANI_DELAY = 45; // milliseconds between screen
											// updates (animation)
	private Thread thrAnim;
	private int nLevel = 1;
	private int nTick = 0;
	private ArrayList<Tuple> tupMarkForRemovals;
	private ArrayList<Tuple> tupMarkForAdds;
	private boolean bMuted = false;
	

	private final int PAUSE = 80, // p key
			QUIT = 81, // q key
			FIRE_LEFT = 37, //fire left, left arrow
			FIRE_RIGHT = 39, // fire right, right arrow
			FIRE_UP = 38, // fire up, up arrow
            FIRE_DOWN = 40,//fire down, down arrow
            MOVE_LEFT = 65,//move left, a key
            MOVE_RIGHT = 68,//move right, d key
            MOVE_UP = 87,//move up, w key
            MOVE_DOWN = 83,//move down, s key
			START = 83, // s key
			FIRE = 32, // space key
			MUTE = 77, // m-key mute

	// for possible future use
	// HYPER = 68, 					// d key
	// SHIELD = 65, 				// a key arrow
	// NUM_ENTER = 10, 				// hyp
	 SPECIAL = 70; 					// fire special weapon;  F key

	private Clip clpThrust;
	private Clip clpMusicBackground;

	private static final int SPAWN_NEW_SHIP_FLOATER = 500;



	// ===============================================
	// ==CONSTRUCTOR
	// ===============================================

	public Game() {

		gmpPanel = new GamePanel(DIM);
		gmpPanel.addKeyListener(this);

		clpThrust = Sound.clipForLoopFactory("whitenoise.wav");
		clpMusicBackground = Sound.clipForLoopFactory("music-background2.wav");

	}

	// ===============================================
	// ==METHODS
	// ===============================================

	public static void main(String args[]) {
		EventQueue.invokeLater(new Runnable() { // uses the Event dispatch thread from Java 5 (refactored)
					public void run() {
						try {
							Game game = new Game(); // construct itself
							game.fireUpAnimThread();

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}

	private void fireUpAnimThread() { // called initially
		if (thrAnim == null) {
			thrAnim = new Thread(this); // pass the thread a runnable object (this)
            thrAnim.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread t, Throwable e) {
                    
                }
            });
			thrAnim.start();
		}
	}

	// implements runnable - must have run method
	public void run() {


		// lower this thread's priority; let the "main" aka 'Event Dispatch'
		// thread do what it needs to do first
		thrAnim.setPriority(Thread.MIN_PRIORITY);

		// and get the current time
		long lStartTime = System.currentTimeMillis();

		// this thread animates the scene
		while (Thread.currentThread() == thrAnim) {
			tick();
			spawnNewShipFloater();
			gmpPanel.update(gmpPanel.getGraphics()); // update takes the graphics context we must 
														// surround the sleep() in a try/catch block
														// this simply controls delay time between 
														// the frames of the animation

			//this might be a good place to check for collisions
			checkCollisions();
			//this might be a god place to check if the level is clear (no more foes)
			//if the level is clear then spawn some big asteroids -- the number of asteroids 
			//should increase with the level. 
			checkNewLevel();
            spawnAsteroids();

			try {
				// The total amount of time is guaranteed to be at least ANI_DELAY long.  If processing (update) 
				// between frames takes longer than ANI_DELAY, then the difference between lStartTime - 
				// System.currentTimeMillis() will be negative, then zero will be the sleep time
				lStartTime += ANI_DELAY;
				Thread.sleep(Math.max(0,
						lStartTime - System.currentTimeMillis()));
			} catch (InterruptedException e) {
				// just skip this frame -- no big deal
				continue;
			}
		} // end while
	} // end run

	private void checkCollisions() {

		
		//@formatter:off
		//for each friend in movFriends
			//for each foe in movFoes
				//if the distance between the two centers is less than the sum of their radii
					//mark it for removal
		
		//for each mark-for-removal
			//remove it
		//for each mark-for-add
			//add it
		//@formatter:on
		
		//we use this ArrayList to keep pairs of movMovables/movTarget for either
		//removal or insertion into our arrayLists later on
		tupMarkForRemovals = new ArrayList<Tuple>();
		tupMarkForAdds = new ArrayList<Tuple>();

		Point pntFriendCenter, pntFoeCenter;
		int nFriendRadiux, nFoeRadiux;

		for (Movable movFriend : CommandCenter.movFriends) {
			for (Movable movFoe : CommandCenter.movFoes) {

				pntFriendCenter = movFriend.getCenter();
				pntFoeCenter = movFoe.getCenter();
				nFriendRadiux = movFriend.getRadius();
				nFoeRadiux = movFoe.getRadius();

				//detect collision
				if (pntFriendCenter.distance(pntFoeCenter) < (nFriendRadiux + nFoeRadiux)) {

					//falcon
					if ((movFriend instanceof Falcon) ){
						if (!CommandCenter.getFalcon().getProtected() && CommandCenter.getFalcon().getShield() == 0){
							tupMarkForRemovals.add(new Tuple(CommandCenter.movFriends, movFriend));
							CommandCenter.spawnFalcon(false);
						}

                        if (CommandCenter.getFalcon().getShield() != 0) {
                            killFoe(movFoe);
                        }
					}
					//not the falcon
					else {
						tupMarkForRemovals.add(new Tuple(CommandCenter.movFriends, movFriend));
						killFoe(movFoe);
					}//end else 

					//explode/remove foe
				
				}//end if 
			}//end inner for
		}//end outer for


		//check for collisions between falcon and floaters
		if (CommandCenter.getFalcon() != null){
			Point pntFalCenter = CommandCenter.getFalcon().getCenter();
			int nFalRadiux = CommandCenter.getFalcon().getRadius();
			Point pntFloaterCenter;
			int nFloaterRadiux;
			
			for (Movable movFloater : CommandCenter.movFloaters) {
				pntFloaterCenter = movFloater.getCenter();
				nFloaterRadiux = movFloater.getRadius();
	
				//detect collision
				if (pntFalCenter.distance(pntFloaterCenter) < (nFalRadiux + nFloaterRadiux)) {
	
					if (movFloater instanceof NewShipFloater) {

                        switch (((NewShipFloater) movFloater).getKind()){
                            case ADDLIFE:CommandCenter.setNumFalcons(CommandCenter.getNumFalcons() + 1);break;
                            case POWERUP:CommandCenter.getFalcon().setPowerUp(200);break;
                            case SHIELD:CommandCenter.getFalcon().setShield(100);break;
                            case BOMB:

                                for (Movable movFoe : CommandCenter.movFoes){
                                    killFoe(movFoe);
                                }

                                break;
                        }

                        CommandCenter.setScore(CommandCenter.getScore() + ((NewShipFloater) movFloater).getScore());

                        tupMarkForRemovals.add(new Tuple(CommandCenter.movFloaters, movFloater));
                        Sound.playSound("pacman_eatghost.wav");
                    }
	
				}//end if 
			}//end inner for
		}//end if not null
		
		//remove these objects from their appropriate ArrayLists
		//this happens after the above iterations are done
		for (Tuple tup : tupMarkForRemovals) 
			tup.removeMovable();
		
		//add these objects to their appropriate ArrayLists
		//this happens after the above iterations are done
		for (Tuple tup : tupMarkForAdds) 
			tup.addMovable();

		//call garbage collection
		System.gc();
		
	}//end meth

	private void killFoe(Movable movFoe) {
		
		if (movFoe instanceof Asteroid){

			//we know this is an Asteroid, so we can cast without threat of ClassCastException
			Asteroid astExploded = (Asteroid)movFoe;
			//big asteroid 
			if(astExploded.getSize() == 0){
				//spawn two medium Asteroids and there ia a chance to get a newShipFloater from this asteroid
				tupMarkForAdds.add(new Tuple(CommandCenter.movFoes,new Asteroid(astExploded)));
				tupMarkForAdds.add(new Tuple(CommandCenter.movFoes,new Asteroid(astExploded)));
                spawnNewShipFloater(astExploded);
				
			} 
			//medium size aseroid exploded
			else if(astExploded.getSize() == 1){
				//spawn three small Asteroids
				tupMarkForAdds.add(new Tuple(CommandCenter.movFoes,new Asteroid(astExploded)));
				tupMarkForAdds.add(new Tuple(CommandCenter.movFoes,new Asteroid(astExploded)));
				tupMarkForAdds.add(new Tuple(CommandCenter.movFoes,new Asteroid(astExploded)));
			}
			//remove the original Foe
            tupMarkForRemovals.add(new Tuple(CommandCenter.movFoes, movFoe));
            tupMarkForAdds.add(new Tuple(CommandCenter.movDebris,new Explosion(astExploded)));

            for (int i = 0; i < R.nextInt(5); i++){
                tupMarkForAdds.add(new Tuple(CommandCenter.movDebris,new Dust(astExploded)));
            }

            //Sound.playSound("blast.wav");

            CommandCenter.setScore(CommandCenter.getScore() + ((Asteroid) movFoe).getScore());
			
		} 
		//not an asteroid
		else {
			//remove the original Foe
            tupMarkForRemovals.add(new Tuple(CommandCenter.movFoes, movFoe));

		}

	}

	//some methods for timing events in the game,
	//such as the appearance of UFOs, floaters (power-ups), etc. 
	public void tick() {
		if (nTick == Integer.MAX_VALUE)
			nTick = 0;
		else
			nTick++;
	}

	public int getTick() {
		return nTick;
	}

    /**
     * show power up
     */
	private void spawnNewShipFloater() {
		//make the appearance of power-up dependent upon ticks and levels
		//the higher the level the more frequent the appearance
		if (nTick % (SPAWN_NEW_SHIP_FLOATER - nLevel * 7) == 0) {
			CommandCenter.movFloaters.add(new NewShipFloater());
		}
	}

    private void spawnNewShipFloater(Asteroid astExploded){
        //20% chance to get a new floater
        if (R.nextInt(10) > 7){
            CommandCenter.movFloaters.add(new NewShipFloater(astExploded));
        }
    }

	// Called when user presses 's'
	private void startGame() {
		CommandCenter.clearAll();
		CommandCenter.initGame();
		CommandCenter.setPlaying(true);
		CommandCenter.setPaused(false);
		if(!bMuted) {
            clpMusicBackground.loop(Clip.LOOP_CONTINUOUSLY);
        }
	}

	//this method spawns new asteroids
	private void spawnAsteroids() {
        //constrain the num of asteroid by level
        if (nTick % 50 == 0 && CommandCenter.getMovFoes().size() <  CommandCenter.getLevel() * 3) {
            //Asteroids with size of zero are big
            CommandCenter.movFoes.add(new Asteroid(0));
        }

	}
	
	
	private boolean isLevelClear(){

        //level up every 10000 points
        return (CommandCenter.getScore() > (CommandCenter.getLevel() * 10000));
		
	}
	
	private void checkNewLevel(){
		
		if (isLevelClear() ){
			if (CommandCenter.getFalcon() !=null)
				CommandCenter.getFalcon().setProtected(true);
			CommandCenter.setLevel(CommandCenter.getLevel() + 1);

		}
	}
	
	
	

	// Varargs for stopping looping-music-clips
	private static void stopLoopingSounds(Clip... clpClips) {
		for (Clip clp : clpClips) {
			clp.stop();
		}
	}

	// ===============================================
	// KEYLISTENER METHODS
	// ===============================================

	@Override
	public void keyPressed(KeyEvent e) {
        Falcon fal = CommandCenter.getFalcon();
		int nKey = e.getKeyCode();

		if (nKey == START && !CommandCenter.isPlaying())
			startGame();

		if (fal != null) {

			switch (nKey) {

			case PAUSE:
				CommandCenter.setPaused(!CommandCenter.isPaused());
				if (CommandCenter.isPaused())
					stopLoopingSounds(clpMusicBackground, clpThrust);
				else
					clpMusicBackground.loop(Clip.LOOP_CONTINUOUSLY);
				break;
			case QUIT:
				System.exit(0);
				break;
			case MOVE_UP:
				fal.thrustOn();
                fal.addMoveOri(fal.UP);
				if (!CommandCenter.isPaused())
					clpThrust.loop(Clip.LOOP_CONTINUOUSLY);
				break;
            case MOVE_DOWN:
                fal.thrustOn();
                fal.addMoveOri(fal.DOWN);
                if (!CommandCenter.isPaused())
                    clpThrust.loop(Clip.LOOP_CONTINUOUSLY);
                break;
			case MOVE_LEFT:
                fal.thrustOn();
                fal.addMoveOri(fal.LEFT);
                if (!CommandCenter.isPaused())
                    clpThrust.loop(Clip.LOOP_CONTINUOUSLY);
				break;
			case MOVE_RIGHT:
                fal.thrustOn();
                fal.addMoveOri(fal.RIGHT);
                if (!CommandCenter.isPaused())
                    clpThrust.loop(Clip.LOOP_CONTINUOUSLY);
                break;
            case FIRE_UP:
                fal.addFireOri(fal.fireUp);
                fal.fire();
                Sound.playSound("laser.wav");
                break;
            case FIRE_DOWN:
                fal.addFireOri(fal.fireDown);
                fal.fire();
                Sound.playSound("laser.wav");
                break;
            case FIRE_LEFT:
                fal.addFireOri(fal.fireLeft);
                fal.fire();
                Sound.playSound("laser.wav");
                break;
            case FIRE_RIGHT:
                fal.addFireOri(fal.fireRight);
                fal.fire();
                Sound.playSound("laser.wav");
                break;
			// possible future use
			// case KILL:
			// case SHIELD:
			// case NUM_ENTER:

			default:
				break;
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		Falcon fal = CommandCenter.getFalcon();
		int nKey = e.getKeyCode();
		// System.out.println(nKey);

		if (fal != null) {
			switch (nKey) {
            case FIRE_UP:
                fal.removeFireOri(fal.fireUp);
                break;
            case FIRE_DOWN:
                fal.removeFireOri(fal.fireDown);
                break;
            case FIRE_LEFT:
                fal.removeFireOri(fal.fireLeft);
                break;
            case FIRE_RIGHT:
                fal.removeFireOri(fal.fireRight);
                break;
			//special is a special weapon, current it just fires the cruise missile. 
//			case SPECIAL:
//				CommandCenter.movFriends.add(new Cruise(fal));
//				Sound.playSound("laser.wav");
//				break;
				
			case MOVE_LEFT:
				//fal.stopRotating();
                clpThrust.stop();
                fal.removeMoveOri(fal.LEFT);
				break;
			case MOVE_RIGHT:
				//fal.stopRotating();
                clpThrust.stop();
                fal.removeMoveOri(fal.RIGHT);
				break;
			case MOVE_UP:
				clpThrust.stop();
                fal.removeMoveOri(fal.UP);
				break;
            case MOVE_DOWN:
                //fal.thrustOff();
                clpThrust.stop();
                fal.removeMoveOri(fal.DOWN);
                break;

			case MUTE:
				if (!bMuted){
					stopLoopingSounds(clpMusicBackground);
					bMuted = !bMuted;
				} 
				else {
					clpMusicBackground.loop(Clip.LOOP_CONTINUOUSLY);
					bMuted = !bMuted;
				}
				break;
				
				
			default:
				break;
			}
		}
	}

	@Override
	// Just need it b/c of KeyListener implementation
	public void keyTyped(KeyEvent e) {
	}
	

	
}

// ===============================================
// ==A tuple takes a reference to an ArrayList and a reference to a Movable
//This class is used in the collision detection method, to avoid mutating the array list while we are iterating
// it has two public methods that either remove or add the movable from the appropriate ArrayList 
// ===============================================

class Tuple{
	//this can be any one of several CopyOnWriteArrayList<Movable>
	private CopyOnWriteArrayList<Movable> movMovs;
	//this is the target movable object to remove
	private Movable movTarget;
	
	public Tuple(CopyOnWriteArrayList<Movable> movMovs, Movable movTarget) {
		this.movMovs = movMovs;
		this.movTarget = movTarget;
	}
	
	public void removeMovable(){
		movMovs.remove(movTarget);
	}
	
	public void addMovable(){
		movMovs.add(movTarget);
	}

}
