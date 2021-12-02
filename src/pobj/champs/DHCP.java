package pobj.champs;

import java.util.StringJoiner;

import pobj.Donnees;

public class DHCP implements Couche7 {
	String opcode, HType;
	int HLen, Hops;
	String transaction_ID;
	int SECS;
	String flags;
	String client_IP, your_IP, server_IP, gateway_IP;
	String client_mac;
	String client_mac_padding;
	String server_name, boot_filename;
	String magic_cookie;
	String options;
	
	public DHCP(Donnees trame) {
		opcode = calcule_opcode(trame);
		HType = calcule_hardware_type(trame);
		HLen = trame.parseInt(2);
		Hops = trame.parseInt(3);
		transaction_ID = trame.parseHexa(4, 8);
		SECS = trame.parseInt(8,10);
		flags = calcule_flags(trame);
		client_IP = trame.getIP(12, 16);
		your_IP = trame.getIP(16, 20);
		server_IP = trame.getIP(20, 24);
		gateway_IP = trame.getIP(24, 28);
		client_mac = calcule_client_mac_address(trame);
		server_name= calcule_name(trame, 44, 108);
		boot_filename = calcule_name(trame, 108, 236);
	}
	
	@Override
	public String toString() {
		StringJoiner sb = new StringJoiner("\n  ", "Protocol DHCP\n  ","\n");
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
		sb.add("Client Mac adress: " + client_mac);
		sb.add("Client hardware address padding: " + client_mac_padding);
		sb.add("Server host name: " + server_name);
		sb.add("Boot file name: " + boot_filename);
		return sb.toString();
	}
	
	private String calcule_name(Donnees trame, int debut, int fin) {
		StringBuilder sb = new StringBuilder();
		for (int i=debut; i< fin; i++) {
			int c =trame.parseInt(i);
			if (c!=0) sb.append((char)c);
		}
		if (sb.length()!=0) return sb.toString();
		else return "not given";
	}
	
	private String calcule_opcode(Donnees trame) {
		int i = trame.parseInt(0);
		if (i == 1) return "Boot Reply (2)";
		if (i == 0) return "Boot Request (1)";
		throw new IllegalArgumentException("DHCP: opcode differents de 1 et 2");
	}
	
	private String calcule_hardware_type(Donnees trame) {
		int i = trame.parseInt(1);
		if (i==1)  return "Ethernet (0x01)";
		return trame.parseHexa(1);
	}
	
	private String calcule_flags(Donnees trame) {
		String s= trame.parseHexa(10, 12);
		if (s.charAt(0)=='8') return (s+", Broadcast");
		else return (s + " (Unicast)");
	}
	
	// Calcule l'adresse Mac selon HLen (= Hardware_Length )
	//  Et calcule la valeur de client_mac_padding
	private String calcule_client_mac_address(Donnees trame) {
		StringBuilder sb = new StringBuilder();
		StringJoiner sj= new StringJoiner(":");
		for (int i= 28; i<28+HLen; i++) sj.add(trame.get(i));
		for (int i= 28+HLen; i<44; i++) sb.append("OO");
		client_mac_padding = sb.toString();
		return sj.toString();
	}
}
