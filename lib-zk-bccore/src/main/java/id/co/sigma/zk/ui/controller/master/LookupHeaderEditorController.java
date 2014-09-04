package id.co.sigma.zk.ui.controller.master;

import java.util.ArrayList;
import java.util.Map;

import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;

import id.co.sigma.common.data.query.SimpleSortArgument;
import id.co.sigma.common.security.domain.lov.LookupDetail;
import id.co.sigma.common.security.domain.lov.LookupHeader;
import id.co.sigma.zk.ui.controller.EditorManager;
import id.co.sigma.zk.ui.controller.ZKEditorState;
import id.co.sigma.zk.ui.controller.base.BaseSimpleDirectToDBEditor;
import id.co.sigma.zk.ui.data.ZKClientSideListDataEditorContainer;

/**
 * controller lookup header
 * @author <a href="mailto:gede.sutarsa@gmail.com">Gede Sutarsa</a>
 */
public class LookupHeaderEditorController extends BaseSimpleDirectToDBEditor<LookupHeader>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2049491314637159622L;
	static final org.slf4j.Logger logger = org.slf4j.LoggerFactory
			.getLogger(LookupHeaderEditorController.class.getName());
	
	public static final SimpleSortArgument[] DEFAULT_SORTS = new SimpleSortArgument[]{
		new SimpleSortArgument("i18Key", true) , 
		new SimpleSortArgument("sequence", true) ,
	}; 
	
	@Wire Textbox txtIdLov ; 
	@Wire Textbox txtRemark ; 
	@Wire Textbox txtVersion; 
	@Wire Listbox lsbLookupDetails ;  
	@Wire Button  btnAdd ; 
	@Wire Button  btnSave;
	@Wire Button  btnClose;
	
	
	private ZKClientSideListDataEditorContainer<LookupDetail> clientDataContainer ; 
	
	@Override
	protected void runAditionalTaskOnDataRevieved(LookupHeader editedData,
			ZKEditorState editorState, Map<?,?>   rawDataParameter) {
		super.runAditionalTaskOnDataRevieved(editedData, editorState, rawDataParameter);
		if (ZKEditorState.EDIT.equals(editorState)  || ZKEditorState.VIEW_READONLY.equals(editorState)) {
			clientDataContainer =  this.zkCommonService.getDataDetails(LookupDetail.class, editedData.getId(), DEFAULT_SORTS, "id.lovID") ;
		}else {
			clientDataContainer = new ZKClientSideListDataEditorContainer<LookupDetail>(); 
			clientDataContainer.initiateAndFillData(new ArrayList<LookupDetail>());
		}
		
		
	}
	
	
	@Listen(value="onClick = #btnClose")
	public void closeClick() { 
		EditorManager.getInstance().closeCurrentEditorPanel();
	};
	@Listen(value="onClick = #btnAdd")
	public void addClick() { 
		LookupDetail d = new LookupDetail(); 
		EditorManager.getInstance().addNewData("~./zul/pages/master/LookupDetailEditor.zul",this.clientDataContainer ,  d, this);
	}
	@Listen(value="onClick = #btnSave")
	public void saveClick() { 
		if ( ZKEditorState.ADD_NEW.equals(getEditorState())) {
			try {
				insertData();
			} catch (Exception e) {
				Messagebox.show("Gagal menyimpan lookup header. error :  " + e.getMessage() ); 
				return ; 
			}
			EditorManager.getInstance().closeCurrentEditorPanel();
		}else if ( ZKEditorState.EDIT.equals(getEditorState())) {
			try {
				updateData();
			} catch (Exception e) {
				Messagebox.show("Gagal update lookup header. error :  " + e.getMessage() ); 
				return ; 
			}
		}
		
	};
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		try {
			lsbLookupDetails.setModel(clientDataContainer);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	
}
