package com.graph.intf;

import com.graph.elements.vertex.VertexElement;

public class ParamsInitializer {
	public static double initBandwidth(){
		return 40.0;
	}
	public static double initDelay(VertexElement vertex1, VertexElement vertex2){
		double distance = Math.sqrt(Math.pow(vertex1.getXCoord() - vertex2.getXCoord(), 2) + Math.pow(vertex1.getYCoord() - vertex2.getYCoord(), 2));
		double delay = distance / 29.9792458; // (in ms)
		
		return delay;
	}
	
}
