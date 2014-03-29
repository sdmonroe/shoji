package org.fusuma.crypto;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;

import javax.crypto.KeyAgreement;
import javax.crypto.spec.DHParameterSpec;

import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.fusuma.shoji.globals.Constants;
import org.fusuma.to.DHKey;
import org.fusuma.to.DHPrivateKey;

import rice.p2p.commonapi.Id;

public class DiffieHellman {
	static {
		Security.addProvider(new BouncyCastleProvider());
	}
	static Logger logger = Logger.getLogger(DiffieHellman.class);

	public static void main(String[] args) throws Exception {
		Constants.configureLogger();
		DiffieHellman dh = new DiffieHellman();
		DiffieHellman.generatePrivateKey();
	}

	public static DHKey createKey(Id from, Id to) {
		DHKey k = new DHKey(from, to);
		try {
			DHParameterSpec dhParams = new DHParameterSpec(k.getModulus(), k.getGenerator());
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DH", "BC");

			keyGen.initialize(dhParams, new SecureRandom());

			// KeyAgreement aKeyAgree = KeyAgreement.getInstance("DH", "BC");
			KeyPair aPair = keyGen.generateKeyPair();

			k.setPublicKey(aPair.getPublic());
		}
		catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
		return k;
	}

	public static DHPrivateKey generatePrivateKey() {
		try {
			DHKey publicKeyMaterial = new DHKey(null, null);

			DHParameterSpec dhParams = new DHParameterSpec(publicKeyMaterial.getModulus(), publicKeyMaterial.getGenerator());
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DH", "BC");

			keyGen.initialize(dhParams);

			// KeyAgreement aKeyAgree = KeyAgreement.getInstance("DH", "BC");
			KeyPair aPair = keyGen.generateKeyPair();

			// KeyAgreement bKeyAgree = KeyAgreement.getInstance("DH", "BC");
			// KeyPair bPair = keyGen.generateKeyPair();

			// aKeyAgree.init(aPair.getPrivate());
			// bKeyAgree.init(bPair.getPrivate());

			// aKeyAgree.doPhase(partnerKeyMaterial.getPublicKey(), true);
			// // bKeyAgree.doPhase(aPair.getPublic(), true);
			//
			// MessageDigest hash = MessageDigest.getInstance("SHA1", "BC");
			// // logger.info(new String(hash.digest(aKeyAgree.generateSecret())));
			// // logger.info("Format: " + aPair.getPublic().getFormat());
			// String key = new String(aPair.getPublic().getEncoded());
			// // logger.info("Key: " + key);
			//
			// // byte[] certData = aPair.getPublic().getEncoded();
			// // X509Certificate cert = X509Certificate.getInstance(certData);
			//
			javax.crypto.interfaces.DHPrivateKey pk = (javax.crypto.interfaces.DHPrivateKey) aPair.getPrivate();
			DHPrivateKey k = new DHPrivateKey(null, null, pk);
			k.setPublicKey(aPair.getPublic());
			// k.setSharedSecret(new String(hash.digest(aKeyAgree.generateSecret())));
			return k;
		}
		catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}

		return null;
	}

	public static DHPrivateKey generatePrivateKey(DHKey partnerKeyMaterial) {
		try {
			DHParameterSpec dhParams = new DHParameterSpec(partnerKeyMaterial.getModulus(), partnerKeyMaterial.getGenerator());
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DH", "BC");

			keyGen.initialize(dhParams);

			KeyAgreement aKeyAgree = KeyAgreement.getInstance("DH", "BC");
			KeyPair aPair = keyGen.generateKeyPair();

			// KeyAgreement bKeyAgree = KeyAgreement.getInstance("DH", "BC");
			// KeyPair bPair = keyGen.generateKeyPair();

			javax.crypto.interfaces.DHPrivateKey pk = (javax.crypto.interfaces.DHPrivateKey) aPair.getPrivate();
			aKeyAgree.init(pk);
			// bKeyAgree.init(bPair.getPrivate());

			boolean match = dhParams.getG().equals(pk.getParams().getG()) && dhParams.getP().equals(pk.getParams().getP());
			logger.info("Components match: " + match);
			aKeyAgree.doPhase(partnerKeyMaterial.getPublicKey(), false);

			// bKeyAgree.doPhase(aPair.getPublic(), true);

			// MessageDigest hash = MessageDigest.getInstance("SHA1", "BC");
			// logger.info(new String(hash.digest(aKeyAgree.generateSecret())));
			// logger.info("Format: " + aPair.getPublic().getFormat());
			// String key = new String(aPair.getPublic().getEncoded());
			DHPrivateKey privateKey = new DHPrivateKey(null, null, pk);
			return generateSharedSecret(privateKey, partnerKeyMaterial);
		}
		catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}

		return null;
	}

	public static DHPrivateKey generateSharedSecret(DHPrivateKey privateKey, DHKey partnerKeyMaterial) {

		try {
			// DHKey key = new DHKey();

			// logger.info("Key: " + key);

			// byte[] certData = aPair.getPublic().getEncoded();
			// X509Certificate cert = X509Certificate.getInstance(certData);

			privateKey.setPublicKey(partnerKeyMaterial.getPublicKey());
			KeyAgreement aKeyAgree = KeyAgreement.getInstance("DH", "BC");
			aKeyAgree.init(privateKey.getPrivateKey());
			aKeyAgree.doPhase(partnerKeyMaterial.getPublicKey(), true);
			privateKey.setSharedSecret(aKeyAgree.generateSecret());
			return privateKey;
		}
		catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}

		return null;

		// PemReader pemReader = new PemReader(new StringReader(key));
		// Object obj = pemReader.readPemObject();
		// pemReader.close();
		// if (obj instanceof X509Certificate) {
		// // Just in case your file contains in fact an X.509 certificate,
		// // useless otherwise.
		// obj = ((X509Certificate) obj).getPublicKey();
		// }
		// if (obj instanceof RSAPublicKey) {
		// // ... use the getters to get the BigIntegers.
		// }
		// // logger.info(new String(hash.digest(bKeyAgree.generateSecret())));
	}
}
