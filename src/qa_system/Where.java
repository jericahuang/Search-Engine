package qa_system;

import java.util.ArrayList;

import org.apache.jena.query.ResultSet;

public class Where extends Answer {

	String [] query;
	String subject;
	String predicate;

	public Where(String query, String subj, String pred) {
		this.query = query.split(" ");
		this.subject = subj;
		this.predicate = pred;
	}

	@Override
	public String executeQuery() {
		// TODO Auto-generated method stub
		String freebase = convertToFreebase(this.subject.substring(0,1).toUpperCase() +  "" + this.subject.substring(1));

		String ans = "";

		switch(predicate) {
		case "born": String query = "SELECT ?o WHERE { " + freebase + " <http://rdf.freebase.com/ns/people.person.place_of_birth> ?o . }";
					ResultSet results = sendQuery(query);
					ArrayList<String> answers = new ArrayList<String>();

					while(results.hasNext()){
							String temp = results.nextSolution().toString();

								if (temp.contains("dbpedia.org/resource/")) {


										temp = temp.substring(temp.indexOf("/", temp.indexOf("c")));
											temp = temp.substring(1, temp.indexOf(">")).replace("_", " ");
											if (!temp.contains("%")) answers.add(temp);
			}
		}

					for (String answer : answers) {
						System.out.println(answer);
					}


		break;
		}

		// TODO Auto-generated method stub


		return ans;


	}
}
