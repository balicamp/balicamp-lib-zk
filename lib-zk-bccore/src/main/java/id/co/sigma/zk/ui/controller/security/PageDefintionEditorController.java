package id.co.sigma.zk.ui.controller.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Textbox;

import id.co.sigma.common.security.domain.PageDefinition;
import id.co.sigma.zk.ui.annotations.ControlDataBinder;
import id.co.sigma.zk.ui.controller.base.BaseSimpleDirectToDBEditor;

/**
 * editor page definition
 * @author <a href='mailto:gede.sutarsa@gmail.com'>Gede Sutarsa</a>
 */
public class PageDefintionEditorController extends BaseSimpleDirectToDBEditor<PageDefinition>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3648705442261546494L;
	
	
	
	
	@Wire
	@ControlDataBinder(targetField="pageUrl")
	private Textbox pageUrlAnnotated ; 
	
	
	private static final Logger logger = LoggerFactory.getLogger(PageDefintionEditorController.class); 
	
	@Override
	public void insertData() throws Exception {
		//FIXME: ini masih di hard code dulu
		logger.info("Set application id");
		getEditedData().setApplicationId(1L);
		super.insertData();
	}
	

	
	
	

}
