package plaintext_processor;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import org.apache.lucene.analysis.core.StopAnalyzer;

import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.ling.CoreAnnotations.*;

public class CoreProcessor implements TextProcessor {
	private StanfordCoreNLP pipeline;
	private static Set<?> stopWords = StopAnalyzer.ENGLISH_STOP_WORDS_SET;
	private boolean setupForProc;
	private static Charset ENCODING = StandardCharsets.UTF_8;

	@Override
	public String process(String query) {
		if (!setupForProc) {
			Properties props = new Properties();
			props.put("annotators", "tokenize, ssplit, pos, lemma, stopword-plemma");
			props.setProperty("customAnnotatorClass.stopword-plemma", "plaintext_processor.StopwordPostLemmaAnnotator");
			pipeline = new StanfordCoreNLP(props);
			setupForProc = true;
		}

		Annotation document = pipeline.process(query.toLowerCase());

		StringBuilder procText = new StringBuilder();

		for (CoreMap sentence : document.get(SentencesAnnotation.class)) {
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				String word = token.getString(TextAnnotation.class);
//				System.out.println(word);
				word = word.replaceAll("\\p{Punct}|\\d", "");
//				System.out.println(word);
//				System.out.println(word.equals(""));
				String lemma = token.getString(LemmaAnnotation.class)/*.replaceAll("\\p{Punct}|\\d", "")*/;
				String pos = token.getString(PartOfSpeechAnnotation.class);
				if (!(stopWords.contains(lemma)) && !word.equals("") && !lemma.contains("-")) {
					procText.append(lemma);
					procText.append(" ");
				}
			}
		}

		return procText.toString();
	}

	public boolean docProcess(String document) {
		if (!setupForProc) {
			Properties props = new Properties();
			props.put("annotators", "tokenize, ssplit, stopword, pos, lemma");
			props.setProperty("customAnnotatorClass.stopword", "category_classifier.StopwordAnnotator");
			pipeline = new StanfordCoreNLP(props);
			setupForProc = true;
		}

		Path pathin = Paths.get(document);
		try {
			BufferedReader reader = Files.newBufferedReader(pathin, ENCODING);
			File file = new File(pathin.toString() + ".out");
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);

			String line = null;

			while ((line = reader.readLine()) != null) {
				// System.out.println("processing: " + line);
				// System.out.println("processed: " + process(line));
				// String proc = process(line);
				// bw.write(proc);
				bw.write(process(line));
			}
			bw.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public String lemmatize(String query) {
		// checks if language model has already been loaded
		if (pipeline == null) {
			Properties props = new Properties();
			props.put("annotators", "tokenize, ssplit, pos, lemma");
			pipeline = new StanfordCoreNLP(props);
			setupForProc = false;
		}

		Annotation document = pipeline.process(query);

		StringBuilder str = new StringBuilder();

		for (CoreMap sentence : document.get(SentencesAnnotation.class)) {
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				String word = token.getString(TextAnnotation.class);
				str.append(word + " ");
				String lemma = token.getString(LemmaAnnotation.class);
				String POS = token.getString(PartOfSpeechAnnotation.class);
				System.out.println("lemmatized version :" + lemma + "; " + POS);
			}
		}
		return str.toString();
	}

	public boolean docLemmatize(String document) {
		// checks if language model has already been loaded
		if (pipeline == null) {
			Properties props = new Properties();
			props.put("annotators", "tokenize, ssplit, pos, lemma");
			pipeline = new StanfordCoreNLP(props);
			setupForProc = false;
		}

		Collection<File> docList = new ArrayList<File>();
		docList.add(new File(document));
		try {
			pipeline.processFiles(docList);
			return true;
		} catch (IOException e) {
			System.out.println("*******Lemmatization Interrupted*******");
			e.printStackTrace();
			return false;
		}
	}

	@Override
	// HAS NOT BEEN IMPLEMENTED YET
	public String removeStopWords(String query) {
		// TODO IMPLEMENT STILL
		// checks if language model has already been loaded
		if (pipeline == null) {
			Properties props = new Properties();
			props.put("annotators", "tokenize, ssplit, stopword-plemma");
			pipeline = new StanfordCoreNLP(props);
			setupForProc = false;
		}

		return null;
	}
}