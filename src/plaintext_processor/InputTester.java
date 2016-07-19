package plaintext_processor;

//import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations.*;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class InputTester {

	public static void main(String[] args) throws Exception {
		TextProcessor test = new CoreProcessor();

		String text1 = "These companies using these bunnies and mathematical axes had many cars (4); 'This,' I say, 'is not good; it is bad***";
		String text2 = "What ponies are killed by wood axe in the forest?";
		String text3 = "{0]][]9]0]}b-";
//		text3 = text3.replaceAll("\\p{Punct}|\\d", "");

		System.out.println(test.process(text1));
		System.out.println(test.process(text2));
		System.out.println(test.process(text3));

		//test.docProcess("test.txt");

		//test.docProcess("Frankenstein.txt");
//
//		Properties props = new Properties();
//		props.put("annotators", "tokenize, ssplit, pos");
//		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
//		Annotation document = pipeline.process(text1.toLowerCase());
//		for (CoreMap sentence : document.get(SentencesAnnotation.class)){
//			for (CoreLabel token : sentence.get(TokensAnnotation.class)){
//				String pos = token.getString(PartOfSpeechAnnotation.class);
//				System.out.println(pos);
//			}
//		}
//
//		System.out.println(text1.replaceAll("\\p{Punct}|\\d", ""));
	}
}