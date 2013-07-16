package sparql;

public class OntoProperty {

	public String label;
	public String variable;
	public String ontoURI;
	
	public OntoProperty() {
		
	}
	
	public OntoProperty(String label, String variable, String ontoURI) {
		this.label = label;
		this.variable = variable;
		this.ontoURI = ontoURI;
	}
	
	public String toString() {
		return label + " " + variable + " " + ontoURI;
	}
}
