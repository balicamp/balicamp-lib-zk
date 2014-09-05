package id.co.sigma.zk.ui.controller.security;

import id.co.sigma.common.security.domain.PageDefinition;
import id.co.sigma.zk.ui.controller.base.BaseSimpleDirectToDBEditor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * editor page definition
 * @author <a href='mailto:gede.sutarsa@gmail.com'>Gede Sutarsa</a>
 */
public class PageDefintionEditorController extends BaseSimpleDirectToDBEditor<PageDefinition>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3648705442261546494L;
	
	
	private static final Logger logger = LoggerFactory.getLogger(PageDefintionEditorController.class); 

	
//	@Wire
//	private Button btnSimpan;
//	@Wire
//	private Button btnBatal;
//	
//	
//	@Wire Textbox txtPageCode ; 
//	@Wire Textbox txtPageUrl; 
//	@Wire Textbox txtPageRemark;
//	@Wire Textbox txtAdditionalData ; 
//
//
//	
//	
//	@Listen(value="onClick = #btnSimpan")
//	public void simpanClick() {
//		if ( ZKEditorState.ADD_NEW.equals(getEditorState())) {
//			try {
//				insertData();
//			} catch (Exception e) {
//				logger.error( "" + e.getMessage() , e);
//				 Messagebox.show("Gagal input data page. error : " + e.getMessage(), "Gagal Tambah Data", Messagebox.OK, Messagebox.ERROR);
//			}
//			
//		}else {
//			try {
//				updateData();
//			} catch (Exception e) {
//				logger.error("gagal update file. error : " + e.getMessage() , e);
//				 Messagebox.show("Gagal input data page. error : " + e.getMessage(), "Gagal Simpan Data", Messagebox.OK, Messagebox.ERROR);
//			}
//			
//		}
//		
//	}
//	
//	@Listen(value="onClick = #btnBatal")
//	public void batalClick() {
//		EditorManager.getInstance().closeCurrentEditorPanel();
//	}
	
	
	@Override
	public void insertData() throws Exception {
		//FIXME: ini masih di hard code dulu
		logger.info("Set application id");
		getEditedData().setApplicationId(1L);
		super.insertData();
	}
	

	
	
	

}
