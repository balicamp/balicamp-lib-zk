package id.co.sigma.zk.ui.controller.master;

import id.co.sigma.common.data.query.SimpleSortArgument;
import id.co.sigma.common.security.domain.lov.LookupDetail;
import id.co.sigma.common.security.domain.lov.LookupHeader;
import id.co.sigma.zk.ui.annotations.ChildGridData;
import id.co.sigma.zk.ui.annotations.ControlDataBinder;
import id.co.sigma.zk.ui.annotations.JoinKey;
import id.co.sigma.zk.ui.controller.EditorManager;
import id.co.sigma.zk.ui.controller.ZKEditorState;
import id.co.sigma.zk.ui.controller.base.BaseSimpleDirectToDBEditor;
import id.co.sigma.zk.ui.data.ZKClientSideListDataEditorContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;

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
	
	@ControlDataBinder(targetField="id")
	@Wire Textbox txtIdLov ; 
	
	@ControlDataBinder(targetField="i18Key")
	@Wire
	Textbox txtI18Key;
	
	@ControlDataBinder(targetField="remark")
	@Wire Textbox txtRemark ; 
	
	@ControlDataBinder(targetField="version")
	@Wire Textbox txtVersion; 
	
	@Wire Listbox lsbLookupDetails ;  
	@Wire Button  btnAdd ; 
	@Wire Button  btnSave;
	@Wire Button  btnClose;
	
	@ChildGridData(entity = LookupDetail.class, 
			gridId="lsbLookupDetails",
			joinKeys={
				@JoinKey(parentKey="id", childKey="headerId"),
				@JoinKey(parentKey="i18Key", childKey="i18Key")
			})
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
		reloadChildGridData();
		LookupDetail d = new LookupDetail();
		d.setI18Key(txtI18Key.getValue());
		clientDataContainer.appendNewItem(d);
//		lsbLookupDetails.setModel(clientDataContainer);
		//EditorManager.getInstance().addNewData("~./zul/pages/master/LookupDetailEditor.zul", this.clientDataContainer ,  d, this, false);
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		lsbLookupDetails.setModel(clientDataContainer);
	}

	

	@Override
	public void deleteChildrenData(List<?> childrenData) throws Exception {
		for(Object del : childrenData) {
			generalPurposeService.delete(del.getClass(), ((LookupDetail)del).getId(), "id");
		}
	}


}
