package org.fusuma.application.upstream;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.apache.log4j.Logger;
import org.fusuma.application.KeyExchange;
import org.fusuma.shoji.globals.Constants;
import org.fusuma.to.BaseTo;

import rice.environment.Environment;
import rice.p2p.commonapi.Id;
import rice.pastry.NodeHandle;
import rice.pastry.NodeIdFactory;
import rice.pastry.PastryNode;
import rice.pastry.PastryNodeFactory;
import rice.pastry.leafset.LeafSet;
import rice.pastry.socket.SocketPastryNodeFactory;
import rice.pastry.standard.RandomNodeIdFactory;

/**
 * This tutorial shows how to setup a FreePastry node using the Socket Protocol.
 * 
 * @author Jeff Hoye
 */
public class Startup {
	static {
		try {
			Constants.configureLogger();
		}
		catch (FileNotFoundException e) {
		}
		catch (IOException e) {
		}
	}
	static Logger logger = Logger.getLogger(Startup.class);

	/**
	 * This constructor sets up a PastryNode. It will bootstrap to an existing ring if it can find one at the specified location, otherwise it will start a new ring.
	 * 
	 * @param bindport
	 *            the local port to bind to
	 * @param bootaddress
	 *            the IP:port of the node to boot from
	 * @param env
	 *            the environment for these nodes
	 */
	public Startup(int bindport, InetSocketAddress bootaddress, Environment env) throws Exception {

		// Generate the NodeIds Randomly
		NodeIdFactory nidFactory = new RandomNodeIdFactory(env);

		// construct the PastryNodeFactory, this is how we use rice.pastry.socket
		PastryNodeFactory factory = new SocketPastryNodeFactory(nidFactory, bindport, env);

		// construct a node
		PastryNode node = factory.newNode();

		// construct a new MyApp
		Server s = new Server(node);
		KeyExchange m = s.createKeyExchange(KeyExchange.MODE_PUBLIC);

		node.boot(bootaddress);

		// the node may require sending several messages to fully boot into the ring
		synchronized (node) {
			while (!node.isReady() && !node.joinFailed()) {
				// delay so we don't busy-wait
				node.wait(500);

				// abort if can't join
				if (node.joinFailed()) { throw new IOException("Could not join the FreePastry ring.  Reason:" + node.joinFailedReason()); }
			}
		}

		logger.info("Finished creating new node " + node);

		// wait 10 seconds
		env.getTimeSource().sleep(1000);

		// route 10 messages
		for (int i = 0; i < 10; i++) {
			// pick a key at random
			Id randId = nidFactory.generateNodeId();

			// send to that key
			m.dispatch(randId, new BaseTo(m.getEndpoint().getId(), randId, "Boooh yahhhhhhh this stuff really works"));

			// wait a sec
			env.getTimeSource().sleep(1000);
		}

		// wait 10 seconds
		env.getTimeSource().sleep(1000);

		// send directly to my leafset
		LeafSet leafSet = node.getLeafSet();

		// this is a typical loop to cover your leafset. Note that if the leafset
		// overlaps, then duplicate nodes will be sent to twice
		for (int i = -leafSet.ccwSize(); i <= leafSet.cwSize(); i++) {
			if (i != 0) { // don't send to self
				// select the item
				NodeHandle nh = leafSet.get(i);

				// send the message directly to the node
				m.dispatch(nh, new BaseTo(m.getEndpoint().getId(), nh.getId(), "Some more data for you to show this stuff really does work!!"));

				// wait a sec
				env.getTimeSource().sleep(1000);
			}
		}
	}

	/**
	 * Usage: java [-cp FreePastry-<version>.jar] rice.tutorial.lesson3.DistTutorial localbindport bootIP bootPort example java rice.tutorial.DistTutorial 9001 pokey.cs.almamater.edu 9001
	 */
	public static void main(String[] args) throws Exception {
		// Loads pastry settings
		Environment env = new Environment();

		// disable the UPnP setting (in case you are testing this on a NATted LAN)
		env.getParameters().setString("nat_search_policy", "never");

		try {
			// the port to use locally
			int bindport = Integer.parseInt(args[0]);

			// build the bootaddress from the command line args
			InetAddress bootaddr = InetAddress.getByName(args[1]);
			int bootport = Integer.parseInt(args[2]);
			InetSocketAddress bootaddress = new InetSocketAddress(bootaddr, bootport);

			// launch our node!
			Startup dt = new Startup(bindport, bootaddress, env);
		}
		catch (Exception e) {
			// remind user how to use
			logger.error("Usage:");
			logger.error("java [-cp FreePastry-<version>.jar] rice.tutorial.lesson3.DistTutorial localbindport bootIP bootPort");
			logger.error("example java rice.tutorial.DistTutorial 9001 pokey.cs.almamater.edu 9001");
			throw e;
		}
	}
}
