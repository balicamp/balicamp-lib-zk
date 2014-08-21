package id.co.sigma.zk.ui;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.web.servlet.dsp.action.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.util.Initiator;
import org.zkoss.zk.ui.util.InitiatorExt;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zul.Window;

/**
 * 
 * @author <a href='mailto:gede.sutarsa@gmail.com'>Gede Sutarsa</a>
 */
public class WindowDataBinderInit implements Initiator, InitiatorExt {

	
	
	private static final Logger log = LoggerFactory.getLogger(WindowDataBinderInit.class); 
	@Override
	public void doAfterCompose(org.zkoss.zk.ui.Page arg0, Component[] cmpnts)
			throws Exception {
		 try {
             for (Component c : cmpnts) {
                   if (c instanceof Window) {
                         AnnotateDataBinder binder = new AnnotateDataBinder(c, true);
                         c.setAttribute("binder", binder, true);
                         binder.loadAll();
                   }
             }
       } catch (WrongValueException e) {
             log.error("wrong value : " + e.getMessage() ,  e);
       } catch (Exception e) {
             throw e;
       }

		
	}

	@Override
	public boolean doCatch(Throwable exc) throws Exception {
		log.error("error : " + exc.getMessage());
		return false;
	}

	@Override
	public void doFinally() throws Exception {
		
		
	}

	@Override
	public void doInit(org.zkoss.zk.ui.Page arg0, Map<String, Object> arg1)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	  

}
