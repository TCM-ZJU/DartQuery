package servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MultivaluedMap;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import configuration.*;
import sparql.*;
import search.SearchInstance;


public class RelationQuery extends HttpServlet {

	/**
	 * Constructor of the object.
	 */  
	public RelationQuery() {
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
		
		System.out.println(key + " " + source + " " + target + " " + pageIndex);
		
		TCMConfig instance = TCMConfig.getInstance();
		DBConfig db = instance.getDBInstance("临床");
		SparqlGenerator sg = new SparqlGenerator(key, source, target, db, 1);
		System.out.println(sg.result);
		String resultXML = SearchInstance.getRelationStatisic(sg.result, pageIndex, pageSize, sg.targetOnto.primaryVariable.substring(1));
		int index = Integer.parseInt(pageIndex);
		int size = Integer.parseInt(pageSize);
		
		String result = db.resultWrap(key, source, target, resultXML.trim(), sg.targetOnto.primaryVariable, index, size);
		response.setContentType("text/xml;charset=utf-8");
		PrintWriter out = response.getWriter();
	//	System.out.println(result);
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
		//	System.out.println("not empty in query relation");
			return;
		}
		ServletContext sContext = this.getServletContext();
		String file = sContext.getInitParameter("config-file");
	//	System.out.println("file" + file);
		instance.init(file);
	//	System.out.println("empty in query relation");
	}

}
