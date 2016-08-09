package qa_system;

public class What extends Answer{

	String [] query;
	String subject;
	String predicate;

	public What(String query, String subj, String pred ) {
		super();
		this.query = query.indexOf(" ") != -1 ? query.split(" ") : null;
		this.subject = subj;
		this.predicate = pred;
	}



	@Override
	public String executeQuery() {
		// System.out.println(this.subject);
		//System.out.println("1 " + this.subject.substring(0,1));

		String freebase = convertToFreebase(this.subject.substring(0,1).toUpperCase() +  "" + this.subject.substring(1));

		String ans = "";

		switch(predicate) {
		case "is": String query = "SELECT ?o WHERE { " + freebase + " dbp-ont:abstract ?o . }";
				   ans = sendQuery(query).next().toString();
				   ans = formatObject(ans);

				   int pos = ans.indexOf(".") +1;
				   int pos2 = ans.indexOf(".", pos) + 1;

				   ans = ans.substring(0, pos2).length() > 180 ? ans.substring(0, pos2) : ans.substring(0, ans.indexOf(".", pos2) + 1);
				   break;
		}

		// TODO Auto-generated method stub


		return ans;
	}

}
