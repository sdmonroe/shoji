package org.fusuma.shoji.globals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.LogManager;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public final class Constants {

	static Logger logger = Logger.getLogger(Constants.class);

	public final static String LOGGER_FILENAME = "logger.properties";
	public final static int KEY_MATERIAL_ARRAY_SIZE = 5;
	public final static int KEY_MATERIAL_LOCAL_KEY_AGREEMENT = 0;
	public final static int KEY_MATERIAL_PRIVATE = 1;
	public final static int KEY_MATERIAL_PUBLIC = 2;
	// public final static int KEY_MATERIAL_PARTNER_PUBLIC = 3;
	public final static int KEY_MATERIAL_SHARED_SECRET = 3;
	public final static int KEY_MATERIAL_SECRET_HASH = 4;

	// keys phases
	public final static int KEY_PHASE_1 = 1;
	public final static int KEY_PHASE_2 = 2;
	public final static int KEY_PHASE_3 = 3;

	// channels
	public final static URI CHANNEL_PREFIX_PRIVATE = createURI("http://onto.fusuma.org/channels/private#"); // creates a private channel within a ring between two members with the same keys
	public final static URI CHANNEL_GENERAL = createURI("http://onto.fusuma.org/channels/general");
	public final static URI CHANNEL_DH_PUBLIC_KEY_EXCHANGE = createURI("http://onto.fusuma.org/channels/pkex");
	// public final static String CHANNEL_PUBLIC_SERVER_RING = "http://onto.fusuma.org/channels/server-ring";

	public final static URI SCRIBE_TOPIC_CIPHERTEXTS = createURI("http://onto.fusuma.org/exchange-topic/cipher-texts"); // share ciphertext with server peers in the same circle/scribeExchange
	public final static URI SCRIBE_TOPIC_PUBLIC_KEYS = createURI("http://onto.fusuma.org/exchange-topic/public-keys"); // share public keys with server peers in the same circle/scribeExchange
	public final static URI SCRIBE_TOPIC_REVEAL = createURI("http://onto.fusuma.org/exchange-topic/reveal"); // publish cleartexts of a round
	public final static URI SCRIBE_TOPIC_PREFIX_PUBLISH = createURI("http://onto.fusuma.org/exchange-topic/publish"); // publish cleartext from a client; suffix is a client pseudonym hash

	public static URI createURI(String s) {
		try {
			return new URI(s);
		}
		catch (URISyntaxException e) {
			logger.error(e);
		}
		return null;
	}

	/**
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public final static void configureLogger() throws IOException, FileNotFoundException {
		BasicConfigurator.configure();
		File f = new File(LOGGER_FILENAME);
		if (!f.exists()) {
			InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(LOGGER_FILENAME);
			IOUtils.copy(is, new FileOutputStream(f));
		}
		LogManager.getLogManager().readConfiguration(new FileInputStream(f));
	}

	public static final String SCRIBE_DEFAULT_INSTANCE_ID = "myinstance";
	public static final String SCRIBE_DEFAULT_CHANNEL = "myscribe";

}
