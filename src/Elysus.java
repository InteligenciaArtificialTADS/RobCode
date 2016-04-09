

import java.awt.geom.Point2D;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import advancedrobots.AdvancedEnemyBot;
import advancedrobots.EnemyBot;
import robocode.AdvancedRobot;
import robocode.Condition;
import robocode.CustomEvent;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.Robot;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

public class Elysus extends AdvancedRobot {
	private AdvancedEnemyBot enemy = new AdvancedEnemyBot();

	//variaveis robot
	private byte direcao_radar = 1;
	private byte direcao_movimento = 1;
	private byte direcao_arma = 1;

	//constante do jogo
	static final int PERTO_PAREDE = 50;
	static final double PI = Math.PI;
	NumberFormat f = NumberFormat.getNumberInstance();
	


	//variaveis do inimigo
	private double inimigo_bearing;
	private double inimigo_distancia;
	private double inimigo_angulo;
	private boolean controle_escaneamento;
	public void onRobotDeath(RobotDeathEvent e) {
		// see if the robot we were tracking died
		if (e.getName().equals(enemy.getName())) {
			enemy.reset();
		}
	}   
	

	public void run() {
		f.setMaximumFractionDigits(0);

		
		setDebugProperty("AHEAD", "");
		setDebugProperty("TURN_BODY", "");
		setDebugProperty("TURN_GUN", "");
		setDebugProperty("TURN_RADAR", "");
		
		
		setAdjustRadarForRobotTurn(true);
		setAdjustGunForRobotTurn(true);
	

		setTurnRadarRight(360);
	

		setDebugProperty("TURN_RADAR", Double.toString(360));
		while (true) {

			fazerEscaneamento();
			fazerMovimento();
			aprontarAtirar();
			execute();
		}
	}
	double absDeg ;
	@Override
	public void onScannedRobot(ScannedRobotEvent e) {

//		controle_escaneamento = true;
//		inimigo_bearing = event.getBearing();
//		inimigo_distancia = event.getDistance();
//		
//		
//		double absBearingDeg = (getHeading() + inimigo_bearing);
//		if (absBearingDeg < 0) absBearingDeg += 360;
//
//		x = getX() + Math.sin(Math.toRadians(absBearingDeg)) * inimigo_distancia;
//
//		y = getY() + Math.cos(Math.toRadians(absBearingDeg)) * inimigo_distancia;
		if ( enemy.none() || e.getDistance() < enemy.getDistance() - 70 ||
				e.getName().equals(enemy.getName())) {

			// track him using the NEW update method
			enemy.update(e, this);
		}
	
		
	}



	@Override
	public void onHitRobot(HitRobotEvent event) {
		// TODO Auto-generated method stub
		if (getVelocity() == 0){
			direcao_movimento *= -1;
		}
	}

	@Override
	public void onHitWall(HitWallEvent event) {
		// TODO Auto-generated method stub
		if (getVelocity() == 0)
			direcao_movimento *= -1;
	}

	public void fazerEscaneamento() {

		if (enemy.none()) {
			// look around if we have no enemy
			setTurnRadarRight(360);
		} else {
			// oscillate the radar
			double turn = getHeading() - getRadarHeading() + enemy.getBearing();
			turn += 30 * direcao_radar;
			setTurnRadarRight(normalizeBearing(turn));
			direcao_radar *= -1;
		}
		
		
	}

	public void fazerMovimento() {

		setTurnRight(normalizeBearing(enemy.getBearing() + 90 - (15 * direcao_movimento)));

		// strafe toward him
		if (getTime() % 20 == 0) {
			direcao_movimento *= -1;
			setAhead(150 * direcao_movimento);
		}
	}

	public void aprontarAtirar() {
		if (enemy.none())
			return;

		double firePower = Math.min(400 / enemy.getDistance(), 3);
		double bulletSpeed = 20 - firePower * 3;
		long time = (long)(enemy.getDistance() / bulletSpeed);
		double futureX = enemy.getFutureX(time);
		double futureY = enemy.getFutureY(time);
		double absDeg = absoluteBearing(getX(), getY(), futureX, futureY);
		setTurnGunRight(normalizeBearing(absDeg - getGunHeading()));

		if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 10) {
			setFire(firePower);
		}

	}
	
	
	
	double absoluteBearing(double x1, double y1, double x2, double y2) {
		double xo = x2-x1;
		double yo = y2-y1;
		double hyp = Point2D.distance(x1, y1, x2, y2);
		double arcSin = Math.toDegrees(Math.asin(xo / hyp));
		double bearing = 0;
		if (xo > 0 && yo > 0) { 
			bearing = arcSin;
		} else if (xo < 0 && yo > 0) { 
			bearing = 360 + arcSin; 
		} else if (xo > 0 && yo < 0) { 
			bearing = 180 - arcSin;
		} else if (xo < 0 && yo < 0) { 
			bearing = 180 - arcSin; 
		}

		return bearing;
	}

	double normalizeBearing(double angle) {
		while (angle >  180) angle -= 360;
		while (angle < -180) angle += 360;
		return angle;
	}
	
	class AdvancedEnemyBot extends EnemyBot {

		// constructor
		public AdvancedEnemyBot() {
			reset();
		}

		public void reset() {
			// tell parent to reset all his stuff
			super.reset();
			// now update our stuff
			x = 0.0;
			y = 0.0;
		}

		public void update(ScannedRobotEvent e, Robot robot) {
			// tell parent to update his stuff
			super.update(e);

			// now update our stuff

			// (convenience variable)
			double absBearingDeg = (robot.getHeading() + e.getBearing());
			if (absBearingDeg < 0) absBearingDeg += 360;

			// yes, you use the _sine_ to get the X value because 0 deg is North
			x = robot.getX() + Math.sin(Math.toRadians(absBearingDeg)) * e.getDistance();

			// likewise, you use the _cosine_ to get the Y value for the same reason
			y = robot.getY() + Math.cos(Math.toRadians(absBearingDeg)) * e.getDistance();
		}

		// accessor methods
		public double getX() { return x; }
		public double getY() { return y; }

		public double getFutureX(long when) {
			/*
			double sin = Math.sin(Math.toRadians(getHeading()));
			double futureX = x + sin * getVelocity() * when;
			return futureX;
			*/
			return x + Math.sin(Math.toRadians(getHeading())) * getVelocity() * when;
		}

		public double getFutureY(long when) {
			/*
			double cos = Math.cos(Math.toRadians(getHeading()));
			double futureY = y + cos * getVelocity() * when;
			return futureY;
			*/
			return y + Math.cos(Math.toRadians(getHeading())) * getVelocity() * when;
		}

		private double x;
		private double y;
	}
}
