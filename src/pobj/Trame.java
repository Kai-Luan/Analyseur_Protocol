package pobj;

import java.util.StringJoiner;
import pobj.champs.Couche7;
import pobj.champs.DHCP;
import pobj.champs.DNS;
import pobj.champs.Ethernet;
import pobj.champs.IP;
import pobj.champs.IPv4;
import pobj.champs.IPv6;
import pobj.champs.UDP;

public class Trame{
	private Donnees donnees;
	private Ethernet eth;
	private IP ip;
	private UDP udp;
	private Couche7 couche7;
	
	public Trame(Donnees trame) {
		donnees = trame;
		try {
			// Initialisation des champs
			eth= new Ethernet(donnees);
			ip = calculeIP();
			udp = calculeUDP();
			couche7 = calculeCouche7();
		}
		catch( Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String toString() {
		StringJoiner sb = new StringJoiner("\n=================================\n");
		sb.add(eth.toString());
		sb.add(ip.toString());
		sb.add(udp.toString());
		sb.add(couche7.toString());
		return sb.toString();	
	}
	
	private IP calculeIP() {
		IP ip;
		donnees = donnees.subDonnees(14);
		// On calcule la version de l'IP
		String s1= donnees.get(0);
		int version= Integer.parseInt(String.valueOf(s1.charAt(0)));
		if (version==4) ip=  new IPv4(donnees);
		else if (version==6) ip= new IPv6(donnees);
		else throw new IllegalArgumentException("IP: version different de 4 et 6");
		donnees = donnees.subDonnees(ip.length());
		return ip;
	}
	
	private UDP calculeUDP() {
		return new UDP(donnees);
	}
	
	private Couche7 calculeCouche7() {
		donnees= donnees.subDonnees(8);
		// On regarde les numéros de port Source et Destination
		// On verifie si on utilise DNS ou DHCP
		if (udp.portSrc==53 || udp.portDest ==53)
			return new DNS(donnees);
		else {
			if (udp.portSrc==67 || udp.portDest ==67)
				return new DHCP(donnees);
			else throw new IllegalArgumentException();
		}
	}
}
