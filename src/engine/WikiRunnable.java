package engine;

import java.io.IOException;
import java.util.Queue;

import redis.clients.jedis.Jedis;

public class WikiRunnable implements Runnable{

	public WikiCrawler wc;
	Jedis jedis;

	public WikiRunnable(String source, JedisIndex index, Jedis jedis){
		wc = new WikiCrawler(source, index, jedis);
		this.jedis = jedis;
	}

	public void run(){
		try {
			String result;

			System.out.println(jedis.llen("jobQueue"));
			while (jedis.llen("jobQueue") > 0){
				System.out.println("hi");
				result = wc.crawl(false);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}