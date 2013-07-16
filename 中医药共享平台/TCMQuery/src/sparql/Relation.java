package sparql;

public class Relation {
	
	public String start_var;
	public String end_var;
	public String relationURI;
	public String endURI;

	public Relation(String start_var, String end_var, String relationURI, String endURI) {
		this.start_var = start_var;
		this.end_var = end_var;
		this.relationURI = relationURI;
		this.endURI = endURI;
	}
	
	public String toString() {
		return start_var + " " + relationURI + " " + end_var;
	}
}
