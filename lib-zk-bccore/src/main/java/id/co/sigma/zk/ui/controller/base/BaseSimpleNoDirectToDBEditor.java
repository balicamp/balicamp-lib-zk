package id.co.sigma.zk.ui.controller.base;

import id.co.sigma.zk.ZKCoreLibConstant;
import id.co.sigma.zk.ui.controller.ZKEditorState;
import id.co.sigma.zk.ui.data.ZKClientSideListDataEditorContainer;

import java.util.Map;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.annotation.Listen;

/**
 * editor ini menyimpan ke data container, bukan ke db
 * @author <a href="mailto:gede.sutarsa@gmail.com">Gede Sutarsa</a>
 */
public abstract class BaseSimpleNoDirectToDBEditor<POJO> extends BaseSimpleEditor<POJO>{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6179032555306541240L;

	static final org.slf4j.Logger logger = org.slf4j.LoggerFactory
			.getLogger(BaseSimpleNoDirectToDBEditor.class.getName());
	
	protected ZKClientSideListDataEditorContainer<POJO> dataContainer ; 
	
	
	@SuppressWarnings("unchecked")
	@Override
	protected void runAditionalTaskOnDataRevieved(POJO editedData,
			ZKEditorState editorState, Map<?, ?> rawDataParameter) {
		
		super.runAditionalTaskOnDataRevieved(editedData, editorState, rawDataParameter);
		dataContainer =(ZKClientSideListDataEditorContainer<POJO>) rawDataParameter.get(ZKCoreLibConstant.EDITED_DATA_CLIENT_CONTAINER_KEY);
		
	}
	
	@Override
	protected void updateData(POJO data) throws Exception {
		dataContainer.modifyItem(data);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void insertData(Object... data) throws Exception {
		dataContainer.appendNewItem((POJO)data[0]);
		
	}

	@Override
	public void deleteData(POJO data) throws Exception {
		dataContainer.eraseData(data);
	}
	
	@Listen("onClick = #btnSave")
	public void saveClick(final Event evt) {

		parseEditedData(evt.getTarget());
		try {
			bindValueFromControl(getEditedData());
		} catch (Exception e) {
			showErrorMessage(getEditorState(), e.getMessage());
			return  ; 
		}
		
		try {
		    validateData();

			String confirmMsg = (String)getSelf().getAttribute("confirmationMsg");
			showSaveConfirmationMessage(evt, getEditorState(), confirmMsg);
		} catch (Exception e) {
		    logger.error(e.getMessage(), e);
		    showInvalidDataMessage(getEditorState(), e.getMessage());
		}
		
	}
	
	@Override
	protected void saveData(Event event) {
		try {
			if ( ZKEditorState.ADD_NEW.equals(getEditorState())) {
				insertData();
			} else if(ZKEditorState.EDIT.equals(getEditorState())) {
				updateData();
			}
		} catch (Exception e) {}
		closeCurrentEditorPanel();
	}

	@Listen("onClick = #btnCancel")
	public void cancelClick() {
		String cancelMsg = (String)getSelf().getAttribute("cancellationMsg");
		showCancelConfirmationMessage(cancelMsg);
	}	
	
    protected void validateData() throws Exception {

    }
	
}
