package servlet;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.FileOutputStream;
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

public class PicQueryService extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public PicQueryService() {
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
		System.out.println(key + " " + source + " " + target + " page " + pageIndex + " type " + type + " pageSize " + pageSize);
		
		ServletContext sContext = this.getServletContext();
		String addr = sContext.getInitParameter("picAddr");
		String resultXML;
		int count = readCache(key);
		if (count != -1) {
			resultXML = ResultWrapper.picWrap(addr, key, count);
		}
		else {
			TCMConfig instance = TCMConfig.getInstance();
			DBConfig db = instance.getDBInstance("临床");
			SparqlGenerator sg = new SparqlGenerator(key, source, target, db, type);
			String sparql = sg.generateRelationSparqlWithNoPref();
			System.out.println(sparql);
			byte[] res = SearchInstance.getPicQuery(sparql, pageIndex, pageSize);
			System.out.println(res.length);
			int num = writeToCache(res, key);
			resultXML = ResultWrapper.picWrap(addr, key, num);
		}
		System.out.println(resultXML);
		response.setContentType("text/xml;charset=utf-8");
		PrintWriter out = response.getWriter();
		out.print(resultXML);
		out.flush();
		out.close();
	}
	
	public int readCache(String name) {
		ServletContext sContext = this.getServletContext();
		String path = sContext.getInitParameter("picCache");
		File dir = new File(path + "\\" + name.hashCode() + "\\");
		if (!dir.exists()) 
			return -1;
		File[] files = dir.listFiles();
		int count = 0;
		for (File img: files) {
			String fileName = img.getName();
			if (fileName.endsWith(".jpg"))
				count++;
		}
		return count;
		
	}
	
	public int writeToCache(byte[] content, String name) {
		int num = 0;
		ServletContext sContext = this.getServletContext();
		String path = sContext.getInitParameter("picCache");
		int index = 0;
		while (index < content.length) {
			byte[] img = new byte[content.length];
        	int imgcount = 0;
    		while(!new String(content, index, 4).equals("$$$$")){
    			img[imgcount++] = content[index++];
    		}
    		index+=4;
    		num++;
    		File dir = new File(path + "\\" + name.hashCode() + "\\");
    		if (!dir.exists())
    			dir.mkdir();
            File f = new File(dir.getPath() + "\\" + num + ".jpg");  
            try {
		        FileOutputStream fos = new FileOutputStream(f);
		        fos.write(img, 0 ,imgcount);
		        fos.flush();
		        fos.close();
            }
            catch (Exception e) {
            	e.printStackTrace();
            }
		}
		return num;
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
