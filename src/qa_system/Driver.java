package qa_system;

import java.io.IOException;

import engine.JedisMaker;
import redis.clients.jedis.Jedis;

public class Driver {

	public static void main(String [] args) throws IOException {

		Jedis jedis = JedisMaker.make();
		jedis.del("jobQueue");

		String query = "Where was Barack Obama born?";

		QuestionProcessor qp = new QuestionProcessor();

		String answer = qp.getResponse(query);
		qp.print();

		System.out.println(answer);

	}
}
