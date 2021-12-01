package pobj.champs;

import pobj.Donnees;

public class DNS implements Couche7 {
	private String id, flags;
	private String question, answer;
	private String autority, additional;
	private String questions;
	private String answers;
	private String autorityAdd;
	private String addInfo;
	
	public DNS(Donnees trame) {
		Object indentifiant;
		id= trame.get(0,2);
		flags= trame.get(2,4);
	}
}
