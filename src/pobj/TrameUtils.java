package pobj;
import java.util.ArrayList;
import java.util.StringJoiner;

public class TrameUtils {
	// Retourne une partie du tableau entre x et y (exclus)
	public static String[] split(String[] trame, int x, int y) {
		String[] t= new String[y-x];
		for (int i=x; i<y; i++) t[i] =trame[i];
		return t;
	}
	
	public static String[] split(String[] trame, int x) {
		String[] t= new String[trame.length-x];
		for (int i=x; i<trame.length; i++) t[i] =trame[i];
		return t;
	}
	
	// Retourne une liste de String en String
	public static String listToString(String[] trame) {
		StringJoiner sb= new StringJoiner(" ");
		for (String s: trame) sb.add(s);
		return sb.toString();
	}
}

