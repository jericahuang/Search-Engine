package qa_system;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

public abstract class Answer {

	static String prefix = "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
			"PREFIX dbpedia: <http://dbpedia.org/resource/>" +
			"PREFIX dbp-ont: <http://dbpedia.org/ontology/>" +
			"PREFIX geo-ont: <http://www.geonames.org/ontology#>" +
			"PREFIX umbel-sc: <http://umbel.org/umbel/sc/>" +
			"PREFIX owl: <http://www.w3.org/2002/07/owl#>" +
			"PREFIX geonames: <http://sws.geonames.org/>";

	public abstract String executeQuery();

	public String formatObject(String object) {
		int index = object.indexOf("\"", 10);

		return object.substring(8, index);
	}

	public String convertToFreebase(String subject) {

		String s = "dbpedia:" + subject;

		String query = "SELECT ?o WHERE { "+ s+ " owl:sameAs ?o . }";
		ResultSet results = sendQuery(query);
		String freebase = "";

		while(results.hasNext()){
			String soln = results.nextSolution().toString();

			if (soln.length() > 36 && soln.toString().substring(7, 36).equals("<http://rdf.freebase.com/ns/m")) {
				return soln.substring(7,44);
			}
		}

		return null;
	}

	public static ResultSet sendQuery(String queryString) {
		QueryExecution qexec = QueryExecutionFactory.sparqlService("http://factforge.net/sparql", prefix + " " + queryString);

		return qexec.execSelect();
	}
}
