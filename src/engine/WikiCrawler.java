package engine;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import redis.clients.jedis.Jedis;

public class WikiCrawler {
	// keeps track of where we started
	private final String source;

	// the index where the results go
	private JedisIndex index;

	private Jedis jedis;

	// queue of URLs to be indexed
	private Queue<String> queue = new ConcurrentLinkedQueue<String>();

	// fetcher used to get pages from Wikipedia
	static WikiFetcher wf = new WikiFetcher();

	/**
	 * Constructor.
	 *
	 * @param source
	 * @param index
	 */
	public WikiCrawler(String source, JedisIndex index, Jedis jedis) {
		this.source = source;
		this.index = index;
		this.jedis = jedis;

		jedis.rpush("jobQueue", source);
		//queue.offer(source);
	}

	/**
	 * Returns the number of URLs in the queue.
	 *
	 * @return
	 */
	public Long queueSize() {
		return jedis.llen("jobQueue");
	}

	public Queue<String> getQueue(){
		return queue;
	}

	/**
	 * Gets a URL from the queue and indexes it.
	 * @param b
	 *
	 * @return Number of pages indexed.
	 * @throws IOException
	 */
	public String crawl(boolean testing) throws IOException {
		if (queueSize() == 0) {
			return null;
		}

		String url = jedis.lpop("jobQueue");
		System.out.println("Crawling " + url);

		if ((testing==false && index.isIndexed(url)) || (jedis.sismember("URLS", url) == true)) {
			System.out.println("Already indexed.");
			return url;
		}

		else {
			Elements paragraphs;
			if (testing) {
				paragraphs = wf.readWikipedia(url);
			} else {
				paragraphs = wf.fetchWikipedia(url);
			}

			index.indexPage(url, paragraphs);
			queueInternalLinks(paragraphs);
			jedis.sadd("URLS", url);
			return url;
		}
	}

	/**
	 * Parses paragraphs and adds internal links to the queue.
	 *
	 * @param paragraphs
	 */
	// NOTE: absence of access level modifier means package-level
	void queueInternalLinks(Elements paragraphs) {
		for (Element paragraph: paragraphs) {
			queueInternalLinks(paragraph);
		}
	}

	/**
	 * Parses a paragraph and adds internal links to the queue.
	 *
	 * @param paragraph
	 */
	private void queueInternalLinks(Element paragraph) {
		Elements elts = paragraph.select("a[href]");
		int counter = 0;
		for (Element elt: elts) {
			String relURL = elt.attr("href");

			if (relURL.startsWith("/wiki/")) {
				String absURL = "https://en.wikipedia.org" + relURL;
				//System.out.println(absURL);

				//TODO: offer to redis list
				jedis.rpush("jobQueue", absURL);
				counter++;
				//queue.offer(absURL);
			}
		}
		//System.out.println("LINK COUNTER " + counter);
	}

	public static void main(String[] args) throws IOException {

		/*
		Jedis jedis = JedisMaker.make();
		Set<String> urls = jedis.smembers("URLS");
		System.out.println(jedis.scard("URLS"));
		Set<String> java = jedis.smembers("URLSet:java");

		for (String url : urls) {
			System.out.println(url);
		}

		System.out.println(jedis.llen("jobQueue"));

		 */
		// make a WikiCrawler
		Jedis jedis = JedisMaker.make();
		JedisIndex index = new JedisIndex(jedis);
		String source = "https://en.wikipedia.org/wiki/Java_(Programming_Language)";
		WikiCrawler wc = new WikiCrawler(source, index, jedis);

		//for testing purposes, load up the queue
		Elements paragraphs = wf.fetchWikipedia(source);
		wc.queueInternalLinks(paragraphs);

		System.out.println();
		// loop until we index a new page
		String res;
		do {
			res = wc.crawl(false);
			System.out.println("Complete");
		} while (res != null);

		Map<String, Double> map = index.getCounts("the");
		for (Entry<String, Double> entry: map.entrySet()) {
			System.out.println(entry);
		}

	}
}