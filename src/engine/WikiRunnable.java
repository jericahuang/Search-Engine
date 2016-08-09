package engine;

import java.io.IOException;
import java.util.Queue;

public class WikiRunnable implements Runnable{
	
	public WikiCrawler wc;
	
	public WikiRunnable(String source, JedisIndex index){
		wc = new WikiCrawler(source, index);	
	}
	
	public void run(){
		try {
			String result;
			Queue<String> crawlQueue = wc.getQueue();
			while (crawlQueue.poll() != null){
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
