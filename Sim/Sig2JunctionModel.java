import java.util.Vector;
import java.util.Iterator;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Color;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Line2D;

import time.*;

public class Sig2JunctionModel extends Sig1JunctionModel implements Timed {

	boolean changeset = false;

	public Sig2JunctionModel(int iD, Shape s, int lighttime,
													 boolean actuated,RoadNetwork parent) {
		super(iD,s,lighttime,actuated,parent);
	}

	public void generatePaths() {

		//Get the maximum possible number of paths
		maxNum = Math.max(ei[0]*(si[1]+si[2]+si[3]),ei[1]*(si[0]+si[2]+si[3]));
		maxNum = Math.max(maxNum,ei[2]*(si[0]+si[1]+si[3]));
		maxNum = Math.max(maxNum,ei[3]*(si[0]+si[1]+si[2]));
	
		paths = new JunctionPath[maxNumRoads][maxNum];

		//Generate a sensible default junction path set.
		for (int i=0;i<maxNumRoads;i++) {
			for (int i2=0;i2<ei[i];i2++) { //all the lane ends of all sides.

				//i is road i.e. N,E,S,W
				//i2 is input lanes.

				if (createPathToOppositeSide(i,i2)) {
					if (i2+1 == ei[i]) createLeftTurns(i,i2);
				} else {
					if (createcorrespondingLeft(i,i2)) {
					} else if (createAnyLeft(i,i2)) {
					} else if (createAnyOpposite(i,i2)) {
					} else if (createcorrespondingRight(i,i2)) {
					} else if (createAnyRight(i,i2)) {
					} else System.out.println("Path option unaccounted for");
				}
				parent.getLane(endLanesID[i][i2]).endJunctionSide = i;
				parent.getLane(endLanesID[i][i2]).endJunctionIndex = i2;
			}
		}

		System.out.println("BBBgenerated junction paths, number= "+totalNumPaths);
		trafficLightArea = new Rectangle2D[maxNumRoads][maxNum];
		lightColor = new Color[maxNumRoads][maxNum];
		startColors = new Color[maxNumRoads][maxNum];
		//lightTimer = (int)(Math.random()*(lightTime-1));
		lightTimer = 0;

		//Place Traffic lights at junction inputs
		for (int i=0;i<maxNumRoads;i++) {
	    for (int j=0;j<ei[i];j++) { //all the lane ends of all sides.
				trafficLightArea[i][j] = new Rectangle2D.Double(
													parent.getLane(endLanesID[i][j]).getEndingXCoord()-3,
		 											parent.getLane(endLanesID[i][j]).getEndingYCoord()-3,
																												6,6);

				if (i%2 == 0) lightColor[i][j] = Color.red;
				else {
					if (lightTimer >= lightTime-ORANGETIME) {
						lightColor[i][j] = Color.orange;
					} else {
						lightColor[i][j] = Color.green;
					}
					startColors[i][j] = lightColor[i][j];
				}
			}
		}
		testForLiveness();
	}

	public void pretick() {
		if (actuated) pretickactuated();
		else preticknonactuated();
	}

	public void preticknonactuated() {

		//turn red lights orange.
		if (lightTimer == lightTime-ORANGETIME) {
	    for (int i=0;i<4;i++) {
				for (int j=0;j<ei[i];j++) {
					if (lightColor[i][j] == Color.green) 
						lightColor[i][j] = Color.orange;
				}
	    }
		}

		if (lightTimer >= lightTime) {
			//change lights.
	    for (int i=0;i<4;i++) {
				for (int j=0;j<ei[i];j++) {
					if (lightColor[i][j] == Color.orange) 
						lightColor[i][j] = Color.red;
					else lightColor[i][j] = Color.green;
				}
	    }
	    lightTimer = 0;
		} else lightTimer++;
	}

	public void pretickactuated() {
	 
		if (lightTimer == lightTime-ORANGETIME) {
	    //calculate whether we need to change lights.
	    //i.e is there any cars waiting.
	    for (int i=0;i<4;i++) {
				if (changeset) break;
				for (int j=0;j<ei[i];j++) {
					if (lightColor[i][j] != Color.green &&
							parent.getLane(endLanesID[i][j]).getEndLaneSection().hasCars()) {
						changeset = true;
						break;
					}
				}
			}
			//turn red lights orange only if going to change sets.
	    if (changeset) {
				for (int i=0;i<4;i++) {
					for (int j=0;j<ei[i];j++) {
						if (lightColor[i][j] == Color.green) 
							lightColor[i][j] = Color.orange;
					}
				}
	    }
		}    
   
		if (lightTimer >= lightTime) {
	    //Swap the sets around if need to.
	    if (changeset) {
				for (int i=0;i<4;i++) {
					for (int j=0;j<ei[i];j++) {
						if (lightColor[i][j] == Color.orange) 
							lightColor[i][j] = Color.red;
						else lightColor[i][j] = Color.green;
					}
				}
	    }
	    changeset = false;
	    lightTimer = 0;
		} else lightTimer++;
	}

	public void tick() {}

	public void resetLights(int greenSide, int timerTime) {
		lightTimer = timerTime;
		int oddOrEven = greenSide%2;

		//Place Traffic lights at junction inputs
		for (int i=0;i<maxNumRoads;i++) {
	    for (int j=0;j<ei[i];j++) { //all the lane ends of all sides.
				
				if (i%2 == oddOrEven) lightColor[i][j] = Color.red;
				else {
					if (lightTimer >= lightTime-ORANGETIME) {
						lightColor[i][j] = Color.orange;
					} else {
						lightColor[i][j] = Color.green;
					}
					startColors[i][j] = lightColor[i][j];
				}
			}
		}
	}
	
	/**************** Due to mouse Events *******************************/

	public String giveInfo() { 
		if (actuated) {
	    return "Vehicle Actuated Signalled junction (alternating-sets).";
		} else {
	    return "Signalled junction (alternating-sets).";
		}
	}
}


















