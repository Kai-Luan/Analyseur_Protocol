package pobj.champs;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import pobj.Donnees;

public class DNS implements Couche7 {
	String id;
	String[] flags;
	int question_count, answer_count;
	int authority_count, additional_count;
	List<String[]> questions = new ArrayList<>();
	List<String[]> answers = new ArrayList<>();
	List<String[]> authority = new ArrayList<>();
	List<String[]> additional = new ArrayList<>();
	
	public DNS(Donnees trame) {
		id= trame.parseHexa(0,2);
		flags= calcule_flags(trame);
		question_count = trame.parseInt(4,6);
		answer_count = trame.parseInt(6,8);
		authority_count = trame.parseInt(8,10);
		additional_count = trame.parseInt(10,12);
		// On calcule les Resource Record
		int indice = 12;
		
		indice = calcule_RRs(trame, indice, questions, question_count, 3);
		
		indice = calcule_RRs(trame, indice, answers, question_count, 6);
		//indice = calcule_RRs(trame, indice, authority, authority_count, 6);
		//indice = calcule_RRs(trame, indice, additional, additional_count, 6);
	}

	
	private int calcule_RRs(Donnees trame, int indice, List<String[]> RRs, int count, int taille_RR) {
		
		for (int i=0; i<count; i++) {
			String[] RR = new String[taille_RR];
			// On remplit 3 premier champs de RR
			
			indice = calcule_RR_Simple(trame, indice, RR);
			// Si besoin, on  ajoute avec les 3 autres champs dans RR
			if (taille_RR == 6) {
				indice = calcule_RR_Complet(trame, indice, RR);
			}
			RRs.add(RR);
		}
		
		
		return indice;
	}
	
	// A partir
	private int calcule_RR_Simple(Donnees trame, int indice, String[] champs) {
		// Calcule les champs d'une question
		// Calcule le nom et ajoute dans champs
		
		StringBuilder sb  = new StringBuilder("Name: ");
		indice = calcule_name(trame, indice, sb);
		
		champs[0] = sb.toString();
		// Calcule le type
		champs[1] = "Type: "+calculeType(trame, indice);
		indice+= 2;
		// Calcule la classe
		int val = trame.parseInt(indice, indice+2);
		if (val==1) champs[2] = "Class: IN (0x0001)";
		else champs[2] = "Class: "+trame.parseHexa(indice, indice+2);
		indice+=2;
		return indice;
	}
	
	// Retourne l'indice de la fin du Nom et ajoute le nom de domaine dans sb
	private int calcule_name(Donnees trame, int indice, StringBuilder sb) {
		int length= trame.parseInt(indice);
		while (length!=0) {
			// Si c'est un nom compressé
			if (length >=192) {
				// On recupère le nom à l'octet indiqué par l'offset
				int offset = length-192+ trame.parseInt(indice+1);
				calcule_name(trame, offset, sb);
				indice+=2;
			}
			// Si c'est un nom non-compressé
			else { // On recupère le nom codé en ASCII
				// On recupère les caractères jusqu'à qu'on tombe sur 0x00
				for  (int i=0; i <=length; i++) {
					// On ajoute le caractère décoder dans le nom de domanine
					sb.append((char) trame.parseInt(++indice));
				}
				sb.append(".");
			}
			length= trame.parseInt(indice);
		}
		return ++indice;
	}
	
	private int calcule_RR_Complet(Donnees trame, int indice, String[] champs) {
		champs[3] = "TTL: "+ trame.parseHexa(indice, indice+4);
		indice+=4;
		int data_length = trame.parseInt(indice, indice+2);
		champs[4] = "Data Length: "+ data_length;
		indice+= 2;
		champs[5] = calcule_RData(trame, indice, data_length);
		return indice + data_length;
	}
	
	// Calcule le nom du type correspondant
	private String calculeType(Donnees trame, int indice) {
		int type = trame.parseInt(indice, indice+2);
		switch(type) {
		case 1:
			return "A (0x0001)";
		case 2:
			return "NS  (0x0002)";
		case 5:
			return "CNAME  (0x0005)";
		case 16:
			return "MX 0x000F";
		case 28:
			return "AAAA  (0x001C)";
		default:
			return trame.parseHexa(indice, indice+2);
		}
	}
	
	private String calcule_RData(Donnees trame, int indice, int length) {
		return "RDATA: ";
	}
	
	
	@Override
	public String toString() {
		StringJoiner sj = new StringJoiner("\n  ","DNS Protocol\n  ", "");
		sj.add("Transaction ID: "+ id);
		sj.add("Flags:" + flags);
		sj.add("Questions: " + question_count);
		sj.add("Answer RRs: " + answer_count);
		sj.add("Authority RRs: " + authority_count);
		sj.add("Additional RRs: " + additional_count);
		sj.add(toString_RR(questions, "Queries"));
		sj.add(toString_RR(answers, "Answers"));
		//sj.add(toString_RR(authority, "Authority"));
		//sj.add(toString_RR(additional, "Additional"));
		return sj.toString();
	}
	
	private String toString_RR(List<String[]> list, String s) {
		StringJoiner sj = new StringJoiner("\n       ",s+":\n       ", "\n");
		for (String[] champs: list) 
			for (String ch: champs)
				sj.add(ch);
		return sj.toString();
	}
	
	private String[] calcule_flags(Donnees trame) {
		String[] res= new String[8];
		// QR
		if (trame.parseBit(2, 0)=='0') res[0] = "QR: the message is a response (1)";
		else res[0] = "QR: the message is a query (0)";
		// OpCode
		res[1] = String.format("OpCode: Standard query (%c)", trame.parseBit(2, 1));
		return res;
	}
}
