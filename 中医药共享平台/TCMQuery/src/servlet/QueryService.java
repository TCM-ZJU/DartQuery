package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import search.ResultWrapper;
import search.SearchInstance;
import sparql.SparqlGenerator;
import configuration.DBConfig;
import configuration.TCMConfig;

public class QueryService extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public static int SEARCH = 1;
	public static int QUERY = 2;
	public QueryService() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String key = request.getParameter("key");
		key = new String(key.getBytes("iso-8859-1"), "UTF-8");
		String source = request.getParameter("source");
		source = new String(source.getBytes("iso-8859-1"), "UTF-8");
		String target = request.getParameter("target");
		target = new String(target.getBytes("iso-8859-1"), "UTF-8");
		String pageIndex = request.getParameter("index");
		String pageSize = request.getParameter("size");
		int type = Integer.parseInt(request.getParameter("type"));
		int cate = getSearchCate(source, target);	
		relationQuery(response, key, source, target, pageIndex, pageSize, type);
	}
	
	public int getSearchCate(String source, String target) {
		TCMConfig instance = TCMConfig.getInstance();
		DBConfig db = instance.getDBInstance("临床");
		String var_source = db.getVariable(source);
		String var_target = db.getVariable(target);
		if (var_source.equals(var_target)) {
			return SEARCH;
		}
		else 
			return QUERY;
	}
	
	public void relationQuery(HttpServletResponse response, String key, String source, String target, 
			String pageIndex, String pageSize, int type) throws ServletException, IOException {
		System.out.println(key + " " + source + " " + target + " page " + pageIndex + " type " + type + " pageSize " + pageSize);
		TCMConfig instance = TCMConfig.getInstance();
		DBConfig db = instance.getDBInstance("临床");
		SparqlGenerator sg = new SparqlGenerator(key, source, target, db, type);
		String sparqlQuery = "";
		int flag = 0;
		flag = db.checkSparqlType(target);
	//	System.out.println(flag);
	//	System.out.println(sg.sourceOnto.variable.equals(sg.targetOnto.variable));
		// flag == 1 则是非prefLabel字段的关联查询
		if (flag == 1) {
			if (!sg.sourceOnto.variable.equals(sg.targetOnto.variable))
				sparqlQuery = sg.generateRelationSparqlWithNoPref();
			else 
				sparqlQuery = sg.generateInnerSparql();
		}
		else
			sparqlQuery = sg.generateRelationSparql();
		System.out.println(sparqlQuery);
		
		String resultXML="";
		if (flag == 1)
			resultXML = SearchInstance.getRelationStatisic(sparqlQuery, pageIndex, pageSize, sg.targetProp.variable.substring(1));
		else
			resultXML = SearchInstance.getRelationStatisic(sparqlQuery, pageIndex, pageSize, sg.targetOnto.primaryVariable.substring(1));
		int index = Integer.parseInt(pageIndex);
		int size = Integer.parseInt(pageSize);
		
		System.out.println("in queryService \n" + resultXML);
		String result = ResultWrapper.Wrap(db, key, source, target, resultXML.trim(), index, size);
		response.setContentType("text/plain;charset=utf-8");
		PrintWriter out = response.getWriter();
		out.print(result);
		out.flush();
		out.close();
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
		TCMConfig instance = TCMConfig.getInstance();
		if (!instance.configure.isEmpty()) {
		//	System.out.println("not empty in query service");
			return;
		}
		ServletContext sContext = this.getServletContext();
		String file = sContext.getInitParameter("config-file");
	//	System.out.println("file" + file);
		instance.init(file);
	//	System.out.println("empty in query service");
	}

}
