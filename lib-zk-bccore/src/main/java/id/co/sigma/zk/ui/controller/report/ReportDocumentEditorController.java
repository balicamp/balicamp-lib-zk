package id.co.sigma.zk.ui.controller.report;

import id.co.sigma.common.report.domain.RptDocParam;
import id.co.sigma.common.report.domain.RptDocument;
import id.co.sigma.zk.ui.annotations.ChildGridData;
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

public class ReportDocumentEditorController extends BaseSimpleDirectToDBEditor<RptDocument> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Wire
	private Button btnAdd;

	@Wire
	private Listbox listRptParams;

	@ChildGridData(entity = RptDocParam.class, 
			gridId="listRptParams",
			joinKeys={
				@JoinKey(parentKey="id", childKey="rptDocId"),
			})
	private ZKClientSideListDataEditorContainer<RptDocParam> clientDataContainer ; 
	
	@Override
	protected void runAditionalTaskOnDataRevieved(RptDocument editedData,
			ZKEditorState editorState, Map<?,?>   rawDataParameter) {
		super.runAditionalTaskOnDataRevieved(editedData, editorState, rawDataParameter);
		if (ZKEditorState.EDIT.equals(editorState)  || ZKEditorState.VIEW_READONLY.equals(editorState)) {
			clientDataContainer =  this.zkCommonService.getDataDetails(RptDocParam.class, editedData.getId(), null, "rptDocId") ;
		}else {
			clientDataContainer = new ZKClientSideListDataEditorContainer<RptDocParam>(); 
			clientDataContainer.initiateAndFillData(new ArrayList<RptDocParam>());
		}
	}
	
	@Listen(value="onClick = #btnAdd")
	public void addClick() { 
		reloadChildGridData();
		RptDocParam d = new RptDocParam();
		EditorManager.getInstance().addNewData("~./zul/pages/report/ReportDocParamEditor.zul", this.clientDataContainer ,  d, this, true);
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		listRptParams.setModel(clientDataContainer);
	}

	@Override
	public void deleteChildrenData(List<?> childrenData) throws Exception {
		for(Object del : childrenData) {
			generalPurposeService.delete(del.getClass(), ((RptDocParam)del).getId(), "id");
		}
	}
	
}
