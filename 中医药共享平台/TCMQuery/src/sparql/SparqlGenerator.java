package sparql;

import java.util.*;

import configuration.*;

public class SparqlGenerator {
	
	public String key;
	public String source;
	public String target;
	public OntoProperty targetProp;
	public Ontology sourceOnto;
	public Ontology targetOnto;
	public String relationURI;
	public ArrayList<Relation> relations;
	public DBConfig config;
//	public HashMap<String, String> map;   //key: variable, value: label
	public ArrayList<OntoProperty> props;
	public String result = "";
	public int searchType;
	
	public SparqlGenerator(String key, String source, String target, DBConfig config, int searchType) {
		this.key = key;
		this.source = source;
		this.target = target;
		this.config = config;
		this.searchType = searchType;
		relations = new ArrayList<Relation>();
		sourceOnto = config.getOntoInstance(source);
		targetOnto = config.getOntoInstance(target);
	//	if (sourceOnto != null && targetOnto != null)
	//		generateSparql();
	}
	
	public String generateRecordContentSparql() {
//		map = new HashMap<String, String>();
		StringBuffer query = new StringBuffer();
		String sourcePrimaryKey;
		String targetPrimaryKey;
		String relationURI = "";
	//	ArrayList<OntoProperty> props;
		int type;
		query.append("PREFIX skos: <http://www.w3.org/2004/02/skos/core#> \n");
		query.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
		query.append("PREFIX ns: <http://cintcm.ac.cn/onto#> \n");
		query.append("SELECT ");
		if (sourceOnto.variable.equals(targetOnto.variable) || targetOnto.variable.equals("")) {
			type = 1;
			sourcePrimaryKey = getTrimURI(sourceOnto.primaryKey);
			props = config.getProps(sourceOnto.variable);
		}
		else {
			type = 2;
			relationURI = config.getRelationURI(sourceOnto.variable, targetOnto.variable);
			if (relationURI.equals(""))
				relations = config.getMultiRelations(sourceOnto.variable, targetOnto.variable);
			else	
				relationURI = getTrimURI(relationURI);
			sourcePrimaryKey = getTrimURI(sourceOnto.primaryKey);
			targetPrimaryKey = getTrimURI(targetOnto.primaryKey);
			props = config.getProps(targetOnto.variable);
		}
		
		for (OntoProperty prop: props) {
//			map.put(prop.variable, prop.label);
			query.append(prop.variable + " ");
		}
		query.append("\nWHERE { \n");
		if (searchType == 1)
			query.append(sourceOnto.variable + " skos:" + sourcePrimaryKey + " '" + key + "'. \n");
		else {
		//	query.append(sourceOnto.variable + " skos:" + sourcePrimaryKey + " " + sourceOnto.primaryVariable + ". \n");
			query.append("FILTER regex(" + sourceOnto.primaryVariable + ", \"%" + key + "%\"). \n");
		}
		if (type == 1) {
			query.append("Optional \n{ \n");
			for (OntoProperty prop: props) {
			//	if (prop.label.equals("概念词")) {
				if (prop.ontoURI.contains("prefLabel")) {
					query.append(sourceOnto.variable + " skos:" + getTrimURI(prop.ontoURI) + " " + prop.variable + ". \n");
					continue;
				}
				query.append(sourceOnto.variable + " ns:" + getTrimURI(prop.ontoURI) + " " + prop.variable + ". \n");
			}
			query.append("} \n");
			query.append(sourceOnto.variable + " rdf:type " + "<" + sourceOnto.ontoURI + ">. \n");
		}
		else {
			if (searchType != 1)
				query.append(sourceOnto.variable + " skos:" + sourcePrimaryKey + " " + sourceOnto.primaryVariable + ". \n");
			if (!relationURI.equals(""))
				query.append(sourceOnto.variable + " ns:" + relationURI + " " + targetOnto.variable + ". \n");
			else {
				for (Relation path: relations) {
					query.append(path.start_var + " ns:" + getTrimURI(path.relationURI) + " " + path.end_var + ". \n");
				}
			}
			query.append("Optional \n{ \n");
			for (OntoProperty prop: props) {
				if (prop.ontoURI.contains("prefLabel")) {
					query.append(targetOnto.variable + " skos:" + getTrimURI(prop.ontoURI) + " " + prop.variable + ". \n");
					continue;
				}
				query.append(targetOnto.variable + " ns:" + getTrimURI(prop.ontoURI) + " " + prop.variable + ". \n");
			}
			query.append("} \n");
			query.append(sourceOnto.variable + " rdf:type " + "<" + sourceOnto.ontoURI + ">. \n");
			if (!relationURI.equals(""))
				query.append(targetOnto.variable + " rdf:type " + "<" + targetOnto.ontoURI + ">. \n");
			else {
				for (Relation path:relations) {
					query.append(path.end_var + " rdf:type " + "<" + path.endURI + ">. \n");
				}
			}
		}
		query.append("}");
		this.result = query.toString();
		return result;
		
	}

	
	public String generateRelationSparql() {
		if (sourceOnto.variable.equals("") || targetOnto.variable.equals(""))
			return "";
		String relationURI = config.getRelationURI(sourceOnto.variable, targetOnto.variable);
		if (relationURI.equals(""))
			relations = config.getMultiRelations(sourceOnto.variable, targetOnto.variable);
		else	
			relationURI = getTrimURI(relationURI);
		
		String sourcePrimaryKey = getTrimURI(sourceOnto.primaryKey);
		String targetPrimaryKey = getTrimURI(targetOnto.primaryKey);
		
		OntoProperty sourceOntoProp = null;
		if (source.contains("治则")) {
			sourceOntoProp = config.getNonePrefURI(sourceOnto.variable, source);
		}
		StringBuffer query = new StringBuffer();
		query.append("PREFIX skos: <http://www.w3.org/2004/02/skos/core#> \n");
		query.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
		query.append("PREFIX ns: <http://cintcm.ac.cn/onto#> \n");
		if (source.contains("治则"))
			query.append("SELECT " + sourceOntoProp.variable + " " +targetOnto.primaryVariable + " \n");
		else
			query.append("SELECT " + sourceOnto.primaryVariable + " " + targetOnto.primaryVariable + " \n");
	//	query.append("SELECT " + targetOnto.primaryVariable + " \n");
		query.append("WHERE { \n");
		if (source.contains("治则")) {
			query.append(sourceOnto.variable + " ns:" + getTrimURI(sourceOntoProp.ontoURI) + " " + sourceOntoProp.variable + ". \n");
			query.append("FILTER regex(" + sourceOntoProp.variable + ", \"%" + key + "%\"). \n");
		}
		else
		{
			if (searchType == 1) {
				query.append(sourceOnto.variable + " skos:" + sourcePrimaryKey + " '" + key + "'. \n");
				query.append(sourceOnto.variable + " skos:" + sourcePrimaryKey + " " + sourceOnto.primaryVariable + ". \n");
			}
			else {
				query.append(sourceOnto.variable + " skos:" + sourcePrimaryKey + " " + sourceOnto.primaryVariable + ". \n");
				query.append("FILTER regex(" + sourceOnto.primaryVariable + ", \"%" + key + "%\"). \n");
			}
		}
		if (!relationURI.equals(""))
			query.append(sourceOnto.variable + " ns:" + relationURI + " " + targetOnto.variable + ". \n");
		else {
			for (Relation path: relations) {
				query.append(path.start_var + " ns:" + getTrimURI(path.relationURI) + " " + path.end_var + ". \n");
			}
		}
		query.append(targetOnto.variable + " skos:" + targetPrimaryKey + " " + targetOnto.primaryVariable + ". \n");
		query.append(sourceOnto.variable + " rdf:type " + "<" + sourceOnto.ontoURI + ">. \n");
		if (!relationURI.equals(""))
			query.append(targetOnto.variable + " rdf:type " + "<" + targetOnto.ontoURI + ">. \n");
		else {
			for (Relation path:relations) {
				query.append(path.end_var + " rdf:type " + "<" + path.endURI + ">. \n");
			}
		}
		query.append("}");
	//	System.out.println(query.toString());
		this.result = query.toString();
		return result;
	}
	
	public String generateRelationSparqlWithNoPref() {
		if (sourceOnto.variable.equals("") || targetOnto.variable.equals(""))
			return "";
		String relationURI = config.getRelationURI(sourceOnto.variable, targetOnto.variable);
		if (relationURI.equals(""))
			relations = config.getMultiRelations(sourceOnto.variable, targetOnto.variable);
		else	
			relationURI = getTrimURI(relationURI);
		
		OntoProperty sourceOntoProp = null;
		if (source.contains("治则")) {
			sourceOntoProp = config.getNonePrefURI(sourceOnto.variable, source);
		}
		String sourcePrimaryKey = getTrimURI(sourceOnto.primaryKey);
	//	String targetPrimaryKey = getTrimURI(targetOnto.primaryKey);
		OntoProperty ontoprop = config.getNonePrefURI(targetOnto.variable, target);
		this.targetProp = ontoprop;
		
		StringBuffer query = new StringBuffer();
		query.append("PREFIX skos: <http://www.w3.org/2004/02/skos/core#> \n");
		query.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
		query.append("PREFIX ns: <http://cintcm.ac.cn/onto#> \n");
		if (source.contains("治则"))
			query.append("SELECT " + sourceOntoProp.variable + " " + ontoprop.variable + " \n");
		else if(target.contains("原文")) 
			query.append("SELECT " + ontoprop.variable + " \n");
		else	
			query.append("SELECT " + sourceOnto.primaryVariable + " " + ontoprop.variable + " \n");
		query.append("WHERE { \n");
		if (source.contains("治则")) {
			query.append(sourceOnto.variable + " ns:" + getTrimURI(sourceOntoProp.ontoURI) + " " + sourceOntoProp.variable + ". \n");
			query.append("FILTER regex(" + sourceOntoProp.variable + ", \"%" + key + "%\"). \n");
		}
		else {
			if (searchType == 1) {
				query.append(sourceOnto.variable + " skos:" + sourcePrimaryKey + " '" + key + "'. \n");
				query.append(sourceOnto.variable + " skos:" + sourcePrimaryKey + " " + sourceOnto.primaryVariable + ". \n");
			}
			else {
				query.append(sourceOnto.variable + " skos:" + sourcePrimaryKey + " " + sourceOnto.primaryVariable + ". \n");
				query.append("FILTER regex(" + sourceOnto.primaryVariable + ", \"%" + key + "%\"). \n");
			}
		}
		if (!relationURI.equals(""))
			query.append(sourceOnto.variable + " ns:" + relationURI + " " + targetOnto.variable + ". \n");
		else {
			for (Relation path: relations) {
				query.append(path.start_var + " ns:" + getTrimURI(path.relationURI) + " " + path.end_var + ". \n");
			}
		}
		query.append(targetOnto.variable + " ns:" + getTrimURI(ontoprop.ontoURI) + " " + ontoprop.variable + ". \n");
		query.append(sourceOnto.variable + " rdf:type " + "<" + sourceOnto.ontoURI + ">. \n");
		if (!relationURI.equals(""))
			query.append(targetOnto.variable + " rdf:type " + "<" + targetOnto.ontoURI + ">. \n");
		else {
			for (Relation path:relations) {
				query.append(path.end_var + " rdf:type " + "<" + path.endURI + ">. \n");
			}
		}
		query.append("}");
	//	System.out.println(query.toString());
		this.result = query.toString();
		return result;
	}
	
	public String generateInnerSparql() {
		if (sourceOnto.variable.equals(""))
			return "";
		
		String sourcePrimaryKey = getTrimURI(sourceOnto.primaryKey);
	//	String targetPrimaryKey = getTrimURI(targetOnto.primaryKey);
		OntoProperty ontoprop = config.getNonePrefURI(sourceOnto.variable, target);
		this.targetProp = ontoprop;
		
		StringBuffer query = new StringBuffer();
		query.append("PREFIX skos: <http://www.w3.org/2004/02/skos/core#> \n");
		query.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
		query.append("PREFIX ns: <http://cintcm.ac.cn/onto#> \n");
		query.append("SELECT " + sourceOnto.primaryVariable + " " +ontoprop.variable + " \n");
		query.append("WHERE { \n");
		if (searchType == 1) {
			query.append(sourceOnto.variable + " skos:" + sourcePrimaryKey + " '" + key + "'. \n");
			query.append(sourceOnto.variable + " skos:" + sourcePrimaryKey + " " + sourceOnto.primaryVariable + ". \n");
		}
		else {
			query.append(sourceOnto.variable + " skos:" + sourcePrimaryKey + " " + sourceOnto.primaryVariable + ". \n");
			query.append("FILTER regex(" + sourceOnto.primaryVariable + ", \"%" + key + "%\"). \n");
		}
		query.append(sourceOnto.variable + " ns:" + getTrimURI(ontoprop.ontoURI) + " " + ontoprop.variable + ". \n");
		query.append(sourceOnto.variable + " rdf:type " + "<" + sourceOnto.ontoURI + ">. \n");
		query.append("}");
	//	System.out.println(query.toString());
		this.result = query.toString();
		return result;
	}
	
	private String getTrimURI(String sourceURI) {
		int pos = sourceURI.lastIndexOf("#");
		return sourceURI.substring(pos+1, sourceURI.length());
	}
	
	
	

}
