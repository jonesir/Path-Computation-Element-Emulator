package com.pcee.architecture.computationmodule.threadpool;

import com.graph.elements.vertex.algorithms.VertexAlgorithm;
import com.graph.elements.vertex.algorithms.constraints.VertexConstraint;
import com.graph.path.algorithms.PathComputationAlgorithm;
import com.graph.path.algorithms.constraints.Constraint;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.PCEPAddress;

public class Request {
    // Variable to store the request ID
    private String requestID;
    
    // variable to store the address for the PCE response
    private PCEPAddress address;

    // Constraints for the path computation request
    private Constraint constrains;
    
    // Algorithm used for the path computation request
    private PathComputationAlgorithm algo;
    
    // Constraints for the IT node searching request
    private VertexConstraint vConstraints;

    // Algorithm used for the IT searching request
    private VertexAlgorithm vAlgo;
    
	//Source Router IP address
    private String sourceRouterIP;
    
    //Destination router IP address
    private String destRouterIP;
    
    //Bandwidth requested
    private double bandwidth;
    
    private boolean isITRequest = false;
    
    public double getBandwidth() {
		return bandwidth;
	}

	public void setBandwidth(double bandwidth) {
		this.bandwidth = bandwidth;
	}

	public String getSourceRouterIP() {
		return sourceRouterIP;
	}

	public void setSourceRouterIP(String sourceRouterIP) {
		this.sourceRouterIP = sourceRouterIP;
	}

	public String getDestRouterIP() {
		return destRouterIP;
	}

	public void setDestRouterIP(String destRouterIP) {
		this.destRouterIP = destRouterIP;
	}

    /**
     * @return the requestID
     */
    public String getRequestID() {
	return requestID;
    }

    /**
     * @param requestID
     *            the requestID to set
     */
    public void setRequestID(String requestID) {
	this.requestID = requestID;
    }

    /**
     * @return the address
     */
    public PCEPAddress getAddress() {
	return address;
    }

    /**
     * @param address
     *            the address to set
     */
    public void setAddress(PCEPAddress address) {
	this.address = address;
    }

    /**
     * @return the constrains
     */
    public Constraint getConstrains() {
	return constrains;
    }

    /**
     * @param constrains
     *            the constrains to set
     */
    public void setConstrains(Constraint constrains) {
	this.constrains = constrains;
    }

    /**
     * @return the algo
     */
    public PathComputationAlgorithm getAlgo() {
	return algo;
    }

    /**
     * @param algo
     *            the algo to set
     */
    public void setAlgo(PathComputationAlgorithm algo) {
	this.algo = algo;
    }
    
    /**
     * @param vConstaints
     */
    public void setVConstraints(VertexConstraint vConstraints) {
    	this.vConstraints = vConstraints;
    }
    
    /**
     * @return
     */
    public VertexConstraint getVConstraints() {
    	return this.vConstraints;
    }
    
    /**
     * @param vAlgo
     */
    public void setVAlgorithm(VertexAlgorithm vAlgo) {
    	this.vAlgo = vAlgo;
    }
    
    public VertexAlgorithm getVAlgorithm() {
    	return this.vAlgo;
    }
    

    public void setITRequest(boolean isITRequest) {
    	this.isITRequest = isITRequest;
    }
    
    public boolean isITRequest() {
    	return this.isITRequest;
    }

}
