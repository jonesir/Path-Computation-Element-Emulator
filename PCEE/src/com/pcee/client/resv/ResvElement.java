package com.pcee.client.resv;

import com.graph.elements.vertex.VertexElement;
import com.graph.graphcontroller.Gcontroller;

public abstract class ResvElement {

	protected static final String classIdentifier = "ResvElement";
	
	/**Variable to store the start and end time for the reservation*/
	protected double startTime, endTime;

	protected Gcontroller graph;
	
	protected 	String sourceID, destID;

	/**Variable to store the specific reservation ID*/
	protected String ID;

	/**variable to store the reservation status of the connection */
	protected boolean resvStatus;
	
	protected double bw;
	
	protected double delay;
	
	protected int pathCount;
	/** Variable to store the specific IT Resource*/
	protected int cpu;
	protected int ram;
	protected int storage;
	
	protected boolean isITRequest;
	
	/**Function to get the reservation ID*/
	public String getResvID(){
		return this.ID;
	}

	public double getBw() {
		return bw;
	}

	public void setBw(double bw) {
		this.bw = bw;
	}
	
	public void setDelay(double delay){
		this.delay = delay;
	}
	
	public double getDelay(){
		return this.delay;
	}

	/**Function to get the start time for the reservation*/
	public double getStartTime(){
		return this.startTime;
	}

	/**Function to get the end time for the reservation*/
	public double getEndTime(){
		return this.endTime;
	}
	
	public boolean getResvStatus(){
		return this.resvStatus;
	}
	
	// for hierarchical pce supporting it resource with single path scenario
	public boolean reserveConnectionAndITResourceWithHierarchicalPCESupportingITExtentionForSinglePathScenario(){
		return true;
	}
	
	// for normal scenario
	public abstract boolean reserveConnection();
	
	public abstract boolean releaseConnection();
	
	public abstract double getPathDelay();
	
	public VertexElement getSource(){
		return graph.getVertex(sourceID);
	}
	
	public VertexElement getDestination(){
		return graph.getVertex(destID);
	}





}
