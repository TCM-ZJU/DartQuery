package configuration;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import sparql.OntoProperty;
import sparql.SparqlGenerator;
import sparql.yaoli.SparqlBackGenerator;

public class TCMConfig {
	
	private static TCMConfig instance;
	private Document doc;
	public HashMap<String, DBConfig> configure;
	
	public static synchronized TCMConfig getInstance() {
		if (instance == null) {
			instance = new TCMConfig();
		}
		return instance;
	}
	
	private TCMConfig() {
		configure = new HashMap<String, DBConfig>();
	}
	
	
	public void init(String configFile) {
		try {
			doc = new SAXBuilder().build(configFile);
			Element root = doc.getRootElement();
			List<Element> domains = root.getChildren("domain");
			for (Element domain: domains) {
				String name = domain.getAttributeValue("name");
				DBConfig confInstance = new DBConfig(name, domain);
				getInstance().configure.put(name, confInstance);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	//	System.out.println("configuration initialized...");
	}
	
	public DBConfig getDBInstance(String name) {
		return getInstance().configure.get(name);
	}
	
	public static void main(String[] args) {
		TCMConfig instance = TCMConfig.getInstance();
		instance.init("conf\\relations-config.xml");
		DBConfig db = instance.getDBInstance("临床");
	//	SparqlBackGenerator sg = new SparqlBackGenerator("大黄","单味药", "相关给药方式", "毒理实验", "", db, 1);
	//	sg.init();
	//	System.out.println(sg.generateStatSparqlWithCate());
		
	//	OntoProperty onto = db.getNonePrefURI("?lcwx", "卷");
	//	System.out.println(onto.ontoURI);
		SparqlGenerator sg = new SparqlGenerator("肺炎", "疾病", "临床文献", db, 1);
		System.out.println(sg.generateRecordContentSparql());
		
		
	//	OntoProperty onto = db.getNonePrefURI("?syjc", "给药方式");
	//	System.out.println(onto.variable);
	//	SparqlGenerator sg = new SparqlGenerator("肺炎", "药方名称", "相关给药方式", db, 1);
	//	System.out.println(sg.sourceOnto.variable);
	//	System.out.println(sg.targetOnto.variable);
	//	System.out.println(db.getMultiRelations("?herb", "?jb"));
	//	System.out.println(db.getPropURI("?yjyw", "药物功效与作用"));
	//	System.out.println(db.getVariable("研究药物"));
	//	SparqlBackGenerator sg = new SparqlBackGenerator("大黄","单味药", "研究药物", "药理实验", "对象名称", db, 1);

		/*try {
			Class c = Class.forName("java.lang.String");
			Method m = c.getMethod("valueOf", new Class[]{int.class});

			System.out.println(m);
		}
		catch (Exception e) {
			
		}*/
		/*try {
			test();
		}
		catch (Exception e) {
			System.out.println("in main");
		}
		finally {
			System.out.println("finally in main");
		}
	}
	
	public static void test() throws Exception {
		try {
			throw new Exception();	
		}
		catch (Exception e) {
			System.out.println("in test");
			throw e;
		}
		finally {
			System.out.println("finally in test");
		}*/
	}
}
