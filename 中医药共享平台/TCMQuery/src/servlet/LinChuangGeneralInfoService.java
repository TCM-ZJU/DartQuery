package servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import search.SearchInstance;
import sparql.SparqlGenerator;
import search.ResultWrapper;
import configuration.DBConfig;
import configuration.TCMConfig;

public class LinChuangGeneralInfoService extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public LinChuangGeneralInfoService() {
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
		int type = Integer.parseInt(request.getParameter("type"));
		
		
		TCMConfig instance = TCMConfig.getInstance();
		DBConfig db = instance.getDBInstance("临床");
		String[] items = target.split("-");
		StringBuffer resInfo = new StringBuffer();
		
		for (int i = 0; i < items.length; i++) {
			System.out.println(key + " " +source + " " + items[i] + " type " + type);
			SparqlGenerator sg = new SparqlGenerator(key, source, items[i], db, type);
			String sparql, result;
			int flag = db.checkSparqlType(items[i]);
		/*	if (items[i].contains("疾病信息")) {
				sparql = sg.generateRecordContentSparql();
				result = SearchInstance.getRelationQuery(sparql, "0", "0");
			}*/
			if (flag == 1) {
				if (!sg.sourceOnto.variable.equals(sg.targetOnto.variable))
					sparql = sg.generateRelationSparqlWithNoPref();
				else 
					sparql = sg.generateInnerSparql();
				result = SearchInstance.getRelationStatisic(sparql, "0", "0", sg.targetProp.variable.substring(1));
			}
			else {
				sparql = sg.generateRelationSparql();
				result = SearchInstance.getRelationStatisic(sparql, "0", "0", sg.targetOnto.primaryVariable.substring(1));
			}
			String totalCount = ResultWrapper.getHitCount(result);
			resInfo.append(items[i] + " " + totalCount);
			if (i < items.length -1)
				resInfo.append("-");
			//System.out.println(items[i] + " " + totalCount);
		}
		response.setContentType("text/xml;charset=utf-8");
		PrintWriter out = response.getWriter();
		out.print(resInfo.toString());
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
