package qa_system;

import java.util.ArrayList;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

public class Who extends Answer{

	String [] query;
	String subject;
	String predicate;

	public Who(String query, String subj, String pred) {
		super();
		this.query = query.split(" ");
		this.subject = subj;
		this.predicate = pred;
	}

	public String executeQuery() {
		// TODO Auto-generated method stub
		String freebase = convertToFreebase(this.subject.substring(0,1).toUpperCase() +  "" + this.subject.substring(1));

		String ans = "";

		switch(predicate) {
		case "founded": String query = "SELECT ?o WHERE { " + freebase + " <http://dbpedia.org/ontology/foundedBy> ?o . }";
				   ResultSet results = sendQuery(query);
				   ArrayList<String> answers = new ArrayList<String>();

				   while(results.hasNext()){
						String temp = results.nextSolution().toString();

						if (temp.contains("dbpedia.org/resource/")) {
							temp = temp.substring(temp.indexOf("/", temp.indexOf("c")));
							temp = temp.substring(1, temp.indexOf(">")).replace("_", " ");
							answers.add(temp);
						}
					}

				   if (answers.size() == 1) {
					   ans = answers.get(0);
					   break;
				   }

				   for (String x : answers) {
					   ans += " And " + x;
				   }
				   ans = ans.substring(5);
				   break;
		}

		return ans;
	}
}
