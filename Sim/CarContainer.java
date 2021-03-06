import java.util.*;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;

import Utils.*;

public abstract class CarContainer {

	public final static double NOTHING_IN_FRONT = 100000;

	//Lane position variables.
	int startX,startY,endX,endY;
	double dy, dx, gradient, length;
	int generalDirection;
	double radAngle;
	int iD;
	int lightCycle = -1;
	int lightStart = -1;

	Vector cars = new Vector(); //Cars on this lane.
 
	public CarContainer (int iD,int startX,int startY,int endX,int endY) {

		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
		this.iD = iD;
		length = GeoUtils.getDistance(startX,startY,endX,endY);

		dy = (double)(endY-startY);
		dx = (double)(endX-startX);
		gradient = dy/dx;

		generalDirection = GeoUtils.calculateGeneralDirection(startX,startY,
																													endX,endY);
		radAngle = GeoUtils.getAngle((double)startX,(double)startY,
																 (double)endX,(double)endY);
	}

	public double getGradient() {return gradient;}
	public double getdy() {return dy;}
	public double getdx() {return dx;}
	public int getstartX() {return startX;}
	public int getstartY() {return startY;}
	public int getendX() {return endX;}
	public int getendY() {return endY;}
	public double getRadAngle() {return radAngle;}
	public int getMidX() {return (startX+endX)/2;}
	public int getMidY () {return (startY+endY)/2;}
	public int getGeneralDirection() {return generalDirection;}
	public int getID() {return iD;}
    
	public abstract boolean isLastOne();
	public abstract CarContainer onToNext();
	public abstract int getParentID();
	
	public abstract ArrayList<CarContainer> getAdjacent(); 
        
	public boolean isFirstOne() {
		return (iD == 0);
	}

	public void drawAllCars(Graphics2D g2d) {
		final Iterator iterator = cars.iterator();
		while (iterator.hasNext()) {
	    final Car tempcar =(Car)iterator.next();
	    tempcar.drawCar(g2d);
		}
	}

	public void drawIDs(Graphics2D g2d, int parentID) {
		g2d.setColor(Color.red);
		g2d.setFont(new Font("Serif",Font.BOLD,8));

		g2d.drawString(parentID+"."+iD,(startX+endX)/2-5,(startY+endY)/2+4);

		g2d.setFont(new Font("Serif",Font.BOLD,10));
	}
 
	public void addCar(Car newCar) {
		cars.addElement(newCar);
	}
    
	public void removeCar(Car newCar){
		cars.removeElement(newCar);
	}

	public boolean hasCars() {
		return (cars.size() != 0);
	}
    
	public boolean anycollisions(Car currentcar) {
	
		final Iterator iterator = cars.iterator();
		while (iterator.hasNext()) {
	    final Car tempcar =(Car)iterator.next();
	    if ((tempcar.collide(currentcar)) && (tempcar != currentcar)) {
				return true;
	    }
		} 
		return false;
	}
    
	/**
	 * given the front of the "currentcar", this returns the shortest distance
	 * between the front of the "currentcar" and the back of the car in front.
	 */
	public double[] getNextObInFront(Car currentcar,double curCarDistFromEnd) {
		double[] inFrontInfo = new double[2];
		inFrontInfo[0] = NOTHING_IN_FRONT; //shortest distance
		inFrontInfo[1] = 0;                //Speed

		final Iterator iterator = cars.iterator();
		while (iterator.hasNext()) {
	    final Car tempcar =(Car)iterator.next();
	    if (tempcar != currentcar) {
				final double dist = curCarDistFromEnd - tempcar.distanceToEnd;
		                    		
				if (dist > 0 && dist < inFrontInfo[0]) {
					inFrontInfo[0] = dist-tempcar.halflength;
					inFrontInfo[1] = tempcar.speed;
				}
	    }
		}
		return inFrontInfo;
	}

	public double[] isOKToGo(Car c,double[] info,double dist) {return info;}
    
	public int numStationaryCars() {
		int sum = 0;

		final Iterator iterator = cars.iterator();
		while (iterator.hasNext()) {
	    final Car tempcar =(Car)iterator.next();
	    if ((int)tempcar.speed == 0) sum++;
		}
	
		return sum;
	}

	public void kill() {

		final Iterator iterator = cars.iterator();
		while (iterator.hasNext()) {
	    final Car tempcar =(Car)iterator.next();
	    tempcar.removefromTimer();
		}
		cars = null;
	}

	/**************** Due to mouse Events *******************************/
	public Car getCar(Point2D p) {
		final Iterator iterator = cars.iterator();
		while (iterator.hasNext()) {
	    final Car tempcar =(Car)iterator.next();
	    if (tempcar == null) System.out.println("problem"); //FIXME
	    if (tempcar.carShape.contains(p)) return tempcar;
		}
		return null;
	}

	public int getSpeeds() {
		int speedSum = 0;

		final Iterator iterator = cars.iterator();
		while (iterator.hasNext()) {
	    final Car tempcar =(Car)iterator.next();
	    speedSum += (int)tempcar.speed;
		}
		return speedSum;
	}
}











