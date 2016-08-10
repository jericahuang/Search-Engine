package engine;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import edu.stanford.nlp.ling.CoreAnnotations.*;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import plaintext_processor.CoreProcessor;
import plaintext_processor.TextProcessor;


/**
 * Encapsulates a map from search term to frequency (count).
 *
 * @author downey
 *
 */
public class TermCounter {

	private Map<String, Double> map;
	private Map<String, Double> tfVal;
	private String label;
	private int numTokens;
	private boolean firstPara = true;

	public TermCounter(String label) {
		this.label = label;
		this.map = new HashMap<String, Double>();
		this.tfVal = new HashMap<String, Double>();
	}

	public String getLabel() {
		return label;
	}

	/**
	 * Returns the total of all counts.
	 *
	 * @return
	 */
	public int size() {
		int total = 0;
		for (Double value: map.values()) {
			total += value;
		}
		return total;
	}

	/**
	 * Takes a collection of Elements and counts their words.
	 *
	 * @param paragraphs
	 */
	public void processElements(Elements paragraphs) {
		for (Node node: paragraphs) {
			processTree(node);
		}

		for (String term : keySet()) {
			Double count = map.get(term);
			Double tf_score = ((double) count) / ((double) numTokens);
			this.tfVal.put(term, tf_score);
			//System.out.println("TEST  " + ((double) count)/array.length);
			//System.out.println(tf_score);
		}
		//System.out.println("SIZE " + array.length);
	}

	/**
	 * Finds TextNodes in a DOM tree and counts their words.
	 *
	 * @param root
	 */
	public void processTree(Node root) {
		// NOTE: we could use select to find the TextNodes, but since
		// we already have a tree iterator, let's use it.
		for (Node node: new WikiNodeIterable(root)) {
			if (node instanceof TextNode) {
				processText(((TextNode) node).text());
			}
		}
	}

	/**
	 * Splits `text` into words and counts them.
	 *
	 * @param text  The text to process.
	 */
	public void processText(String text) {

		TextProcessor test = new CoreProcessor();

		// replace punctuation with spaces, convert to lower case, and split on whitespace
		String[] array = test.process(text).replaceAll("\\pP", " ").toLowerCase().split("\\s+");

		for (int i=0; i<array.length; i++) {
			String term = array[i];
			incrementTermCount(term);
			numTokens++;
		}
	}

	/**
	 * Increments the counter associated with `term`.
	 *
	 * @param term
	 */
	public void incrementTermCount(String term) {
		// System.out.println(term);
		put(term, get(term) + 1);
	}

	/**
	 * Adds a term to the map with a given count.
	 *
	 * @param term
	 * @param d
	 */
	public void put(String term, double d) {
		map.put(term, d);
	}

	public double getTfVal(String term) {
		//return tfVal.get(term);
		Double count = tfVal.get(term);
		return count == null ? 0 : count;
	}

	/**
	 * Returns the count associated with this term, or 0 if it is unseen.
	 *
	 * @param term
	 * @return
	 */
	public Double get(String term) {
		Double count = map.get(term);
		return count == null ? 0 : count;
	}

	/**
	 * Returns the set of terms that have been counted.
	 *
	 * @return
	 */
	public Set<String> keySet() {
		return map.keySet();
	}

	/**
	 * Print the terms and their counts in arbitrary order.
	 */
	public void printCounts() {
		for (String key: keySet()) {
			Double count = get(key);
			System.out.println(key + ", " + count);
		}
		System.out.println("Total of all counts = " + size());
		System.out.println();

		for (String key : tfVal.keySet()) {
			System.out.println(key + " " + tfVal.get(key));
		}
		System.out.println(tfVal.size());
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		String url = "https://en.wikipedia.org/wiki/Google";

		WikiFetcher wf = new WikiFetcher();
		Elements paragraphs = wf.fetchWikipedia(url);

		TermCounter counter = new TermCounter(url.toString());
		counter.processElements(paragraphs);
		counter.printCounts();
	}
}
