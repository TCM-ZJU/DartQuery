package servlet.yaoli;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import search.SearchInstance;
import search.yaoli.ResultWrapper;
import sparql.yaoli.SparqlBackGenerator;

import configuration.DBConfig;
import configuration.TCMConfig;

public class GeneralInfoService extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public GeneralInfoService() {
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
		int type = Integer.parseInt(request.getParameter("type"));
		
		TCMConfig instance = TCMConfig.getInstance();
		DBConfig db = instance.getDBInstance("药理");
		String[] items = target.split("-");
		response.setContentType("text/xml;charset=utf-8");
		PrintWriter out = response.getWriter();
		
		StringBuffer resInfo = new StringBuffer();
		for (int i = 0; i < items.length; i++) {
			System.out.println(key + " " + cate + " " + source + " " + items[i]);
		//	SparqlBackGenerator sg = new SparqlBackGenerator(key, cate, source, items[i], db, type);
			SparqlBackGenerator sg = new SparqlBackGenerator(key, cate, source, items[i], items[i], db, type);
			String sparql="";
			/*if (cate.equals("药物功效与作用") || cate.equals("检测指标") || cate.equals("动物模型"))
			//	sparql = sg.generateRecordSparqlWithAttr();
				sparql = sg.generateStatSparqlWithAttr();
		//	else sparql = sg.generateRecordSparqlWithCate();
			else sparql = sg.generateStatSparqlWithCate();*/
			if (source.indexOf("研究药物")!= -1)
				sparql = sg.generateStatSparqlWithCate();
			else if (items[i].indexOf("单味药") != -1 || items[i].indexOf("化学成分") != -1 || items[i].indexOf("方剂") != -1)
				sparql = sg.generateStatSparqlWithCate();
			else 
				sparql = sg.generateStatSparqlWithAttr();
			System.out.println(sparql);
		//	String result = SearchInstance.getRelationQuery(sparql, "0", "0");
			String result = SearchInstance.getRelationStatisic(sparql, "0", "0", sg.statProp.variable.substring(1));
			String totalCount = ResultWrapper.getHitCount(result);
			resInfo.append(items[i] + " " + totalCount);
			if (i < items.length -1)
				resInfo.append("-");
			//System.out.println(items[i] + " " + totalCount);
		}
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
