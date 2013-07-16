package sparql.yaoli;

import java.util.ArrayList;

import sparql.OntoProperty;
import sparql.Ontology;
import sparql.Relation;
import configuration.DBConfig;


public class SparqlGenerator {
	
	public String key;
	public String cate;
	public String field;
	public Ontology sourceOnto;
	public Ontology targetOnto;
	public String relationURI;
	public ArrayList<Relation> relations;
	public DBConfig config;
	public ArrayList<OntoProperty> props;
	public ArrayList<OntoProperty> statProps;
	public int searchType;
	public String result = "";
	
	public SparqlGenerator(String key, String cate, String source, String target, DBConfig config, int searchType) {
		this.key = key;
		this.cate = cate;
		this.config = config;
		this.searchType = searchType;
		relations = new ArrayList<Relation>();
		sourceOnto = config.getOntoInstance(source);
		targetOnto = config.getOntoInstance(target);
	//	if (sourceOnto != null && targetOnto != null)
	//		generateSparql();
	}
	
	public SparqlGenerator(String key, String cate, String source, String target, String field, DBConfig config, int searchType) {
		this.key = key;
		this.cate = cate;
		this.field = field;
		this.config = config;
		this.searchType = searchType;
		relations = new ArrayList<Relation>();
		sourceOnto = config.getOntoInstance(source);
		targetOnto = config.getOntoInstance(target);
	}
	
	public int getType() {
		int type;
		if (sourceOnto.variable.equals(targetOnto.variable)) {
			type = 1;
			props = config.getProps(sourceOnto.variable);
			statProps = config.getStatsProps(sourceOnto.variable);
		}
		else {
			type = 2;
			relationURI = config.getRelationURI(sourceOnto.variable, targetOnto.variable);
			if (relationURI.equals(""))
				relations = config.getMultiRelations(sourceOnto.variable, targetOnto.variable);
			props = config.getProps(targetOnto.variable);
			statProps = config.getStatsProps(targetOnto.variable);
		}
		return type;
	}
	
	public String generateStatSparqlWithCate() {
		int type;
		String fieldURI = "";
		String fieldVar = "";
		String cateURI = getTrimURI(sourceOnto.cateKey);
		if (sourceOnto.variable.equals(targetOnto.variable)) {
			type = 1;
			props = config.getProps(sourceOnto.variable);
			statProps = config.getStatsProps(sourceOnto.variable);
		}
		else {
			type = 2;
			relationURI = config.getRelationURI(sourceOnto.variable, targetOnto.variable);
			if (relationURI.equals(""))
				relations = config.getMultiRelations(sourceOnto.variable, targetOnto.variable);
			props = config.getProps(targetOnto.variable);
			statProps = config.getStatsProps(targetOnto.variable);
		}
		for (OntoProperty prop: statProps) {
			if (field.equals("") || field.contains(prop.label)) {
				field = prop.label;
				fieldURI = prop.ontoURI;
				fieldVar = prop.variable;
				break;
			}
		}
		StringBuffer query = new StringBuffer();
		query.append(getPrefix());
		query.append("SELECT " + fieldVar + " \n");
		query.append("WHERE { \n");
		query.append(sourceOnto.variable + " ns:" + cateURI + " '" + cate + "'. \n");
		// =1 精确查询 =2 模糊查询
		if (searchType == 1)
			query.append(sourceOnto.variable + " " + getPrefixURI(sourceOnto.primaryKey) + " '" + key + "'. \n");
		else {
			query.append(sourceOnto.variable + " " + getPrefixURI(sourceOnto.primaryKey) + " " + sourceOnto.primaryVariable + ". \n");
			query.append("FILTER regex(" + sourceOnto.primaryVariable + ", \"%" + key + "%\"). \n");
		}
		// =1 非关联 =2 关联
		if (type == 1) {
			query.append(sourceOnto.variable + " " + getPrefixURI(fieldURI) + " " + fieldVar + ". \n");
			query.append(sourceOnto.variable + " rdf:type " + "<" + sourceOnto.ontoURI + ">. \n");
		}
		else {
			if (!relationURI.equals(""))
				query.append(sourceOnto.variable + " " + getPrefixURI(relationURI) + " " + targetOnto.variable + ". \n");
			else {
				for (Relation path:relations) {
					query.append(path.start_var + " " + getPrefixURI(path.relationURI) + " " + path.end_var + ". \n");
				}
			}
			query.append(targetOnto.variable + " " + getPrefixURI(fieldURI) + " " + fieldVar + ". \n");
			if (!relationURI.equals("")) {
				query.append(sourceOnto.variable + " rdf:type " + "<" + sourceOnto.ontoURI + ">. \n");
				query.append(targetOnto.variable + " rdf:type " + "<" + targetOnto.ontoURI + ">. \n");
			}
			else {
				query.append(sourceOnto.variable + " rdf:type " + "<" + sourceOnto.ontoURI + ">. \n");
				for (Relation path:relations) {
					query.append(path.end_var + " rdf:type " + "<" + path.endURI + ">. \n");
				}
			}
		}
		query.append("} \n");
		this.result = query.toString();
		return result;
	}
	
	public String generateStatSparqlWithAttr() {
		int type;
		String fieldURI = "";
		String fieldVar = "";
		type = getType();
		for (OntoProperty prop: statProps) {
			if (field.equals("") || field.contains(prop.label)) {
				field = prop.label;
				fieldURI = prop.ontoURI;
				fieldVar = prop.variable;
				break;
			}
		}
		String attrVar = config.getVariable(cate);
		String attrURI = config.getPropURI(sourceOnto.variable, cate);
		
		StringBuffer query = new StringBuffer();
		query.append(getPrefix());
		query.append("SELECT " + fieldVar + " \n");
		query.append("WHERE { \n");
		// 精确查找 = 1
		if (searchType == 1) {
			query.append(sourceOnto.variable + " " + getPrefixURI(attrURI) + " '" + key + "'. \n");
		}
		else {
			query.append(sourceOnto.variable + " " + getPrefixURI(attrURI) + " " + attrVar + ". \n");
			query.append("FILTER regex(" + attrVar + ", \"%" + key + "%\"). \n");
		}
		// 非关联 = 1
		if (type == 1) {
			query.append(sourceOnto.variable + " " + getPrefixURI(fieldURI) + " " + fieldVar + ". \n");
		}
		else {
			query.append(addRelation());
			query.append(targetOnto.variable + " " + getPrefixURI(fieldURI) + " " + fieldVar + ". \n");
		}
		query.append(addRDFType(type));
		query.append("} \n");
		this.result = query.toString();
		return result;
	}
	
	public String generateRecordSparqlWithCate() {
		StringBuffer query = new StringBuffer();
		String sourcePrimaryKey;
		String targetPrimaryKey;
		String cateURI;
		String relationURI = "";
	//	ArrayList<OntoProperty> props;
		int type;
		query.append(getPrefix());
		query.append("SELECT ");
		if (sourceOnto.variable.equals(targetOnto.variable)) {
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
		cateURI = getTrimURI(sourceOnto.cateKey);
		
		for (OntoProperty prop: props) {
//			map.put(prop.variable, prop.label);
			query.append(prop.variable + " ");
		}
		query.append("\nWHERE { \n");
		query.append(sourceOnto.variable + " ns:" + cateURI + " '" + cate + "'. \n");
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
				for (Relation path:relations) {
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
			if (!relationURI.equals("")) {
				query.append(sourceOnto.variable + " rdf:type " + "<" + sourceOnto.ontoURI + ">. \n");
				query.append(targetOnto.variable + " rdf:type " + "<" + targetOnto.ontoURI + ">. \n");
			}
			else {
				query.append(sourceOnto.variable + " rdf:type " + "<" + sourceOnto.ontoURI + ">. \n");
				for (Relation path:relations) {
					query.append(path.end_var + " rdf:type " + "<" + path.endURI + ">. \n");
				}
			}
		}
		query.append("}");
		this.result = query.toString();
		return result;
	}
	
	public String generateRecordSparqlWithAttr() {
		int type;
		StringBuffer query = new StringBuffer();
		query.append(getPrefix());
		if (sourceOnto.variable.equals(targetOnto.variable)) {
			type = 1;
			props = config.getProps(sourceOnto.variable);
		}
		else {
			type = 2;
			relationURI = config.getRelationURI(sourceOnto.variable, targetOnto.variable);
			if (relationURI.equals(""))
				relations = config.getMultiRelations(sourceOnto.variable, targetOnto.variable);
			else
				relationURI = getTrimURI(relationURI);
			props = config.getProps(targetOnto.variable);
		}
		query.append(getSelect());
		query.append(getWhere(type));
		this.result = query.toString();
		return result;
	}
	
	public String getPrefix() {
		StringBuffer sb = new StringBuffer();
		sb.append("PREFIX skos: <http://www.w3.org/2004/02/skos/core#> \n");
		sb.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
		sb.append("PREFIX ns: <http://cintcm.ac.cn/onto#> \n");
		return sb.toString();
	}
	
	public String getSelect() {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT ");
		for (OntoProperty prop: props) {
			sb.append(prop.variable + " ");
		}
		sb.append("\n");
		return sb.toString();
	}
	
	public String getWhere(int flag) {
		String attrVar = config.getVariable(cate);
		String attrURI = config.getPropURI(sourceOnto.variable, cate);
		attrURI = getTrimURI(attrURI);
		StringBuffer sb = new StringBuffer();
		sb.append("WHERE { \n");
		if (searchType == 1) {
			if (attrURI.contains("prefLabel"))
				sb.append(sourceOnto.variable + " skos:" + attrURI + " '" + key + "'. \n");
			else 
				sb.append(sourceOnto.variable + " ns:" + attrURI + " '" + key + "'. \n");
		}
		else {
			if (flag == 1) {
				sb.append("FILTER regex(" + attrVar + ", \"%" + key + "%\"). \n");
			}
			else {
				sb.append("FILTER regex(" + attrVar + ", \"%" + key + "%\"). \n");
				if (attrURI.contains("prefLabel"))
					sb.append(sourceOnto.variable + " skos:" + attrURI + " " + attrVar + ". \n");
				else
					sb.append(sourceOnto.variable + " ns:" + attrURI + " " + attrVar + ". \n");
			}
		}
		if (flag == 2) {
			if (!relationURI.equals(""))
				sb.append(sourceOnto.variable + " ns:" + relationURI + " " + targetOnto.variable + ". \n");
			else {
				for (Relation path:relations) {
					sb.append(path.start_var + " ns:" + getTrimURI(path.relationURI) + " " + path.end_var + ". \n");
				}
			}
		}
		sb.append("Optional \n{ \n");
		if (flag == 1) {
			for (OntoProperty prop: props) {
				if (prop.ontoURI.contains("prefLabel")) {
					sb.append(sourceOnto.variable + " skos:" + getTrimURI(prop.ontoURI) + " " + prop.variable + ". \n");
					continue;
				}
				sb.append(sourceOnto.variable + " ns:" + getTrimURI(prop.ontoURI) + " " + prop.variable + ". \n");
			}
			sb.append("} \n");
			sb.append(sourceOnto.variable + " rdf:type " + "<" + sourceOnto.ontoURI + ">. \n");
		}
		else {
			for (OntoProperty prop: props) {
				if (prop.ontoURI.contains("prefLabel")) {
					sb.append(targetOnto.variable + " skos:" + getTrimURI(prop.ontoURI) + " " + prop.variable + ". \n");
					continue;
				}
				sb.append(targetOnto.variable + " ns:" + getTrimURI(prop.ontoURI) + " " + prop.variable + ". \n");
			}
			sb.append("} \n");
			if (!relationURI.equals("")) {
				sb.append(sourceOnto.variable + " rdf:type " + "<" + sourceOnto.ontoURI + ">. \n");
				sb.append(targetOnto.variable + " rdf:type " + "<" + targetOnto.ontoURI + ">. \n");
			}
			else {
				sb.append(sourceOnto.variable + " rdf:type " + "<" + sourceOnto.ontoURI + ">. \n");
				for (Relation path:relations) {
					sb.append(path.end_var + " rdf:type " + "<" + path.endURI + ">. \n");
				}
			}
		}
		sb.append("}");
		return sb.toString();
	}
	
	public String addRelation() {
		StringBuffer sb = new StringBuffer();
		if (!relationURI.equals(""))
			sb.append(sourceOnto.variable + " " + getPrefixURI(relationURI) + " " + targetOnto.variable + ". \n");
		else {
			for (Relation path:relations) {
				sb.append(path.start_var + " " + getPrefixURI(path.relationURI) + " " + path.end_var + ". \n");
			}
		}
		return sb.toString();
	}
	
	public String addRDFType(int type) {
		StringBuffer sb = new StringBuffer();
		sb.append(sourceOnto.variable + " rdf:type " + "<" + sourceOnto.ontoURI + ">. \n");	
		if (type != 1) { 
			if (!relationURI.equals("")) {
				sb.append(targetOnto.variable + " rdf:type " + "<" + targetOnto.ontoURI + ">. \n");
			}
			else {
				for (Relation path:relations) {
					sb.append(path.end_var + " rdf:type " + "<" + path.endURI + ">. \n");
				}
			}
		}
		return sb.toString();
	}
	
	private String getTrimURI(String sourceURI) {
		int pos = sourceURI.lastIndexOf("#");
		return sourceURI.substring(pos+1, sourceURI.length());
	}
	
	private String getPrefixURI(String sourceURI) {
		int pos = sourceURI.lastIndexOf("#");
		String trimURI = sourceURI.substring(pos+1, sourceURI.length());
		if (sourceURI.contains("prefLabel")) {
			return "skos:" + trimURI;
		}
		else return "ns:" + trimURI;
	}
	
/*	
	public String generateRecordSparqlWithCate_bak() {
		StringBuffer query = new StringBuffer();
		String sourcePrimaryKey;
		String targetPrimaryKey;
		String cateURI;
		String relationURI = "";
	//	ArrayList<OntoProperty> props;
		int type;
		query.append("PREFIX skos: <http://www.w3.org/2004/02/skos/core#> \n");
		query.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
		query.append("PREFIX ns: <http://cintcm.ac.cn/onto#> \n");
		query.append("SELECT ");
		if (sourceOnto.variable.equals(targetOnto.variable)) {
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
		cateURI = getTrimURI(sourceOnto.cateKey);
		
		for (OntoProperty prop: props) {
//			map.put(prop.variable, prop.label);
			query.append(prop.variable + " ");
		}
		query.append("\nWHERE { \n");
		query.append(sourceOnto.variable + " ns:" + cateURI + " '" + cate + "'. \n");
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
				for (Relation path:relations) {
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
			if (!relationURI.equals("")) {
				query.append(sourceOnto.variable + " rdf:type " + "<" + sourceOnto.ontoURI + ">. \n");
				query.append(targetOnto.variable + " rdf:type " + "<" + targetOnto.ontoURI + ">. \n");
			}
			else {
				query.append(sourceOnto.variable + " rdf:type " + "<" + sourceOnto.ontoURI + ">. \n");
				for (Relation path:relations) {
					query.append(path.end_var + " rdf:type " + "<" + path.endURI + ">. \n");
				}
			}
		}
		query.append("}");
		this.result = query.toString();
		return result;
	}
	*/
}
