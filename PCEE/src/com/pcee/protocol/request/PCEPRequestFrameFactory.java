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

package com.pcee.protocol.request;

import java.util.LinkedList;


import com.pcee.protocol.message.PCEPMessage;
import com.pcee.protocol.message.objectframe.PCEPCommonObjectHeader;
import com.pcee.protocol.message.objectframe.PCEPObjectFrame;
import com.pcee.protocol.message.objectframe.impl.PCEPBandwidthObject;
import com.pcee.protocol.message.objectframe.impl.PCEPEndPointsObject;
import com.pcee.protocol.message.objectframe.impl.PCEPITResourceObject;
import com.pcee.protocol.message.objectframe.impl.PCEPIncludeRouteObject;
import com.pcee.protocol.message.objectframe.impl.PCEPLabelSwitchedPathAttributesObject;
import com.pcee.protocol.message.objectframe.impl.PCEPLoadBalancingObject;
import com.pcee.protocol.message.objectframe.impl.PCEPMetricObject;
import com.pcee.protocol.message.objectframe.impl.PCEPReportedRouteObject;
import com.pcee.protocol.message.objectframe.impl.PCEPRequestParametersObject;
import com.pcee.protocol.message.objectframe.impl.PCEPSynchronizationVectorObject;

public class PCEPRequestFrameFactory {

	
	
	public static PCEPRequestFrame generatePathComputationRequestFrame(PCEPRequestParametersObject RP, PCEPEndPointsObject endPoints) {

		PCEPRequestFrame requestFrame = new PCEPRequestFrame(RP, endPoints);

		return requestFrame;
	}

	public static PCEPRequestFrame generatePathComputationRequestFrame(PCEPRequestParametersObject RP, PCEPEndPointsObject endPoints, PCEPBandwidthObject bandwidth){
		PCEPRequestFrame requestFrame = new PCEPRequestFrame(RP, endPoints, bandwidth);
		return requestFrame;
	}
	
	public static PCEPRequestFrame getPathComputationRequestFrame(PCEPMessage message) {

		LinkedList<PCEPSynchronizationVectorObject> SVECList = null;
		PCEPRequestParametersObject RP = null;
		PCEPEndPointsObject endPoints = null;
		PCEPLabelSwitchedPathAttributesObject LSPA = null;
		PCEPBandwidthObject bandwidth = null;
		LinkedList<PCEPMetricObject> metricList = null;
		PCEPReportedRouteObject RRO = null;
		PCEPIncludeRouteObject IRO = null;
		PCEPLoadBalancingObject loadBalancing = null;
		PCEPITResourceObject it = null;
		
		LinkedList<PCEPObjectFrame> objectList = message.getObjectsList();

		for (int i = 0; i < objectList.size(); i++) {

			PCEPObjectFrame objectFrame = objectList.get(i);
			PCEPCommonObjectHeader objectFrameHeader = objectFrame.getObjectHeader();

			switch (objectFrameHeader.getClassDecimalValue()) {

			case 11: {
				PCEPSynchronizationVectorObject SVEC = (PCEPSynchronizationVectorObject) objectFrame;

				if (SVECList != null) {
					SVECList.add(SVEC);
				} else {
					SVECList = new LinkedList<PCEPSynchronizationVectorObject>();
					SVECList.add(SVEC);
				}

				break;
			}

			case 2: {
				RP = (PCEPRequestParametersObject) objectFrame;
				break;
			}
			case 4: {
				endPoints = (PCEPEndPointsObject) objectFrame;
				break;
			}
			case 9: {
				LSPA = (PCEPLabelSwitchedPathAttributesObject) objectFrame;
				break;
			}

			case 5: {
				bandwidth = (PCEPBandwidthObject) objectFrame;
				break;
			}

			case 6: {
				PCEPMetricObject metricObject = (PCEPMetricObject) objectFrame;

				if (metricList != null) {
					metricList.add(metricObject);
				} else {
					metricList = new LinkedList<PCEPMetricObject>();
					metricList.add(metricObject);
				}

				break;
			}

			case 8: {
				RRO = (PCEPReportedRouteObject) objectFrame;
				break;
			}

			case 10: {
				IRO = (PCEPIncludeRouteObject) objectFrame;
				break;
			}

			case 14: {
				loadBalancing = (PCEPLoadBalancingObject) objectFrame;
				break;
			}
			
			case 16: {
				it = (PCEPITResourceObject) objectFrame;
				break;
			}
			
			default: {
				break;
			}

			}

		}

		PCEPRequestFrame requestFrame = new PCEPRequestFrame(RP, endPoints);

		if(endPoints !=null){
			requestFrame = new PCEPRequestFrame(RP,endPoints);
		}
		
		
		if (SVECList != null) {
			requestFrame.insertSynchronizationVectorObjectList(SVECList);
		}
		if (LSPA != null) {
			requestFrame.insertLabelSwitchedPathAttributesObject(LSPA);
		}
		if (bandwidth != null) {
			requestFrame.insertBandwidthObject(bandwidth);
		}
		if (metricList != null) {
			requestFrame.insertMetricObjectList(metricList);
		}
		if (RRO != null) {
			requestFrame.insertReportedRouteObject(RRO);
		}
		if (IRO != null) {
			requestFrame.insertIncludeRouteObject(IRO);
		}
		if (loadBalancing != null) {
			requestFrame.insertLoadBalancingObject(loadBalancing);
		}
		if (it != null) {
			requestFrame.insertITResourceObject(it);
		}
		
		return requestFrame;
	}

	public static PCEPRequestFrame generatePathComputationRequestFrame(
			PCEPRequestParametersObject RP, PCEPEndPointsObject endPoints,
			PCEPBandwidthObject bandwidth, PCEPMetricObject metric) {
		PCEPRequestFrame requestFrame = new PCEPRequestFrame(RP,endPoints,bandwidth,metric);
		return requestFrame;
	}
	
	public static PCEPRequestFrame generateITResourceRequestFrame(
			PCEPRequestParametersObject RP, PCEPEndPointsObject endPoints,
			PCEPBandwidthObject bandwidth, PCEPMetricObject metric,
			PCEPITResourceObject it
			) {
		
		PCEPRequestFrame requestFrame = new PCEPRequestFrame(RP,endPoints,bandwidth,metric,it);
		return requestFrame;
	}

}
