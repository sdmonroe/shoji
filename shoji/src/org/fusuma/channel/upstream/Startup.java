package org.fusuma.channel.upstream;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.fusuma.channel.scribe.Conversation;
import org.fusuma.channel.scribe.ScribeChannel;
import org.fusuma.shoji.globals.Constants;
import org.fusuma.to.message.PublicKey;
import org.fusuma.to.message.ScribePost;

import rice.environment.Environment;
import rice.p2p.commonapi.NodeHandle;
import rice.pastry.NodeIdFactory;
import rice.pastry.PastryNode;
import rice.pastry.PastryNodeFactory;
import rice.pastry.socket.SocketPastryNodeFactory;
import rice.pastry.standard.RandomNodeIdFactory;

/**
 * This tutorial shows how to use Scribe.
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
	 * this will keep track of our Scribe applications
	 */
	Vector<Conversation> conversations = new Vector<Conversation>();

	/**
	 * Based on the rice.tutorial.lesson4.DistTutorial
	 * 
	 * This constructor launches numNodes PastryNodes. They will bootstrap to an existing ring if one exists at the specified location, otherwise it will start a new ring.
	 * 
	 * @param bindport
	 *            the local port to bind to
	 * @param bootaddress
	 *            the IP:port of the node to boot from
	 * @param numNodes
	 *            the number of nodes to create in this JVM
	 * @param env
	 *            the Environment
	 */
	public Startup(int bindport, InetSocketAddress bootaddress, int numNodes, Environment env) throws Exception {

		// Generate the NodeIds Randomly
		NodeIdFactory nidFactory = new RandomNodeIdFactory(env);

		// construct the PastryNodeFactory, this is how we use rice.pastry.socket
		PastryNodeFactory factory = new SocketPastryNodeFactory(nidFactory, bindport, env);

		// loop to construct the nodes/conversations
		// for (int curNode = 0; curNode < numNodes; curNode++) {
		// construct a new node
		PastryNode node = factory.newNode();

		// construct a new channel application
		Server s = new Server(node);
		s.joinDHChannel(); // join the DH key exchange channel and listen for clients initiating key exchanges
		ScribeChannel sc = s.joinScribeChannel(Constants.CHANNEL_DEFAULT_SERVER_SCRIBE);
		Conversation cvn = sc.joinConversation(Constants.SCRIBE_TOPIC_PUBLIC_KEYS);
		// Conversation c2 = se.joinConversation(Constants.SCRIBE_TOPIC_PUBLIC_KEYS);
		conversations.add(cvn);
		// conversations.add(c2);

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

		logger.info("Finished creating new node: " + node);
		// }

		// for the first app subscribe then start the publishtask
		// Iterator<conversations> i = conversations.iterator();
		// conversations channel = (conversations) i.next();
		// channel.subscribe();
		// channel.startPublishTask();
		// for all the rest just subscribe
		// for (Conversation c : conversations) {
		// // channel = (conversations) i.next();
		// }
		env.getTimeSource().sleep(10000);
		for (Conversation c : conversations) {
			// channel = (conversations) i.next();
			// channel.subscribe();
			ScribePost message = new ScribePost(c.getChannel().getEndpoint().getId());
			message.setChannel(c.getChannel().getUid());
			message.setData("Scribe exchange joined.");
			c.sendMulticast(message);
			// c.sendAnycast(message);

			PublicKey pk = s.getPublicKey();
			pk.setChannel(c.getChannel().getUid());

			c.sendMulticast(pk);
			// c.sendAnycast(pk);

		}

		// now, print the tree
		env.getTimeSource().sleep(5000);
		// printTree(conversations);
	}

	/**
	 * Note that this function only works because we have global knowledge. Doing this in an actual distributed environment will take some more work.
	 * 
	 * @param conversations
	 *            Vector of the applications.
	 */
	public static void printTree(Vector<Conversation> conversations) {
		// build a hashtable of the conversations, keyed by nodehandle
		Hashtable<NodeHandle, Conversation> appTable = new Hashtable<NodeHandle, Conversation>();
		Iterator<Conversation> i = conversations.iterator();
		while (i.hasNext()) {
			Conversation c = (Conversation) i.next();
			appTable.put(c.getChannel().getEndpoint().getLocalNodeHandle(), c);
		}
		NodeHandle seed = ((Conversation) conversations.get(0)).getChannel().getEndpoint().getLocalNodeHandle();

		// get the root
		NodeHandle root = getRoot(seed, appTable);

		// print the tree from the root down
		recursivelyPrintChildren(root, 0, appTable);
	}

	/**
	 * Recursively crawl up the tree to find the root.
	 */
	public static NodeHandle getRoot(NodeHandle seed, Hashtable<NodeHandle, Conversation> appTable) {
		Conversation c = (Conversation) appTable.get(seed);
		// if (c == null) return seed;
		if (c.isRoot()) return seed;
		NodeHandle nextSeed = c.getParent();
		return getRoot(nextSeed, appTable);
	}

	/**
	 * Print's self, then children.
	 */
	public static void recursivelyPrintChildren(NodeHandle curNode, int recursionDepth, Hashtable<NodeHandle, Conversation> appTable) {
		// print self at appropriate tab level
		String s = "";
		for (int numTabs = 0; numTabs < recursionDepth; numTabs++) {
			s += "  ";
		}
		s += curNode.getId().toString();
		logger.info(s);

		// recursively print all children
		Conversation c = (Conversation) appTable.get(curNode);
		// if (c == null) return;
		NodeHandle[] children = c.getChildren();
		for (int curChild = 0; curChild < children.length; curChild++) {
			recursivelyPrintChildren(children[curChild], recursionDepth + 1, appTable);
		}
	}

	/**
	 * Usage: java [-cp FreePastry- <version>.jar] rice.tutorial.lesson6.ScribeTutorial localbindport bootIP bootPort numNodes example java rice.tutorial.DistTutorial 9001 pokey.cs.almamater.edu 9001
	 */
	public static void main(String[] args) throws Exception {
		// Loads pastry configurations
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

			// the port to use locally
			int numNodes = Integer.parseInt(args[3]);

			// launch our node!
			Startup dt = new Startup(bindport, bootaddress, numNodes, env);
		}
		catch (Exception e) {
			// remind user how to use
			logger.info("Usage:");
			logger.info("java [-cp FreePastry-<version>.jar] rice.tutorial.scribe.ScribeTutorial localbindport bootIP bootPort numNodes");
			logger.info("example java rice.tutorial.scribe.ScribeTutorial 9001 pokey.cs.almamater.edu 9001 10");
			throw e;
		}
	}
}