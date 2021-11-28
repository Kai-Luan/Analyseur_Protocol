package pobj;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class TrameUtils {	
	// Retourne une liste de String en String
	public static String listToString(List<String> trame) {
		StringJoiner sb= new StringJoiner(" ");
		for (String s: trame) sb.add(s);
		return sb.toString();
	}
}

