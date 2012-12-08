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
import com.pcee.protocol.message.objectframe.PCEPObjectFrameFactory;

/**
 * <pre>
 *  0                   1                   2                   3
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                          Flags                    |O|B|R| Pri |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                        Request-ID-number                      |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                                                               |
 * //                      Optional TLVs                          //
 * |                                                               |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * </pre>
 */

public class PCEPRequestParametersObject implements PCEPObjectFrame {

	private final String NAME = "Request Parameters";

	private String requestIDNumber;
	private String flags;

	private PCEPCommonObjectHeader objectHeader;
	// private LinkedList<PCEPTLVObject> tlvList;

	private int flagsStartBit = PCEPConstantValues.REQUEST_PARAMETERS_OBJECT_FLAGS_START_BIT;
	private int flagsEndBit = PCEPConstantValues.REQUEST_PARAMETERS_OBJECT_FLAGS_END_BIT;
	private int flagsLength = PCEPConstantValues.REQUEST_PARAMETERS_OBJECT_FLAGS_LENGTH;

	private int oFlagStartBit = PCEPConstantValues.REQUEST_PARAMETERS_OBJECT_FLAG_O_START_BIT;
	private int oFlagEndBit = PCEPConstantValues.REQUEST_PARAMETERS_OBJECT_FLAG_O_END_BIT;
	private int oFlagLength = PCEPConstantValues.REQUEST_PARAMETERS_OBJECT_FLAG_O_LENGTH;

	private int bFlagStartBit = PCEPConstantValues.REQUEST_PARAMETERS_OBJECT_FLAG_B_START_BIT;
	private int bFlagEndBit = PCEPConstantValues.REQUEST_PARAMETERS_OBJECT_FLAG_B_END_BIT;
	private int bFlagLength = PCEPConstantValues.REQUEST_PARAMETERS_OBJECT_FLAG_B_LENGTH;

	private int rFlagStartBit = PCEPConstantValues.REQUEST_PARAMETERS_OBJECT_FLAG_R_START_BIT;
	private int rFlagEndBit = PCEPConstantValues.REQUEST_PARAMETERS_OBJECT_FLAG_R_END_BIT;
	private int rFlagLength = PCEPConstantValues.REQUEST_PARAMETERS_OBJECT_FLAG_R_LENGTH;

	private int priFlagStartBit = PCEPConstantValues.REQUEST_PARAMETERS_OBJECT_FLAG_PRI_START_BIT;
	private int priFlagEndBit = PCEPConstantValues.REQUEST_PARAMETERS_OBJECT_FLAG_PRI_END_BIT;
	private int priFlagLength = PCEPConstantValues.REQUEST_PARAMETERS_OBJECT_FLAG_PRI_LENGTH;

	private int requestIDNumberStartBit = PCEPConstantValues.REQUEST_PARAMETERS_OBJECT_REQUEST_ID_NUMBER_START_BIT;
	private int requestIDNumberEndBit = PCEPConstantValues.REQUEST_PARAMETERS_OBJECT_REQUEST_ID_NUMBER_END_BIT;
	private int requestIDNumberLength = PCEPConstantValues.REQUEST_PARAMETERS_OBJECT_REQUEST_ID_NUMBER_LENGTH;

	public PCEPRequestParametersObject(PCEPCommonObjectHeader objectHeader, String binaryString) {
		this.setObjectHeader(objectHeader);
		this.setObjectBinaryString(binaryString);
		this.updateHeaderLength();
	}

	public PCEPRequestParametersObject(PCEPCommonObjectHeader objectHeader, String oFlag, String bFlag, String rFlag, String priFlag, String requestIDNumber) {
		this.setObjectHeader(objectHeader);
		this.setFlagsBinaryString(PCEPComputationFactory.generateZeroString(flagsLength));
		this.setOFlagBinaryString(oFlag);
		this.setBFlagBinaryString(bFlag);
		this.setRFlagBinaryString(rFlag);
		this.setPriFlagBinaryString(priFlag);
		this.setRequestIDNumberDecimalValue(Integer.parseInt(requestIDNumber));
		this.updateHeaderLength();
	}

	public static void main (String [] args){
	    PCEPRequestParametersObject RP = PCEPObjectFrameFactory.generatePCEPRequestParametersObject("1", "0", "1", "0", "0", "1", "32");
	    System.out.println(RP.getRequestIDNumberDecimalValue());
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
		String binaryString = flags + requestIDNumber;
		return binaryString;
	}

	public void setObjectBinaryString(String binaryString) {
		String flagsBinaryString = binaryString.substring(flagsStartBit, flagsEndBit + 1);
		String oFlagBinaryString = binaryString.substring(oFlagStartBit, oFlagEndBit + 1);
		String bFlagBinaryString = binaryString.substring(bFlagStartBit, bFlagEndBit + 1);
		String rFlagBinaryString = binaryString.substring(rFlagStartBit, rFlagEndBit + 1);
		String priFlagBinaryString = binaryString.substring(priFlagStartBit, priFlagEndBit + 1);
		String requestIDNumberBinaryString = binaryString.substring(requestIDNumberStartBit, requestIDNumberEndBit + 1);

		this.setFlagsBinaryString(flagsBinaryString);
		this.setOFlagBinaryString(oFlagBinaryString);
		this.setBFlagBinaryString(bFlagBinaryString);
		this.setRFlagBinaryString(rFlagBinaryString);
		this.setPriFlagBinaryString(priFlagBinaryString);
		this.setRequestIDNumberBinaryString(requestIDNumberBinaryString);
	}

	public int getObjectFrameByteLength() {
		int objectLength = flags.length() + requestIDNumber.length();
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
	 * requestIDNumber
	 */
	public int getRequestIDNumberDecimalValue() {
		int decimalValue = (int) PCEPComputationFactory.getDecimalValue(requestIDNumber);
		return decimalValue;
	}

	public String getRequestIDNumberBinaryString() {
		return this.requestIDNumber;
	}

	public void setRequestIDNumberDecimalValue(int decimalValue) {
		int binaryLength = requestIDNumberLength;
		int maxValue = (int) PCEPComputationFactory.MaxValueFabrication(binaryLength-1);

		this.requestIDNumber = PCEPComputationFactory.setDecimalValue(decimalValue, maxValue, binaryLength);
	}

	public void setRequestIDNumberBinaryString(String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(binaryString, requestIDNumberLength);
		this.requestIDNumber = checkedBinaryString;
	}

	public void setRequestIDNumberBinaryString(int startingBit, String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(requestIDNumber, startingBit, binaryString, requestIDNumberLength);
		this.requestIDNumber = checkedBinaryString;
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
	 * oFlag
	 */

	public int getOFlagDecimalValue() {
		int relativeStartBit = (oFlagStartBit - flagsStartBit);
		String flagString = flags.substring(relativeStartBit, relativeStartBit + oFlagLength);
		int decimalValue = (int) PCEPComputationFactory.getDecimalValue(flagString);
		return decimalValue;
	}

	public String getOFlagBinaryString() {
		String binaryString = flags.substring(0, (oFlagStartBit - flagsStartBit) + oFlagLength);
		return binaryString;
	}

	public void setOFlagBinaryString(String binaryString) {
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(this.flags, (oFlagStartBit - flagsStartBit), binaryString, flagsLength);
		this.flags = checkedBinaryString;
	}

	/**
	 * bFlag
	 */
	public int getBFlagDecimalValue() {
		int relativeStartBit = (bFlagStartBit - flagsStartBit);
		String flagString = flags.substring(relativeStartBit, relativeStartBit + bFlagLength);
		int decimalValue = (int) PCEPComputationFactory.getDecimalValue(flagString);
		return decimalValue;
	}

	public String getBFlagBinaryString() {
		int relativeStartBit = (bFlagStartBit - flagsStartBit);
		String binaryString = flags.substring(relativeStartBit, relativeStartBit + bFlagLength);
		return binaryString;
	}

	public void setBFlagBinaryString(String binaryString) {
		int relativeStartBit = (bFlagStartBit - flagsStartBit);
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(flags, relativeStartBit, binaryString, flagsLength);
		this.flags = checkedBinaryString;
	}

	/**
	 * rFlag
	 */
	public int getRFlagDecimalValue() {
		int relativeStartBit = (rFlagStartBit - flagsStartBit);
		String flagString = flags.substring(relativeStartBit, relativeStartBit + rFlagLength);
		int decimalValue = (int) PCEPComputationFactory.getDecimalValue(flagString);
		return decimalValue;
	}

	public String getRFlagBinaryString() {
		int relativeStartBit = (rFlagStartBit - flagsStartBit);
		String binaryString = flags.substring(relativeStartBit, relativeStartBit + rFlagLength);
		return binaryString;
	}

	public void setRFlagBinaryString(String binaryString) {
		int relativeStartBit = (rFlagStartBit - flagsStartBit);
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(flags, relativeStartBit, binaryString, flagsLength);
		this.flags = checkedBinaryString;
	}

	/**
	 * priFlag
	 */
	public int getPriFlagDecimalValue() {
		int relativeStartBit = (priFlagStartBit - flagsStartBit);
		String flagString = flags.substring(relativeStartBit, relativeStartBit + priFlagLength);
		int decimalValue = (int) PCEPComputationFactory.getDecimalValue(flagString);
		return decimalValue;
	}

	public String getPriFlagBinaryString() {
		int relativeStartBit = (priFlagStartBit - flagsStartBit);
		String binaryString = flags.substring(relativeStartBit, relativeStartBit + priFlagLength);
		return binaryString;
	}

	public void setPriFlagBinaryString(String binaryString) {
		int relativeStartBit = (priFlagStartBit - flagsStartBit);
		String checkedBinaryString = PCEPComputationFactory.setBinaryString(flags, relativeStartBit, binaryString, flagsLength);
		this.flags = checkedBinaryString;
	}

	public String toString() {
		String flagsInfo = "Flags=" + this.getFlagsBinaryString();
		String requestIDNumberInfo = ",RequestIDNumber=" + this.getRequestIDNumberBinaryString();

		String headerInfo = this.getObjectHeader().toString();
		String objectInfo = NAME + ":" + flagsInfo + requestIDNumberInfo + ">";

		return headerInfo + objectInfo;
	}

	public String binaryInformation() {
		String flagsInfo = this.getFlagsBinaryString();
		String requestIDNumberBinaryInfo = "'" + getRequestIDNumberBinaryString();

		String headerInfo = this.getObjectHeader().binaryInformation();
		String objectInfo = "[" + flagsInfo + requestIDNumberBinaryInfo + "]";

		return headerInfo + objectInfo;
	}

	public String contentInformation() {
		return "[" + NAME + "]";
	}

}
