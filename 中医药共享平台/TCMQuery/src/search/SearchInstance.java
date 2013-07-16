package search;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.ws.rs.core.MultivaluedMap;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class SearchInstance {
	
	public static String baseurl;
	
	static {
		try {
			BufferedReader in = new BufferedReader(new FileReader("..\\webapps\\TCMQuery\\conf\\server.txt"));
			baseurl = in.readLine();
			System.out.println(baseurl);
		}
		catch (Exception e) {
			baseurl = "http://10.214.0.148:8080/";
			e.printStackTrace();
		}
	}

	public static String getRelationQuery(String sparql, String index, String size) {
		String url = baseurl + "DartQuery/QueryService";
		Client client = Client.create();
		WebResource webResource =client.resource(url);
		MultivaluedMap queryParams = new MultivaluedMapImpl();
		queryParams.add("query", sparql);
		queryParams.add("pagenum", index);
		queryParams.add("pagesize", size);
	//	System.out.println("relation query: \n" + sparql);
	//	String res = (String)webResource.queryParams(queryParams).post(String.class);
		String res = (String)webResource.type("application/x-www-form-urlencoded").post(String.class,queryParams); 
		return res;		
	}
	
	public static String getRelationStatisic(String sparql, String index, String size, String field) {
		String url = baseurl + "DartQuery/StatisticsService";
		Client client = Client.create();
		WebResource webResource =client.resource(url);
		MultivaluedMap queryParams = new MultivaluedMapImpl();
		queryParams.add("query", sparql);
		queryParams.add("pagenum", index);
		queryParams.add("pagesize", size);
		queryParams.add("field", field);
	//	System.out.println("relation stats: \n" + sparql);
	//	String res = "";
	//	String res = (String)webResource.queryParams(queryParams).post(String.class);
		String res = (String)webResource.type("application/x-www-form-urlencoded").post(String.class,queryParams); 
		return res;		
	}
	/*
	public static byte[] getPicQuery(String sparql, String index, String size) {
		String url = "http://10.214.33.188:8080/DartQuery/PicQueryService";
		Client client = Client.create();
		WebResource webResource =client.resource(url);
		MultivaluedMap queryParams = new MultivaluedMapImpl();
		queryParams.add("query", sparql);
		queryParams.add("pagenum", index);
		queryParams.add("pagesize", size);
	//	System.out.println("relation stats: \n" + sparql);
	//	String res = "";
	//	String res = (String)webResource.queryParams(queryParams).post(String.class);
		byte[] res = webResource.type("application/x-www-form-urlencoded").post(byte[].class,queryParams); 
		return res;		
	}*/
	
	public static byte[] getPicQuery(String sparql, String index, String size) {
		int maxSize = 1024*4000;
		byte[] buffer = new byte[4096];
		byte[] result = new byte[maxSize];
		int count = 0;
		//byte[] result = new byte[maxSize];
		try {
			String url = baseurl + "DartQuery/PicQueryService";
			URL baseURL = new URL(url);
			HttpURLConnection con = (HttpURLConnection)baseURL.openConnection();
			con.setRequestMethod("POST");
			con.setDoInput(true);
			con.setDoOutput(true);
			StringBuffer param = new StringBuffer();
			param.append("query=");
			param.append(sparql);
			param.append("&pagenum=");
			param.append(index);
			param.append("&pagesize=");
			param.append(size);
			PrintWriter out = new PrintWriter(new OutputStreamWriter(con.getOutputStream(), "UTF-8"));
		//	PrintWriter out = new PrintWriter(con.getOutputStream());
			out.print(param);
			out.flush();
			out.close();		
			if (con.getResponseCode() != HttpURLConnection.HTTP_OK)
			{
				System.out.println("failed");
				return null;
			}
			InputStream is = con.getInputStream();
			int length;
			count = 0;
			while ((length = is.read(buffer))!= -1) {
				for (int i = 0; i < length; i++)
				{
					result[count++] = buffer[i];
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		byte[] res = new byte[count];
		for (int i = 0; i < count; i++)
			res[i] = result[i];
		System.out.println(count);
		return res;
	}
	
	public static String getSearchQuery(String key, String field, String index, String size) {
		Client client=Client.create();
		WebResource webResource=client.resource("http://10.214.33.102:8080/NewSearchServer/tcmSearch");
		MultivaluedMap queryParams=new MultivaluedMapImpl();
	//	String queryString = "鼻塞";
	//	System.out.println(key + " " + field + " " + index + " "+size);
		queryParams.add("queryExpression", key);
		queryParams.add("serviceIdentity", "test");
		queryParams.add("searchField", "showContent");
		queryParams.add("pageIndex", index);
		queryParams.add("pageSize", size);
		
		String res = webResource.queryParams(queryParams).get(String.class);
		return res;
	}
	
	public static String getSparql() {
	//	String url = "http://localhost:8080/TCMQuery/servlet/RelationQuery";
		String url = "http://localhost:8080/TCMQuery/servlet/QueryService";
		Client client = Client.create();
		WebResource webResource =client.resource(url);
		MultivaluedMap queryParams = new MultivaluedMapImpl();
		queryParams.add("key", "肺炎");
		queryParams.add("source", "疾病名称");
		queryParams.add("target", "相关症状");
		queryParams.add("index", "0");
		queryParams.add("size", "5");
		queryParams.add("type", "1");
		String res = (String)webResource.queryParams(queryParams).get(String.class);
		//String res = (String)webResource.type("application/x-www-form-urlencoded").post(String.class,queryParams);
		return res;		
	}
	
	public static String getRecordSparql() {
		//	String url = "http://localhost:8080/TCMQuery/servlet/RelationQuery";
			String url = "http://localhost:8080/TCMQuery/servlet/RecordInfoService";
			Client client = Client.create();
			WebResource webResource =client.resource(url);
			MultivaluedMap queryParams = new MultivaluedMapImpl();
			queryParams.add("key", "白矾");
			queryParams.add("source", "单味药");
			queryParams.add("target", "药");
			queryParams.add("index", "0");
			queryParams.add("size", "5");
			queryParams.add("type", "1");
			String res = (String)webResource.queryParams(queryParams).get(String.class);
			//String res = (String)webResource.type("application/x-www-form-urlencoded").post(String.class,queryParams);
			return res;		
		}
	
	public static String getIndexSearch() {
		String url = "http://localhost:8080/TCMQuery/servlet/SearchService";
		Client client = Client.create();
		WebResource webResource =client.resource(url);
		MultivaluedMap queryParams = new MultivaluedMapImpl();
		queryParams.add("serviceIdentity", "test");
		queryParams.add("key", "中医");
		queryParams.add("source", "showContent");
		queryParams.add("index", "0");
		queryParams.add("size", "10");
		String res = (String)webResource.queryParams(queryParams).get(String.class);
		//String res = (String)webResource.type("application/x-www-form-urlencoded").post(String.class,queryParams);
		return res;		
	}
	
	public static String getBasicSearch() {
		String url = "http://localhost:8080/TCMQuery/servlet/BasicSearchService";
		Client client = Client.create();
		WebResource webResource =client.resource(url);
		MultivaluedMap queryParams = new MultivaluedMapImpl();
		queryParams.add("key", "大黄");
		queryParams.add("source", "研究药物");
		queryParams.add("target", "文献");
		queryParams.add("cate", "单味药");
		queryParams.add("index", "0");
		queryParams.add("size", "10");
		queryParams.add("type", "1");
		queryParams.add("identity", "1");
		String res = (String)webResource.queryParams(queryParams).get(String.class);
		//String res = (String)webResource.type("application/x-www-form-urlencoded").post(String.class,queryParams);
		return res;		
	}
	
	public static String getStatSearch() {
		String url = "http://localhost:8080/TCMQuery/servlet/StatSearchService";
		Client client = Client.create();
		WebResource webResource =client.resource(url);
		MultivaluedMap queryParams = new MultivaluedMapImpl();
		queryParams.add("key", "大黄");
		queryParams.add("source", "研究药物");
		queryParams.add("target", "毒理实验");
		queryParams.add("cate", "单味药");
		queryParams.add("field", "");
		queryParams.add("index", "0");
		queryParams.add("size", "10");
		queryParams.add("type", "1");
		queryParams.add("identity", "1");
		String res = (String)webResource.queryParams(queryParams).get(String.class);
		//String res = (String)webResource.type("application/x-www-form-urlencoded").post(String.class,queryParams);
		return res;		
	}
	
	public static String getGeneralSearch() {
		String url = "http://localhost:8080/TCMQuery/servlet/GeneralInfoService";
		Client client = Client.create();
		WebResource webResource =client.resource(url);
		MultivaluedMap queryParams = new MultivaluedMapImpl();
		queryParams.add("key", "黄");
		queryParams.add("source", "研究药物");
		queryParams.add("target", "研究药物-对照组");
		queryParams.add("cate", "单味药");
		queryParams.add("type", "2");
		String res = (String)webResource.queryParams(queryParams).get(String.class);
		//String res = (String)webResource.type("application/x-www-form-urlencoded").post(String.class,queryParams);
		return res;		
	}
	
	public static String getLinchuangGeneralSearch() {
		String url = "http://localhost:8080/TCMQuery/servlet/LinChuangGeneralInfoService";
		Client client = Client.create();
		WebResource webResource =client.resource(url);
		MultivaluedMap queryParams = new MultivaluedMapImpl();
		queryParams.add("key", "鼻炎");
		queryParams.add("source", "疾病");
		queryParams.add("target", "疾病信息-症状-证候-临床研究");
		queryParams.add("type", "1");
		String res = (String)webResource.queryParams(queryParams).get(String.class);
		//String res = (String)webResource.type("application/x-www-form-urlencoded").post(String.class,queryParams);
		return res;		
	}
	
	public static void main(String[] args) throws Exception{
		//	long start = System.currentTimeMillis();
		//	System.out.println(getSparql());
		//	String res = getSparql();  
		//	String res = getRecordSparql();
		//	String res = getSearchQuery("糖尿病", "jbmc", "1", "10");
		//	String res = getIndexSearch();
		//	String res = getBasicSearch();
			String res = getSparql();
		//	String res = getLinchuangGeneralSearch();
			System.out.println(res);
		//	String res = getSearchQuery("疾病","", "0", "10");
		//	long end = System.currentTimeMillis();
		//	long span = end - start;
		//	System.out.println(span);
		//	System.out.println(res);
			
			/*try{
				sparql = new String(sparql.getBytes(), "GBK");
			}catch(Exception ex){
				
			}*/
		//	System.out.println(sparql);
		//	System.out.println(getRelationQuery(sparql,"1","10"));
		//	System.out.println(getRelationStatisic(sparql,"1","10", "zz_name"));
		//	System.out.println(getPic("大黄", 1));
		//	System.out.println(getTableContent("{http://ccnt.cn/tcm}shanghai", "SHENBING", "ID:658"));
		//	System.out.println(getRelatedOnto("http://cintcm.ac.cn/onto#herb"));
		//	System.out.println(getWebPage("大黄", 1));
		/*try
		{
			String result = null;
			URL U = new URL("http://bncweb.lancs.ac.uk/cgi-binbncXML/main.pl?" +
					"chunk=1&inst=50&qname=sironeko83_1275283693&queryID=sironeko83_1275283693&" +
					"oldthMode=M26690%232241%23no_subcorpus%23%23&numOfFiles=2241&" +
					"theData=[word%3D%22car%22%25c]&warned=&numOfSolutions=26690&" +
					"view=list&theID=sironeko83_1275283693&thMode=1&thin=5000&max=INIT&urlTest=yes");
		    HttpURLConnection conn  = (HttpURLConnection)U.openConnection();
		    String userPassword = "keaven:dust08288";
		    String encoding = new sun.misc.BASE64Encoder().encode(userPassword.getBytes());

		    conn.setRequestProperty ("Authorization", "Basic " + encoding);
		   BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		   String line;
		   while ((line = in.readLine())!= null)
		   {
		    result += line;
		   }
		   in.close(); 
		   System.out.println(result);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}*/

		
			/*TCMConfig instance = TCMConfig.getInstance();
			instance.init("conf\\relations-config.xml");
			DBConfig db = instance.getDBInstance("临床");
			SparqlGenerator sg = new SparqlGenerator("马达加斯加高地低水平疟疾流行区原虫密度与发热的相互关系", "临床文献", "原文", db, 1);
			String result = sg.generateRelationSparqlWithNoPref();
			System.out.println(result);
			byte[] b = getPicQuery(result, "0", "5");
			System.out.println(b.length);*/
			/*try {
				int index = 0;
				while (index < b.length) {
					byte[] img = new byte[b.length];
		        	int imgcount = 0;
		    		while(!new String(b, index, 4).equals("$$$$")){
		    			img[imgcount++] = b[index++];
		    		}
		    		index+=4;
		            File f = new File( "E:\\Pic" + index + ".jpg");  
			        FileOutputStream fos = new FileOutputStream(f);
			        fos.write(img, 0 ,imgcount);
			        fos.flush();
			        fos.close();
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}*/
	}
}


