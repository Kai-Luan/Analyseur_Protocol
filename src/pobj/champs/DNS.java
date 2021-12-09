package pobj.champs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import pobj.Donnees;
// Classe qui decode l'entete DNS
public class DNS implements Couche7 {
	String id;
	String[] flags;
	// Nombre de records dans chaque RRs
	int question_count, answer_count;
	int authority_count, additional_count;
	// RRs; Question, Answer, Authority, Additional
	List<String[]> questions = new ArrayList<>();
	List<String[]> answers = new ArrayList<>();
	List<String[]> authority = new ArrayList<>();
	List<String[]> additional = new ArrayList<>();
	// Met dans un dictoinnaire, tous les noms décodés dans DNS avec leurs position dans l'entête
	Map<Integer, String> noms_domaine= new HashMap<>();
	
	// Constructeur
	public DNS(Donnees trame) {
		id= trame.parseHexa(0,2);
		flags= calcule_flags(trame);
		// Calcule le nombre de records dans chaque RRs
		question_count = trame.parseInt(4,6);
		answer_count = trame.parseInt(6,8);
		authority_count = trame.parseInt(8,10);
		additional_count = trame.parseInt(10,12);
		// On calcule les Resource Record
		int indice = 12;
		
		indice = calcule_RRs(trame, indice, questions, question_count, 3);
		indice = calcule_RRs(trame, indice, answers, answer_count, 6);
		indice = calcule_RRs(trame, indice, authority, authority_count, 6);
		indice = calcule_RRs(trame, indice, additional, additional_count, 6);
	}

	// Ajoute dans RRs les records correspondant,
	// RRs: peut prendre en argument Question, Answer, ..., pour y ajouter ses records
	// taille_RR: 3 (Question), 6 (Answer, Authority, Additional)
	// count: nombre de records dans Question, Answer, Authority ou Additional
	private int calcule_RRs(Donnees trame, int indice, List<String[]> RRs, int count, int taille_RR) {
		// On calcule dans RRs,  "count" records
		for (int i=0; i<count; i++) {
			String[] RR = new String[taille_RR];
			// On remplit 3 premier champs de RR
			indice = calcule_RR_Simple(trame, indice, RR);
			// Si besoin, on  ajoute avec les 3 autres champs dans RR
			if (taille_RR == 6) {
				indice = calcule_RR_Complet(trame, indice, RR);	
			}
			RR[1]= calculeType(RR[1]);
			
			RRs.add(RR);
		}	
		return indice;
	}
	
	// A partir
	private int calcule_RR_Simple(Donnees trame, int indice, String[] champs) {
		// Calcule les champs d'une question
		// Calcule le nom et ajoute dans champs
		
		StringBuilder sb  = new StringBuilder("Name: ");
		
		indice = calcule_name(trame, indice, 255, sb);
		champs[0] = sb.toString();
		// Calcule le type
		champs[1] =  trame.get(indice, indice+2);
		indice+= 2;
		// Calcule la classe
		int val = trame.parseInt(indice, indice+2);
		if (val==1) champs[2] = "Class: IN (0x0001)";
		else champs[2] = "Class: "+trame.parseHexa(indice, indice+2);
		indice+=2;
		return indice;
	}
	
	// Remplit les 3 autres champs de Resource Recor, retourne l'indice de l'octet après RR
	private int calcule_RR_Complet(Donnees trame, int indice, String[] champs) {
		int ttl= trame.parseInt(indice, indice+4);
		champs[3] = "TTL: "+ ttl + conversion_temps(ttl);
		indice+=4;
		int data_length = trame.parseInt(indice, indice+2);
		champs[4] = "Data Length: "+ data_length;
		indice+= 2;
		champs[5] = calcule_RData(trame, indice, data_length, champs[1]);
		return indice + data_length;
	}
	
	// Calcule Record Data selon son type (CNAME, A, AAAA, ...)
	private String calcule_RData(Donnees trame, int indice, int data_length, String typeHexa) {
		StringBuilder sb = new StringBuilder();;
		int typeInt = Integer.parseInt(typeHexa, 16);
		switch(typeInt) {
		// A Record: recupère l'adresse IP
		case 1:
			return "Adress: "+trame.getIP(indice, indice+4);
		// NS Record: récupère le nom de domaine
		case 2:
			calcule_name(trame, indice, data_length, sb);
			return "Name Server: " + sb.toString();
		// CNAME Record:  recupère le nom du domaine 
		case 5:
			calcule_name(trame, indice, data_length, sb);
			return "CNAME: "+ sb.toString();
		// MX Record: récupère le mail et la priorité
		case 15:
			String s = "Priority: "+ trame.parseInt(indice, indice+2);
			sb.append("\nMail exchange: ");
			calcule_name(trame, indice+2, data_length-2, sb);
			return s + sb.toString();
		// AAAA: récupère l'adresse IPv6
		case 28:
			return "Adress: "+ trame.getIPv6(indice, indice+ data_length);
		default:
			return "RData: " + trame.parseHexa(indice, indice+ data_length);
		}
	}
	
	// Calcule le nom du type correspondant selon son type en Hexa
	private String calculeType(String typeHexa) {
		int typeInt = Integer.parseInt(typeHexa, 16);
		switch(typeInt) {
		case 1:
			return "Type: A (0x0001)";
		case 2:
			return "Type: NS  (0x0002)";
		case 5:
			return "Type: CNAME  (0x0005)";
		case 16:
			return "Type: MX (0x000F)";
		case 28:
			return "Type: AAAA  (0x001C)";
		default:
			return "Type: 0x"+typeHexa;
		}
	}
	
	// Retourne l'indice de l'octet après le nom et ajoute le nom de domaine dans sb
	private int calcule_name(Donnees trame, int indice, int Rdata_length,  StringBuilder sb) {
		int indice_initial = indice;
		List<Integer> tab_indice_initial = new ArrayList<>(indice_initial);
		
		int length= trame.parseInt(indice);
		int indice_max= indice+ Rdata_length;
		while (indice< indice_max) {
			// Si c'est un nom compressé
			if (length >=192) { 
				// On recupère le nom à l'octet indiqué par l'offset
				int offset = trame.parseInt(indice, indice+2) - 49152;
				String nom = noms_domaine.get(offset);
				if (nom==null)
					calcule_name(trame, offset, 255, sb);
				else sb.append(nom);
				indice+=2;
				length= trame.parseInt(indice);
				if (length<192) return indice;
			}
			// Si c'est un nom non-compressé
			else { // On recupère le nom codé en ASCII
				// On recupère les caractères jusqu'à qu'on tombe sur 0x00
				tab_indice_initial.add(indice); 
				for  (int i=0; i <length; i++) {
					// On ajoute le caractère décoder dans le nom de domanine
					sb.append((char) trame.parseInt(++indice));
				}
				// si c'est le dernier label, et on retourne indice+2 (à cause du 0x00)
				length =trame.parseInt(++indice);
				if (length==0) return ++indice;
				sb.append(".");
			}
		}
		String nom = sb.toString();
		for (int numero: tab_indice_initial)
			if (!(noms_domaine.containsKey(numero)))
				noms_domaine.put(numero, nom.substring(numero-indice_initial));
		return indice;
	}
	
	// Calcule les flags
	private String[] calcule_flags(Donnees trame) {
		int code;
		String[] res= new String[8];
		String bits = trame.parseBit(2, 4);
		// QR
		if (bits.charAt(0)=='0') res[0] = "QR: the message is a response (1)";
		else res[0] = "QR: the message is a query (0)";
		// OpCode
		code = Integer.parseInt(bits.substring(1, 5), 2);
		res[1] = String.format("OpCode: %s (%d)", opCode(code), code);
		// Authoritative Answer
		if (bits.charAt(5)=='0') 
			res[2] = "(0) = Authoritative: Server is not an authoritative for domaine";
		else res[2]= "(1) = Authoritative: Server is an authoritative for domaine";
		// TrunCation
		if (bits.charAt(6)=='0') 
			res[3] = "(0) = Truncated: Message is not truncated";
		else res[3] = "(1) = Truncated: Message is truncated";
		// Recursion desired
		if (bits.charAt(7)=='0') 
			res[4] = "(0) = Recursion not desired: Don't query recursively";
		else res[4] = "(1) = Recursion desired: Do query recursively";
		// Recursion available
		if (bits.charAt(8)=='0') 
			res[5] = "(0) = Recursion available: Server can't do recursive queries";
		else res[5] = "(1) = Recursion available: Server can do recursive queries";
		// Zero
		if (bits.charAt(9)=='0') 
			res[6] = "Z: reserved (0)";
		else res[6] = "Z: reserved (1) - ! IT'S NOT ZERO !";
		// Answer authenticed:
		if (bits.charAt(10)=='0') 
			res[7] = "(0) = Answer authenticed: Answer/authority portion was not authenticated by the server";
		else res[7] = "(1) = Answer authenticed: Answer/authority portion was authenticated by the server";
		// Non-Authenticated data
		if (bits.charAt(11)=='0') 
			res[7] = "(0) = Non-Authenticated data: Unacceptable";
		else res[7] = "(1) = Non-Authenticated data: Unacceptable";
		// Response Code
		code = Integer.parseInt(bits.substring(12, 16), 2);
		res[1] = String.format("Reply Code: %s (%d)", replyCode(code), code);
		return res;
	}
	
	public String replyCode(int code) {
		switch(code) {
		case 0: return "No error";
		case 1: return "Format Error";
		case 2: return "Server fail";
		case 3: return "Nonexistent domain";
		case 4: return "Not implemented";
		case 5: return "Nonexistent domainQuery refused";
		case 6: return "Name Exists when it should not";
		case 7: return "YXDomain";
		case 8: return "RR Set that should exist does not";
		case 9: return "Server Not Authoritative for zone";
		case 10: return "Name not contained in zone";
		case 11: return "DSO-TYPE Not Implemented";
		default: return "Unassigned";
		}
	}
	
	public String opCode(int code) {
		switch(code) {
		case 0: return "Standard query";
		case 1: return "Inverse query";
		case 2: return "Server Status request";
		case 3: return "Unassigned";
		case 4: return "Notify";
		case 5: return "Update";
		case 6: return "DNS Stateful Operations (DSO)";
		default: return "Unassigned";
		}
	}
	
	@Override
	public String toString() {
		StringJoiner sj = new StringJoiner("\n  ","DNS Protocol\n  ", "");
		sj.add("Transaction ID: "+ id);
		sj.add(toString_flags());
		sj.add("Questions: " + question_count);
		sj.add("Answer RRs: " + answer_count);
		sj.add("Authority RRs: " + authority_count);
		sj.add("Additional RRs: " + additional_count);
		if (question_count!=0)
		sj.add(toString_RRs(questions, "\nQueries"));
		if (answer_count!=0)
			sj.add(toString_RRs(answers, "Answers"));
		if (authority_count!=0)
			sj.add(toString_RRs(authority, "Authoritative nameservers"));
		if (additional_count!=0)
		sj.add(toString_RRs(additional, "Additional records"));
		return sj.toString();
	}
	
	// Retoune un String, contenant la description de RRs
	private String toString_RRs(List<String[]> list, String s) {
		StringJoiner sj = new StringJoiner("\n       ",s+":\n       ", "\n");
		for (String[] champs: list) { 
			sj.add("RR: ");
			for (int i=0; i < champs.length; i++) {
				sj.add(champs[i]);
			}
			sj.add("");
		}
		return sj.toString();
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
	
	private String toString_flags(){
		StringJoiner sj = new StringJoiner("\n       ","Flags:\n       ", "\n");
		for (String subField: flags) { 
			sj.add(subField);
		}
		return sj.toString();
	}
}
