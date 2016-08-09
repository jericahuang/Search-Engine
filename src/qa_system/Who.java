package qa_system;

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
		return null;
	}
}
