package br.edu.ifpe.tads;

import static robocode.util.Utils.normalRelativeAngleDegrees;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

import robocode.*;
import robots.Corners;
//import java.awt.Color;

// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html

/**
 * @author rbsa Elysus2 - a robot by (rbsa)
 */
public class Elysus2 extends Robot {
	/**
	 * run: Elysus2's default behavior
	 */
	public static double metadeVerticial = 0;
	public static double metadeHorizontal = 0;
	public static boolean adjustGunForRobotTurn = false;
	public static boolean adjustRadarForGunTurn = false;
	public static boolean adjustRadarForRobotTurn = false;
	

	
	double pontoMedioX;
	double pontoMedioY;
	
	private byte direcao_radar = 1;
	private byte direcao_movimento = 1;
	private byte direcao_arma = 1;


	double energyEnemy = 0;
	double distanceEnemy = 0;

	double ahead_back = 0;
	double turn_gun = 0;
	double turn_body = 0;
	double turn_radar = 0;
	String quadrante = "";
	final double PI = Math.PI;
	double enemy_energ = 0;
	double enemy_bearing = 0;
	double enemy_distance = 0;
	double enemy_heading = 0;
	String enemy_name = "";
	double enemy_bullet_x = 0;
	double enemy_bullet_y = 0;
	String last_method_call = "";

	private byte moveDirection = 1;

	int scannedX = Integer.MIN_VALUE;
	int scannedY = Integer.MIN_VALUE;
	
	

	public void run() {

		
		setDebugProperty("AHEAD_BACK", Double.toString(ahead_back));
		setDebugProperty("TURN_BODY", Double.toString(turn_body));
		setDebugProperty("TURN_GUN", Double.toString(turn_gun));
		setDebugProperty("TURN_RADAR", Double.toString(turn_radar));
		setDebugProperty("QUADRANTE", quadrante);

		setDebugProperty("ENEMY_NAME", enemy_name);
		setDebugProperty("ENEMY_HEADING", Double.toString(enemy_heading));
		setDebugProperty("ENEMY_BEARING", Double.toString(enemy_bearing));
		setDebugProperty("ENEMY_DISTANCE", Double.toString(enemy_distance));
		setDebugProperty("ENEMY_ENERGY", Double.toString(enemy_energ));
		setDebugProperty("ENEMY_DISTANCE", Double.toString(enemy_distance));
		setDebugProperty("ENEMY_ENERGY", Double.toString(enemy_energ));

		// modifica cor do robot
		setBodyColor(Color.black);
		setGunColor(Color.orange);
		setRadarColor(Color.black);
		setScanColor(Color.magenta);
		setBulletColor(Color.yellow);

		int cont = 0;
		metadeVerticial = getBattleFieldHeight() / 2;
		metadeHorizontal = getBattleFieldWidth() / 2;

		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(false);
		setAdjustRadarForRobotTurn(true);

		ahead(500);

		setDebugProperty("AHEAD_BACK", Integer.toString(500));

		turnGunRight(Double.POSITIVE_INFINITY);
		turnRadarRight(Double.POSITIVE_INFINITY);


	

		double gira;
		int count = 0;
		boolean test = true;
		while (true) {

			setDebugProperty("METHOD", last_method_call);

			gira = verificaQudrante();

			turnRadarRight(10000);

			setDebugProperty("AHEAD_BACK", Integer.toString(200));

			setDebugProperty("TURN_RADAR", Integer.toString(360));
//			if (cont == 3600) {
//				cont = 0;
//
//				if (test) {
//					test = false;
//				} else {
//					test = true;
//				}
//
//			}
//
//			if (test) {
//
//				turnRight(gira);
//				turnRadarRight(180);
//				setDebugProperty("TURN_BODY", Double.toString(gira));
//			} else {
//
//				turnRadarRight(-180);
//				turnRight(-gira);
//				setDebugProperty("TURN_BODY", Double.toString(-gira));
//
//			}
//			count++;
		}

	}

	private double verificaQudrante() {

		double gira;

		if (getX() < metadeHorizontal && getY() < metadeVerticial) {

			gira = giraProprioEixoQuadrante(1, getHeading());

		} else if (getX() < metadeHorizontal && getY() > metadeVerticial) {
			gira = giraProprioEixoQuadrante(2, getHeading());
		} else if (getX() > metadeHorizontal && getY() > metadeVerticial) {

			gira = giraProprioEixoQuadrante(3, getHeading());
		} else {

			gira = giraProprioEixoQuadrante(4, getHeading());

		}

		return gira;
	}

	private double giraProprioEixoQuadrante(int i, double heading) {
		// TODO Auto-generated method stub

		last_method_call = "giraProprioEixoQuadrante";
		setDebugProperty("METHOD", last_method_call);

		double gira = 0;

		switch (i) {
		case 1:
			if (heading <= 90) {
				gira = 90 - heading;
			} else {
				gira = -(heading - 90);
			}
			setDebugProperty("QUADRANTE", "III");
			break;

		case 2:
			if (heading <= 180) {
				gira = 180 - heading;
			} else {
				gira = -(heading - 180);
			}
			setDebugProperty("QUADRANTE", "II");
			break;

		case 3:
			if (heading <= 270) {
				gira = 270 - heading;
			} else {
				gira = -(heading - 270);
			}
			setDebugProperty("QUADRANTE", "I");
			break;

		case 4:

			gira = -heading;
			setDebugProperty("QUADRANTE", "IV");
			break;
		}
		setDebugProperty("TURN_BODY", Double.toString(gira));
		return gira;
	}

	/**
	 * onScannedRobot: What to do when you see another robot
j	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		// Replace the next line with any behavior you would like
		out.println("========== // ==========");
		out.println("EVENT: " + "onScannedRobot - entering");

		last_method_call = "onScannedRobot";
		setDebugProperty("METHOD", last_method_call);

		// debbunging
		setDebugProperty("ENEMY_NAME", (e.getName()));
		setDebugProperty("ENEMY_HEADING", Double.toString(e.getHeading()));
		setDebugProperty("ENEMY_BEARING", Double.toString(e.getBearing()));
		setDebugProperty("ENEMY_DISTANCE", Double.toString(e.getDistance()));
		setDebugProperty("ENEMY_ENERGY", Double.toString(e.getEnergy()));

		double angle = Math.toRadians((getHeading() + e.getBearing()) % 360);

		scannedX = (int) (getX() + Math.sin(angle) * e.getDistance());
		scannedY = (int) (getY() + Math.cos(angle) * e.getDistance());

		// double angulo = e.getBearing() + getHeading() - getRadarHeading();
		// double angulo = normalRelativeAngleDegrees((e.getBearing() +
		// getHeading())
		// - getGunHeading());
		double angulo = normalizeBearing((e.getBearing() + getHeading())
				- getGunHeading());

		setDebugProperty("ANGLE", Double.toString(angulo));
		turnGunRight(angulo);
		setDebugProperty("TURN_GUN", Double.toString(angulo));

		// turnRadarRight(angulo);
		// setDebugProperty("TURN_RADAR", Double.toString(angulo));

		energyEnemy = e.getEnergy();
		distanceEnemy = e.getDistance();

		// smartFire();
		if (getGunHeat() == 0) {
			fire(Math.min(400 / e.getDistance(), 3));
		}

		scan();
		out.println("EVENT: " + "onScannedRobot - exiting");

	}

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		out.println("========== // ==========");
		out.println("EVENT: " + "onHitByBullet");

		last_method_call = "onHitByBullet - enter";
		setDebugProperty("METHOD", last_method_call);

		setDebugProperty("ENEMY_NAME", (e.getName()));
		setDebugProperty("ENEMY_HEADING", Double.toString(e.getHeading()));
		setDebugProperty("ENEMY_BEARING", Double.toString(e.getBearing()));
		setDebugProperty("ENEMY_BULLET_X",
				Double.toString(e.getBullet().getX()));
		setDebugProperty("ENEMY_BULLET_Y",
				Double.toString(e.getBullet().getY()));

		double angulo = normalRelativeAngleDegrees((e.getBearing() + getHeading())
				- getGunHeading());
		setDebugProperty("ANGLE", Double.toString(angulo));
		turnRight(angulo);
		setDebugProperty("TURN_BODY", Double.toString(angulo));
		turnGunRight(angulo);
		setDebugProperty("TURN_GUN", Double.toString(angulo));
		turnRadarRight(angulo);
		setDebugProperty("TURN_RADAR", Double.toString(angulo));

		if (e.getBearing() > -90 && e.getBearing() < 90) {

			distanceEnemy = Point2D.distance(getX(), getY(), e.getBullet()
					.getX(), e.getBullet().getY());
			smartFire();

		}

		direcao_movimento *= -1;


		last_method_call = "onHitByBullet - exit";
		setDebugProperty("METHOD", last_method_call);

	}

	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) {
		// Replace the next line with any behavior you would like
		// e
		out.println("========== // ==========");
		out.println("EVENT: " + "onHitWall");
		last_method_call = "onHitWall - enter";
		setDebugProperty("METHOD", last_method_call);

		direcao_movimento *= -1;


		last_method_call = "onHitWall - exit";
		setDebugProperty("METHOD", last_method_call);

		// fire(Rules.MAX_BULLET_POWER);

	}


	@Override
	public void onBulletHit(BulletHitEvent event) {
		out.println("========== // ==========");
		out.println("EVENT: " + "onBulletHit");
		last_method_call = "onBulletHit - enter";
		setDebugProperty("METHOD", last_method_call);

		distanceEnemy = Point2D.distance(getX(), getY(), event.getBullet()
				.getX(), event.getBullet().getY());

		medirDistanciaDoisPontos(getX(), getY(), event.getBullet().getX(),
				event.getBullet().getY());

		distanceEnemy = normalizeBearing(getHeading());
		ahead(distanceEnemy * direcao_movimento);

		smartFire();
		last_method_call = "onBulletHit - exit";
		setDebugProperty("METHOD", last_method_call);

	}

	public void onHitRobot(HitRobotEvent e) {
		out.println("========== // ==========");
		out.println("EVENT: " + "onHitRobot");
		last_method_call = "onHitRobot - enter";
		setDebugProperty("METHOD", last_method_call);

		setDebugProperty("ENEMY_NAME", (e.getName()));
		setDebugProperty("ENEMY_ENERGY", Double.toString(e.getEnergy()));
		setDebugProperty("ENEMY_BEARING", Double.toString(e.getBearing()));

		double angulo = normalRelativeAngleDegrees((e.getBearing() + getHeading())
				- getGunHeading());
		setDebugProperty("ANGLE", Double.toString(angulo));

		turnRadarRight(angulo);
		setDebugProperty("TURN_RADAR", Double.toString(angulo));
		turnGunRight(angulo);
		setDebugProperty("TURN_GUN", Double.toString(angulo));

		ahead(100 * direcao_movimento);

		last_method_call = "onBulletHit - exit";
		setDebugProperty("METHOD", last_method_call);

	}

	public void onWin(WinEvent e) {
		rizadinha();
	}

	public void rizadinha() {
		for (int i = 0; i < 50; i++) {
			turnRight(30);
			turnLeft(30);
		}
	}

	// public void onPaint(Graphics2D g) {
	// g.setColor(Color.red);
	// g.drawOval((int) (getX() - 50), (int) (getY() - 50), 100, 100);
	// g.setColor(new Color(0, 0xFF, 0, 30));
	// g.fillOval((int) (getX() - 60), (int) (getY() - 60), 120, 120);
	// }

	public void onPaint(Graphics2D g) {
		g.setColor(new Color(0xff, 0x00, 0x00, 0x80));
		g.drawLine(scannedX, scannedY, (int) getX(), (int) getY());
		g.fillRect(scannedX - 20, scannedY - 20, 40, 40);
	}

	public void smartFire() {

		double power = 0;
		double power_choice = 0;

		if (energyEnemy > 16) {
			power = 3;
		} else if (energyEnemy > 10) {
			power = 2;
		} else if (energyEnemy > 4) {
			power = 1;
		} else if (energyEnemy > 2) {
			power = 0.5;
		} else if (energyEnemy > .4) {
			power = 0.1;
		}
		power_choice = power;
		if (distanceEnemy > 300) {
			power = 0.5;
		} else if (distanceEnemy > 200) {
			power = 1.0;
		} else if (distanceEnemy > 150) {
			power = 1.5;
		} else if (distanceEnemy > 100) {
			power = 2.0;
		}

		if (power_choice > power) {
			power_choice = power;
		}

		if (getEnergy() >= 70 && getEnergy() < 90) {
			power = 1.7;
		} else if (getEnergy() >= 50 && getEnergy() < 70) {
			power = 1.5;
		} else if (getEnergy() >= 30 && getEnergy() < 50) {
			power = 1.3;
		} else if (getEnergy() >= 15 && getEnergy() < 30) {
			power = 1.1;
		} else if (getEnergy() < 15) {
			power = 0.5;
		}
		if (power_choice > power) {
			power_choice = power;
		}

		fire(power);

	}

	public void onHitWallCorner() {

		last_method_call = "onHitWallCorner";
		setDebugProperty("METHOD", last_method_call);
		if ((getX() < 10 && getY() > getBattleFieldHeight() - 10)
				|| (getX() < 10 && getY() < 10)
				|| (getX() > getBattleFieldHeight() - 10 && getY() < 10)
				|| (getX() > getBattleFieldHeight() - 10 && getY() > getBattleFieldHeight() - 10)) {
			if ((getHeading() == 180 || getHeading() == 270)
					|| (getHeading() == 0 || getHeading() == 90)
					|| (getHeading() == 180 || getHeading() == 90 || (getHeading() == 0 || getHeading() == 270))) {
				turnRight(180);
				setDebugProperty("TURN_BODY", Integer.toString(180));
				ahead(100 * 			direcao_movimento);
				setDebugProperty("AHEAD_BACK", Integer.toString(100));
				turnRight(90);
				setDebugProperty("TURN BODY", Integer.toString(90));
			}
		}

	}

	public void onHitWallNotCorner() {

		last_method_call = "onHitWallNotCorner";
		setDebugProperty("METHOD", last_method_call);
		if (getX() < 10
				&& (getY() > 10 && getY() < getBattleFieldHeight() - 10)
				|| getX() > getBattleFieldWidth() - 10
				&& (getY() > 10 && getY() < getBattleFieldHeight() - 10)
				|| getY() < 10
				&& (getX() > 10 && getX() < getBattleFieldHeight() - 10)
				|| getY() > getBattleFieldHeight() - 10
				&& (getX() > 10 && getX() < getBattleFieldWidth() - 10)

		) {
			if (getHeading() == 180 || getHeading() == 90)
				turnRight(-135);
			setDebugProperty("TURN_BODY", Integer.toString(-135));
			ahead(100);
			setDebugProperty("AHEAD_BACK", Integer.toString(100));

			if (getHeading() == 0 || getHeading() == 270)
				turnRight(135);
			setDebugProperty("TURN_BODY", Integer.toString(-135));
			ahead(100);
			setDebugProperty("AHEAD_BACK", Integer.toString(100));

			turnRadarRight(-90);
			setDebugProperty("TURN_RADAR", Integer.toString(-90));
			turnGunRight(-90);
			setDebugProperty("TURN_GUN", Integer.toString(-90));
		}
	}

	double normalizeBearing(double angle) {
		while (angle > 180)
			angle -= 360;
		while (angle < -180)
			angle += 360;
		return angle;
	}

	double absoluteBearing(double x1, double y1, double x2, double y2) {
		double xo = x2 - x1;
		double yo = y2 - y1;
		double hyp = Point2D.distance(x1, y1, x2, y2);
		double arcSin = Math.toDegrees(Math.asin(xo / hyp));
		double bearing = 0;

		if (xo > 0 && yo > 0) { // both pos: lower-Left
			bearing = arcSin;
		} else if (xo < 0 && yo > 0) { // x neg, y pos: lower-right
			bearing = 360 + arcSin; // arcsin is negative here, actuall 360 -
									// ang
		} else if (xo > 0 && yo < 0) { // x pos, y neg: upper-left
			bearing = 180 - arcSin;
		} else if (xo < 0 && yo < 0) { // both neg: upper-right
			bearing = 180 - arcSin; // arcsin is negative here, actually 180 +
									// ang
		}

		return bearing;
	}

	public double medirDistanciaDoisPontos(double x1, double y1, double x2,
			double y2) {

		return Math.sqrt(Math.pow(x2 - x1, x2 - x1)
				+ Math.pow(y2 - y1, y2 - y1));
	}

	public void medirPontoDivideSegmentoMeio(double x1, double y1, double x2,
			double y2) {
		pontoMedioX = (x1 + x2) / 2;
		pontoMedioY = (y1 + y2) / 2;
	}

}
