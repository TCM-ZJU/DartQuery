package search.yaoli;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.xml.sax.InputSource;

import configuration.DBConfig;

import sparql.DataGridHeader;
import sparql.OntoProperty;
import sparql.Ontology;

public class ResultWrapper {
	
	public static String getHitCount(String res) {
		String count = "";
		try {
			StringReader sr = new StringReader(res);
			InputSource is = new InputSource(sr);
			Document resdoc = new SAXBuilder().build(is);
			Element root = resdoc.getRootElement();
			count = root.getChildText("TotalNum");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}

	public static String wrap(String record, ArrayList<OntoProperty> props, String key, 
			String cate, String source, String target, int identity, int index, int size) {
		record = record.replaceAll("[$]+", " ");
		String res = "";
		try {
			StringReader sr = new StringReader(record);
			InputSource is = new InputSource(sr);
			Document resdoc = new SAXBuilder().build(is);
			List<Element> children = resdoc.getRootElement().getChild("Records").getChildren("Record");
			Document doc = new Document();
			Element root = new Element("root");
			Element header = new Element("header");
			doc.addContent(root);
			root.addContent(header);
			Element idheader = new Element("column");
			idheader.setAttribute("headerText", "序号");
			idheader.setAttribute("dataField", "id");
			header.addContent(idheader);
			for (OntoProperty prop: props) {
				String headerText = prop.label;
				String field = prop.variable.substring(1);
				Element headColumn = new Element("column");
				headColumn.setAttribute("headerText", headerText);
				headColumn.setAttribute("dataField", field);
				header.addContent(headColumn);
			}
			Element query = new Element("query");
			Element keywordElement = new Element("key");
			keywordElement.setText(key);
			query.addContent(keywordElement);
			Element cateElement = new Element("cate");
			cateElement.setText(cate);
			query.addContent(cateElement);
			Element sourceElement = new Element("source");
			sourceElement.setText(source);
			query.addContent(sourceElement);
			Element targetElement = new Element("target");
			targetElement.setText(target);
			query.addContent(targetElement);
			Element identityElement = new Element("identity");
			identityElement.setText(Integer.toString(identity));
			query.addContent(identityElement);
			Element sizeElement = new Element("pageSize");
			sizeElement.setText(Integer.toString(size));
			query.addContent(sizeElement);
			root.addContent(query);
			Element results = new Element("results");
			root.addContent(results);
			Element totalhit = new Element("totalHits");
			totalhit.setText(resdoc.getRootElement().getChildText("TotalNum"));
			results.addContent(totalhit);
			int count = 0;
			for (Element child:children) {
				count++;
				Element newChild = (Element)child.clone();
				newChild.setName("record");
				Element id = new Element("id");
				id.setText(Integer.toString(index*size + count));
				newChild.addContent(id);
				results.addContent(newChild);
			}
			
			Format format = Format.getPrettyFormat();
			format.setEncoding("GBK");
		
			XMLOutputter xmlout = new XMLOutputter(format);
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			xmlout.output(doc,bo);
			res = bo.toString();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	
	public static String wrap(DBConfig db, String record, OntoProperty statProp, String key, String cate,
			String source, String target, String field, int identity, int index, int size) {
		String res = "";
		Element xml = db.getGrid(target);
		if (xml == null)
			return "";
		try {
			StringReader sr = new StringReader(record);
			InputSource is = new InputSource(sr);
			Document resdoc = new SAXBuilder().build(is);
			ArrayList<DataGridHeader> headers = new ArrayList<DataGridHeader>();
			List<Element> columns = xml.getChildren("column");
			Document doc = new Document();
			Element root = new Element("result");
			Element head = new Element("header");
			for (Element column:columns) {
				String label = column.getChildText("label");
				String fieldCol = column.getChildText("field");
				String type = "";
				if (column.getChild("field").getAttribute("type") != null)
					type = column.getChild("field").getAttributeValue("type");	
				if (!fieldCol.equals("link"))
					headers.add(new DataGridHeader(label, fieldCol, type));
				
				Element headColumn = new Element("column");
				headColumn.setAttribute("headerText", label);
				if (type.equals("primary"))
					headColumn.setAttribute("dataField", "primary");
				else headColumn.setAttribute("dataField", fieldCol);
				head.addContent(headColumn);
			}
			root.addContent(head);
			Element query = new Element("query");
			Element keywordElement = new Element("key");
			keywordElement.setText(key);
			query.addContent(keywordElement);
			Element cateElement = new Element("cate");
			cateElement.setText(cate);
			query.addContent(cateElement);
			Element sourceElement = new Element("source");
			sourceElement.setText(source);
			query.addContent(sourceElement);
			Element targetElement = new Element("target");
			targetElement.setText(target);
			query.addContent(targetElement);
			Element fieldElement = new Element("field");
			fieldElement.setText(statProp.label);
			query.addContent(fieldElement);
			Element identityElement = new Element("identity");
			identityElement.setText(Integer.toString(identity));
			query.addContent(identityElement);
			Element sizeElement = new Element("pageSize");
			sizeElement.setText(Integer.toString(size));
			query.addContent(sizeElement);
			root.addContent(query);
			
			Element results = new Element("results");
			root.addContent(results);
			Element totalhit = new Element("totalHits");
			totalhit.setText(resdoc.getRootElement().getChildText("TotalNum"));
			results.addContent(totalhit);
			int count = 0;
			List<Element> children = resdoc.getRootElement().getChild("Records").getChildren("Record");
			for (Element child:children) {
				count++;
				Element newRecord = new Element("record");
				for (DataGridHeader header: headers) {
					if (header.field.equals("id")) {
						Element id = new Element("id");
						int xuhao = index * size + count;
						id.setText(Integer.toString(xuhao));
						newRecord.addContent(id);
					}
					else if (header.type.equals("primary")) {
						Element primaryElement = new Element("primary");
						String primary = child.getChildText(header.field);
						primaryElement.setText(primary);
						newRecord.addContent(primaryElement);
					}
					else {
						Element element = new Element(header.field);
						String value = child.getChildText(header.field);
						element.setText(value);
						newRecord.addContent(element);
					}
				}
				results.addContent(newRecord);
			}
			doc.addContent(root);
			Format format = Format.getPrettyFormat();
			format.setEncoding("GBK");
			
			XMLOutputter xmlout = new XMLOutputter(format);
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			xmlout.output(doc,bo);
			res = bo.toString();
		}
		catch (Exception e) {
			
		}
		return res;
	}
	
	public static String wrap(String record, ArrayList<OntoProperty> props, OntoProperty statProp, String key, 
			String cate, String source, String target, String field, int identity, int index, int size) {
		String res = "";
		try {
			StringReader sr = new StringReader(record);
			InputSource is = new InputSource(sr);
			Document resdoc = new SAXBuilder().build(is);
			Document doc = new Document();
			Element root = new Element("root");
			doc.addContent(root);
			Element items = new Element("items");
			root.addContent(items);		
			for (OntoProperty prop: props) {
				String label = prop.label;
				Element item = new Element("item");
				item.setText(label);
				items.addContent(item);
			}
			
			Element header = new Element("header");
			root.addContent(header);
			Element idheader = getHeaderColumn("序号", "id");
			header.addContent(idheader);
			Element statheader = getHeaderColumn(statProp.label, statProp.variable.substring(1));
			header.addContent(statheader);
			Element countheader = getHeaderColumn("关联次数", "count");
			header.addContent(countheader);
			Element linkheader = getHeaderColumn(target, "link");
			header.addContent(linkheader);
			
			Element query = new Element("query");
			root.addContent(query);
			Element keywordElement = new Element("key");
			keywordElement.setText(key);
			query.addContent(keywordElement);
			Element cateElement = new Element("cate");
			cateElement.setText(cate);
			query.addContent(cateElement);
			Element sourceElement = new Element("source");
			sourceElement.setText(source);
			query.addContent(sourceElement);
			Element targetElement = new Element("target");
			targetElement.setText(target);
			query.addContent(targetElement);
			Element fieldElement = new Element("field");
			fieldElement.setText(statProp.label);
			query.addContent(fieldElement);
			Element identityElement = new Element("identity");
			identityElement.setText(Integer.toString(identity));
			query.addContent(identityElement);
			Element sizeElement = new Element("pageSize");
			sizeElement.setText(Integer.toString(size));
			query.addContent(sizeElement);
			
			Element results = new Element("results");
			root.addContent(results);
			Element totalhit = new Element("totalHits");
			totalhit.setText(resdoc.getRootElement().getChildText("TotalNum"));
			results.addContent(totalhit);
			int count = 0;
			List<Element> children = resdoc.getRootElement().getChild("Records").getChildren("Record");
			for (Element child:children) {
				count++;
				Element newChild = (Element)child.clone();
				newChild.setName("record");
				Element id = new Element("id");
				id.setText(Integer.toString(index*size + count));
				newChild.addContent(id);
				Element primary = new Element("primary");
				primary.setText(newChild.getChildText(statProp.variable.substring(1)));
				newChild.addContent(primary);
				results.addContent(newChild);
			}
			Format format = Format.getPrettyFormat();
			format.setEncoding("GBK");
		
			XMLOutputter xmlout = new XMLOutputter(format);
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			xmlout.output(doc,bo);
			res = bo.toString();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	
	public static Element getHeaderColumn(String text, String field) {
		Element header = new Element("column");
		header.setAttribute("headerText", text);
		header.setAttribute("dataField", field);
		return header;
	}
}
