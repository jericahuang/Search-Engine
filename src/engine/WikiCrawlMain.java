package engine;

import java.io.IOException;

import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.concurrent.*;

import com.github.andrewoma.dexx.collection.ArrayList;

public class WikiCrawlMain {

	private static final int THREAD_SIZE = 10;
	String sourceWCM;
	JedisIndex indexWCM;

	public WikiCrawlMain(String source, JedisIndex index){
		sourceWCM = source;
		indexWCM = index;
	}

	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException{
		// TODO Auto-generated method stub

		Jedis jedis = JedisMaker.make();
		JedisIndex index = new JedisIndex(jedis);
		String source = "https://en.wikipedia.org/wiki/Java_(programming_language)";

		WikiCrawlMain wcm = new WikiCrawlMain(source, index);
		ArrayList<Future<?>> futureList = new ArrayList<Future<?>>();

		ThreadPoolExecutor threads = (ThreadPoolExecutor) Executors.newFixedThreadPool(THREAD_SIZE);

		for(int i = 0; i < THREAD_SIZE; i++){
			WikiRunnable wr = new WikiRunnable(source, index, jedis);
			Future<?> futureVal = threads.submit(wr);
			futureList.append(futureVal);
			System.out.println("HI");
			wr.run();

		}

		for(Future<?> futureE: futureList){
			futureE.get();
		}

		threads.shutdown();

	}

}