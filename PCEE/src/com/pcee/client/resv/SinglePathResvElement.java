package com.pcee.client.resv;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;

import com.graph.graphcontroller.Gcontroller;
import com.pcee.client.connectionsource.Source;
import com.pcee.client.launcher.Launcher;
import com.pcee.client.reserverelease.MultiDomainReserveRelease;
import com.pcee.protocol.message.objectframe.impl.PCEPExplicitRouteObject;
import com.pcee.protocol.message.objectframe.impl.PCEPGenericExplicitRouteObjectImpl;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.EROSubobjects;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.PCEPAddress;
import com.pcee.protocol.response.PCEPResponseFrame;

public class SinglePathResvElement extends ResvElement {

	public LinkedList<PCEPExplicitRouteObject> objectList;

	public SinglePathResvElement(String nextID, String string, String string2, double startTime, double endTime, double bw, double delay) {
		this.ID = nextID;
		this.bw = bw;
		this.startTime = startTime;
		this.endTime = endTime;
		this.sourceID = string;
		this.destID = string2;
		this.delay = delay;
	}
	
	public SinglePathResvElement(String nextID, String string, String string2, double startTime, double endTime, int cpu, int ram, int storage, double bw, double delay) {
		this.ID = nextID;
		this.bw = bw;
		this.startTime = startTime;
		this.endTime = endTime;
		this.sourceID = string;
		this.destID = string2;
		this.delay = delay;
		this.cpu = cpu;
		this.ram = ram;
		this.storage = storage;
	}
	

	@Override
	public boolean reserveConnection() {
		// Call function to PCE client to compute path from source to destion
		System.out.println("Making path computation request to server from : " + sourceID + " to" + destID);
		System.out.println("\n\n\n\n\n\n");
		PCEPResponseFrame frame = Launcher.getPath(sourceID, destID, cpu, ram, storage, (float) bw, (float) delay);
		if (frame.containsNoPathObject()) {
			System.out.println("ERROR: Resquest " + this.ID + " , Path from " + sourceID + " to " + destID + " with specified requirements does not exist. \n Request " + this.ID + " has been blocked");
			return false;
		}
		// do the reservation work with specified bandwidth on returned
		// this is single path scenario, so there is only one path in
		// the object list
		// therefor, objectList.get(0) will get the return path
		System.out.println("Path Exists!!!Making Reserve Request!!!");
		this.objectList = frame.extractExplicitRouteObjectList();
		ArrayList<EROSubobjects> objs = ((PCEPGenericExplicitRouteObjectImpl)this.objectList.get(0)).getTraversedVertexList();
		ArrayList<String> vertexSequence = new ArrayList<String>();
		for(int i = 0 ; i < objs.size() ; i++) {
			vertexSequence.add(((PCEPAddress)objs.get(i)).getIPv4Address(false));
		}
		
		MultiDomainReserveRelease.reserve(bw, vertexSequence);
		if(Launcher.isITRequest){
			MultiDomainReserveRelease.itReserve(cpu, ram, storage, vertexSequence.get(vertexSequence.size()-1), true);
		}
		return true;
	}

	@Override
	public boolean releaseConnection() {
		System.out.println("release bandwidth from " + sourceID + " to " + destID);
		// do the release work with specified bandwidth on EROs that have
		// been reserved
		// like the reserve function, the release function also release
		// bandwidth along only one path
		// object.get(0) will get the path that needed to be release
		ArrayList<EROSubobjects> subobjects = ((PCEPGenericExplicitRouteObjectImpl) objectList.get(0)).getTraversedVertexList();
		for (int j = 0; j < subobjects.size() - 1; j++) {
			String ingress = ((PCEPAddress) (subobjects.get(j))).getIPv4Address(false);
			String egress = ((PCEPAddress) (subobjects.get(j + 1))).getIPv4Address(false);
			System.out.println("||||Making release between " + ingress + " and " + egress + ".");
		}
		return true;
	}

	@Override
	public double getPathDelay() {
		return 0;
	}

	@Override
	public boolean reserveConnection1P1() {
		return false;
	}

	@Override
	public boolean releaseConnectionAndITResource() {
		return false;
	}

	@Override
	public boolean reserveConnectionWithITResource() {
		return false;
	}

}
