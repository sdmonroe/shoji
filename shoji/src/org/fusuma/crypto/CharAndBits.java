package org.fusuma.crypto;

/**
 * A class for easy representation of all ASCII character on the console and on GUIs. This class contains the binary representation of the char it represents If the char is prinatble it returns its ASCII representation else it returns '.'
 */
public class CharAndBits implements Comparable<CharAndBits> {

	// the character by itself
	private char theChar;
	// the char to display (will be '.' if not printable
	private char toAscii;
	// it's int value used for bitwise operation
	private int intValue;
	// its 8 bits representation as a String
	private String binaryStr;
	// its 8 bits representation as 8 char containing '0' or '1'
	private char[] binaryChar;
	// its 8 bits representation as 8 int containing 0 or 1
	private int[] binaryInt;

	/**
	 * Constructor that receives the char as parameter
	 */
	public CharAndBits(char theChar) {
		// save it
		this.theChar = theChar;
		// test if it is printable if it is the case use it else use '.'
		if (isPrintable()) toAscii = theChar;
		else toAscii = '.';
		// get it's int value
		intValue = theChar;
		intValue &= 0xFF; // ok we just support the 255 Ascii characters
		// convert to binary char and int
		binaryChar = new char[8];
		binaryInt = new int[8];
		int temp = intValue;
		for (int i = 7; i >= 0; i--) {
			binaryInt[i] = temp & 1;
			binaryChar[i] = (char) binaryInt[i];
			binaryChar[i] += '0';
			temp >>>= 1;
		}
		// and the whole String
		binaryStr = new String(binaryChar);
	}

	/**
	 * return true of false depending if the char is printable (GUI or console)
	 */
	public boolean isPrintable() {
		return !Character.isISOControl(theChar);
	}

	/**
	 * Returns the binary representation of this char
	 */
	public String toBinaryString() {
		return binaryStr;
	}

	/**
	 * returns a printable version of an encoded String
	 */
	public static String toAsciiString(String encoded) {
		// convert the String to an array of CharAndBits
		CharAndBits[] array = newCharAndBitsArray(encoded);
		// prepare an array of char[] of the same length
		char[] digit = new char[encoded.length()];
		// get the printable version of every char
		for (int i = 0; i < encoded.length(); i++)
			digit[i] = array[i].toAscii;
		// return a String out of it
		return new String(digit);
	}

	/**
	 * Get the printable version of this char
	 */
	public char getPrintableChar() {
		return toAscii;
	}

	/**
	 * Returns the int value of this char
	 */
	public int getIntValue() {
		return intValue;
	}

	/**
	 * Getter for the binary char
	 */
	public char[] getBinaryChar() {
		return binaryChar;
	}

	/**
	 * To test 2 EasyCharacter for equality
	 */
	public boolean equals(CharAndBits other) {
		return compareTo(other) == 0;
	}

	/**
	 * Used to sort an array of EasyCharacter
	 */
	public int compareTo(CharAndBits other) {
		return intValue - other.intValue;
	}

	/**
	 * A static method to get an array of EasyCharacter from a String (so the caller does not have to perform the loop himself)
	 */
	public static CharAndBits[] newCharAndBitsArray(String str) {
		// if str is null return a 0 length array of EasyCharacter
		if (str == null) return new CharAndBits[0];
		// convert String received as parameter as an array of char
		char[] digit = str.toCharArray();
		CharAndBits[] array = new CharAndBits[digit.length];
		for (int i = 0; i < digit.length; i++)
			array[i] = new CharAndBits(digit[i]);
		return array;
	}

	/**
	 * A static method to get a printable String from an array of EasyCharacter[] to display at the console or in a GUI
	 */
	public static String getMsgString(CharAndBits[] array) {
		StringBuilder sb = new StringBuilder(array.length);
		// copy our EasyCharacter into the buffer
		for (CharAndBits ea : array)
			sb.append(ea.toAscii); // printable version
		// return the StringBuilder as a String
		return sb.toString();
	}

	/**
	 * To perform the Xor between 2 arrays of EasyCharcter and return an String with the 2 arrays XORed The first parameter is the message, the second the keys The array returned will have the size of the message if the keys is smaller than the message a wrapAround will occur
	 */
	public static String xorArray(CharAndBits[] msg, CharAndBits[] key) {
		// check for null or no length message
		if (msg == null || msg.length == 0) return ""; // return String of 0 length
		// create the digit to hold the xored value
		int msgLen = msg.length;
		StringBuilder sb = new StringBuilder(msgLen);

		// check for null or empty keys in that case just return a copy of our message
		if (key == null || key.length == 0) {
			for (CharAndBits ea : msg)
				sb.append(ea.theChar);
			return sb.toString();
		}
		// get length of the keys and create new array
		int keyLen = key.length;
		// loop to perform the XOR between each element of the data array and the keys with wrap around
		for (int i = 0; i < msgLen; i++) {
			int val = msg[i].intValue ^ key[i % keyLen].intValue;
			sb.append((char) val);
		}
		// convert the StringBuilder array to a String
		return sb.toString();
	}

	/**
	 * static methods that return a String representation in binary of an array of char
	 */
	public static String toBinaryString(String str) {
		return toBinaryString(str.toCharArray());
	}

	public static String toBinaryString(char[] digit) {
		// use a StringBuilder to append the binary represenation
		StringBuilder sb = new StringBuilder(digit.length * 9); // * 9 for the blank space
		for (char c : digit) {
			CharAndBits ea = new CharAndBits(c);
			// append the 01010101010
			sb.append(ea.toBinaryString());
			sb.append(' ');
		}
		// return as a String
		return sb.toString();
	}
}
