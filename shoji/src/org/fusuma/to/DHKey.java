package org.fusuma.to;

import java.math.BigInteger;
import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.PublicKey;

import javax.crypto.spec.DHParameterSpec;

import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.Message;

public class DHKey extends BaseTo {

	private BigInteger generator = null;
	private BigInteger modulus = null;
	private PublicKey publicKey = null;

	// private BigInteger publicKey = null;

	public DHKey(Id from, Id to, BigInteger modulus, BigInteger generator) {
		super(from, to);
		this.generator = generator;
		this.modulus = modulus;
	}

	public DHKey(Id from, Id to) {
		super(from, to);
		try {
			AlgorithmParameterGenerator paramGen = AlgorithmParameterGenerator.getInstance("DH");
			paramGen.init(512); // number of bits
			AlgorithmParameters params = paramGen.generateParameters();
			DHParameterSpec dhSpec = (DHParameterSpec) params.getParameterSpec(DHParameterSpec.class);

			setModulus(dhSpec.getP());
			setGenerator(dhSpec.getG());
		}
		catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
	}

	public BigInteger getGenerator() {
		return this.generator;
	}

	private void setGenerator(BigInteger g512) {
		this.generator = g512;
	}

	public BigInteger getModulus() {
		return modulus;
	}

	private void setModulus(BigInteger p512) {
		this.modulus = p512;
	}

	@Override
	public int getPriority() {
		return Message.LOW_PRIORITY;
	}

	public PublicKey getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(PublicKey publicKey) {
		this.publicKey = publicKey;
	}

	public String toString() {
		String s = "\n\n\n\n\n\n\n\n***********************************************PUBLIC KEY*****************************************************************//";
		s += "\nModulus: " + getModulus() + "\nExponent: " + getGenerator() + "\n" + "Public Key Format: " + getPublicKey().getFormat() + "\nPublic Key: " + new String(getPublicKey().getEncoded());
		return s;
	}

}
