package org.fusuma.to;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.KeyAgreement;

import rice.p2p.commonapi.Id;

public class DHPrivateKey extends DHKey {

	// private KeyAgreement keyAgreement;
	private javax.crypto.interfaces.DHPrivateKey privateKey;
	private byte[] sharedSecret;

	public DHPrivateKey(Id from, Id to, javax.crypto.interfaces.DHPrivateKey privateKey) {
		super(from, to, privateKey.getParams().getP(), privateKey.getParams().getG());
		this.privateKey = privateKey;
	}

	// public DHPrivateKey(KeyAgreement keyAgreement, KeyPair keyPair) {
	// }

	// public KeyAgreement getKeyAgreement() {
	// return keyAgreement;
	// }
	//
	// public void setKeyAgreement(KeyAgreement keyAgreement) {
	// this.keyAgreement = keyAgreement;
	// }

	public javax.crypto.interfaces.DHPrivateKey getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(javax.crypto.interfaces.DHPrivateKey privateKey) {
		this.privateKey = privateKey;
	}

	public byte[] getSharedSecretHash() {
		MessageDigest hash;
		try {
			hash = MessageDigest.getInstance("SHA1", "BC");
			KeyAgreement ka = KeyAgreement.getInstance("DH", "BC");
			ka.init(getPrivateKey());
			return hash.digest(ka.generateSecret());
		}
		catch (InvalidKeyException e) {
			logger.error(e.getMessage(), e);
		}
		catch (NoSuchAlgorithmException e) {
			logger.error(e.getMessage(), e);
		}
		catch (NoSuchProviderException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	public byte[] getSharedSecret() {
		return sharedSecret;
	}

	public void setSharedSecret(byte[] sharedSecret) {
		this.sharedSecret = sharedSecret;
	}

	public DHKey extractPublicKey() {
		DHKey dhkey = new DHKey(this.getFrom(), this.getTo(), this.getModulus(), this.getGenerator());
		dhkey.setData(this.getData());
		dhkey.setPublicKey(this.getPublicKey());
		return dhkey;
	}

	public String toString() {
		String s = super.toString();
		s += "\t\t\n\n***********************************************PRIVATE KEY PART*****************************************************************//";
		s += "\t\t\nShared Secret: " + getSharedSecret() + "\t\t\nPrivate Key Format: " + getPrivateKey().getFormat() + "\t\t\nPrivate Key: " + getPrivateKey();
		return s;
	}

}
