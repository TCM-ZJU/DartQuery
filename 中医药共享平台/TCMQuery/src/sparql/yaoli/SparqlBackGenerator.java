package sparql.yaoli;


import java.util.ArrayList;
import sparql.OntoProperty;
import sparql.Ontology;
import sparql.Relation;
import configuration.DBConfig;

public class SparqlBackGenerator {
	
	public String key;
	public String source;
	public String cate;
	public String target;
	public String field;
	public Ontology sourceOnto;
	public Ontology targetOnto;
	public String relationURI;
	public ArrayList<Relation> relations;
	public DBConfig config;
	public OntoProperty statProp;
	public ArrayList<OntoProperty> props;
	public ArrayList<OntoProperty> sourceProps;
	public ArrayList<OntoProperty> targetProps;
	public ArrayList<OntoProperty> statProps;
	public int precise;
	public int type;
	public String result = "";
	
	public SparqlBackGenerator(String key, String cate, String source, String target, DBConfig config, int precise) {
		this.key = key;
		this.source = source;
		this.cate = cate;
		this.target = target;
		this.config = config;
		this.precise = precise;
		relations = new ArrayList<Relation>();
		sourceOnto = config.getOntoInstance(source);
		targetOnto = config.getOntoInstance(target);
	}

	public SparqlBackGenerator(String key, String cate, String source, String target, String field, DBConfig config, int precise) {
		this.key = key;
		this.source = source;
		this.target = target;
		this.cate = cate;
		this.field = field;
		this.config = config;
		this.precise = precise;
		relations = new ArrayList<Relation>();
		sourceOnto = config.getOntoInstance(source);
		targetOnto = config.getOntoInstance(target);
	}
	
	public String generateRecordSparqlWithCate() {
		StringBuffer query = new StringBuffer();
		init();
		query.append(addPrefix());
		if (type == 1)
			query.append(addSelect(sourceProps));
		else query.append(addSelect(targetProps));
		
		query.append("WHERE {\n");
		query.append(sourceOnto.variable + " " + getPrefixURI(sourceOnto.cateKey) + " '" + cate + "'. \n");
		query.append(addCondition(sourceOnto.variable, sourceOnto.primaryVariable, sourceOnto.primaryKey, key));
		if (type == 2)
			query.append(addRelation());
		query.append(addOption());
		query.append(addRDFType());
		query.append("}");
		this.result = query.toString();
		return result;
	}
	
	public String generateStatSparqlWithCate() {
		StringBuffer query = new StringBuffer();
		init();
		OntoProperty sourceProp = getSourceProp();
		//OntoProperty statProp = getStatProp();
		ArrayList<OntoProperty> selectProps = new ArrayList<OntoProperty>();
		statProp = getStatProp();
		selectProps.add(statProp);
		query.append(addPrefix());
		query.append(addSelect(selectProps));
		query.append("WHERE {\n");
		if (source.indexOf("研究对象")!= -1 || source.indexOf("药物作用") != -1) {
			if (target.indexOf("单味药") != -1 || target.indexOf("化学成分") != -1 || target.indexOf("方剂") != -1)
				query.append(targetOnto.variable + " " + getPrefixURI(targetOnto.cateKey) + " '" + target + "'. \n");
			else query.append(sourceOnto.variable + " " + getPrefixURI(sourceOnto.cateKey) + " '" + cate + "'. \n");
		}
		else
			query.append(sourceOnto.variable + " " + getPrefixURI(sourceOnto.cateKey) + " '" + cate + "'. \n");
		
		query.append(addCondition(sourceOnto.variable, sourceProp.variable, sourceProp.ontoURI, key));
		if (type == 2)
			query.append(addRelation());
		OntoProperty conProp = new OntoProperty(sourceOnto.ontoLabel, sourceOnto.primaryVariable, sourceOnto.primaryKey);
		query.append(addStatVariable(statProp, conProp));
		query.append(addRDFType());
		query.append("}");
		this.result = query.toString();
		return result;
	}
	
	public String generateRecordSparqlWithAttr() {
		StringBuffer query = new StringBuffer();
		init();
		//OntoProperty prop = config.getProp(sourceOnto.variable, cate);
		OntoProperty prop = getSourceProp();
		query.append(addPrefix());
		if (type == 1)
			query.append(addSelect(sourceProps));
		else query.append(addSelect(targetProps));
		query.append("WHERE {\n");
		query.append(addCondition(sourceOnto.variable, prop.variable, prop.ontoURI, key));
		if (type == 2)
			query.append(addRelation());
		query.append(addOption());
		query.append(addRDFType());
		query.append("}");
		this.result = query.toString();
		return result;
	}
	
	public String generateStatSparqlWithAttr() {
		StringBuffer query = new StringBuffer();
		init();
		//OntoProperty statProp = getStatProp();
		ArrayList<OntoProperty> selectProps = new ArrayList<OntoProperty>();
		statProp = getStatProp();
		selectProps.add(statProp);
		OntoProperty sourceProp = getSourceProp();
		query.append(addPrefix());
		query.append(addSelect(selectProps));
		query.append("WHERE {\n");
		query.append(addCondition(sourceOnto.variable, sourceProp.variable, sourceProp.ontoURI, key));
		if (type == 2)
			query.append(addRelation());
		query.append(addStatVariable(statProp, sourceProp));
		query.append(addRDFType());
		query.append("}");
		this.result = query.toString();
		return result;
	}
	
	public void init() {
		if (sourceOnto.variable.equals(targetOnto.variable)) {
			type = 1;
		//	props = config.getProps(sourceOnto.variable);
			sourceProps = config.getProps(sourceOnto.variable);
			statProps = config.getStatsProps(sourceOnto.variable);
			props = sourceProps;
			
		}
		else {
			type = 2;
			relationURI = config.getRelationURI(sourceOnto.variable, targetOnto.variable);
			if (relationURI.equals(""))
				relations = config.getMultiRelations(sourceOnto.variable, targetOnto.variable);
		//	props = config.getProps(targetOnto.variable);
			sourceProps = config.getProps(sourceOnto.variable);
			targetProps = config.getProps(targetOnto.variable);
			statProps = config.getStatsProps(targetOnto.variable);
			props = targetProps;
		}
		//statProp = getStatProp();
	}
	
	private OntoProperty getStatProp() {
		//OntoProperty statProp = new OntoProperty();
		for (OntoProperty prop: props) {
			if (field.contains(prop.label)) {
				return prop;
				/*statProp.label = prop.label;
				statProp.ontoURI = prop.ontoURI;
				statProp.variable = prop.variable;
				this.statProp = statProp;
				break;*/
			}
		}
		if (type == 1)
			return new OntoProperty(sourceOnto.primaryLabel, sourceOnto.primaryVariable, sourceOnto.primaryKey);
		else return new OntoProperty(targetOnto.primaryLabel, targetOnto.primaryVariable, targetOnto.primaryKey);

	}
	
	public OntoProperty getSourceProp() {
		OntoProperty sourceProp = new OntoProperty();
		for (OntoProperty prop: sourceProps) {
			if (source.contains(prop.label)) 
				return prop;
		}
		return new OntoProperty(sourceOnto.primaryLabel, sourceOnto.primaryVariable, sourceOnto.primaryKey);
	}
	
	// 添加命名空间
	private String addPrefix() {
		StringBuffer sb = new StringBuffer();
		sb.append("PREFIX skos: <http://www.w3.org/2004/02/skos/core#> \n");
		sb.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
		sb.append("PREFIX ns: <http://cintcm.ac.cn/onto#> \n");
		return sb.toString();
	}	
	
	// 添加select语句
	private String addSelect(ArrayList<OntoProperty> props) {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT ");
		for (OntoProperty prop: props) {
//			map.put(prop.variable, prop.label);
			sb.append(prop.variable + " ");
		}
		sb.append("\n");
		return sb.toString();
	}
	
	// 添加已知条件
	private String addCondition(String ontoVar, String propVar, String propURI, String key) {
		StringBuffer sb = new StringBuffer();
		if (precise == 1) {
			sb.append(ontoVar + " " + getPrefixURI(propURI) + " '" + key + "'. \n");
		}
		else {
		//	query.append(sourceOnto.variable + " skos:" + sourcePrimaryKey + " " + sourceOnto.primaryVariable + ". \n");
			sb.append("FILTER regex(" + propVar + ", \"%" + key + "%\"). \n");
			if (type == 2) {
				sb.append(ontoVar + " " + getPrefixURI(propURI) + " " + propVar + ". \n");
			}
		}
		return sb.toString();
	}
	
	private String addStatVariable(OntoProperty statProp, OntoProperty conProp) {
		StringBuffer sb = new StringBuffer();
		if (precise == 1) {
			if (type == 1)
				sb.append(sourceOnto.variable + " " + getPrefixURI(statProp.ontoURI) + " " + statProp.variable + ". \n");
			else
				sb.append(targetOnto.variable + " " + getPrefixURI(statProp.ontoURI) + " " + statProp.variable + ". \n");
		}
		else {
			if (type == 1) { 
				sb.append(sourceOnto.variable + " " + getPrefixURI(conProp.ontoURI) + " " + conProp.variable + ". \n");
				if(!statProp.variable.equals(conProp.variable))
					sb.append(sourceOnto.variable + " " + getPrefixURI(statProp.ontoURI) + " " + statProp.variable + ". \n");
			}
			else 
				sb.append(targetOnto.variable + " " + getPrefixURI(statProp.ontoURI) + " " + statProp.variable + ". \n");
		}
		/*if (type == 1){	
			sb.append(sourceOnto.variable + " " + getPrefixURI(conProp.ontoURI) + " " + conProp.variable + ". \n");
			if (precise == 1 || !statProp.variable.equals(sourceOnto.primaryLabel))
				sb.append(sourceOnto.variable + " " + getPrefixURI(statProp.ontoURI) + " " + statProp.variable + ". \n");
		}
		else {
			sb.append(targetOnto.variable + " " + getPrefixURI(statProp.ontoURI) + " " + statProp.variable + ". \n");
		}*/
		return sb.toString();
	}
	
	// 添加关联关系
	private String addRelation() {
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
	
	// 添加option
	private String addOption() {
		StringBuffer sb = new StringBuffer();
		sb.append("Optional {\n");
		for (OntoProperty prop: props) {
			sb.append(targetOnto.variable + " " + getPrefixURI(prop.ontoURI) + " " + prop.variable + ". \n");
		}
		sb.append("}\n");
		return sb.toString();
	}
	
	// 添加rdf type
	private String addRDFType() {
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
	
	private String getPrefixURI(String sourceURI) {
		int pos = sourceURI.lastIndexOf("#");
		String trimURI = sourceURI.substring(pos+1, sourceURI.length());
		if (sourceURI.contains("prefLabel")) {
			return "skos:" + trimURI;
		}
		else return "ns:" + trimURI;
	}	
}
