package id.co.sigma.zk.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

/**
 * 
 * @author <a href='mailto:gede.sutarsa@gmail.com'>Gede Sutarsa</a>
 */
@WebServlet(
		description="provider resource static. image dan css dari dalam jar" , 
		urlPatterns={
				"/web/zul/img/ctrlimages/*"
		} , name="zk-common-control-static-resource-provider" , loadOnStartup=1 
		)
public class ZKComponentImageStaticResourceProviderServlet extends  HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7113852219294786011L;
	
	
	
	private HashMap<String, File> cachedFiles = new HashMap<String, File>() ; 

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		//IOUtils.cop
		String path = req.getRequestURI().substring(req.getContextPath().length() ) ; 
		if ( cachedFiles.containsKey(path)){
			File pth = cachedFiles.get(path); 
			System.out.println("menulis dari cache:" + pth.getAbsolutePath());
			IOUtils.copy(new FileInputStream(pth), resp.getOutputStream());
			return ; 
		}
		
		System.out.println("menulis dari jar");
		InputStream is =  getClass().getResourceAsStream(path);
		File tmp = File.createTempFile("__auto_cache", "gps__");
		IOUtils.copy(is, new FileOutputStream(tmp));
		cachedFiles.put(path, tmp);
		IOUtils.copy(new FileInputStream(tmp), resp.getOutputStream());
	}

}
