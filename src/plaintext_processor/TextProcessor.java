package plaintext_processor;
//HAVE CAPABILITIES FOR TAGGING ACRONYMS/THINGS THAT ARE ALL CAPS



/**
 * An interface that preprocesses a user's category_classifier query that allows multiple
 * types of processing algorithms to be implemented underneath.
 * 
 * @author data
 *
 */

public interface TextProcessor {
	/**
	 * Processes the user's query by lemmatizing & removing stopwords.
	 * 
	 * @param query
	 *            is the user's original category_classifier
	 * @return the user's processed category_classifier
	 */
	String process(String query);

	/**
	 * Processes the given document and creates an annotated document that
	 * contains POSTagging, Lemmas, and whether word is StopWord
	 * 
	 * @param document
	 *            the document to be processed
	 * @return whether processing successful
	 */
	boolean docProcess(String document);

	/**
	 * Lemmatizes the user's category_classifier.
	 * 
	 * @param query
	 *            is the user's original category_classifier
	 * @return the user's stemmed category_classifier
	 */
	String lemmatize(String query);

	/**
	 * Lemmatizes the given document and creates an annotated document that
	 * contains POSTagging & Lemmas for the words
	 * 
	 * @param string
	 *            the document to be processed
	 * @return whether lemmatization successful
	 */
	boolean docLemmatize(String document);

	/**
	 * removes stop words in user's category_classifier
	 * 
	 * @param query
	 *            the user's original category_classifier
	 * @return the user's stemmed category_classifier
	 */
	String removeStopWords(String query);
}
