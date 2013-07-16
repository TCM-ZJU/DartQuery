package servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import search.SearchInstance;
import search.ResultWrapper;
import sparql.SparqlGenerator;

import configuration.DBConfig;
import configuration.TCMConfig;


public class RecordInfoService extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public RecordInfoService() {
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
		System.out.println(key + " " +source + " " + target + " page " + pageIndex + " type " + type);
		TCMConfig instance = TCMConfig.getInstance();
		DBConfig db = instance.getDBInstance("临床");
		SparqlGenerator sg = new SparqlGenerator(key, source, target, db, type);
		String result = sg.generateRecordContentSparql();
		System.out.println(result);
		String resultXML;
		resultXML = SearchInstance.getRelationQuery(result, pageIndex, pageSize);
	//	String resultXML = SearchInstance.getPicQuery(result, pageIndex, pageSize);
		System.out.println(resultXML);
		String wrappedRes;
		if (Integer.parseInt(pageSize) == 1)
			wrappedRes = ResultWrapper.wrap(resultXML, source, sg.props);   
		else {
			int index = Integer.parseInt(pageIndex);
			int size = Integer.parseInt(pageSize);
			wrappedRes = ResultWrapper.wrap(resultXML, sg.props, key, source, target, index, size);
		}
		//System.out.println(wrappedRes);
		response.setContentType("text/xml;charset=utf-8");
		PrintWriter out = response.getWriter();
		
		out.print(wrappedRes);
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
