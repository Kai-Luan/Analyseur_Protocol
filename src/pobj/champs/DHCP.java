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
		
	private String calcule_options(Donnees trame) {
		int indice =240;
		trame.affiche(indice);
		int num_option = trame.parseInt(indice);
		while (num_option!=255) {
			int tmp_taille = trame.parseInt(indice+1);
			String opt = calcule_option(trame,indice);
			this.options.add(opt);
			indice+=tmp_taille+2;
			num_option = trame.parseInt(indice);
			trame.affiche();
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
		String[] options = {"Pad","Subnet Mask","Time Offset","Router","Time Server","Name Server","Domain Server","Log Server","Quotes Server","LPR Server","Impress Server","RLP Server","Hostname","Boot File Size","Merit Dump File","Domain Name","Swap Server","Root Path","Extension File","Forward On/Off","SrcRte On/Off","Policy Filter","Max DG Assembly","Default IP TTL","MTU Timeout","MTU Plateau","MTU Interface","MTU Subnet","Broadcast Address","Mask Discovery","Mask Supplier","Router Discovery","Router Request","Static Route","Trailers","ARP Timeout","Ethernet","Default TCP TTL","Keepalive Time","Keepalive Data","NIS Domain","NIS Servers","NTP Servers","Vendor Specific","NETBIOS Name Srv","NETBIOS Dist Srv","NETBIOS Node Type,NETBIOS Scope","X Window Font","X Window Manager","Address Request","Address Time","Overload","DHCP Msg Type","DHCP Server Id","Parameter List","DHCP Message","DHCP Max Msg Size","Renewal Time","Rebinding Time","Class Id","Client Id","NetWare/IP Domain","NetWare/IP Option","NIS-Domain-Name","NIS-Server-Addr","Server-Name","Bootfile-Name","Home-Agent-Addrs","SMTP-Server","POP3-Server","NNTP-Server","WWW-Server","Finger-Server","IRC-Server","StreetTalk-Server","STDA-Server","User-Class","Directory Agent","Service Scope","Rapid Commit","Client FQDN","Relay Agent Information","iSNS,REMOVED/Unassigned","NDS Servers","NDS Tree Name","NDS Context","BCMCS Controller Domain Name list","BCMCS Controller IPv4 address option","Authentication","client-last-transaction-time option","associated-ip option","Client System","Client NDI","LDAP","REMOVED/Unassigned","UUID/GUID","User-Auth","GEOCONF_CIVICPCode","PCode","TCode","REMOVED/Unassigned","REMOVED/Unassigned","REMOVED/Unassigned","REMOVED/Unassigned","REMOVED/Unassigned","REMOVED/Unassigned","IPv6-Only Preferred","OPTION_DHCP4O6_S46_SADDR","REMOVED/Unassigned","Unassigned","Netinfo Address","Netinfo Tag","DHCP Captive-Portal","REMOVED/Unassigned","Auto-Config","Name Service Search","Subnet Selection Option","Domain Search","SIP Servers DHCP Option","Classless Static Route Option","CCC","GeoConf Option","V-I Vendor Class","V-I Vendor-Specific Information","Removed/Unassigned","Removed/Unassigned","PXE - undefined (vendor specific)","PXE - undefined (vendor specific)","PXE - undefined (vendor specific)","PXE - undefined (vendor specific)","PXE - undefined (vendor specific)","PXE - undefined (vendor specific)","PXE - undefined (vendor specific)","PXE - undefined (vendor specific)","OPTION_PANA_AGENT","OPTION_V4_LOST","OPTION_CAPWAP_AC_V4","OPTION-IPv4_Address-MoS","OPTION-IPv4_FQDN-MoS","SIP UA Configuration Service Domains","OPTION-IPv4_Address-ANDSF","OPTION_V4_SZTP_REDIRECT","GeoLoc","FORCERENEW_NONCE_CAPABLE","RDNSS Selection","OPTION_V4_DOTS_RI","OPTION_V4_DOTS_ADDRESS","Unassigned","TFTP server address","status-code","base-time","start-time-of-state","query-start-time","query-end-time","dhcp-state","data-source","OPTION_V4_PCP_SERVER","OPTION_V4_PORTPARAMS","Unassigned","OPTION_MUD_URL_V4","Unassigned","Unassigned","Unassigned","Unassigned","Unassigned","Unassigned","Unassigned","Unassigned","Unassigned","Unassigned","Unassigned","Unassigned","Unassigned","Etherboot (Tentatively Assigned - 2005-06-23)","IP Telephone (Tentatively Assigned - 2005-06-23)","Etherboot (Tentatively Assigned - 2005-06-23)","PacketCable and CableHome (replaced by 122)","Unassigned","Unassigned","Unassigned","Unassigned","Unassigned","Unassigned","Unassigned","Unassigned","Unassigned","Unassigned","Unassigned","Unassigned","Unassigned","Unassigned","Unassigned","Unassigned","Unassigned","Unassigned","Unassigned","Unassigned","Unassigned","Unassigned","Unassigned","Unassigned","Unassigned","Unassigned","Unassigned","Unassigned","Unassigned","PXELINUX Magic","Configuration File","Path Prefix","Reboot Time","OPTION_6RD","OPTION_V4_ACCESS_DOMAIN","Unassigned","Unassigned","Unassigned","Unassigned","Unassigned","Unassigned","Subnet Allocation Option","Virtual Subnet Selection (VSS) Option","Unassigned","Unassigned","Reserved (Private Use)","Reserved (Private Use)","Reserved (Private Use)","Reserved (Private Use)","Reserved (Private Use)","Reserved (Private Use)","Reserved (Private Use)","Reserved (Private Use)","Reserved (Private Use)","Reserved (Private Use)","Reserved (Private Use)","Reserved (Private Use)","Reserved (Private Use)","Reserved (Private Use)","Reserved (Private Use)","Reserved (Private Use)","Reserved (Private Use)","Reserved (Private Use)","Reserved (Private Use)","Reserved (Private Use)","Reserved (Private Use)","Reserved (Private Use)","Reserved (Private Use)","Reserved (Private Use)","Reserved (Private Use)","Reserved (Private Use)","Reserved (Private Use)","Reserved (Private Use)","Reserved (Private Use)","Reserved (Private Use)","Reserved (Private Use)","End"};

		String resp = String.format("Option: (%d) %s \n     length: %d  \n     ", num_option, options[num_option], length);
		int tmp = 0;
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
		    	sj.add(String.format("(%d) %s", code, options[code]));
		    }
			return resp + sj.toString();
		// Vender class Identifier
		case 60:
			return resp + calcule_name(trame, indice, length, "Vender class identifier: ") ;
			// Vender class Identifier
		case 61:
			return resp + calcule_name(trame, indice, length, "Client identifier: ") ;
		// Server Nmae
		case 66:
			return resp + calcule_name(trame, indice, length, "TFTP Sever Name: ");
		case 67:
			return resp + calcule_name(trame, indice, length, "Bootfile name: ");
		case 121:
			resp += trame.parseHexa(indice, indice+length);
			return resp ;
		case 150:
			resp += trame.parseHexa(indice, indice+length);
			return resp ;
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
	
	public int calcule_Option_Inconnue(Donnees trame, int indice) {
		StringBuilder sj = new StringBuilder("Option: "+ trame.parseInt(0));
		int length= trame.parseInt(indice+1);
		sj.append("\nLength: "+ length);
		sj.append("\nValue: " + trame.parseHexa(indice+ 2, indice+2+length));
		options.add(sj.toString());
		return 3;
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
	
	public String calcule_name(Donnees trame, int indice, int length, String s) {
	    StringBuilder sb= new StringBuilder(s);
		for (int i=0; i<length; i++) {
	    	int code= trame.parseInt(indice+1);
	    	sb.append((char) trame.parseInt(indice++));
	    }
		return sb.toString();
	}
}
