Asteroid
========

A Java video game, colorful asteroid game

New Features
------------
There are four kind of floaters:
* Red: Bomb when falcon collides with it, all asteroid will be killed
* Yellow: get fire power up(expires in 200 ticks). Fire changes from Bullet to Cruise and Cruise will reflect will it
touches the edges of the screen
* Blue: add a life
* Green: get a shield(expires in 100 ticks).

* Two situation for generating a floater:
 - When a big asteroid is killed, there is 50% chance to get a new floater
 -  After some interval, a new floater is put in randomly

Score and level
* There will show current score and level on the left-top corner.
* Level-up rule is changed:
    Level will add one every time user earns 10000 points.

Explosion and dust
* Add explosion effect(kind like firework) when an asteroid is killed. 
* Dust will be generated with explosion which is not harmful to the falcon and will shrink with time goes by(expires in 100 ticks)

Asteroid generating rule changed
* Asteroid will keep adding until the sum reaches upper bound which is current level * 5

New Control
* use "WASD" keys to move and diagonal moves are supported
* use "→←↑↓" keys to fire and diagonal firing are supported
