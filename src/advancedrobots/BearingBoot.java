package advancedrobots;

import robocode.AdvancedRobot;
import robocode.HitRobotEvent;
import robocode.ScannedRobotEvent;

public class BearingBoot extends AdvancedRobot {

	@Override
	public void run() {
		// TODO Auto-generated method stub
		// super.run();
		setAdjustRadarForGunTurn(true);

		while (true) {

			turnRadarRight(10000);

		}

	}
	@Override
	public void onScannedRobot(ScannedRobotEvent event) {
		// TODO Auto-generated method stub
		turnRight(event.getBearing());
		
		fire(3);
		ahead(event.getDistance());
	}
	
	@Override
	public void onHitRobot(HitRobotEvent event) {
		// TODO Auto-generated method stub
		
		if(event.isMyFault()){
			
		}else{
			
		}
	}
}
