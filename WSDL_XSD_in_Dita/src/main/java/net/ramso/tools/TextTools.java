package net.ramso.tools;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class TextTools {

	public static final Pattern DIACRITICS_AND_FRIENDS = Pattern
			.compile("[\\p{InCombiningDiacriticalMarks}\\p{IsLm}\\p{IsSk}]+");

	public static String EliminaAcentos(String str) {
		str = str.replace("Á", "A").replace("É", "E").replace("Í", "I").replace("Ó", "O").replace("Ú", "U")
				.replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o").replace("ú", "u");
		return str;
	}

	public static String stripDiacritics(String str) {
		// TODO: Estto elimina tambien las ñ

		str = Normalizer.normalize(str, Normalizer.Form.NFD);

		str = DIACRITICS_AND_FRIENDS.matcher(str).replaceAll("");
		return str;
	}
}
