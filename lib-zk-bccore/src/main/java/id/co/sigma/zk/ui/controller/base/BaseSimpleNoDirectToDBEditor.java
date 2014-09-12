package id.co.sigma.zk.ui.controller.base;

import java.util.Map;

import id.co.sigma.zk.ZKCoreLibConstant;
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
	
	@Override
	protected void insertData(POJO data) throws Exception {
		dataContainer.appendNewItem(data);
		
	}
	
}
