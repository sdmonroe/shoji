package org.fusuma.to.message;

import rice.p2p.commonapi.Id;

public class PublicKey extends ScribeMessage implements java.security.PublicKey {

	private String format;
	private String algorithm;

	public PublicKey(Id from, java.security.PublicKey key) {
		super(from);
		this.data = key.getEncoded();
		this.format = key.getFormat();
		this.algorithm = key.getAlgorithm();
	}

	@Override
	public int getPriority() {
		return super.getPriority();
	}

	@Override
	public String getAlgorithm() {
		return this.algorithm;
	}

	@Override
	public String getFormat() {
		return this.format;
	}

	@Override
	public byte[] getEncoded() {
		return (byte[]) getData();
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	public String toString() {

		String s = super.toString();
		s += "\n\n<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< PUBLIC KEY >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n";
		s += "\nFormat: " + getFormat();
		s += "\nAlgorithm: " + getAlgorithm();
		s += "\nKey: " + new String(getEncoded());
		return s;

	}

}
