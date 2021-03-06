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

package com.pcee.client;

import com.pcee.architecture.ModuleManagement;
import com.pcee.gui.ConnectorGUI;
import com.pcee.logger.Logger;
import com.pcee.logger.gui.GUILogObject;

/**GUI based implementation of the PCE client
 * 
 * @author Marek Drogon
 *
 */
public class ClientLauncherTID {
	
	/**Launch point to initialize the client GUI*/
	public static void main(String[] args) throws Exception {
		
		//Address of the PCE server
//		String pceServerAddress = "127.0.0.1";
		String pceServerAddress = "172.16.1.2";
		
		//Default source and destination values for path computation requests

		String defaultSourceAddress = "172.20.1.10";
		String defaultDestinationAddress = "172.20.1.40";
		
//		String defaultSourceAddress = "192.169.2.1";
//		String defaultDestinationAddress = "192.169.2.12";
		
		//Set Logger to log to the GUI
		Logger.setLogObject(new GUILogObject());
		
		//Initialize the layer management module
		ModuleManagement lm = new ModuleManagement(false);
		
		//Start the GUI
		new ConnectorGUI(lm, pceServerAddress, defaultSourceAddress, defaultDestinationAddress);
		
	}

}