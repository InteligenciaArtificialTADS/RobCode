package br.edu.ifpe.tads;

import java.awt.geom.Point2D;
import java.text.NumberFormat;
import java.util.HashMap;

import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.Robot;
import robocode.ScannedRobotEvent;

public class SheyllaRobot extends Robot {

	// variaveis robot
	private byte direcao_radar = 1;
	private byte direcao_movimento = 1;
	private byte direcao_arma = 1;

	// constantes do jogo
	static final int PERTO_PAREDE = 50;
	static final double PI = Math.PI;

	// variaveis do inimigo
	private double inimigo_bearing;
	private double inimigo_distancia;
	private double inimigo_angulo;
	private boolean controle_escaneamento;

	public void run() {

		setDebugProperty("AHEAD", "");
		setDebugProperty("TURN_BODY", "");
		setDebugProperty("TURN_GUN", "");
		setDebugProperty("TURN_RADAR", "");

		setAdjustRadarForRobotTurn(true);
		setAdjustGunForRobotTurn(true);
		// setAdjustRadarForGunTurn(true);

		turnRadarRight(360);

		setDebugProperty("TURN_RADAR", Double.toString(360));
		while (true) {

			fazerEscaneamento();
			fazerMovimento();
			aprontarAtirar();
			// execute();
		}
	}

	double absDeg;
	String inimigo_name;

	NumberFormat f = NumberFormat.getNumberInstance();

	private String posicao;

	@Override
	public void onScannedRobot(ScannedRobotEvent event) {

		f.setMaximumFractionDigits(0);
		controle_escaneamento = true;
		inimigo_bearing = event.getBearing();
		inimigo_distancia = event.getDistance();
		inimigo_name = event.getName();

		double absBearingDeg = (getHeading() + inimigo_bearing);
		if (absBearingDeg < 0)
			absBearingDeg += 360;

		x = getX() + Math.sin(Math.toRadians(absBearingDeg))
				* inimigo_distancia;

		y = getY() + Math.cos(Math.toRadians(absBearingDeg))
				* inimigo_distancia;


		// scanDirection += -1;
		// setTurnGunRight(360 * scanDirection);
		// scan();
	}

	@Override
	public void onHitRobot(HitRobotEvent event) {
		// TODO Auto-generated method stub
		if (getVelocity() == 0) {
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

		if (controle_escaneamento) {
			double angulo = getHeading() - getRadarHeading() + inimigo_bearing;
			angulo += 30 * direcao_radar;

			setDebugProperty("TURN_RADAR", f.format(normalizeBearing(angulo)));
			turnRadarRight(normalizeBearing(angulo));
			direcao_radar *= -1;
			controle_escaneamento = false;
		} else {
			turnRadarRight(360);
			setDebugProperty("TURN_RADAR", f.format((360)));

			controle_escaneamento = true;

		}
	}

	public void fazerMovimento() {

		turnRight(normalizeBearing(inimigo_bearing + 90
				- (15 * direcao_movimento)));
		setDebugProperty(
				"TURN_BODY",
				f.format(normalizeBearing(inimigo_bearing + 90
						- (15 * direcao_movimento))));

		if (getTime() % 20 == 0) {
			direcao_movimento *= -1;
			ahead(150 * direcao_movimento);
			setDebugProperty("AHEAD", f.format(150 * direcao_movimento));
		}
	}

	private double x;
	private double y;

	public void aprontarAtirar() {
		double energia_tiro = Math.min(400 / inimigo_distancia, 3);
		double velocidade_bala = 20 - energia_tiro * 3;

		long tempo = (long) (inimigo_distancia / velocidade_bala);
		// absDeg = absoluteBearing(getX(), getY(), x +
		// Math.sin(Math.toRadians(getHeading())) * getVelocity()* tempo, y +
		// Math.cos(Math.toRadians(getHeading())) * getVelocity()* tempo);

		// turnGunRight(normalizeBearing(absDeg - getGunHeading()));

		double absBearingDeg = (getHeading() + inimigo_bearing );
		if (absBearingDeg < 0)
			absBearingDeg += 360;

		turnGunRight(normalizeBearing(absDeg - getGunHeading()));

		if (getGunHeat() == 0) {
			fire(Math.min(150 / inimigo_distancia, 3));
		}

	}

	double absoluteBearing(double x1, double y1, double x2, double y2) {
		double xo = x2 - x1;
		double yo = y2 - y1;
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
		while (angle > 180)
			angle -= 360;
		while (angle < -180)
			angle += 360;
		return angle;
	}

	class Inimigo {
		private double bearing;
		private double distancia;
		private double energia;
		private double heading;
		private String nome;
		private double velocidade;

		public double getBearing() {
			return bearing;
		}

		public void setBearing(double bearing) {
			this.bearing = bearing;
		}

		public double getDistancia() {
			return distancia;
		}

		public void setDistancia(double distancia) {
			this.distancia = distancia;
		}

		public double getEnergia() {
			return energia;
		}

		public void setEnergia(double energia) {
			this.energia = energia;
		}

		public double getHeading() {
			return heading;
		}

		public void setHeading(double heading) {
			this.heading = heading;
		}

		public String getNome() {
			return nome;
		}

		public void setNome(String nome) {
			this.nome = nome;
		}

		public double getVelocidade() {
			return velocidade;
		}

		public void setVelocidade(double velocidade) {
			this.velocidade = velocidade;
		}

	}
}
