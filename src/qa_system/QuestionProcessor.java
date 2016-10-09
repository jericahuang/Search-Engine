package qa_system;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.io.StringReader;

import java.io.IOException;
import java.util.*;

import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Label;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;

public class QuestionProcessor {

	String grammar = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";
	String[] options = { "-maxLength", "80", "-retainTmpSubcategories" };
	private LexicalizedParser lp;
	Answer responseGen;

	String query;
	Tree parsed;
	String subject = "";
	String predicate = "";

	public void classifyQuestion(String query) {
		String firstWord = query.split(" ")[0];

		switch(firstWord) {
		case "What":  this.responseGen = new What(query, this.subject, this.predicate);
					  break;
		case "When":  this.responseGen = new When(query, this.subject, this.predicate);
		 			  break;
		case "Who":   this.responseGen = new Who(query, this.subject, this.predicate);
		 			  break;
		case "Where": System.out.println("YEAH");
				this.responseGen = new Where(query, this.subject, this.predicate);

		 			  break;
		case "How":	  this.responseGen = new How(query, this.subject, this.predicate);
		 			  break;
		}
	}

	/**
	 * Constructor
	 *
	 * @param root
	 * @return
	 */
	public QuestionProcessor() {
		this.lp = LexicalizedParser.loadModel(this.grammar, this.options);
	}

	public String getSubj() {
		return this.subject;
	}

	public String getPred() {
		return this.predicate;
	}

	public void setRDF(String query) {

		Tree parsed = this.lp.parse(query);
		ArrayList<TaggedWord> list = parsed.firstChild().taggedYield();
		boolean foundSubj = false;
		boolean subjComplete = false;
		boolean foundVerb = false;
		boolean verbComplete = false;

		for (TaggedWord tw : list) {
			String tag = tw.tag();

			if (subjComplete == false ) {

				if (tag.equals("NNP") || tag.equals("NN")) {
					subject += " " + tw.word();
					foundSubj = true;
				}
				else {
					if (foundSubj == true) subjComplete = true;
				}
			}

			if (foundVerb == false && tag.substring(0,2).equals("VB")) {
				this.predicate = tw.word();
				foundVerb = true;
				if (tag.substring(0,3).equals("VBN") || tag.length() == 2) verbComplete = true;
			}

			else if (verbComplete == false && (tag.equals("VB") || (tag.length() > 2 && tag.substring(0,3).equals("VBN"))) ) {
				this.predicate = tw.word();
			}

		}
		this.subject = this.subject.trim();
		this.subject = this.subject.replace(" ", "_");
	}

	public void print(){
		System.out.println("SUBJECT " + subject);
		System.out.println("PREDICATE " + predicate);

	}

	public String getResponse(String query) {
		setRDF(query);
		classifyQuestion(query);

		return responseGen.executeQuery();
	}

	public static void main (String [] args) {
		String x = "When did Lee Harvey Oswald kill Lincoln?";

		QuestionProcessor q = new QuestionProcessor();

		Tree result = q.lp.parse(x);

		System.out.println(result.taggedYield());
		System.out.println("---------------------------------------------------------");

		System.out.println("HELLOOOOOOO " + result.children().length);
		Tree root = result.firstChild().firstChild();
		System.out.println(root);
		System.out.println();
		result.pennPrint();
		System.out.println("Value: " + root.value());

		ArrayList<TaggedWord> list = result.taggedYield();
		System.out.println(list);

		System.out.println();

		System.out.println("SUBJECT " + q.subject);
		System.out.println("VERB " + q.predicate);



	}


}
