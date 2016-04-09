package advancedrobots;
import robocode.*;
import java.awt.Color;

/**
 * SnippetBot - a robot by Alisdair Owens
 * This bot includes all sorts of useful snippets.  It is not
 * designed to be a good fighter (although it does well 1v1),
 * just to show how certain things are done
 * Bits of code lifted from Nicator and Chrisbot
 * Conventions in this bot include: Use of radians throughout
 * Storing absolute positions of enemy bots rather than relative ones
 * Very little code in events
 * These are all good programming practices for robocode
 * There may also be methods that arent used; these might just be useful for you.
 */
public class SnippetBot extends AdvancedRobot
{
	/**
	 * run: SnippetBot's default behavior
	 */
	Enemy target;					//our current enemy
	final double PI = Math.PI;			//just a constant
	int direction = 1;				//direction we are heading...1 = forward, -1 = backwards
	double firePower;       			//the power of the shot we will be using - set by do firePower()

	public void run() {
		target = new Enemy();
		target.distance = 100000;			//initialise the distance so that we can select a target
		setColors(Color.red,Color.blue,Color.green);	//sets the colours of the robot

		//the next two lines mean that the turns of the robot, gun and radar are independant
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		turnRadarRightRadians(2*PI);			//turns the radar right around to get a view of the field
		while(true) {
			doMovement();				//Move the bot
			doFirePower();				//select the fire power to use
			doScanner();				//Oscillate the scanner over the bot
			doGun();
			out.println(target.distance);		//move the gun to predict where the enemy will be
			fire(firePower);
			execute();				//execute all commands
		}
	}

	void doFirePower() {
		firePower = 400/target.distance;//selects a bullet power based on our distance away from the target
	}

	void doMovement() {
		if (getTime()%20 == 0)  { 		//every twenty 'ticks'
			direction *= -1;		//reverse direction
			setAhead(direction*300);	//move in that direction
		}
		setTurnRightRadians(target.bearing + (PI/2)); //every turn move to circle strafe the enemy
	}
	
	void doScanner() {
		double radarOffset;
		if (getTime() - target.ctime > 4) { 	//if we haven't seen anybody for a bit....
			radarOffset = 360;		//rotate the radar to find a target
		} else {	
			
			//next is the amount we need to rotate the radar by to scan where the target is now
			radarOffset = getRadarHeadingRadians() - absbearing(getX(),getY(),target.x,target.y);
			
			//this adds or subtracts small amounts from the bearing for the radar to produce the wobbling
			//and make sure we don't lose the target
			if (radarOffset < 0)
				radarOffset -= PI/8;
			else
				radarOffset += PI/8; 
		}
		//turn the radar
		setTurnRadarLeftRadians(NormaliseBearing(radarOffset));
	}
	
	void doGun() {
		
		//works out how long it would take a bullet to travel to where the enemy is *now*
		//this is the best estimation we have
		long time = getTime() + (int)(target.distance/(20-(3*firePower)));
		
		//offsets the gun by the angle to the next shot based on linear targeting provided by the enemy class
		double gunOffset = getGunHeadingRadians() - absbearing(getX(),getY(),target.guessX(time),target.guessY(time));
		setTurnGunLeftRadians(NormaliseBearing(gunOffset));
	}
	
	
	//if a bearing is not within the -pi to pi range, alters it to provide the shortest angle
	double NormaliseBearing(double ang) {
		if (ang > PI)
			ang -= 2*PI;
		if (ang < -PI)
			ang += 2*PI;
		return ang;
	}
	
	//if a heading is not within the 0 to 2pi range, alters it to provide the shortest angle
	double NormaliseHeading(double ang) {
		if (ang > 2*PI)
			ang -= 2*PI;
		if (ang < 0)
			ang += 2*PI;
		return ang;
	}
	
	//returns the distance between two x,y coordinates
	public double getrange( double x1,double y1, double x2,double y2 )
	{
		double xo = x2-x1;
		double yo = y2-y1;
		double h = Math.sqrt( xo*xo + yo*yo );
		return h;	
	}
	
	//gets the absolute bearing between to x,y coordinates
	public double absbearing( double x1,double y1, double x2,double y2 )
	{
		double xo = x2-x1;
		double yo = y2-y1;
		double h = getrange( x1,y1, x2,y2 );
		if( xo > 0 && yo > 0 )
		{
			return Math.asin( xo / h );
		}
		if( xo > 0 && yo < 0 )
		{
			return Math.PI - Math.asin( xo / h );
		}
		if( xo < 0 && yo < 0 )
		{
			return Math.PI + Math.asin( -xo / h );
		}
		if( xo < 0 && yo > 0 )
		{
			return 2.0*Math.PI - Math.asin( -xo / h );
		}
		return 0;
	}


	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		//if we have found a closer robot....
		if ((e.getDistance() < target.distance)||(target.name == e.getName())) {
			//the next line gets the absolute bearing to the point where the bot is
			double absbearing_rad = (getHeadingRadians()+e.getBearingRadians())%(2*PI);
			//this section sets all the information about our target
			target.name = e.getName();
			target.x = getX()+Math.sin(absbearing_rad)*e.getDistance(); //works out the x coordinate of where the target is
			target.y = getY()+Math.cos(absbearing_rad)*e.getDistance(); //works out the y coordinate of where the target is
			target.bearing = e.getBearingRadians();
			target.head = e.getHeadingRadians();
			target.ctime = getTime();				//game time at which this scan was produced
			target.speed = e.getVelocity();
			target.distance = e.getDistance();
		}
	}

	public void onRobotDeath(RobotDeathEvent e) {
		if (e.getName() == target.name)
			target.distance = 10000; //this will effectively make it search for a new target
	}

}

class Enemy {
	/*
	 * ok, we should really be using accessors and mutators here,
	 * (i.e getName() and setName()) but life's too short.
	 */
	String name;
	public double bearing;
	public double head;
	public long ctime; //game time that the scan was produced
	public double speed;
	public double x,y;
	public double distance;
	public double guessX(long when)
	{
		long diff = when - ctime;
		return x+Math.sin(head)*speed*diff;
	}
	public double guessY(long when)
	{
		long diff = when - ctime;
		return y+Math.cos(head)*speed*diff;
	}

}
