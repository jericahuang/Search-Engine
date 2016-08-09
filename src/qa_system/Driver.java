package qa_system;

public class Driver {

	public static void main(String [] args) {

		String query = "What is Khaled Mohamed Khaled?";


		QuestionProcessor qp = new QuestionProcessor();

		String answer = qp.getResponse(query);
		qp.print();

		System.out.println(answer);



	}
}
