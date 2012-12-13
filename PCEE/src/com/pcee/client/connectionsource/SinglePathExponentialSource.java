package com.pcee.client.connectionsource;

import java.util.Random;

import com.graph.graphcontroller.Gcontroller;
import com.pcee.client.event.eventhandler.EventHandler;
import com.pcee.client.event.impl.ReserveConnection;
import com.pcee.client.launcher.Launcher;
import com.pcee.client.resv.ResvElement;
import com.pcee.client.resv.SinglePathResvElement;

public class SinglePathExponentialSource extends Source {

	int ID = 0;
	double bwLow;
	double bwHigh;
	double delayLow;
	double delayHigh;
	int cpuLow, cpuHigh, ramLow, ramHigh, storageLow, storageHigh;

	public String getNextID() {
		ID++;
		return Integer.toString(ID);
	}

	@Override
	public void initSource() {
		// TODO Auto-generated method stub
	}

	@Override
	public void nextRequest(boolean currentResvStatus) {
		double bw = random.nextInt((int) bwHigh - (int) bwLow + 1) + bwLow;
		double delay = random.nextInt((int) delayHigh - (int) delayLow + 1) + delayLow;
		double startTime = EventHandler.getTime() + this.getInterArrivalTime();
		double endTime = startTime + this.getHoldingTime();
		
		int source = random.nextInt(Launcher.multiDomainNodes.size());
		int destination;
		if (Launcher.itOrNormal()) {
			destination = source;
			int cpu = random.nextInt(cpuHigh-cpuLow + 1) + cpuLow;
			int ram = random.nextInt(ramHigh-ramLow + 1) + ramLow;
			int storage = random.nextInt(storageHigh-storageLow + 1) + storageLow;
			
			ResvElement element = new SinglePathResvElement(getNextID(), Launcher.multiDomainNodes.get(source), Launcher.multiDomainNodes.get(destination), startTime, endTime, cpu, ram ,storage, bw, delay);

			EventHandler.addEvent(new ReserveConnection(element, this));
			
		} else {
			do {
				destination = random.nextInt(Launcher.multiDomainNodes.size());
			} while (destination == source);
			ResvElement element = new SinglePathResvElement(getNextID(), Launcher.multiDomainNodes.get(source), Launcher.multiDomainNodes.get(destination), startTime, endTime, bw, delay);

			EventHandler.addEvent(new ReserveConnection(element, this));
		}

		

		
	}

	int totConnections = 0;

	int blockedConnections = 0;

	private double interArrivalTime, holdingTime;

	private Random random;

	public SinglePathExponentialSource(double interArrivalTime, double holdingTime, String logFile, int cpuLow, int cpuHigh, int ramLow, int ramHigh, int storageLow, int storageHigh, double bwLow, double bwHigh, double delayLow, double delayHigh) {
		this.random = new Random(10);
		this.interArrivalTime = interArrivalTime;
		this.holdingTime = holdingTime;
		this.logFile = logFile;
		this.bwLow = bwLow;
		this.bwHigh = bwHigh;
		this.delayLow = delayLow;
		this.delayHigh = delayHigh;
		this.cpuHigh = cpuHigh;
		this.cpuLow = cpuLow;
		this.ramLow = ramLow;
		this.ramHigh = ramHigh;
		this.storageLow = storageLow;
		this.storageHigh = storageHigh;
	}

	public double getHoldingTime() {
		double u = random.nextDouble();
		return -1.0 * Math.log(u) * holdingTime;
	}

	public double getInterArrivalTime() {
		double u = random.nextDouble();
		return -1.0 * Math.log(u) * interArrivalTime;
	}

	@Override
	public void connectionBlocked() {
		this.blockedConnections++;
		this.totConnections++;
		System.out.println("Blocked Connections = " + blockedConnections);
		System.out.println("Total Connections = " + totConnections);
	}

	@Override
	public void connectionReserved() {
		this.totConnections++;
		System.out.println("Total Connections = " + totConnections);
	}

	@Override
	public int getTotConnections() {
		return totConnections;
	}

	@Override
	public double getBlockingProbability() {
		return (double) blockedConnections / (double) totConnections;
	}

	@Override
	public int getBlockedConnections() {
		// TODO Auto-generated method stub
		return this.blockedConnections;
	}

}
