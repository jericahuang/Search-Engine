package benchmark_tests;

import static org.junit.Assert.*;

import java.util.Comparator;

import org.junit.Before;
import org.junit.Test;

public class search_performanceTest {



	@Before
	public void setUp() throws Exception {

	}

	//Case: exact search query is indexed
	@Test
	public void testIndexedQuery() {
		fail("Not yet implemented");
	}

	//Case: Not all words/terms in query are indexed but some are
	@Test
	public void test() {
		fail("Not yet implemented");
	}

	//Case: No terms in the search query are indexed but then uses ontologies to improve accuracy
	@Test
	public void testOntology() {
		fail("Not yet implemented");
	}

	//Case: No terms in the search query are indexed
	//		Make sure to index all relevant pages after performing manual lookup
	@Test
	public void testUnindexedSlowLookUp() {
		fail("Not yet implemented");
	}


}
