package engine;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.jsoup.select.Elements;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

/**
 * Represents a Redis-backed web search index.
 *
 */
public class JedisIndex {

	private Jedis jedis;
	private static final double TITLE_WEIGHT = 1.5;

	/**
	 * Constructor.
	 *
	 * @param jedis
	 */
	public JedisIndex(Jedis jedis) {
		this.jedis = jedis;
	}

	/**
	 * Returns the Redis key for a given search term.
	 *
	 * @return Redis key.
	 */
	private static String urlSetKey(String term) {
		return "URLSet:" + term.toLowerCase();
	}

	/**
	 * Returns the Redis key for a URL's TermCounter.
	 *
	 * @return Redis key.
	 */
	private String termCounterKey(String url) {
		return "TermCounter:" + url;
	}

	/**
	 * Checks whether we have a TermCounter for a given URL.
	 *
	 * @param url
	 * @return
	 */
	public boolean isIndexed(String url) {
		String redisKey = termCounterKey(url);
		return jedis.exists(redisKey);
	}

	/**
	 * Adds a URL to the set associated with `term`.
	 *
	 * @param term
	 * @param tc
	 */
	public void add(String term, TermCounter tc) {
		jedis.sadd(urlSetKey(term), tc.getLabel());
	}

	public Long getNumDocs() {
		//System.out.println(jedis.scard("URL"));
		//Set<String> s = jedis.smembers("URLS");
		return jedis.scard("URLS");
	}

	/**
	 * Looks up a search term and returns a set of URLs.
	 *
	 * @param term
	 * @return Set of URLs.
	 */
	public Set<String> getURLs(String term) {
		Set<String> set = jedis.smembers(urlSetKey(term));

		return set;
	}

	public Double getURLSetSize(String term) {
		double setSize = (double) jedis.scard(urlSetKey(term));
		return setSize;
	}

	/**
	 * Looks up a term and returns a map from URL to count.
	 *
	 * @param term
	 * @return Map from URL to count.
	 */
	public Map<String, Double> getCounts(String term) {
		Map<String, Double> map = new HashMap<String, Double>();
		Set<String> urls = getURLs(term);
		for (String url: urls) {
			Double count = getCount(url, term);
			map.put(url, count);
		}
		return map;
	}

	public boolean checkTitleForTerm(String checkUrl, String checkTerm){
		
		int titleStInd = checkUrl.lastIndexOf("/");
		System.out.println("last index is " + titleStInd);
		if (checkUrl.toLowerCase().contains(checkTerm.toLowerCase())){
			return true;
		}
		else {
			return false;
		}
		
	}
	
	/**
	 * Looks up a term and returns a map from URL to count.
	 *
	 * @param term
	 * @return Map from URL to count.
	 */
	public Map<String, Double> getCountsFaster(String term) {
		// convert the set of strings to a list so we get the
		// same traversal order every time
		List<String> urls = new ArrayList<String>();
		urls.addAll(getURLs(term));

		// construct a transaction to perform all lookups
		Transaction t = jedis.multi();
		for (String url: urls) {
			String redisKey = termCounterKey(url);
			//System.out.println(redisKey);
			t.hget(redisKey, term);
			//System.out.println("Counter " + t.hget(redisKey, term));
		}
		List<Object> res = t.exec();

		// iterate the results and make the map
		Map<String, Double> map = new HashMap<String, Double>();
		int i = 0;
		for (String url: urls) {
			boolean titleHasTerm;
			String url2 = "https://en.wikipedia.org/wiki/Java_(programming_language)";
			String term2 = "java";
			titleHasTerm = checkTitleForTerm(url, term);
			System.out.println("TERM: " + term + " URL: " + url + " CONTAINS: " + titleHasTerm);
			
//			System.out.println("URL " + url);
//			System.out.println("NUMBER OF DOCUMENTS: " + getNumDocs());
//			System.out.println(term + " " + getURLSetSize(term));
//			System.out.println("IDF Val " + Math.log10(getNumDocs()/getURLSetSize(term)));

			Double idf = Math.log10(getNumDocs()/getURLSetSize(term));
			if (getNumDocs()/getURLSetSize(term) == 1.0) idf = Math.log10(1.000111);

			Double count = (new Double((String) res.get(i++))) * (idf * Math.pow(10, 4));
			
			System.out.println("COUNT BEFORE " + count);
			if (titleHasTerm){
				count = count*TITLE_WEIGHT;
				System.out.println("COUNT AFTER " + count);
			}

			map.put(url, count);
		}
		return map;
	}

	/**
	 * Returns the number of times the given term appears at the given URL.
	 *
	 * @param url
	 * @param term
	 * @return
	 */
	public Double getCount(String url, String term) {
		String redisKey = termCounterKey(url);
		String count = jedis.hget(redisKey, term);
		return new Double(count);
	}

	/**
	 * Add a page to the index.
	 *
	 * @param url         URL of the page.
	 * @param paragraphs  Collection of elements that should be indexed.
	 */
	public void indexPage(String url, Elements paragraphs) {
		System.out.println("Indexing " + url);

		// make a TermCounter and count the terms in the paragraphs
		TermCounter tc = new TermCounter(url);
		tc.processElements(paragraphs);

		// push the contents of the TermCounter to Redis
		pushTermCounterToRedis(tc);
	}

	/**
	 * Pushes the contents of the TermCounter to Redis.
	 *
	 * @param tc
	 * @return List of return values from Redis.
	 */
	public List<Object> pushTermCounterToRedis(TermCounter tc) {
		Transaction t = jedis.multi();

		String url = tc.getLabel();
		String hashname = termCounterKey(url);

		// if this page has already been indexed; delete the old hash
		t.del(hashname);

		// for each term, add an entry in the termcounter and a new
		// member of the index
		for (String term: tc.keySet()) {
			Double count = tc.getTfVal(term);
			System.out.println(term + "  " + count);
			t.hset(hashname, term, count.toString());
			t.sadd(urlSetKey(term), url);
		}
		List<Object> res = t.exec();
		return res;
	}

	/**
	 * Prints the contents of the index.
	 *
	 * Should be used for development and testing, not production.
	 */
	public void printIndex() {
		// loop through the search terms
		for (String term: termSet()) {
			System.out.println(term);

			// for each term, print the pages where it appears
			Set<String> urls = getURLs(term);
			for (String url: urls) {
				Double count = getCount(url, term);
				System.out.println("    " + url + " " + count);
			}
		}
	}

	/**
	 * Returns the set of terms that have been indexed.
	 *
	 * Should be used for development and testing, not production.
	 *
	 * @return
	 */
	public Set<String> termSet() {
		Set<String> keys = urlSetKeys();
		Set<String> terms = new HashSet<String>();
		for (String key: keys) {
			String[] array = key.split(":");
			if (array.length < 2) {
				terms.add("");
			} else {
				terms.add(array[1]);
			}
		}
		return terms;
	}

	/**
	 * Returns URLSet keys for the terms that have been indexed.
	 *
	 * Should be used for development and testing, not production.
	 *
	 * @return
	 */
	public Set<String> urlSetKeys() {
		return jedis.keys("URLSet:*");
	}

	/**
	 * Returns TermCounter keys for the URLS that have been indexed.
	 *
	 * Should be used for development and testing, not production.
	 *
	 * @return
	 */
	public Set<String> termCounterKeys() {
		return jedis.keys("TermCounter:*");
	}

	/**
	 * Deletes all URLSet objects from the database.
	 *
	 * Should be used for development and testing, not production.
	 *
	 * @return
	 */
	public void deleteURLSets() {
		Set<String> keys = urlSetKeys();
		Transaction t = jedis.multi();
		for (String key: keys) {
			t.del(key);
		}
		t.del("URLS");
		t.exec();
	}

	/**
	 * Deletes all URLSet objects from the database.
	 *
	 * Should be used for development and testing, not production.
	 *
	 * @return
	 */
	public void deleteTermCounters() {
		Set<String> keys = termCounterKeys();
		Transaction t = jedis.multi();
		for (String key: keys) {
			t.del(key);
		}
		t.exec();
	}

	/**
	 * Deletes all keys from the database.
	 *
	 * Should be used for development and testing, not production.
	 *
	 * @return
	 */
	public void deleteAllKeys() {
		Set<String> keys = jedis.keys("*");
		Transaction t = jedis.multi();
		for (String key: keys) {
			t.del(key);
		}
		t.exec();
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		Jedis jedis = JedisMaker.make();
		JedisIndex index = new JedisIndex(jedis);

//		index.deleteTermCounters();
//		index.deleteURLSets();
//		index.deleteAllKeys();
		loadIndex(index);

		Map<String, Double> map = index.getCountsFaster("the");
		for (Entry<String, Double> entry: map.entrySet()) {
			System.out.println(entry);
		}


		Set<String> set = jedis.smembers(urlSetKey("java"));
		for (String s : set) {
			System.out.println(s);
		}

		System.out.println(jedis.smembers("URLS").size());

	}

	/**
	 * Stores two pages in the index for testing purposes.
	 *
	 * @return
	 * @throws IOException
	 */
	private static void loadIndex(JedisIndex index) throws IOException {
		WikiFetcher wf = new WikiFetcher();

		/*
		String url = "https://en.wikipedia.org/wiki/Java_(programming_language)";
		Elements paragraphs = wf.readWikipedia(url);
		index.indexPage(url, paragraphs);


		url = "https://en.wikipedia.org/wiki/Programming_language";
		paragraphs = wf.readWikipedia(url);
		index.indexPage(url, paragraphs);
		 */
	}
}
