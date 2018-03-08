package com.compassites;

import java.util.Collection;

import java.util.Scanner;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.Chat;

import org.jivesoftware.smack.ChatManager;

import org.jivesoftware.smack.ConnectionConfiguration;

import org.jivesoftware.smack.MessageListener;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
//import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackConfiguration;

import org.jivesoftware.smack.XMPPConnection;

import org.jivesoftware.smack.XMPPException;

//import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;

import org.jivesoftware.smack.packet.Message;

import org.jivesoftware.smack.packet.Presence;

import org.jivesoftware.smack.packet.Presence.Type;

public class GroupChat {

	private static final int packetReplyTimeout = 500; // millis

	private String server;

	private int port;

	private ConnectionConfiguration config;

	private XMPPConnection connection;

	private ChatManager chatManager;

	private MessageListener messageListener;

	public GroupChat(String server, int port) {

		this.server = server;

		this.port = port;

	}

	public void init() throws XMPPException {

		System.out.println(String.format("Initializing connection to server %1$s port %2$d", server, port));

		SmackConfiguration.setPacketReplyTimeout(packetReplyTimeout);

		config = new ConnectionConfiguration(server, port);
	
		connection = new XMPPConnection(config);
		
		connection.connect();

		System.out.println("Connected: " + connection.isConnected());

		chatManager = connection.getChatManager();

		/*
		 * Chat chat=chatManager.createChat("pavan@localhost", new MyMessageListener());
		 * chat.sendMessage("hi user");
		 */

		/*
		 * messageListener = new MyMessageListener();
		 */
	}

	public void performLogin(String username, String password) throws XMPPException {

		try {
			if (connection != null && connection.isConnected()) {

				connection.login(username, password);
				System.out.println("logged in");

			}
		} catch (Exception e2) {
			System.out.println(e2.getMessage());
		}

	}

	public void setStatus(boolean available, String status) {

		Presence.Type type = available ? Type.available : Type.unavailable;

		Presence presence = new Presence(type);

		presence.setStatus(status);
		connection.sendPacket(presence);

	}

	public void destroy() {

		if (connection != null && connection.isConnected()) {
			connection.disconnect();
		}

	}

	public void sendMessage(String message, String buddyJID) throws XMPPException {

		System.out.println(String.format("Sending mesage '%1$s' to user %2$s", message, buddyJID));

		Chat chat = chatManager.createChat(buddyJID, new MyMessageListener());

		chat.sendMessage(message);
	}

	public void createEntry(String user, String name, String[] arg2) throws Exception {

		System.out.println(String.format("Creating entry for buddy '%1$s' with name %2$s", user, name));

		Roster roster = connection.getRoster();

		roster.createEntry(user, name, null);
		roster.createEntry(user, name, arg2);

	}

	class MyMessageListener implements MessageListener {

		public void processMessage(Chat chat, Message message) {

			String from = message.getFrom();
			String body = message.getBody();

			System.out.println(String.format("Received message '%1$s' from %2$s", body, from));

		}

	}

	public void printRoster() throws Exception {
		Roster roster = connection.getRoster();
		Collection<RosterEntry> entries = roster.getEntries();
		for (RosterEntry entry : entries) {
			System.out.println(String.format("Buddy:%1$s - Status:%2$s", entry.getName(), entry.getStatus()));
		}
	}

	public RosterEntry addEntry(String jid, String nickname, String group) {
		String[] groups = { group };
		
		Roster roster = connection.getRoster();
		RosterEntry userEntry = roster.getEntry(jid);
		boolean isSubscribed = true;
		if (userEntry != null)
			isSubscribed = userEntry.getGroups().isEmpty();

		if (isSubscribed) {
			try {
				roster.createEntry(jid, nickname, new String[] { group });
			} catch (XMPPException e) {
				System.out.println(e.getMessage());
			}
			return roster.getEntry(jid);
		}

		try {
			RosterGroup rosterGroup = roster.getGroup(group);
			if (rosterGroup == null)
				rosterGroup = roster.createGroup(group);
			if (userEntry == null) {
				roster.createEntry(jid, nickname, groups);
				userEntry = roster.getEntry(jid);
			} else {
				userEntry.setName(nickname);
				rosterGroup.addEntry(userEntry);
			}
			userEntry = roster.getEntry(jid);
		} catch (XMPPException ex) {
			System.out.println(ex.getMessage());
		}
		return userEntry;
	}
	
	public void addUser(String userName, String password) throws XMPPException {
		AccountManager accountManager = connection.getAccountManager();
		try {
			accountManager.createAccount(userName, password); // Create User
			System.out.println("User Created");
			//accountManager.changePassword("12345"); // Change password
		} catch (XMPPException e1) {
			System.out.println(e1.getMessage());
		}

	}

	public static void main(String[] args) throws Exception {

		
		Scanner scanner = new Scanner(System.in);
		GroupChat xmppManager = new GroupChat("localhost", 5222);

		xmppManager.init();

		 //xmppManager.performLogin(username, password);
		xmppManager.performLogin("pallavi", "1234");
		//xmppManager.performLogin("ganga", "1234");

		//xmppManager.setStatus(true, "Hello everyone");

		/*
		 * String buddyJID = "jackid";
		 * 
		 * String buddyName = "jackname"; 
		 */
		//String[] chitChat = { "AGroup" };
		// xmppManager.createEntry(buddyJID, buddyName, chitChat);
		//xmppManager.createEntry("gangaid", "ganga", chitChat);

		//.addEntry("8id", "sonu1", "chitChat1");

//		xmppManager.addUser("aditya", "1234");
//		System.out.println("Enter message to be sent");
//		String message = scanner.nextLine();
		while(true)
		{
			System.out.println("Enter message to be sent");
			String message = scanner.nextLine();
         xmppManager.sendMessage(message, "pallavi@localhost");
         System.out.println("sent");
		}
//		System.out.println("sent");
//		
//		xmppManager.printRoster();
		//xmppManager.printRoster();

		/*boolean isRunning = true;

		while (isRunning) {

			Thread.sleep(1);

		}

		xmppManager.destroy();
*/
	}

}
