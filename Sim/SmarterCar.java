import java.awt.Rectangle;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Dimension;
import java.awt.Shape;

import java.awt.Polygon;

import java.lang.Math;

import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;

import time.*;
import Utils.*;
import java.util.*;

public class SmarterCar extends Car implements Timed {
 
	public SmarterCar(LaneSection startLane,CarGenerator parent,
						 TimeManager theTick, int topspeed) {
     	
     	
		super(startLane, parent, theTick, topspeed);
		
		init(startLane);
		

	}
	
	public void init(CarContainer startLane) {
	
		
	
		// find shortest path (dijkstra, no A* yet)
		reachableCarContainers = new ArrayList<CarContainer>();
		endPoints = new ArrayList<Edge>();
		PriorityQueue<Edge> pq = new PriorityQueue<Edge>();
		
		reachableCarContainers.add(startLane);
		pq.add(new Edge(startLane, startLane.length, null));
		
		
		int i = 0;
		while (!pq.isEmpty()) {
			Edge temp = pq.poll();
			//System.out.println("--"+(i++)+"--");
			//System.out.println("start " + temp.cc.lightStart);
			int time = 0;
			int isNow = 1;
			
			if (temp.cc.lightCycle > 0) {
				time = ((int)((double)temp.d/5+ticker.ticks));//estimation
				System.out.print("est time " + time + " ");
				if ((time/temp.cc.lightCycle)%2==0)
					isNow = temp.cc.lightStart;
				else
					isNow = 1-temp.cc.lightStart;
					
				if (isNow != 1)
					time = time%temp.cc.lightCycle;
				else
					time = 0;
					
				System.out.print("est wait " + time +" ");
				System.out.println("est light " + isNow);
				
				//System.out.println(ticker.ticks + " / " + temp.cc.lightCycle);
				//System.out.println(isNow);
			}

			if (temp.cc.isLastOne()) {
				// store the endpoints
				endPoints.add(temp);
			}
			else {
				// check every adjacent car container
				for (CarContainer c : temp.cc.getAdjacent()) {  
					// if it has not been visited (O(n), can be made O(1))
					if (!reachableCarContainers.contains(c)) {
						reachableCarContainers.add(c);
						//System.out.println("est wait time " + time);
						pq.offer(new Edge(c, temp.d + c.length + time, temp));

					}
				}
			}
			
		}
		
		// choose an end point and create the path of car containers
		Edge temp = endPoints.get(rand.nextInt(endPoints.size()));
		while (temp.cc != startLane) {
			plannedPath.addFirst(temp.cc);
			temp = temp.prev;
		}
		plannedPath.addFirst(plannedPath.removeLast());
		
		// print the path
		// System.out.println("--- PATH ---");
		// for (CarContainer c : plannedPath)
		//	System.out.println(c);
		
		addCar();
	}
}





