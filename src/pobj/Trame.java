package pobj;

import java.util.StringJoiner;
import pobj.champs.Couche7;
import pobj.champs.DHCP;
import pobj.champs.DNS;
import pobj.champs.Ethernet;
import pobj.champs.IP;
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
			eth= calculeEthernet();
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
	
	// Fonctions pour le constructeur
	private Ethernet calculeEthernet() {
		return new Ethernet(donnees);
	}
	
	private IP calculeIP() {
		donnees = donnees.subDonnees(14);
		return new IP(donnees);
	}
	
	private UDP calculeUDP() {
		donnees = donnees.subDonnees(ip.IHL*4);
		donnees.affiche();
		return new UDP(donnees);
	}
	
	private Couche7 calculeCouche7() {
		donnees= donnees.subDonnees(8);
		donnees.affiche();
		System.out.println("taille: " +donnees.size());
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
