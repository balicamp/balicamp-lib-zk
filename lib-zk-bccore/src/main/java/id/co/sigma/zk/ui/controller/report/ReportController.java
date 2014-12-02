package id.co.sigma.zk.ui.controller.report;

import id.co.sigma.zk.ui.controller.base.BaseSimpleController;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.Map;

import org.zkoss.util.media.AMedia;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.ClientInfoEvent;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Toolbar;
import org.zkoss.zul.Window;

public class ReportController extends BaseSimpleController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5141315517810906819L;
	
	@Wire
	private Iframe reportFrame;
	
	@Wire
	private Toolbar buttonToolbar;
	
	@Wire
	private Window rptWindow;
	
	private String reportUrl;
	
	@Listen("onClick = #btnClose")
	public void closeWindow() {
		
		if(rptWindow != null) {
//			rptWindow.setVisible(false);
			rptWindow.detach();
		}
		
	}

	@Override
	public ComponentInfo doBeforeCompose(Page page, Component parent,
			ComponentInfo compInfo) {
		
		ComponentInfo info = super.doBeforeCompose(page, parent, compInfo);
		
		Map<?,?> passedParameter  = Executions.getCurrent().getArg();
		
		reportUrl = (String) passedParameter.get("reportUrl");
		
		System.out.println("URL: " + reportUrl);
		
		return info;
	}

	@Listen("onClientInfo = #rptWindow")
	public void onShowReport(ClientInfoEvent event) {
		reportFrame.setHeight(((int)(0.8 * event.getDesktopHeight())) + "px");
	}

	/* (non-Javadoc)
	 * @see id.co.sigma.zk.ui.controller.base.BaseSimpleController#doAfterCompose(org.zkoss.zk.ui.Component)
	 */
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		if(buttonToolbar != null) {
			buttonToolbar.getChildren().clear();
			Button btnClose = new Button(Labels.getLabel("action.button.close"));
			btnClose.setId("btnClose");
			buttonToolbar.appendChild(btnClose);
		}
		
		showReport();
	}
	
	private void showReport() {
		try {
			URL url = new URL(reportUrl);
			
			BufferedInputStream bis = new BufferedInputStream(url.openStream());
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
			byte[] ba1 = new byte[1024];
            
		    int baLength;
            
            while ((baLength = bis.read(ba1)) != -1) {
                baos.write(ba1, 0, baLength);
            } 			    
		    
            AMedia media  = new AMedia("mypdf.pdf", "pdf", "application/pdf", baos.toByteArray());
            
            if(reportFrame != null) {
            	reportFrame.setContent(media);            	
            }
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
