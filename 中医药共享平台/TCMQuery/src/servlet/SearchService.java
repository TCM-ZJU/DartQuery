package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import configuration.DBConfig;
import configuration.TCMConfig;
import sparql.Ontology;
import sparql.OntoProperty;
import search.SearchInstance;
import search.ResultWrapper;

public class SearchService extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public SearchService() {
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
		String pageIndex = request.getParameter("index");
		String pageSize = request.getParameter("size");
		TCMConfig instance = TCMConfig.getInstance();
		DBConfig db = instance.getDBInstance("临床"); 
		Ontology sourceOnto = db.getOntoInstance(source);
		String field = sourceOnto.primaryField;
		System.out.println(key + " " + source + " " + field);
		ArrayList<OntoProperty> props = db.getProps(sourceOnto.variable);
		String result = SearchInstance.getSearchQuery(key, field, pageIndex, pageSize);
		int index = Integer.parseInt(pageIndex);
		int size = Integer.parseInt(pageSize);
	//	result = ResultWrapper.wrap(result, props, index, size);
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
			return;
		}
		ServletContext sContext = this.getServletContext();
		String file = sContext.getInitParameter("config-file");
		instance.init(file);
	}

}
