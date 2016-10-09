package Jena;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.query.Syntax;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;

public class Sparql {

	public static void sparqlTest()
	{
		/*String queryString = "SELECT ?o WHERE {"+
                          "?s ?p ?o ."+
                          "} LIMIT 10";*/
		String str="Obama";

		/*
		String queryString =
				"PREFIX p: "+
						"PREFIX dbpedia: "+
						"PREFIX category: "+
						"PREFIX rdfs: "+
						"PREFIX skos: "+
						"PREFIX geo: "+

						"SELECT DISTINCT ?m ?n ?p ?d"+
						"WHERE {"+
						" ?m rdfs:label ?n."+
						" ?m skos:subject ?c."+
						" ?c skos:broader category:Churches_in_Paris."+
						" ?m p:abstract ?d."+
						" ?m geo:point ?p"+
						" FILTER ( lang(?n) = 'fr' )"+
						" FILTER ( lang(?d) = 'fr' )"+
						" }";
		 */


		String prefix = "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
			"PREFIX dbpedia: <http://dbpedia.org/resource/>" +
			"PREFIX dbp-ont: <http://dbpedia.org/ontology/>" +
			"PREFIX geo-ont: <http://www.geonames.org/ontology#>" +
			"PREFIX umbel-sc: <http://umbel.org/umbel/sc/>" +
			"PREFIX owl: <http://www.w3.org/2002/07/owl#>" +
			"PREFIX geonames: <http://sws.geonames.org/>";

			String queryString = prefix + "SELECT DISTINCT ?Company ?Location" +
			"WHERE {" +
			   "?Company rdf:type dbp-ont:Company ;" +
			             "dbp-ont:industry dbpedia:Computer_software ;" +
			             "dbp-ont:foundationPlace ?Location ." +
			    "?Location geo-ont:parentFeature ?o." +
			    "?o geo-ont:parentCountry dbpedia:United_States . }";

			String gf = prefix  + " SELECT ?o WHERE { dbpedia:Barack_Obama owl:sameAs ?o  . }";
			String q = prefix + " SELECT ?o WHERE { <http://rdf.freebase.com/ns/m.02mjmr> <http://rdf.freebase.com/ns/people.person.place_of_birth> ?o  . }";


		//String queryString = " DESCRIBE ?object WHERE {<http://dbpedia.org/resource/Google> ?predicate ?object .}";



		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.sparqlService("http://factforge.net/sparql", q);
		try
		{
			ResultSet results = qexec.execSelect();
			while(results.hasNext()){
				QuerySolution soln = results.nextSolution();
				//Literal name = soln.getLiteral("x");
				System.out.println(soln);
			}
		}
		finally{
			qexec.close();
		}

	}

	public static void main(String[]args){
		sparqlTest();
	}

}




