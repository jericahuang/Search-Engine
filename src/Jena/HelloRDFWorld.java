package Jena;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

public class HelloRDFWorld {


	public static void main (String [] args) throws FileNotFoundException {

		Model model = ModelFactory.createDefaultModel();

		String ns = new String("http://www.example.com/example#");

		//Create two resources
		Resource john = model.createResource(ns + "John");
		Resource jane = model.createResource(ns + "Jane");

		//Create the hasBrother property
		Property hasBrother = model.createProperty(ns, "hasBrother");

		jane.addProperty(hasBrother, john);

		Property hasSister = model.createProperty(ns, "hasSister");

		Statement sisterStmt = model.createStatement(john, hasSister, jane);
		model.add(sisterStmt);
	}
}
