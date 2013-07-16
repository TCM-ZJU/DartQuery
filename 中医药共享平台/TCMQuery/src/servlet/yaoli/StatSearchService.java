package servlet.yaoli;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import search.SearchInstance;
import sparql.yaoli.SparqlBackGenerator;
import search.yaoli.ResultWrapper;
import configuration.DBConfig;
import configuration.TCMConfig;

public class StatSearchService extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public StatSearchService() {
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
		String cate = request.getParameter("cate");
		cate = new String(cate.getBytes("iso-8859-1"), "UTF-8");
		String field = request.getParameter("field");
		field = new String(field.getBytes("iso-8859-1"), "UTF-8");
		String pageIndex = request.getParameter("index");
		String pageSize = request.getParameter("size");
		int type = Integer.parseInt(request.getParameter("type"));
		int identity = Integer.parseInt(request.getParameter("identity"));
		System.out.println(key + " " + source + " " + target + " " + cate + " " + field + " " + type + " id " + identity);
		
		TCMConfig instance = TCMConfig.getInstance();
		DBConfig db = instance.getDBInstance("药理");
	//	SparqlGenerator sg = new SparqlGenerator(key, cate, source, target, db, type);
		SparqlBackGenerator sg = new SparqlBackGenerator(key, cate, source, target, field, db, type);
		String sparql;
	//	if (cate.equals("药物功效与作用") || cate.equals("检测指标") || cate.equals("动物模型"))
		//	sparql = sg.generateStatSparqlWithAttr();
		if (identity == 1)
			sparql = sg.generateStatSparqlWithCate();
		else sparql = sg.generateStatSparqlWithAttr();
		System.out.println(sparql);
	//	System.out.println(pageIndex + " " + pageSize + " " + sg.statProp.variable);
		
		String result = SearchInstance.getRelationStatisic(sparql, pageIndex, pageSize, sg.statProp.variable.substring(1));
		int index = Integer.parseInt(pageIndex);
		int size = Integer.parseInt(pageSize);
		System.out.println(result);
		
	//	result = ResultWrapper.wrap(result, sg.props, key, cate, source, target, index, size);
		
	//	result = ResultWrapper.wrap(result, sg.statProps, sg.statProp, key, cate, source, target, field, identity, index, size);
	
		result = ResultWrapper.wrap(db, result, sg.statProp, key, cate, source, target, field, identity, index, size);

		response.setContentType("text/xml;charset=utf-8");
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
