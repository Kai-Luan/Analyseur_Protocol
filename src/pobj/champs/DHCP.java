package pobj.champs;

import java.util.StringJoiner;

import pobj.Donnees;

public class DHCP implements Couche7 {
	int opcode, HType, HLen, Hops;
	String transaction_ID;
	int SECS;
	String flags;
	String client_IP, your_IP, server_IP, gateway_IP;
	String[] client_mac = new String[4];
	String server_name, boot_filename;
	String magic_cookie;
	String options;
	public DHCP(Donnees trame) {
		opcode = trame.parseInt(0);
		HType = trame.parseInt(1);
		HLen = trame.parseInt(2);
		Hops = trame.parseInt(3);
		transaction_ID = trame.parseHexa(4, 8);
		SECS = trame.parseInt(8,10);
		flags = trame.parseHexa(10, 12);
		client_IP = trame.getIP(12, 16);
		your_IP = trame.getIP(16, 20);
		server_IP = trame.getIP(20, 24);
		gateway_IP = trame.getIP(24, 28);
		client_mac[0] = trame.get(28, 32, ":");
		client_mac[1] = trame.get(32, 36, ":");
		client_mac[2] = trame.get(36, 40, ":");
		client_mac[3] = trame.get(40, 44, ":");
		server_name = trame.get(44,52);
		boot_filename = trame.get(52, 68);
	}
	
	@Override
	public String toString() {
		StringJoiner sb = new StringJoiner("\n  ", "Protocol UDP\n  ","\n");
		sb.add("Message type: " + opcode);
		sb.add("Hardware type: " + HType);
		sb.add("Hardware adress length: " + HLen);
		sb.add("Hops: " + HType);
		sb.add("Transaction ID: " + transaction_ID);
		sb.add("Seconds elapsed: " + SECS);
		sb.add("Bootp flags: " + flags);
		sb.add("Client ID: " + client_IP);
		sb.add("Your (client) IP adress: " + your_IP);
		sb.add("Next server IP address: " + server_IP);
		sb.add("Relay agent IP address: " + gateway_IP);
		sb.add("Client Mac adress: " + client_mac[0]);
		sb.add(" : " + client_mac[1]);
		sb.add(" : " + client_mac[2]);
		sb.add(" : " + client_mac[3]);
		sb.add("Server host name: " + server_name);
		sb.add("Boot file name: " + boot_filename);
		return sb.toString();
	}
}
