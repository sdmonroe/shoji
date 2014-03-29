package org.fusuma.shoji.globals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.BasicConfigurator;

public final class Constants {

	public final static String LOGGER_FILENAME = "logger.properties";
	public final static int KEY_MATERIAL_PRIVATE = 0;
	public final static int KEY_MATERIAL_PARTNER_PUBLIC = 1;

	// channels
	public final static String CHANNEL_PREFIX_PRIVATE = "CHANNEL-PRIVATE."; // creates a private channel within a ring between two members with the same key
	public final static String CHANNEL_GENERAL = "CHANNEL-GENERAL";
	public final static String CHANNEL_PUBLIC_KEY_EXCHANGE = "CHANNEL-PUBLIC-KEY-EXCHANGE";

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

}
