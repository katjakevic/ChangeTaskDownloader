package ctdownloader.util;

public class Punctuation {
	
	public String removeAllPunctuation(String term) {
		String t = term
				.replaceAll(
						"[< | > | & | \\- | \\+ | \\? | \" | @ | * | # | % | / | \\( | \\) | = | \\^ | ' | ! | \\{ | \\} | \\[ | \\] | \\$ | , | ; | : | \\. | _ | [0-9] ]",
						"");
		return t;

	}
	
	
	public String removeAllPunctuationButNumbers(String term) {
		String t = term
				.replaceAll(
						"[< | > | & | \\- | \\+ | \\? | \" | @ | * | # | % | / | \\( | \\) | = | \\^ | ' | ! | \\{ | \\} | \\[ | \\] | \\$ | , | ; | : | \\. | _ ]",
						"");
		return t;

	}

}
