package qa_system;

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
		return null;
	}
}
