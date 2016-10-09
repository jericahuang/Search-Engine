package plaintext_processor;


import java.util.*;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.pipeline.XMLOutputter;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.ling.CoreAnnotations.*;

public class CoreLemmatizer implements TextProcessor {
	@Override
	public String lemmatize(String query) {
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		String text = "These companies using bunnies and mathematical axes had many cars.";

		System.out.println("testing text:\n" + text + "\n");

		Annotation document = pipeline.process(text);

		for (CoreMap sentence : document.get(SentencesAnnotation.class)) {
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				String word = token.getString(TextAnnotation.class);
				String lemma = token.getString(LemmaAnnotation.class);
				String POS = token.getString(PartOfSpeechAnnotation.class);
				System.out.println("lemmatized version :" + lemma + "; " + POS);
			}
		}
		return null;
	}

	@Override
	public String removeStopWords(String query) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String process(String query) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean docProcess(String document) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean docLemmatize(String document) {
		// TODO Auto-generated method stub
		return false;
	}

}
