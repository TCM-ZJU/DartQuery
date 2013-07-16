package configuration;

import java.util.*;
import java.io.*;
import org.jdom.input.SAXBuilder;
import org.jdom.Element;
import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.xml.sax.InputSource;

import sparql.Ontology;
import sparql.OntoProperty;
import sparql.DataGridHeader;
import sparql.Relation;

public class DBConfig {
	
	private String DBname;
	public ArrayList<String> entryList;
	private HashMap<String, String> URIMap;             // 中文标签：URI
	private HashMap<String, String> paramMap;          // 中文标签：参数
 	private HashMap<String, ArrayList<String>> relationMap;
	private HashMap<String, Element> ontoMap;
	private HashMap<String, Element> gridMap;
	private HashSet<String> identitySet;
	public Element xml;
	
	public DBConfig(String name, Element xml) {
		DBname = name;
		entryList = new ArrayList<String>();
		URIMap = new HashMap<String, String>();
		paramMap = new HashMap<String, String>();
		ontoMap = new HashMap<String, Element>();
		gridMap = new HashMap<String, Element>();
		identitySet = new HashSet<String>();
		init(xml);
	}
	
	public String getName() {
		return DBname;
	}
	
	public void init(Element xml) {
		List<Element> params = xml.getChild("param").getChildren("variable");	
		for (Element param: params) {
			String label = param.getAttributeValue("label");
			String variable = param.getText();
			paramMap.put(label, variable);
		}
		
		List<Element> indentities = xml.getChild("identity").getChildren("variable");
		for (Element id: indentities) {
			String label = id.getText();
			identitySet.add(label);
		}
		
		
		List<Element> ontoNodes = xml.getChild("ontologies").getChildren("ontology");
		for (Element node: ontoNodes) {
			String ontoLabel = node.getAttributeValue("label");
			String ontoVariable = node.getAttributeValue("variable");
			String ontoURI = node.getChildText("ontologyURI");
			URIMap.put(ontoVariable, ontoURI);
			ontoMap.put(ontoVariable, node);
		}
		
		List<Element> grids = xml.getChild("views").getChildren("grid");
		for (Element grid: grids) {
			String label = grid.getAttributeValue("name");
			gridMap.put(label, grid);
		}
 	}
	
	
	// 得到sparql变量名称
	public String getVariable(String label) {
		Set<String> keys = paramMap.keySet();
		for (String key: keys) {
			if (label.contains(key)) {
				return paramMap.get(key);
			}
		}
		return "";
	}
	
	public int checkSparqlType(String label) {
		for (String key: identitySet) {
			if (label.contains(key))
				return 1;
		}
		return 0;
	}
	
	public Element getGrid(String label) {
		Set<String> keys = gridMap.keySet();
		for (String key: keys) {
			if (label.contains(key)) {
				return gridMap.get(key);
			}
		}
		return null;
	}
	
	//得到本体URI
	public String getOntoURI(String variable) {
		return URIMap.get(variable);
	}
	
	//得到本体的主键URI
	public String getPrimaryKeyOnto(String variable) {
		Element xml = ontoMap.get(variable);
		List<Element> ontoProps = xml.getChild("ontoProps").getChildren("ontoProp");
		for (Element ontoProp: ontoProps) {
			Element propURI = ontoProp.getChild("ontologyURI");
			String uri = propURI.getText();
			if (uri.contains("prefLabel"))
				return uri;
		}
		return "";
	}
	
	//得到本体实例 包含本体URI,变量名称， 主键URI ,主键变量
	public Ontology getOntoInstance(String label) {
		Ontology onto = new Ontology();
		onto.variable = getVariable(label);
		if (!onto.variable.equals("")) {
			onto.ontoURI = getOntoURI(onto.variable);
			Element xml = ontoMap.get(onto.variable);
			onto.ontoLabel = xml.getAttributeValue("label");
			List<Element> ontoProps = xml.getChild("ontoProps").getChildren("ontoProp");
			for (Element ontoProp: ontoProps) {
				Element propURI = ontoProp.getChild("ontologyURI");
				String uri = propURI.getText();
				if (uri.contains("prefLabel") || propURI.getAttribute("pref") != null) {
					onto.primaryKey = uri;
					onto.primaryLabel = propURI.getAttributeValue("label");
					onto.primaryVariable = propURI.getAttributeValue("variable");
					onto.primaryField = ontoProp.getChildText("column");
				}
				if (propURI.getAttribute("type") != null) {
					onto.cateKey = propURI.getText();   //二级入口分类
				}
			}
		}
		return onto;
	}
	
	
	public String getPropURI(String onto_var, String attr_label) {
		String attr_var = getVariable(attr_label);
		ArrayList<OntoProperty> props = getProps(onto_var);
		for (OntoProperty prop: props) {
			if (attr_var.equals(prop.variable)) {
				return prop.ontoURI;
			}
		}
		return "";
	}
	
	public OntoProperty getNonePrefURI(String onto_var, String prop_label) {
		String propLabel = "";
		String propVar = "";
		String propURI = "";
		ArrayList<OntoProperty> props = getProps(onto_var);
		for (OntoProperty prop: props) {
			if (prop_label.contains(prop.label))
			{
				propLabel = prop.label;
				propVar = prop.variable;
				propURI = prop.ontoURI;
			}
		}
		return new OntoProperty(propLabel, propVar, propURI);
	}
	
	public String getRelationURI(String source_var, String target_var) {
		Element xml = ontoMap.get(source_var);
		
		List<Element> ontoProps = xml.getChild("relations").getChildren("relation");
	//	System.out.println(ontoProps.size());
		for (Element ontoProp: ontoProps) {
			Element propURI = ontoProp.getChild("ontologyURI");
			if (propURI.getAttribute("variable") != null) {
				if(propURI.getAttributeValue("variable").equals(target_var)) {
					return propURI.getText();
				}
			}
		}
		return "";
	}
	
	public ArrayList<Relation> getMultiRelations(String source_var, String target_var) {
		ArrayList<Relation> result = new ArrayList<Relation>();
		Element xml = ontoMap.get(source_var);
		List<Element> relationPaths = xml.getChildren("relationPath");
		String cur_var = source_var;
		for (Element path: relationPaths) {
			if (path.getAttributeValue("target").equals(target_var)) {
				List<Element> nodes = path.getChildren("path");
				for (Element node:nodes) {
					String node_var = node.getAttributeValue("variable");
					String relationURI = getRelationURI(cur_var, node_var);
					String endURI = getOntoURI(node_var);
					result.add(new Relation(cur_var, node_var, relationURI, endURI));
					cur_var = node_var;
				}
				break;
			}
		}
		return result;
	}
	
	/*public String getMultiRelations(String source_var, String target_var) {
		Element xml = ontoMap.get(source_var);
		List<Element> props = xml.getChild("relations").getChildren("relation");
		ArrayList<Relation> relations = new ArrayList<Relation>();
		for (Element prop:props) {
			String start_var = source_var;
			String end_var = 
		}
	}*/
	
	public ArrayList<OntoProperty> getStatsProps(String variable) {
		Element xml = ontoMap.get(variable);
		if (xml == null)
			return null;
		ArrayList<OntoProperty> result = new ArrayList<OntoProperty>();
		List<Element> ontoProps = xml.getChild("ontoProps").getChildren("ontoProp");
		for (Element ontoProp: ontoProps) {
			Element ontoURI = ontoProp.getChild("ontologyURI");
			if (ontoURI.getAttribute("stat") == null)
				continue;
			if (ontoURI.getAttributeValue("stat").equals("true")) {
				String label = ontoURI.getAttributeValue("label");
				String var = ontoURI.getAttributeValue("variable");
				String URI = ontoURI.getText();
				result.add(new OntoProperty(label, var, URI));
			}
		}
		return result;
	}
	
	public OntoProperty getProp(String ontoVar, String label) {
		String propLabel = "";
		String propVar = "";
		String propURI = "";
		propVar = getVariable(label);
		ArrayList<OntoProperty> props = getProps(ontoVar);
		for (OntoProperty prop: props) {
			if (propVar.equals(prop.variable) || label.equals(prop.label)) {
				propLabel = prop.label;
				propVar = prop.variable;
				propURI = prop.ontoURI;
			}
		}
		return new OntoProperty(propLabel, propVar, propURI);
	}
	
	public ArrayList<OntoProperty> getProps(String variable) {
		Element xml = ontoMap.get(variable);
		if (xml == null)
			return null;
		ArrayList<OntoProperty> result = new ArrayList<OntoProperty>();
		List<Element> ontoProps = xml.getChild("ontoProps").getChildren("ontoProp");
		for (Element ontoProp: ontoProps) {
			Element ontoURI = ontoProp.getChild("ontologyURI");
			String label = ontoURI.getAttributeValue("label");
			String var = ontoURI.getAttributeValue("variable");
			String URI = ontoURI.getText();
			result.add(new OntoProperty(label, var, URI));
		}
		return result;
	}
	
	public String resultWrap(String keyword, String source, String target, String res, String primarykey, int index, int size) {
		/*res = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + 
"<Message>" +
"<Records>" +
"<Record><zz_name>出血</zz_name><count>20</count></Record>" +
"<Record><zz_name>异物</zz_name><count>15</count></Record>" +
"</Records>" +
"<TotalNum>482</TotalNum></Message>";
		*/
		
		Element xml = getGrid(target);
		if (xml == null)
			return "";
		try {
			ArrayList<DataGridHeader> headers = new ArrayList<DataGridHeader>();
			List<Element> columns = xml.getChildren("column");
			Document doc = new Document();
			Element root = new Element("result");
			Element head = new Element("header");
			for (Element column:columns) {
				String label = column.getChildText("label");
				String field = column.getChildText("field");
				String type = "";
				if (column.getChild("field").getAttribute("type") != null)
					type = column.getChild("field").getAttributeValue("type");	
				if (!field.equals("link"))
					headers.add(new DataGridHeader(label, field, type));
				Element headColumn = new Element("column");
				headColumn.setAttribute("headerText", label);
				if (type.equals("primary"))
					headColumn.setAttribute("dataField", "primary");
				else headColumn.setAttribute("dataField", field);
				head.addContent(headColumn);
			}
			root.addContent(head);
			Element query = new Element("query");
			Element keywordElement = new Element("key");
			keywordElement.setText(keyword);
			query.addContent(keywordElement);
			Element sourceElement = new Element("source");
			sourceElement.setText(source);
			query.addContent(sourceElement);
			Element targetElement = new Element("target");
			targetElement.setText(target);
			query.addContent(targetElement);
			root.addContent(query);
			Element result = new Element("results");
			StringReader sr = new StringReader(res);
			InputSource is = new InputSource(sr);
			Document resdoc = new SAXBuilder().build(is);
			Element totalhit = new Element("totalHits");
			totalhit.setText(resdoc.getRootElement().getChildText("TotalNum"));
			result.addContent(totalhit);
			List<Element> records = resdoc.getRootElement().getChild("Records").getChildren("Record");
			int count = 0;
			for (Element record: records) {
				count++;
				Element newRecord = new Element("record");
				for (DataGridHeader header: headers) {
					if (header.field.equals("id")) {
						Element id = new Element("id");
						int xuhao = index * size + count;
						id.setText(Integer.toString(xuhao));
						newRecord.addContent(id);
					//	continue;
					}
					else if (header.type.equals("primary")) {
						Element primaryElement = new Element("primary");
						String primary = record.getChildText(header.field);
						primaryElement.setText(primary);
						newRecord.addContent(primaryElement);
					}
					/*else if (header.field.equals("count")) {
						Element relationCount = new Element("count");
						String count_record = record.getChildText("count");
						relationCount.setText(count_record);
						newRecord.addContent(relationCount);
					//	continue;
					}*/
					else {
						Element element = new Element(header.field);
						String value = record.getChildText(header.field);
						element.setText(value);
						newRecord.addContent(element);
					}
				}
				result.addContent(newRecord);
			}
			root.addContent(result);
			doc.addContent(root);
			Format format = Format.getPrettyFormat();
			format.setEncoding("GBK");
		
			XMLOutputter xmlout = new XMLOutputter(format);
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			xmlout.output(doc,bo);
			res = bo.toString();
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return res;
	} 
	
	public static void main(String[] args) {
		Integer b = Integer.valueOf("1101", 2);
		System.out.println(b.byteValue());
	}
}
