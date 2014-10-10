package id.co.sigma.zk.ui.controller.base;

import java.util.Map;

import org.zkoss.util.resource.Labels;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.annotation.Listen;

import id.co.sigma.zk.ZKCoreLibConstant;
import id.co.sigma.zk.ui.controller.EditorManager;
import id.co.sigma.zk.ui.controller.ZKEditorState;
import id.co.sigma.zk.ui.data.ZKClientSideListDataEditorContainer;

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
			if ( ZKEditorState.ADD_NEW.equals(getEditorState())) {
				Messagebox.show(Labels.getLabel("msg.save.edit.fail"), 
						Labels.getLabel("title.msgbox.error"),
						new Messagebox.Button[]{Messagebox.Button.OK},
						new String[]{Labels.getLabel("action.button.ok")},
						Messagebox.ERROR,
						Messagebox.Button.OK, null);
			} else {
				Messagebox.show(Labels.getLabel("msg.save.edit.fail"), 
						Labels.getLabel("title.msgbox.error"),
						new Messagebox.Button[]{Messagebox.Button.OK},
						new String[]{Labels.getLabel("action.button.ok")},
						Messagebox.ERROR,
						Messagebox.Button.OK, null);
			}
			return  ; 
		}
		
		
		String confirmMsg = (String)getSelf().getAttribute("confirmationMsg");
		if(confirmMsg != null && confirmMsg.trim().length() > 0) {
			
			Messagebox.show(confirmMsg, "Prompt", 
					Messagebox.YES|Messagebox.NO, 
					Messagebox.QUESTION, 
			new EventListener<Event>() {
				
				@Override
				public void onEvent(Event event) throws Exception {
					switch(((Integer)event.getData()).intValue()) {
					case Messagebox.YES:
						saveData(evt);
						break;
					case Messagebox.NO:
						break;
					}
				}
			});				
		} else saveData(evt); 
	}
	
	@Listen("onClick = #btnCancel")
	public void cancelClick() {
		String cancelMsg = (String)getSelf().getAttribute("cancellationMsg");
		if(cancelMsg != null && cancelMsg.trim().length() > 0) {
			
			Messagebox.show(cancelMsg, "Prompt", 
					Messagebox.YES|Messagebox.NO, 
					Messagebox.QUESTION, 
			new EventListener<Event>() {
				
				@Override
				public void onEvent(Event event) throws Exception {
					switch(((Integer)event.getData()).intValue()) {
					case Messagebox.YES:
						EditorManager.getInstance().closeCurrentEditorPanel();
						break;
					case Messagebox.NO:
						break;
					}
				}
			});				
		} else EditorManager.getInstance().closeCurrentEditorPanel();
	}

	private final void saveData(final Event event) {
		try {
			if ( ZKEditorState.ADD_NEW.equals(getEditorState())) {
				insertData();
			} else if(ZKEditorState.EDIT.equals(getEditorState())) {
				updateData();
			}
		} catch (Exception e) {
		}
	}	
}
