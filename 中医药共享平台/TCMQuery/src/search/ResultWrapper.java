package search;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import sparql.DataGridHeader;
import sparql.OntoProperty;
import sparql.Ontology;

import configuration.DBConfig;

import org.jdom.input.SAXBuilder;
import org.jdom.Element;
import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.xml.sax.InputSource;


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
	
	public static String Wrap(DBConfig db, String keyword, String source, String target, String res, int index, int size) {

		Element xml = db.getGrid(target);
		Ontology sourceOnto = db.getOntoInstance(source);
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
				if (field.equals("source_name")) {
					label = sourceOnto.primaryLabel;
				//	field = sourceOnto.primaryVariable.substring(1);
				}
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
			Element sizeElement = new Element("pageSize");
			sizeElement.setText(Integer.toString(size));
			query.addContent(sizeElement);
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
					else if (header.field.equals("source_name")) {
						String label_field = sourceOnto.primaryVariable.substring(1);
						Element element = new Element("source_name");
						String value = record.getChildText(label_field);
						element.setText(value);
						newRecord.addContent(element);
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
	
	
	//  用于多条记录多字段的wrap
	public static String wrap(String record, ArrayList<OntoProperty> props, String key, String source, String target, int index, int size) {
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
			Element sourceElement = new Element("source");
			sourceElement.setText(source);
			query.addContent(sourceElement);
			Element targetElement = new Element("target");
			targetElement.setText(target);
			query.addContent(targetElement);
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
	
	
	// 单条记录的wrap
	public static String wrap(String record, String source, ArrayList<OntoProperty> props) {
		String res = "";
		try {
			StringReader sr = new StringReader(record);
			InputSource is = new InputSource(sr);
			Document resdoc = new SAXBuilder().build(is);
			Element recordElement = resdoc.getRootElement().getChild("Records").getChild("Record");
			Document doc = new Document();
			Element root = new Element("result");
			doc.addContent(root);
			for (OntoProperty prop: props) {
				
				String variable = prop.variable;
				String label = prop.label;
				Element item = new Element("item");
				Element col_label = new Element("label");
			//	if (label.equals("概念词"))
			//		label = source + "名称";
				col_label.setText(label);
				Element col_value = new Element("value");
				if (recordElement != null)
					col_value.setText(recordElement.getChildText(variable.substring(1)));
				item.addContent(col_label);
				item.addContent(col_value);
				root.addContent(item);
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
	
	public static String wrap(String res, ArrayList<OntoProperty> props, int index, int size) {
		try {
			StringReader sr = new StringReader(res);
			InputSource is = new InputSource(sr);
			Document resdoc = new SAXBuilder().build(is);
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
			Element results = new Element("results");
			root.addContent(results);
			Element totalhit = new Element("totalHits");
			totalhit.setText(resdoc.getRootElement().getChildText("totalHits"));
			results.addContent(totalhit);
			List<Element> children = resdoc.getRootElement().getChildren("hit");
			for (Element child: children) {
				
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
	
	public static String picWrap(String addr, String key, int num) {
		String res = "";
		try {
			Document doc = new Document();
			Element root = new Element("root");
			Element keyElement = new Element("key");
			keyElement.setText(key);
			root.addContent(keyElement);
			Element countElement = new Element("count");
			countElement.setText(String.valueOf(num));
			root.addContent(countElement);
			for (int i = 1; i <= num; i++) {
				Element img = new Element("img");
				Element source = new Element("source");
				source.setText(addr + key.hashCode() + "/" + i + ".jpg");
				img.addContent(source);
				root.addContent(img);
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
			e.printStackTrace();
		}
		return res;
	}

}
