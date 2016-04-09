

import java.awt.geom.Point2D;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import robocode.AdvancedRobot;
import robocode.Condition;
import robocode.CustomEvent;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

public class ElysusBeta extends AdvancedRobot {
	
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
	
	HashMap<String, Map<String, Double>> historicoPercepcaoPosicaoInimigo = new HashMap<String, Map<String,Double>>();

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
	public void onScannedRobot(ScannedRobotEvent event) {

		controle_escaneamento = true;
		inimigo_bearing = event.getBearing();
		inimigo_distancia = event.getDistance();
		
		
		double absBearingDeg = (getHeading() + inimigo_bearing);
		if (absBearingDeg < 0) absBearingDeg += 360;

		x = getX() + Math.sin(Math.toRadians(absBearingDeg)) * inimigo_distancia;

		y = getY() + Math.cos(Math.toRadians(absBearingDeg)) * inimigo_distancia;


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

		if (controle_escaneamento) {
			double angulo = getHeading() - getRadarHeading() + inimigo_bearing;
			angulo += 30 * direcao_radar;

			setDebugProperty("TURN_RADAR", Double.toString(normalizeBearing(angulo)));
			setTurnRadarRight(normalizeBearing(angulo));
			direcao_radar *= -1;
			controle_escaneamento = false;
		} else {
			turnRadarRight(360);
			setDebugProperty("TURN_RADAR", Double.toString(360));

			controle_escaneamento = true;

		}
	}

	public void fazerMovimento() {

		setTurnRight(normalizeBearing(inimigo_bearing + 90
				- (15 * direcao_movimento)));
		setDebugProperty("TURN_BODY", Double.toString(normalizeBearing(inimigo_bearing + 90
				- (15 * direcao_movimento))));


		if (getTime() % 20 == 0) {
			direcao_movimento *= -1;
			setAhead(150 * direcao_movimento);setDebugProperty("AHEAD", Double.toString(150 * direcao_movimento));
		}
	}
	private double x;
	private double y;
	public void aprontarAtirar() {
		
		if(controle_escaneamento == false){
			return;
		}
		double energia_tiro = Math.min(400 / inimigo_distancia, 3);
		 double velocidade_bala = 20 - energia_tiro * 3;

		
		 long tempo = (long)(inimigo_distancia / velocidade_bala);
		 absDeg = absoluteBearing(getX(), getY(), x   + Math.sin(Math.toRadians(getHeading())) * getVelocity()* tempo, y + Math.cos(Math.toRadians(getHeading())) * getVelocity()* tempo);
		
		 setDebugProperty("NORMALIZED_FUNCTION_absoluteBearing",f.format(absDeg - getGunHeading()));
		 double temp = Utils.normalAbsoluteAngleDegrees((inimigo_bearing + getHeading() - getGunHeading()));
		 

		
		 setDebugProperty("NORMALIZED_UTIL_normalAbsoluteAngleDegrees1",f.format(temp ));

		 setDebugProperty("NORMALIZED_FUNCTION_normalizeBearing",f.format(normalizeBearing(absDeg - getGunHeading())));
		 setDebugProperty("NORMALIZED_UTIL_normalRelativeAngleDegrees",f.format(Utils.normalRelativeAngleDegrees(absDeg - getGunHeading()) ));

		 setTurnGunRight(normalizeBearing(absDeg - getGunHeading()));
		 setDebugProperty("TURN_GUN", Double.toString(normalizeBearing(absDeg - getGunHeading())));

		if (getGunHeat() == 0 && getTurnRemaining() < 10) {
			fire(Math.min(150 / inimigo_distancia, 3));
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
	
	class Inimigo{
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
