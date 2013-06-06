package org.jersey.resource;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Node {
	
	Node() {}
	
	Node(String nodeID, double xCoord, double yCoord) {
		this.nodeID = nodeID;
		this.xCoord = xCoord;
		this.yCoord = yCoord;
	}
	
	public String nodeID;
	public double xCoord;
	public double yCoord;
	public String getNodeID() {
		return nodeID;
	}
	public void setNodeID(String nodeID) {
		this.nodeID = nodeID;
	}
	public double getxCoord() {
		return xCoord;
	}
	public void setxCoord(double xCoord) {
		this.xCoord = xCoord;
	}
	public double getyCoord() {
		return yCoord;
	}
	public void setyCoord(double yCoord) {
		this.yCoord = yCoord;
	}

}
