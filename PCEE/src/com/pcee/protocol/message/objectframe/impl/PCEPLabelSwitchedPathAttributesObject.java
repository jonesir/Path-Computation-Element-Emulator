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

package com.pcee.protocol.message.objectframe.impl;

import com.pcee.protocol.message.PCEPComputationFactory;
import com.pcee.protocol.message.PCEPConstantValues;
import com.pcee.protocol.message.objectframe.PCEPCommonObjectHeader;
import com.pcee.protocol.message.objectframe.PCEPObjectFrame;

/**
 * <pre>
 *  0                   1                   2                   3
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                       Exclude-any                             |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                       Include-any                             |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                       Include-all                             |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |  Setup Prio   |  Holding Prio |     Flags   |L|   Reserved    |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                                                               |
 * //                     Optional TLVs                           //
 * |                                                               |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * </pre>
 */

public class PCEPLabelSwitchedPathAttributesObject implements PCEPObjectFrame {

	private final String NAME = "Label Switched Path Attributes";

	private String excludeAny;
	private String includeAny;
	private String includeAll;
	private String setupPrio;
	private String holdingPrio;
	private String reserved;
	private String flags;

	private PCEPCommonObjectHeader objectHeader;
	// private LinkedList<PCEPTLVObject> tlvList;

	private int excludeAnyStartBit = PCEPConstantValues.LSPA_OBJECT_EXCLUDE_ANY_START_BIT;
	private int excludeAnyEndBit = PCEPConstantValues.LSPA_OBJECT_EXCLUDE_ANY_END_BIT;
	private int excludeAnyLength = PCEPConstantValues.LSPA_OBJECT_EXCLUDE_ANY_LENGTH;

	private int includeAnyStartBit = PCEPConstantValues.LSPA_OBJECT_INCLUDE_ANY_START_BIT;
	private int includeAnyEndBit = PCEPConstantValues.LSPA_OBJECT_INCLUDE_ANY_END_BIT;
	private int includeAnyLength = PCEPConstantValues.LSPA_OBJECT_INCLUDE_ANY_LENGTH;

	private int includeAllStartBit = PCEPConstantValues.LSPA_OBJECT_INCLUDE_ALL_START_BIT;
	private int includeAllEndBit = PCEPConstantValues.LSPA_OBJECT_INCLUDE_ALL_END_BIT;
	private int includeAllLength = PCEPConstantValues.LSPA_OBJECT_INCLUDE_ALL_LENGTH;

	private int setupPrioStartBit = PCEPConstantValues.LSPA_OBJECT_SETUP_PRIO_START_BIT;
	private int setupPrioEndBit = PCEPConstantValues.LSPA_OBJECT_SETUP_PRIO_END_BIT;
	private int setupPrioLength = PCEPConstantValues.LSPA_OBJECT_SETUP_PRIO_LENGTH;

	private int holdingPrioStartBit = PCEPConstantValues.LSPA_OBJECT_HOLDING_PRIO_START_BIT;
	private int holdingPrioEndBit = PCEPConstantValues.LSPA_OBJECT_HOLDING_PRIO_END_BIT;
	private int holdingPrioLength = PCEPConstantValues.LSPA_OBJECT_HOLDING_PRIO_LENGTH;

	private int flagsStartBit = PCEPConstantValues.LSPA_OBJECT_FLAGS_START_BIT;
	private int flagsEndBit = PCEPConstantValues.LSPA_OBJECT_FLAGS_END_BIT;
	private int flagsLength = PCEPConstantValues.LSPA_OBJECT_FLAGS_LENGTH;

	private int lFlagStartBit = PCEPConstantValues.LSPA_OBJECT_FLAG_L_START_BIT;
	private int lFlagEndBit = PCEPConstantValues.LSPA_OBJECT_FLAG_L_END_BIT;
	private int lFlagLength = PCEPConstantValues.LSPA_OBJECT_FLAG_L_LENGTH;

	private int reservedStartBit = PCEPConstantValues.LSPA_OBJECT_RESERVED_START_BIT;
	private int reservedEndBit = PCEPConstantValues.LSPA_OBJECT_RESERVED_END_BIT;
	private int reservedLength = PCEPConstantValues.LSPA_OBJECT_RESERVED_LENGTH;

	public PCEPLabelSwitchedPathAttributesObject(PCEPCommonObjectHeader objectHeader, String binaryString) {
		this.setObjectHeader(objectHeader);
		this.setObjectBinaryString(binaryString);
		this.updateHeaderLength();
	}

	public PCEPLabelSwitchedPathAttributesObject(PCEPCommonObjectHeader objectHeader, String excludeAny, String includeAny, String includeAll, int setupPrio, int holdingPrio, String lFlag) {
		this.setObjectHeader(objectHeader);
		this.setExcludeAnyBinaryString(excludeAny);
		this.setIncludeAnyBinaryString(includeAny);
		this.setIncludeAllBinaryString(includeAll);
		this.setSetupPrioDecimalValue(setupPrio);
		this.setHoldingPrioDecimalValue(holdingPrio);
		this.setFlagsBinaryString(PCEPComputationFactory.generateZeroString(flagsLength));
		this.setLFlagBinaryString(lFlag);
		this.setReservedBinaryString(PCEPComputationFactory.generateZeroString(reservedLength));
		this.updateHeaderLength();
	}

	private void updateHeaderLength() {
		int objectFrameByteLength = this.getObjectFrameByteLength();
		this.getObjectHeader().setLengthDecimalValue(objectFrameByteLength);
	}

	/**
	 * Object
	 */
	public PCEPCommonObjectHeader getObjectHeader() {
		return objectHeader;
	}

	public void setObjectHeader(PCEPCommonObjectHeader objectHeader) {
		this.objectHeader = objectHeader;
	}

	public String getObjectBinaryString() {
		String binaryString = excludeAny + includeAny + includeAll + setupPrio + holdingPrio + flags + reserved;
		return binaryString;
	}

	public void setObjectBinaryString(String binaryString) {
		String excludeAnyBinaryString = binaryString.substring(excludeAnyStartBit, excludeAnyEndBit + 1);
		String includeAnyBinaryString = binaryString.substring(includeAnyStartBit, includeAnyEndBit + 1);
		String includeAllBinaryString = binaryString.substring(includeAllStartBit, includeAllEndBit + 1);
		String setupPrioBinaryString = binaryString.substring(setupPrioStartBit, setupPrioEndBit + 1);
		String holdingPrioBinaryString = binaryString.substring(holdingPrioStartBit, holdingPrioEndBit + 1);
		String flagsBinaryString = binaryString.substring(flagsStartBit, flagsEndBit + 1);
		String lFlagBinaryString = binaryString.substring(lFlagStartBit, lFlagEndBit + 1);
		String reservedBinaryString = binaryString.substring(reservedStartBit, reservedEndBit + 1);

		this.setExcludeAnyBinaryString(excludeAnyBinaryString);
		this.setIncludeAnyBinaryString(includeAnyBinaryString);
		this.setIncludeAllBinaryString(includeAllBinaryString);
		this.setSetupPrioBinaryString(setupPrioBinaryString);
		this.setHoldingPrioBinaryString(holdingPrioBinaryString);
		this.setFlagsBinaryString(flagsBinaryString);
		this.setLFlagBinaryString(lFlagBinaryString);
		this.setReservedBinaryString(reservedBinaryString);
	}

	public int getObjectFrameByteLength() {
		int objectLength = excludeAny.length() + includeAny.length() + includeAll.length() + setupPrio.length() + holdingPrio.length() + flags.length() + reserved.length();
		int headerLength = PCEPConstantValues.COMMON_OBJECT_HEADER_LENGTH;
		int objectFrameByteLength = (objectLength + headerLength) / 8;
		return objectFrameByteLength;
	}

	public String getObjectFrameBinaryString() {
		String headerBinaryString = this.getObjectHeader().getHeaderBinaryString();
		String objectBinaryString = this.getObjectBinaryString();

		return headerBinaryString + objectBinaryString;
	}

	/**
	 * excludeAny
	 */
	// public int getExcludeAnyDecimalValue() {
	// int decimalValue = (int) getDecimalValue(excludeAny);
	// return decimalValue;
	// }
	public String getExcludeAnyBinaryString() {
		return this.excludeAny;
	}

	// public void setExcludeAnyDecimalValue(int decimalValue) {
	// int binaryLength = excludeAnyLength;
	// int maxValue = (int)
	// PCEPConstantValues.MaxValueFabrication(binaryLength);
	//
	// this.excludeAny = this.setDecimalValue(decimalValue, maxValue,
	// binaryLength);
	// }
	public void setExcludeAnyBinaryString(String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(binaryString, excludeAnyLength);
		this.excludeAny = checkedBinaryString;
	}

	public void setExcludeAnyBinaryString(int startingBit, String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(excludeAny, startingBit, binaryString, excludeAnyLength);
		this.excludeAny = checkedBinaryString;
	}

	/**
	 * includeAny
	 */
	// public int getIncludeAnyDecimalValue() {
	// int decimalValue = (int) getDecimalValue(includeAny);
	// return decimalValue;
	// }
	public String getIncludeAnyBinaryString() {
		return this.includeAny;
	}

	// public void setIncludeAnyDecimalValue(int decimalValue) {
	// int binaryLength = includeAnyLength;
	// int maxValue = (int)
	// PCEPConstantValues.MaxValueFabrication(binaryLength);
	//
	// this.includeAny = this.setDecimalValue(decimalValue, maxValue,
	// binaryLength);
	// }
	public void setIncludeAnyBinaryString(String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(binaryString, includeAnyLength);
		this.includeAny = checkedBinaryString;
	}

	public void setIncludeAnyBinaryString(int startingBit, String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(includeAny, startingBit, binaryString, includeAnyLength);
		this.includeAny = checkedBinaryString;
	}

	/**
	 * includeAll
	 */
	// public int getIncludeAllDecimalValue() {
	// int decimalValue = (int) getDecimalValue(includeAll);
	// return decimalValue;
	// }
	public String getIncludeAllBinaryString() {
		return this.includeAll;
	}

	// public void setIncludeAllDecimalValue(int decimalValue) {
	// int binaryLength = includeAllLength;
	// int maxValue = (int)
	// PCEPConstantValues.MaxValueFabrication(binaryLength);
	//
	// this.includeAll = this.setDecimalValue(decimalValue, maxValue,
	// binaryLength);
	// }
	public void setIncludeAllBinaryString(String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(binaryString, includeAllLength);
		this.includeAll = checkedBinaryString;
	}

	public void setIncludeAllBinaryString(int startingBit, String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(includeAll, startingBit, binaryString, includeAllLength);
		this.includeAll = checkedBinaryString;
	}

	/**
	 * setupPrio
	 */
	public int getSetupPrioDecimalValue() {
		int decimalValue = (int) PCEPComputationFactory.getDecimalValue(setupPrio);
		return decimalValue;
	}

	public String getSetupPrioBinaryString() {
		return this.setupPrio;
	}

	public void setSetupPrioDecimalValue(int decimalValue) {
		int binaryLength = setupPrioLength;
		int maxValue = (int) PCEPComputationFactory.MaxValueFabrication(binaryLength);

		this.setupPrio = PCEPComputationFactory.setDecimalValue(decimalValue, maxValue, binaryLength);
	}

	public void setSetupPrioBinaryString(String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(binaryString, setupPrioLength);
		this.setupPrio = checkedBinaryString;
	}

	public void setSetupPrioBinaryString(int startingBit, String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(setupPrio, startingBit, binaryString, setupPrioLength);
		this.setupPrio = checkedBinaryString;
	}

	/**
	 * holdingPrio
	 */
	public int getHoldingPrioDecimalValue() {
		int decimalValue = (int) PCEPComputationFactory.getDecimalValue(holdingPrio);
		return decimalValue;
	}

	public String getHoldingPrioBinaryString() {
		return this.holdingPrio;
	}

	public void setHoldingPrioDecimalValue(int decimalValue) {
		int binaryLength = holdingPrioLength;
		int maxValue = (int) PCEPComputationFactory.MaxValueFabrication(binaryLength);

		this.holdingPrio = PCEPComputationFactory.setDecimalValue(decimalValue, maxValue, binaryLength);
	}

	public void setHoldingPrioBinaryString(String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(binaryString, holdingPrioLength);
		this.holdingPrio = checkedBinaryString;
	}

	public void setHoldingPrioBinaryString(int startingBit, String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(holdingPrio, startingBit, binaryString, holdingPrioLength);
		this.holdingPrio = checkedBinaryString;
	}

	/**
	 * reserved
	 */
	public int getReservedDecimalValue() {
		int decimalValue = (int) PCEPComputationFactory.getDecimalValue(reserved);
		return decimalValue;
	}

	public String getReservedBinaryString() {
		return this.reserved;
	}

	public void setReservedDecimalValue(int decimalValue) {
		int binaryLength = reservedLength;
		int maxValue = (int) PCEPComputationFactory.MaxValueFabrication(binaryLength);

		this.reserved = PCEPComputationFactory.setDecimalValue(decimalValue, maxValue, binaryLength);
	}

	public void setReservedBinaryString(String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(binaryString, reservedLength);
		this.reserved = checkedBinaryString;
	}

	public void setReservedBinaryString(int startingBit, String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(reserved, startingBit, binaryString, reservedLength);
		this.reserved = checkedBinaryString;
	}

	/**
	 * flags
	 */
	// public int getFlagsDecimalValue() {
	// int decimalValue = (int) getDecimalValue(flags);
	// return decimalValue;
	// }
	public String getFlagsBinaryString() {
		return this.flags;
	}

	// public void setFlagsDecimalValue(int decimalValue) {
	// int binaryLength = flagsLength;
	// int maxValue = (int)
	// PCEPConstantValues.MaxValueFabrication(binaryLength);
	//
	// this.flags = this.setDecimalValue(decimalValue, maxValue, binaryLength);
	// }
	public void setFlagsBinaryString(String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(binaryString, flagsLength);
		this.flags = checkedBinaryString;
	}

	public void setFlagsBinaryString(int startingBit, String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(flags, startingBit, binaryString, flagsLength);
		this.flags = checkedBinaryString;
	}

	/**
	 * lFlag
	 */
	public int getLFlagDecimalValue() {
		int relativeStartBit = (lFlagStartBit - flagsStartBit);
		String flagString = flags.substring(relativeStartBit, relativeStartBit + lFlagLength);
		int decimalValue = (int) PCEPComputationFactory.getDecimalValue(flagString);
		return decimalValue;
	}

	public String getLFlagBinaryString() {
		String binaryString = flags.substring(0, (lFlagStartBit - flagsStartBit) + lFlagLength);
		return binaryString;
	}

	public void setLFlagBinaryString(String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(this.flags, (lFlagStartBit - flagsStartBit), binaryString, flagsLength);
		this.flags = checkedBinaryString;
	}

	public String toString() {
		String excludeAnyInfo = "ExcludeAny=" + this.getExcludeAnyBinaryString();
		String includeAnyInfo = ",IncludeAny=" + this.getIncludeAnyBinaryString();
		String includeAllInfo = ",IncludeAll=" + this.getIncludeAllBinaryString();
		String setupPrioInfo = ",SetupPrio=" + this.getSetupPrioDecimalValue();
		String holdingPrioInfo = ",HoldingPrio=" + this.getHoldingPrioDecimalValue();
		String flagsInfo = ",Flags=" + this.getFlagsBinaryString();
		String reservedInfo = ",Reserved=" + this.getReservedBinaryString();

		String headerInfo = this.getObjectHeader().toString();
		String objectInfo = "<LSPA:" + excludeAnyInfo + includeAnyInfo + includeAllInfo + setupPrioInfo + holdingPrioInfo + flagsInfo + reservedInfo + ">";

		return headerInfo + objectInfo;
	}

	public String binaryInformation() {
		String excludeAnyBinaryInfo = getExcludeAnyBinaryString();
		String includeAnyBinaryInfo = "'" + getIncludeAnyBinaryString();
		String includeAllBinaryInfo = "'" + getIncludeAllBinaryString();
		String setupPrioBinaryInfo = "'" + getSetupPrioBinaryString();
		String holdingPrioBinaryInfo = "'" + getHoldingPrioBinaryString();
		String flagsInfo = "'" + this.getFlagsBinaryString();
		String reservedBinaryInfo = "'" + getReservedBinaryString();

		String headerInfo = this.getObjectHeader().binaryInformation();
		String objectInfo = "[" + excludeAnyBinaryInfo + includeAnyBinaryInfo + includeAllBinaryInfo + setupPrioBinaryInfo + holdingPrioBinaryInfo + flagsInfo + reservedBinaryInfo + "]";

		return headerInfo + objectInfo;
	}

	public String contentInformation() {
		return "[" + NAME + "]";
	}
}
