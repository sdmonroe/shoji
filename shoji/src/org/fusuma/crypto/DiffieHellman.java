package org.fusuma.crypto;

import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.Security;

import javax.crypto.KeyAgreement;
import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;

import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.fusuma.shoji.globals.Constants;

public class DiffieHellman {
	static {
		Security.addProvider(new BouncyCastleProvider());
	}
	static Logger logger = Logger.getLogger(DiffieHellman.class);

	public static void main(String[] args) throws Exception {
		Constants.configureLogger();
		DiffieHellman dh = new DiffieHellman();
		// DiffieHellman.generatePrivateKey(null, null);
	}

	// public static DHPrivateKey generatePrivateKey(Id from, Id to) {
	// DHPrivateKey k = new DHPrivateKey(from, to);
	// try {
	// DHParameterSpec dhParams = new DHParameterSpec(k.getModulus(), k.getGenerator());
	// KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DH", "BC");
	//
	// keyGen.initialize(dhParams);
	//
	// // KeyAgreement aKeyAgree = KeyAgreement.getInstance("DH", "BC");
	// KeyPair aPair = keyGen.generateKeyPair();
	//
	// k.setPublicKey((javax.crypto.interfaces.DHPublicKey) aPair.getPublic());
	// k.setPrivateKey((javax.crypto.interfaces.DHPrivateKey) aPair.getPrivate());
	// }
	// catch (Exception ex) {
	// logger.error(ex.getMessage(), ex);
	// }
	// return k;
	// }

	// public static DHPrivateKey generatePrivateKey(Id from, Id to) {
	// try {
	// DHPrivateKey keyMaterial = createKey(from, to);
	//
	// DHParameterSpec dhParams = new DHParameterSpec(keyMaterial.getModulus(), keyMaterial.getGenerator());
	// KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DH", "BC");
	//
	// keyGen.initialize(dhParams);
	//
	// // KeyAgreement aKeyAgree = KeyAgreement.getInstance("DH", "BC");
	// KeyPair aPair = keyGen.generateKeyPair();
	//
	// // KeyAgreement bKeyAgree = KeyAgreement.getInstance("DH", "BC");
	// // KeyPair bPair = keyGen.generateKeyPair();
	//
	// // aKeyAgree.init(aPair.getPrivate());
	// // bKeyAgree.init(bPair.getPrivate());
	//
	// // aKeyAgree.doPhase(partnerKeyMaterial.getPublicKey(), true);
	// // // bKeyAgree.doPhase(aPair.getPublic(), true);
	// //
	// // MessageDigest hash = MessageDigest.getInstance("SHA1", "BC");
	// // // logger.info(new String(hash.digest(aKeyAgree.generateSecret())));
	// // // logger.info("Format: " + aPair.getPublic().getFormat());
	// // String keys = new String(aPair.getPublic().getEncoded());
	// // // logger.info("Key: " + keys);
	// //
	// // // byte[] certData = aPair.getPublic().getEncoded();
	// // // X509Certificate cert = X509Certificate.getInstance(certData);
	// //
	// keyMaterial.setPrivateKey((javax.crypto.interfaces.DHPrivateKey) aPair.getPrivate());
	// // keyMaterial.setPublicKey((javax.crypto.interfaces.DHPublicKey) aPair.getPublic());
	// // k.setSharedSecret(new String(hash.digest(aKeyAgree.generateSecret())));
	// return keyMaterial;
	// }
	// catch (Exception ex) {
	// logger.error(ex.getMessage(), ex);
	// }
	//
	// return null;
	// }

	// public static DHPrivateKey generatePrivateKey(DHKeyMaterial partnerKeyMaterial) {
	// try {
	// DHParameterSpec dhParams = new DHParameterSpec(partnerKeyMaterial.getModulus(), partnerKeyMaterial.getGenerator());
	// KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DH", "BC");
	//
	// keyGen.initialize(dhParams);
	//
	// KeyAgreement aKeyAgree = KeyAgreement.getInstance("DH", "BC");
	// KeyPair aPair = keyGen.generateKeyPair();
	//
	// // KeyAgreement bKeyAgree = KeyAgreement.getInstance("DH", "BC");
	// // KeyPair bPair = keyGen.generateKeyPair();
	//
	// Key pk = (javax.crypto.interfaces.DHPrivateKey) aPair.getPrivate();
	// aKeyAgree.init(pk);
	// // bKeyAgree.init(bPair.getPrivate());
	//
	// // boolean match = dhParams.getG().equals(pk.getParams().getG()) && dhParams.getP().equals(pk.getParams().getP());
	// // logger.info("Components match: " + match);
	// DHPublicKey pub = (DHPublicKey) aKeyAgree.doPhase(partnerKeyMaterial.getPublicKey(), false);
	//
	// // bKeyAgree.doPhase(aPair.getPublic(), true);
	//
	// // MessageDigest hash = MessageDigest.getInstance("SHA1", "BC");
	// // logger.info(new String(hash.digest(aKeyAgree.generateSecret())));
	// // logger.info("Format: " + aPair.getPublic().getFormat());
	// // String keys = new String(aPair.getPublic().getEncoded());
	// DHPrivateKey keys = null;
	// // if (pk instanceof org.bouncycastle.jcajce.provider.asymmetric.dh.BCDHPrivateKey) keys = new DHPrivateKey(partnerKeyMaterial.getTo(), partnerKeyMaterial.getFrom(), (org.bouncycastle.jcajce.provider.asymmetric.dh.BCDHPrivateKey) pk);
	// // else if (pk instanceof javax.crypto.interfaces.DHPrivateKey)
	// keys = new DHPrivateKey(partnerKeyMaterial.getTo(), partnerKeyMaterial.getFrom(), (javax.crypto.interfaces.DHPrivateKey) pk); // send this keys back to the partner
	// keys.updateKeyMaterial(pub);
	// return generateSharedSecret(keys, partnerKeyMaterial);
	// }
	// catch (Exception ex) {
	// logger.error(ex.getMessage(), ex);
	// }
	//
	// return null;
	// }
	//
	// public static Object[] generateKeyMaterial(javax.crypto.interfaces.DHPublicKey partnerKey, javax.crypto.interfaces.DHPrivateKey keys) {
	// try {
	// // KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DH", "BC");
	// KeyAgreement aKeyAgree = KeyAgreement.getInstance("DH", "BC");
	// // KeyPair aPair = keyGen.generateKeyPair();
	// aKeyAgree.init(keys);
	// // bKeyAgree.init(bPair.getPrivate());
	//
	// // boolean match = dhParams.getG().equals(pk.getParams().getG()) && dhParams.getP().equals(pk.getParams().getP());
	// // logger.info("Components match: " + match);
	// DHPublicKey pub = (DHPublicKey) aKeyAgree.doPhase(partnerKey, false);
	// return new Object[] { keys, pub, generateSharedSecret(partnerKey, keys, false) };
	// }
	// catch (Exception ex) {
	// logger.error(ex.getMessage(), ex);
	// }
	// return new Object[0];
	// }

	public static Object[] generatePhase2Material(DHPublicKey partnerKey) {
		try {
			KeyAgreement localSync = KeyAgreement.getInstance("DH", "BC");
			KeyPairGenerator gen = KeyPairGenerator.getInstance("DH", "BC");
			gen.initialize(partnerKey.getParams());
			KeyPair keys = gen.generateKeyPair();
			localSync.init(keys.getPrivate());
			return generatePhase2Material(localSync, keys, partnerKey);
		}
		catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
		return null;
	}

	public static Object[] generatePhase2Material(KeyAgreement localSync, KeyPair keys, DHPublicKey partnerKey) {
		try {
			// AlgorithmParameterGenerator paramGen = AlgorithmParameterGenerator.getInstance("DH");
			// paramGen.init(512); // number of bits
			// AlgorithmParameters params = paramGen.generateParameters();
			// DHParameterSpec dhSpec = (DHParameterSpec) .getParameterSpec(DHParameterSpec.class);

			// KeyAgreement sync = KeyAgreement.getInstance("DH", "BC");

			// javax.crypto.interfaces.DHPrivateKey pk = (javax.crypto.interfaces.DHPrivateKey) keys.getPrivate();
			// sync.init(pk);

			byte[] secret = null;
			MessageDigest hash = MessageDigest.getInstance("SHA1", "BC");
			DHPublicKey pub = (DHPublicKey) localSync.doPhase(partnerKey, true);
			if (pub == null) pub = (DHPublicKey) keys.getPublic();
			secret = localSync.generateSecret();
			return new Object[] { localSync, keys.getPrivate(), pub, secret, hash.digest(secret) };

		}
		catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}

		return null;
	}

	public static void generatePhase3Material(KeyAgreement localSync, DHPublicKey partnerKey, Object[] local) {
		try {
			byte[] secret = null;
			MessageDigest hash = MessageDigest.getInstance("SHA1", "BC");
			localSync.doPhase(partnerKey, true);
			secret = localSync.generateSecret();
			local[Constants.KEY_MATERIAL_SHARED_SECRET] = secret;
			local[Constants.KEY_MATERIAL_SECRET_HASH] = hash.digest(secret);
		}
		catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
	}

	public static Object[] generatePhase1Material() {
		try {
			AlgorithmParameterGenerator paramGen = AlgorithmParameterGenerator.getInstance("DH");
			paramGen.init(512); // number of bits
			AlgorithmParameters params = paramGen.generateParameters();
			DHParameterSpec spec = (DHParameterSpec) params.getParameterSpec(DHParameterSpec.class);
			KeyPairGenerator gen = KeyPairGenerator.getInstance("DH", "BC");

			gen.initialize(spec);

			KeyAgreement localSync = KeyAgreement.getInstance("DH", "BC");
			KeyPair keys = gen.generateKeyPair();

			DHPrivateKey pk = (DHPrivateKey) keys.getPrivate();
			localSync.init(pk);

			return new Object[] { localSync, keys.getPrivate(), keys.getPublic(), null, null };

		}
		catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}

		return null;

	}

	// public static byte[] generateSharedSecret(javax.crypto.interfaces.DHPublicKey partnerKey, javax.crypto.interfaces.DHPrivateKey keys, boolean last) {
	//
	// try {
	// // DHKeyMaterial keys = new DHKeyMaterial();
	//
	// // logger.info("Key: " + keys);
	//
	// // byte[] certData = aPair.getPublic().getEncoded();
	// // X509Certificate cert = X509Certificate.getInstance(certData);
	//
	// // keys.setPublicKey(partnerKeyMaterial.getPublicKey());
	// KeyAgreement aKeyAgree = KeyAgreement.getInstance("DH", "BC");
	// aKeyAgree.init(keys);
	// javax.crypto.interfaces.DHPrivateKey pk = (javax.crypto.interfaces.DHPrivateKey) aKeyAgree.doPhase(partnerKey, last);
	// return aKeyAgree.generateSecret();
	// }
	// catch (Exception ex) {
	// logger.error(ex.getMessage(), ex);
	// }
	//
	// return new byte[0];
	//
	// // PemReader pemReader = new PemReader(new StringReader(keys));
	// // Object obj = pemReader.readPemObject();
	// // pemReader.close();
	// // if (obj instanceof X509Certificate) {
	// // // Just in case your file contains in fact an X.509 certificate,
	// // // useless otherwise.
	// // obj = ((X509Certificate) obj).getPublicKey();
	// // }
	// // if (obj instanceof RSAPublicKey) {
	// // // ... use the getters to get the BigIntegers.
	// // }
	// // // logger.info(new String(hash.digest(bKeyAgree.generateSecret())));
	// }
}
