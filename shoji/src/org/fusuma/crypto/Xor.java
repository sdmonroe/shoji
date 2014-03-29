package org.fusuma.crypto;

import java.util.Scanner;

import org.apache.log4j.Logger;
import org.fusuma.shoji.globals.Constants;

/**
 * Secret Code VI
 * 
 * If you have played with tutorial Secret Code V (SwapAndRotate) you will have seen as message can now be considered as a big number instead of different digits or even symbols.
 * 
 * Before going to the complete RSA encoding/decoding system let us play with more basic encoding/decoding mechanisms using binary code.
 * 
 * Ok so what's so special with characters on a computer ? It is because characters are represented by a serie of bits. We will stay with plain Ascii for now for simplicity.
 * 
 * Imagine the message "Hello". The Ascii representation of Hello in 8 bits bytes is H e l l o 01001000 01100101 01101100 01101100 01101111
 * 
 * In the Secret Code V tutorial we have seen how to swap and rotate bits in a message.
 * 
 * Now most encryptions rely on the bitwise operator XOR property that says that if b and c are bits fields a = b XOR c a XOR c gives back b and a XOR b gives back c The XOR operator in Java is ^ and can be applied to integer.
 * 
 * Let us verify this assertion with all possible versions of 0 and 1 Message: 1100 Key: 1010 ---- XOR Msg and Key: 0110 this is the encrypted message
 * 
 * Now let's XOR the encrypted message with the key Encrypted message: 0110 Key: 1010 ---- XOR 1100 back to the original message
 * 
 * One big thing about this mechanism is that the process to encode is exactly the same as the one to decode we just XOR with the key both the message to encode and the message to decode. No need for a Encode() method and a Decode() method. The same one is used and the method does not need to know if it is actually encoding or decoding.
 * 
 * In this tutorial we will just play with this XOR feature to encode/decode messages For that we will use a key if the key is smaller than the message we just repeat it So the encode "Hello world" with the key "Dave" we will use as key "DaveDaveDav" The following console application uses this technique As in the previous tutorial we will used the CharAndBits class to output as a series of 0 and 1 the bits contained in a character.
 * 
 * If you have already the CharAndBits.java class/file from the previous tutotial, take this new one, it has new functionnalities. The new version still support the code from tutorial V so you can erase the old one, take the new one, and the code of SwapAndRotate will still work.
 * 
 */
public class Xor {
	static Logger logger = Logger.getLogger(Xor.class);

	// the key used for encrypt/decrypt
	private String key;

	/**
	 * Constructor that receives the key as parameter
	 */
	public Xor(String key) {
		// call common method to set the initial key or change it
		setKey(key);
	}

	/**
	 * Method to set the original key and permit to change it on the fly
	 */
	private void setKey(String key) {
		// avoid null key
		if (key == null) key = "";
		// save it
		this.key = key;

	}

	/**
	 * Method that one-time pad on the message based on the registered key Contrary to other coding mechanisms seen in the previous tutorials the mechanism to encode and decode is the same wo we do not need an encode and a decode method. The same method can be used for both operations
	 */
	public String otp(String msg) {
		// validate that the message is not null or length == 0
		// if it is the case, just return the original message
		if (msg == null || msg.length() == 0) return msg;
		// if the key is "" we return the original message
		if (key.length() == 0) return msg;
		// make an array of CharAndBits from both the message and the key
		CharAndBits[] m = CharAndBits.newCharAndBitsArray(msg);
		CharAndBits[] k = CharAndBits.newCharAndBitsArray(key);
		// and call the method that performs the XOR operation
		String encodeDecodeValue = CharAndBits.xorArray(m, k);
		return encodeDecodeValue;
	}

	/**
	 * A quick and dirty method to return the key duplicated enough times so it will have the length of the message. This is just for printing purpose only both in the main() method and in the GUI. The method is not involved in the encoding/decoding process itself
	 */
	private String dupKey(int msgLen) {
		// if the key is invalid no
		if (key.length() == 0) return "";
		String dup = key;
		while (dup.length() < msgLen)
			dup += key;
		return dup.substring(0, msgLen);
	}

	/**
	 * To test the class
	 */
	public static void main(String[] args) throws Exception {
		Constants.configureLogger();

		// -------- unit tests to see that the whole thing works --------
		String msg = "DreamInCode";
		String key = "dave";
		// create the Xor object
		Xor xor = new Xor(key);

		// for print out purpose only get the key used (it will be as log as the message)
		String dupKey = xor.dupKey(msg.length());
		logger.info("The original message is: \"" + msg + "\" the key used will be \"" + dupKey + "\"");
		// call the utility method for binary representation of the message
		String msgInBin = CharAndBits.toBinaryString(msg);
		logger.info(msgInBin);

		// the repeated key in binary
		String keyInBin = CharAndBits.toBinaryString(dupKey);
		logger.info(keyInBin);
		// build a series of -------
		char[] dash = new char[msgInBin.length()];
		for (int i = 0; i < msgInBin.length(); i++)
			dash[i] = '-';
		// print the serie of ------
		logger.info(new String(dash));

		// encode the message which is the result of the XOR
		String encoded = xor.otp(msg);
		// display the encoded bits
		String encodedInBinary = CharAndBits.toBinaryString(encoded);
		logger.info(encodedInBinary);
		// display what is printable out of it
		logger.info("The encrypted message is: \"" + CharAndBits.toAsciiString(encoded) + "\"");

		// now the reverse process
		logger.info("The encoded message XORed with the key");
		logger.info(encodedInBinary); // encoded message
		logger.info(keyInBin); // key in binary
		logger.info(new String(dash)); // the ------------

		// decode the encoded message calling the SAME method
		String decoded = xor.otp(encoded);
		// display the decoded message
		logger.info(CharAndBits.toBinaryString(decoded));
		logger.info("The decoded message is \"" + decoded + "\" is it the same as \"" + msg + "\": " + msg.equals(decoded));
		// ----------------------------- end of unit tests ---------------------------------

		// Now prompting the user
		Scanner scan = new Scanner(System.in);
		String userKey;
		// get a key of length > 0 from the user
		do {
			logger.info("Enter the key to use: ");
			userKey = scan.nextLine();
		}
		while (userKey.length() == 0);

		// build the Xor object
		Xor userXor = new Xor(userKey);
		// get the message to encode/decode
		logger.info("Enter message to encode: ");
		String userMsg = scan.nextLine();

		// generate the key that will be used for print out purpose only
		String userDupKey = userXor.dupKey(userMsg.length());
		logger.info("The original message is: \"" + userMsg + "\" the key used will be \"" + userDupKey + "\"");
		// call the utility method for binary representation of the message
		String userMsgInBin = CharAndBits.toBinaryString(userMsg);
		logger.info(userMsgInBin);

		// the repeated key in binary
		String userKeyInBin = CharAndBits.toBinaryString(userDupKey);
		logger.info(userKeyInBin);
		// build a series of -------
		char[] userDash = new char[userMsgInBin.length()];
		for (int i = 0; i < userMsgInBin.length(); i++)
			userDash[i] = '-';
		// print the serie of ------
		logger.info(new String(userDash));

		// encode the message which is the result of the XOR
		String userEncoded = userXor.otp(userMsg);
		// display the encoded bits
		String userEncodedInBinary = CharAndBits.toBinaryString(userEncoded);
		logger.info(userEncodedInBinary);
		// display what is printable out of it
		logger.info("The encrypted message is: \"" + CharAndBits.toAsciiString(userEncoded) + "\"");

		// now the reverse process
		logger.info("The encoded message XORed with the key");
		logger.info(userEncodedInBinary); // encoded message
		logger.info(userKeyInBin); // key in binary
		logger.info(new String(userDash)); // the ------------

		// decode the encoded message calling the SAME method
		String userDecoded = userXor.otp(userEncoded);
		// display the decoded message
		logger.info(CharAndBits.toBinaryString(userDecoded));
		logger.info("The decoded message is \"" + userDecoded + "\" is it the same as \"" + userMsg + "\": " + userMsg.equals(userDecoded));

	}

}
