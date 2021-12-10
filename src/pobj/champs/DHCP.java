package pobj.champs;

import java.util.ArrayList;
import java.util.List;
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
	List<String> options = new ArrayList<>();
	
	// Constructeur
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
		options.add("Magic cookie: DHCP ("+ trame.parseHexa(236, 240)+")");
		calcule_options(trame);
	}
	
	// Decode le nom avec le debut et la fin des octets
	private String calcule_name(Donnees trame, int debut, int fin) {
		StringBuilder sb = new StringBuilder();
		for (int i=debut; i< fin; i++) {
			int c =trame.parseInt(i);
			if (c!=0) sb.append((char)c);
		}
		if (sb.length()!=0) return sb.toString();
		else return "not given";
	}
	
	// Calcule le champs opcode de DHCP
	private String calcule_opcode(Donnees trame) {
		int i = trame.parseInt(0);
		if (i == 1) return "Boot Reply (2)";
		if (i == 0) return "Boot Request (1)";
		return ("DHCP: opcode differents de 1 et 2");
	}
	
	// Calcule le champs Hardware type
	private String calcule_hardware_type(Donnees trame) {
		int i = trame.parseInt(1);
		if (i==1)  return "Ethernet (0x01)";
		return trame.parseHexa(1);
	}
	
	// Calcule les flags
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
	// Decode tout la partie optionS de DNS
	private String calcule_options(Donnees trame) {
		int indice =240;
		int num_option = trame.parseInt(indice);
		while (num_option!=255) {
			int tmp_taille = trame.parseInt(indice+1);
			String opt = calcule_option(trame,indice);
			this.options.add(opt);
			indice+=tmp_taille+2;
			num_option = trame.parseInt(indice);
		}
		String resp = " Option numero 255 : End\n";
		this.options.add(resp);
		return "";
	}
	
	// Decode l'option à partir de d'un octet
	private String calcule_option(Donnees trame, int indice) {
		int num_option = trame.parseInt(indice);
		int length = trame.parseInt(indice+1);
		int indice_max;
		indice= indice+2;
		
		String resp = String.format("Option: (%d) %s \n     length: %d  \n     ", num_option, option_name(num_option), length);
		switch(num_option) {
		// SubNet Mask
		case 1:
			return resp+ String.format("Subnet Mask: %s", trame.getIP(indice, indice+4));
		// Router
		case 3:
			indice_max= indice+length;
			while(indice < indice_max) {
				resp += "Router : " + trame.getIP(indice,indice+4) + "\n";
				indice= indice+4;
			}
			return resp ;
		// Domain Server
		case 6:
			indice_max= indice+length;
			while(indice < indice_max) {
				resp += String.format("Domain Name Server: %s \n", trame.getIP(indice,indice+4));
				indice= indice+4;
			}
			return resp ;
		// Domain Name
		case 15:
			return resp+ calcule_name(trame, indice, length, "Domain Name: ");
		// Static Route
		case 33:
			indice_max = indice +length;
			while(indice< indice_max) {
				resp += "Destination : " + trame.getIP(indice,indice+4) + "\n";
				resp += "Router : " + trame.getIP(indice+4,indice+8) + "\n\n";
				indice+=8;
			}
			return resp ;
		// IP Adress Lease Time:
		case 51:
			int seconds = trame.parseInt(indice,indice+length);
			resp += String.format("IP Adress Lease Time: (%ds) %s \n", seconds,  conversion_temps(seconds));
			return resp ;
		// DHCP Message type
		case 53:
			return resp+ message_Type(trame.parseInt(indice));
		//DHCP Server Identifier
		case 54:
			return resp+ String.format("DHCP Server Identifier: %s", trame.getIP(indice, indice+4));
		// Parameter Request
		case 55:
		    StringJoiner sj = new StringJoiner("\n     Parameter Request List Item:  ", "Parameter Request List Item:  ", "\n");
		    for (int i=0; i<length; i++) {
		    	int code= trame.parseInt(indice++);
		    	sj.add(String.format("(%d) %s", code, option_name(code)));
		    }
			return resp + sj.toString();
		// Vender class Identifier
		case 60:
			return resp + calcule_name(trame, indice, length, "Vender class identifier: ") ;
		// Server Name
		case 66:
			return resp + calcule_name(trame, indice, length, "TFTP Sever Name: ");
		// Bootfile Name
		case 67:
			return resp + calcule_name(trame, indice, length, "Bootfile name: ");
		case 121:
			resp += trame.parseHexa(indice, indice+length);
			return resp ;
		// TFTP Server Adress option
		case 150:
		    sj = new StringJoiner("\n     IPv4 Configuration Serrver Adress:  ", "     IPv4 Configuration Serrver Adress:", "\n");
		    for (int i=indice; i<indice+length; i+=4) {
		    	sj.add(String.format("(%d) %s", trame.getIP(i, i+4)));
		    }
		    return resp + sj.toString();
		// Cas par defaut
		default:
			return resp + trame.parseHexa(indice, indice+length);
		}
	}
	
	// Calcule Option 53: DHCP Message Type
	private String message_Type(int type) {
		switch(type) {
			case 1: return "DHCP: Discover (1)";
			case 2: return "DHCP: Offer (2)";
			case 3: return "DHCP: Request (3)";
			case 4: return "DHCP: Decline (4)";
			case 5: return "DHCP: Pack (5)";
			case 6: return "DHCP: Nak (6)";
			case 7: return "DHCP: Release (7)";
			case 8: return "DHCP: Inform (8)";
			default: return "DHCP: Unassigned";
		}
	}
	
	// Faire la conversion du temps en secondes 
	//en temps en format jours / heures / minutes / secondes
	private String conversion_temps(int ttl) {
		StringJoiner sj = new StringJoiner(", ", " (", ")");
		int jours, heures, minutes;
		if (ttl/86400 >= 1) {
			jours = ttl/86400;
			ttl=  ttl - 86400*jours;
			sj.add(jours + " days");
		}	
		if (ttl/3600 >= 1) {
			heures = ttl/3600;
			ttl=  ttl - 3600*heures;
			sj.add(heures + "  hours");
		}
		if (ttl/60>= 1) {
			minutes = ttl/60;
			ttl=  ttl - 60*minutes;
			sj.add(minutes + "  minutes");
		}
		if (ttl != 0) sj.add(ttl + " seconds");
		return sj.toString();
	}
	// Donne le nom de l'option selon le code donné
	public String option_name(int type) {
		if (type>=224 && type<=254) return "Reserved (Private Use)";
		if (type>=162 && type<= 174) return "Unassigned";
		if (type>=178 && type<= 207) return "Unassigned";
		if (type>=214 && type<= 219) return "Unassigned";
		if (type>=102 && type<= 107) return "Unassigned";
		switch(type) {
			case 0: return "Pad";
			case 1: return "Subnet Mask";
			case 2: return "Time Offset";
			case 3: return "Router";
			case 4: return "Time Server";
			case 5: return "Name Server";
			case 6: return "Domain Server";
			case 7: return "Log Server";
			case 8: return "Quotes Server";
			case 9: return "LPR Server";
			case 10: return "Impress Server";
			case 11: return "RLP Server";
			case 12: return "Hostname";
			case 13: return "Boot File Size";
			case 14: return "Merit Dump File";
			case 15: return "Domain Name";
			case 16: return "Swap Server";
			case 17: return "Root Path";
			case 18: return "Extension File";
			case 19: return "Forward On/Off";
			case 20: return "SrcRte On/Off";
			case 21: return "Policy Filter";
			case 22: return "Max DG Assembly";
			case 23: return "Default IP TTL";
			case 24: return "MTU Timeout";
			case 25: return "MTU Plateau";
			case 26: return "MTU Interface";
			case 27: return "MTU Subnet";
			case 28: return "Broadcast Address";
			case 29: return "Mask Discovery";
			case 30: return "Mask Supplier";
			case 31: return "Router Discovery";
			case 32: return "Router Request";
			case 33: return "Static Route";
			case 34: return "Trailers";
			case 35: return "ARP Timeout";
			case 36: return "Ethernet";
			case 37: return "Default TCP TTL";
			case 38: return "Keepalive Time";
			case 39: return "Keepalive Data";
			case 40: return "NIS Domain";
			case 41: return "NIS Servers";
			case 42: return "NTP Servers";
			case 43: return "Vendor Specific";
			case 44: return "NETBIOS Name Srv";
			case 45: return "NETBIOS Dist Srv";
			case 46: return "NETBIOS Node Type";
			case 47: return "NETBIOS Scope";
			case 48: return "X Window Font";
			case 49: return "X Window Manager";
			case 50: return "Address Request";
			case 51: return "Address Time";
			case 52: return "Overload";
			case 53: return "DHCP Message Type";
			case 54: return "DHCP Server Id";
			case 55: return "Parameter List";
			case 56: return "DHCP Message";
			case 57: return "DHCP Max Msg Size";
			case 58: return "Renewal Time";
			case 59: return "Rebinding Time";
			case 60: return "Class Identifier";
			case 61: return "Client Identifier";
			case 62: return "NetWare/IP Domain";
			case 63: return "NetWare/IP Option";
			case 64: return "NIS-Domain-Name";
			case 65: return "NIS-Server-Addr";
			case 66: return "Server-Name";
			case 67: return "Bootfile-Name";
			case 68: return "Home-Agent-Addrs";
			case 69: return "SMTP-Server";
			case 70: return "POP3-Server";
			case 71: return "NNTP-Server";
			case 72: return "WWW-Server";
			case 73: return "Finger-Server";
			case 74: return "IRC-Server";
			case 75: return "StreetTalk-Server";
			case 76: return "STDA-Server";
			case 77: return "User-Class";
			case 78: return "Directory Agent";
			case 79: return "Service Scope";
			case 80: return "Rapid Commit";
			case 81: return "Client FQDN";
			case 82: return "Relay Agent Information";
			case 83: return "iSNS";
			case 84: return "REMOVED/Unassigned";
			case 85: return "NDS Servers";
			case 86: return "NDS Tree Name";
			case 87: return "NDS Context";
			case 88: return "BCMCS Controller Domain Name list";
			case 89: return "BCMCS Controller IPv4 address option";
			case 90: return "Authentication";
			case 91: return "client-last-transaction-time option";
			case 92: return "associated-ip option";
			case 93: return "Client System";
			case 94: return "Client NDI";
			case 95: return "LDAP";
			case 96: return "REMOVED/Unassigned";
			case 97: return "UUID/GUID";
			case 98: return "User-Auth";
			case 99: return "GEOCONF_CIVIC";
			case 100: return "PCode";
			case 101: return "TCode";
			case 108: return "IPv6-Only Preferred";
			case 109: return "OPTION_DHCP4O6_S46_SADDR";
			case 110: return "Unassigned";
			case 111: return "Unassigned";
			case 112: return "Netinfo Address";
			case 113: return "Netinfo Tag";
			case 114: return "DHCP Captive-Portal";
			case 115: return "REMOVED/Unassigned";
			case 116: return "Auto-Config";
			case 117: return "Name Service Search";
			case 118: return "Subnet Selection Option";
			case 119: return "Domain Search";
			case 120: return "SIP Servers DHCP Option";
			case 121: return "Classless Static Route Option";
			case 122: return "CCC";
			case 123: return "GeoConf Option";
			case 124: return "V-I Vendor Class";
			case 125: return "V-I Vendor-Specific Information";
			case 126: return "Removed/Unassigned";
			case 127: return "Removed/Unassigned";
			case 128: return "PXE - undefined (vendor specific)";
			case 129: return "PXE - undefined (vendor specific)";
			case 130: return "PXE - undefined (vendor specific)";
			case 131: return "PXE - undefined (vendor specific)";
			case 132: return "PXE - undefined (vendor specific)";
			case 133: return "PXE - undefined (vendor specific)";
			case 134: return "PXE - undefined (vendor specific)";
			case 135: return "PXE - undefined (vendor specific)";
			case 136: return "OPTION_PANA_AGENT";
			case 137: return "OPTION_V4_LOST";
			case 138: return "OPTION_CAPWAP_AC_V4";
			case 139: return "OPTION-IPv4_Address-MoS";
			case 140: return "OPTION-IPv4_FQDN-MoS";
			case 141: return "SIP UA Configuration Service Domains";
			case 142: return "OPTION-IPv4_Address-ANDSF";
			case 143: return "OPTION_V4_SZTP_REDIRECT";
			case 144: return "GeoLoc";
			case 145: return "FORCERENEW_NONCE_CAPABLE";
			case 146: return "RDNSS Selection";
			case 147: return "OPTION_V4_DOTS_RI";
			case 148: return "OPTION_V4_DOTS_ADDRESS";
			case 149: return "Unassigned";
			case 150: return "TFTP server address";
			case 151: return "status-code";
			case 152: return "base-time";
			case 153: return "start-time-of-state";
			case 154: return "query-start-time";
			case 155: return "query-end-time";
			case 156: return "dhcp-state";
			case 157: return "data-source";
			case 158: return "OPTION_V4_PCP_SERVER";
			case 159: return "OPTION_V4_PORTPARAMS";
			case 160: return "Unassigned";
			case 161: return "OPTION_MUD_URL_V4";
			case 175: return "Etherboot";
			case 176: return "IP Telephone";
			case 177: return "Etherboot";
			case 208: return "PXELINUX Magic";
			case 209: return "Configuration File";
			case 210: return "Path Prefix";
			case 211: return "Reboot Time";
			case 212: return "OPTION_6RD";
			case 213: return "OPTION_V4_ACCESS_DOMAIN";
			case 220: return "Subnet Allocation Option";
			case 221: return "Virtual Subnet Selection (VSS) Option";
			case 222: return "Unassigned";
			case 223: return "Unassigned";
			case 255: return "End";
			default : return "Pas une option";
		}
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
		for (String s: options) sb.add(s);
		return sb.toString();
	}
	// Recupere le nom en code Ascii dans les octets données
	// indice: debut du nom, length: le nombre d'octets a decoder
	public String calcule_name(Donnees trame, int indice, int length, String s) {
	    StringBuilder sb= new StringBuilder(s);
		for (int i=0; i<length; i++) {
	    	sb.append((char) trame.parseInt(indice++));
	    }
		return sb.toString();
	}
}
