/**
 *  This file is part of Path Computation Element Emulator (PCEE).
 *
 *  PCEE is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  PCEE is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with PCEE.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.pcee.architecture;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import com.pcee.architecture.clientmodule.ClientModule;
import com.pcee.architecture.clientmodule.ClientModuleImpl;
import com.pcee.architecture.computationmodule.ComputationModule;
import com.pcee.architecture.computationmodule.ComputationModuleDomainImpl;
import com.pcee.architecture.computationmodule.ComputationModuleParentImpl;
import com.pcee.architecture.computationmodule.ted.TopologyInformationDomain;
import com.pcee.architecture.computationmodule.ted.TopologyInformationParent;
import com.pcee.architecture.computationmodule.ted.TopologyUpdateListener;
import com.pcee.architecture.networkmodule.NetworkModule;
import com.pcee.architecture.networkmodule.NetworkModuleImpl;
import com.pcee.architecture.sessionmodule.SessionModule;
import com.pcee.architecture.sessionmodule.SessionModuleImpl;
import com.pcee.logger.Logger;

public class ModuleManagement {

	private NetworkModule networkModule;
	private SessionModule sessionModule;
	private ComputationModule computationModule;
	private ClientModule clientModule;

	boolean running = false;
	boolean isServer = false;

	public ModuleManagement(boolean isServer) {
		if (running == false) {

			this.isServer = isServer;
			networkModule = new NetworkModuleImpl(isServer, this); // FIXME
			sessionModule = new SessionModuleImpl(this);
			if (isServer == true) {
				computationModule = new ComputationModuleDomainImpl(this, "127.0.0.1", 4189); //Default location of the parent PCE is 127.0.0.1:4189
				System.out.println("Using default location of the parent PCE as : 127.0.0.1:4189 to change please use the config file" );
			} else {
				clientModule = new ClientModuleImpl(this);
				Logger.logging = true;
				Logger.debugging = true;
			}

			running = true;
		}
	}

	public ModuleManagement(boolean isServer, String configFile) {

		try {
			Properties reader = new Properties();
			reader.load(new FileInputStream(configFile));

			int port = 0, rrPort = 0, sessionThreads = 0, computationThreads = 0;

			String logger = reader.getProperty("logging");
			if (logger.equalsIgnoreCase("on")) {
				Logger.logging = true;
			} else {
				Logger.logging = false;
			}

			String debug = reader.getProperty("debug");
			if (debug.equalsIgnoreCase("on")) {
				Logger.debugging = true;
			} else {
				Logger.debugging = false;
			}


			port = Integer.valueOf(reader.getProperty("port"));
			rrPort = Integer.valueOf(reader.getProperty("rrPort"));
			
			sessionThreads = Integer.valueOf(reader
					.getProperty("sessionThreads"));
			computationThreads = Integer.valueOf(reader
					.getProperty("computationThreads"));

			if (running == false) {

				this.isServer = isServer;
				networkModule = new NetworkModuleImpl(isServer, this, port);
				if (isServer == false)
					sessionModule = new SessionModuleImpl(this, sessionThreads);
				else
					sessionModule = new SessionModuleImpl(this, sessionThreads);
				if (isServer == true) {
					String role = reader.getProperty("pceRole");
					if (role.equalsIgnoreCase("domain")) {
						String pPCEIP = reader.getProperty("parentPCEIP");
						int pPCEport = Integer.parseInt(reader.getProperty("parentPCEport"));
						TopologyInformationDomain.setBnListPath(reader.getProperty("borderNodeList"));
						TopologyInformationDomain.setTopoPath(reader.getProperty("topology"));
						TopologyInformationDomain.setImporter(reader.getProperty("importer"));
						TopologyInformationDomain.setTopologyUpdatePort(Integer.parseInt(reader.getProperty("topologyUpdatePort")));
						TopologyInformationDomain.setTopologyUpdateParentIP(reader.getProperty("parentPCEIP"));
						TopologyInformationDomain.setTopologyUpdateParentPort(Integer.parseInt(reader.getProperty("topologyUpdateParentPort")));
						computationModule = new ComputationModuleDomainImpl(this, computationThreads, pPCEIP, pPCEport);
					} else if (role.equalsIgnoreCase("parent")) {
						TopologyInformationParent.setTopoPath(reader.getProperty("topology"));
						TopologyInformationParent.setImporter(reader.getProperty("importer"));
						TopologyInformationParent.setMultiDomainInfoPath(reader.getProperty("multiDomainInfo"));
						TopologyInformationParent.setTopologyUpdatePort(Integer.parseInt(reader.getProperty("topologyUpdatePort")));
						computationModule = new ComputationModuleParentImpl(this, computationThreads);
					}
					TopologyUpdateListener listener = new TopologyUpdateListener(this, role, rrPort);
					listener.start();
				} else {
					clientModule = new ClientModuleImpl(this);
				}
				running = true;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 			catch (Exception e) {
			System.out.println("Wrong Configuration Inputs!");
			System.exit(0);
		}
	}

	public void stop() {
		running = false;
		sessionModule.stop();
		networkModule.stop();
		if (isServer == true) {
			computationModule.stop();
		} else {
			clientModule.stop();
		}
	}

	public NetworkModule getNetworkModule() {
		return networkModule;
	}

	public SessionModule getSessionModule() {
		return sessionModule;
	}

	public ComputationModule getComputationModule() {
		return computationModule;
	}

	public ClientModule getClientModule() {
		return clientModule;
	}

	public boolean isServer() {
		return isServer;
	}

}
