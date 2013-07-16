package servlet;

import search.ResultWrapper;
import search.SearchInstance;
import sparql.SparqlGenerator;
import configuration.DBConfig;
import configuration.TCMConfig;

public class LinchuangSearchThread extends Thread {
	public String key;
	public String source;
	public String target;
	public DBConfig db;
	public int type;
	
	public LinchuangSearchThread(String key, String source, String target, DBConfig db, int type) {
		this.key = key;
		this.source = source;
		this.target = target;
		this.db = db;
		this.type = type;
	}

	public void run() {
		// TODO Auto-generated method stub
		SparqlGenerator sg = new SparqlGenerator(key, source, target, db, type);
		String sparql, result;
		if (target.contains("疾病信息")) {
			sparql = sg.generateRecordContentSparql();
			result = SearchInstance.getRelationQuery(sparql, "0", "0");
		}
		else {
			sparql = sg.generateRelationSparql();
			result = SearchInstance.getRelationStatisic(sparql, "0", "0", sg.targetOnto.primaryVariable.substring(1));
		}
		String totalCount = ResultWrapper.getHitCount(result);
		System.out.println(target+ " " + totalCount);
	}
	
	public static void main(String[] args) {
		String key = "鼻炎";
		String source = "疾病";
		String target = "疾病信息-临床研究-证候";
		int type = 1;
		long start = System.currentTimeMillis();
		TCMConfig instance = TCMConfig.getInstance();
		instance.init("conf\\app-config.xml");
		DBConfig db = instance.getDBInstance("临床");
		String[] items = target.split("-");
	//	StringBuffer resInfo = new StringBuffer();
		
		for (int i = 0; i < items.length; i++) {
			Thread td = new LinchuangSearchThread(key, source, items[i],db, type);
			td.start();
			
		}
		long end = System.currentTimeMillis();
		long span = end - start;
		System.out.println(span);
	}
	
}
