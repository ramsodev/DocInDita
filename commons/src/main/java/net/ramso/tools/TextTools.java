package net.ramso.tools;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class TextTools {
	private TextTools() {
		super();
	}

	public static final Pattern DIACRITICS_AND_FRIENDS = Pattern
			.compile("[\\p{InCombiningDiacriticalMarks}\\p{IsLm}\\p{IsSk}]+"); //$NON-NLS-1$

	public static String eliminaAcentos(String str) {
		str = str.replace("Á", "A").replace("É", "E").replace("Í", "I").replace("Ó", "O").replace("Ú", "U") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$
				.replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o").replace("ú", "u"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$
		return str;
	}

	public static String stripDiacritics(String str) {
		str = Normalizer.normalize(str, Normalizer.Form.NFD);
		str = DIACRITICS_AND_FRIENDS.matcher(str).replaceAll(""); //$NON-NLS-1$
		return str;
	}
}
