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
	Donnees donnees;
	Ethernet eth;
	IP ip;
	UDP udp;
	Couche7 couche7;

	// Decode la trame
	public Trame(Donnees trame) {
		donnees = trame;
		// On vérifie si la trame est incomplete
		if (trame.isIncomplete())return;
		try {
			// Initialisation des champs
			eth= new Ethernet(donnees);
			ip = calculeIP();
			udp = calculeUDP();
			if (udp!=null)
				couche7 = calculeCouche7();
		}
		catch( Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String toString() {
		if (donnees.isIncomplete()){
			return String.format("La trame est incomplete:\n Il manque des octets au fichier à la ligne %d \n ligne: %s", donnees.num_ligne_incomplete, donnees.ligne_incomplete);
		}		
		StringJoiner sb = new StringJoiner("\n=================================\n");
		sb.add(eth.toString());
		sb.add(ip.toString());
		if (udp!=null)
			sb.add(udp.toString());
		else sb.add("Protocole autre que udp");
		if (couche7!=null)
			sb.add(couche7.toString());
		else sb.add("Protocole autre que DHCP et DNS");
		return sb.toString();	
	}
	// Decode l'entête IP de la trame
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
	// Decode l'entête UDP de la trame
	private UDP calculeUDP() {
		if (ip instanceof IPv4 && ((IPv4) ip).protocol==17)
			return new UDP(donnees);
		else return null;
	}
	// Decode la couche7, determine si c'est une entête DNS ou DHCP dans la trame
	private Couche7 calculeCouche7() {
		donnees= donnees.subDonnees(8);
		// On regarde les numéros de port Source et Destination
		// On verifie si on utilise DNS ou DHCP
		if (udp.portSrc==53 || udp.portDest ==53)
			return new DNS(donnees);
		else {
			if (udp.portSrc==67 || udp.portDest ==67)
				return new DHCP(donnees);
			else return null;
		}
	}
}
