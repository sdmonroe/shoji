package org.fusuma.to.message;

import javax.crypto.KeyAgreement;
import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.interfaces.DHPublicKey;

import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.Message;

public class DHKeyMaterial extends BaseMessage {

	private javax.crypto.interfaces.DHPublicKey publicKey;
	private javax.crypto.interfaces.DHPrivateKey privateKey;
	private byte[] sharedSecret = new byte[0];
	private byte[] secretHash = new byte[0];
	private KeyAgreement localKeyAgreement;
	private int phase = -1;

	// private BigInteger publicKey = null;

	public DHKeyMaterial(Id from, Id to, DHPrivateKey privateKey, DHPublicKey publicKey, KeyAgreement localKeyAgreement) {
		super(from, to);
		this.privateKey = privateKey;
		this.publicKey = publicKey;
		this.localKeyAgreement = localKeyAgreement;
	}

	@Override
	public int getPriority() {
		return Message.LOW_PRIORITY;
	}

	public DHPublicKey getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(DHPublicKey publicKey) {
		this.publicKey = publicKey;
	}

	public String toString() {
		String s = super.toString() + "\n***********************************************PUBLIC KEY*****************************************************************//";
		s += "\n\nPhase: " + getPhase();
		if (getPublicKey() != null) {
			s += "\n" + "Public Key Format: " + getPublicKey().getFormat() + "\nPublic Key: " + new String(getPublicKey().getEncoded());
			s += "\nModulus: " + getPublicKey().getParams().getP() + "\nExponent: " + getPublicKey().getParams().getG() + "\n\n";
		}
		if (getPrivateKey() != null) {
			s += "\n" + "Private Key Format: " + getPrivateKey().getFormat() + "\nPrivate Key: " + new String(getPrivateKey().getEncoded());
			s += "\nModulus: " + getPrivateKey().getParams().getP() + "\nExponent: " + getPrivateKey().getParams().getG();
		}
		s += "\n" + "Shared Secret: " + new String(getSharedSecret());
		return s;
	}

	public javax.crypto.interfaces.DHPrivateKey getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(javax.crypto.interfaces.DHPrivateKey privateKey) {
		this.privateKey = privateKey;
	}

	public byte[] getSharedSecret() {
		return sharedSecret;
	}

	public void setSharedSecret(byte[] sharedSecret) {
		this.sharedSecret = sharedSecret;
	}

	public DHKeyMaterial extractPublicKey() {
		DHKeyMaterial k = new DHKeyMaterial(from, to, null, getPublicKey(), null);
		k.setPhase(this.getPhase());
		return k;
	}

	public byte[] getSecretHash() {
		return secretHash;
	}

	public void setSecretHash(byte[] secretHash) {
		this.secretHash = secretHash;
	}

	public int getPhase() {
		return phase;
	}

	public void setPhase(int phase) {
		this.phase = phase;
	}

	public KeyAgreement getLocalKeyAgreement() {
		return localKeyAgreement;
	}

	public void setLocalKeyAgreement(KeyAgreement keyAgreement) {
		this.localKeyAgreement = keyAgreement;
	}

}
