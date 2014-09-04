package id.co.sigma.zk.ui.controller;

import id.co.sigma.common.data.SingleKeyEntityData;
import id.co.sigma.zk.ui.ZKCoreLibConstant;
import id.co.sigma.zk.ui.controller.base.BaseSimpleController;
import id.co.sigma.zk.ui.controller.base.IEditorPanel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Include;
import org.zkoss.zul.Window;

/**
 * editor manager
 * @author <a href='mailto:gede.sutarsa@gmail.com'>Gede Sutarsa</a>
 */
public final class EditorManager {
	
	private Window editorContainerWindow ; 
	
	private Include includePanel ; 
	
	private EditorManager() {}
	
	
	private static EditorManager instance ; 
	
	public static EditorManager getInstance() {
		if ( instance== null)
			instance = new EditorManager(); 
		return instance;
	}
	
	
	
	private void showHideLatestComponent (boolean show ) {
		List<Component> components =  editorContainerWindow.getChildren();
		if (! components.isEmpty()){
			components.get(components.size()-1).setVisible(show) ; 
		}
	}
	
	
	
	
	/**
	 * navigate panel. ini di desain untuk zul
	 * @param zulPath zul path
	 */
	public void navigate ( String zulPath ) {
		List<Component> components =  editorContainerWindow.getChildren();
		if (! components.isEmpty()){
			for  (int i = components.size()-1 ; i>= 0 ; i++) {
				Component cmpn = editorContainerWindow.getLastChild() ;
				if ( cmpn!= null)
					editorContainerWindow.removeChild(cmpn); 
			}
		}
		editorContainerWindow.setVisible(false); 
		includePanel.setVisible(true);
		includePanel.setSrc(zulPath);
		Executions.getCurrent().getSession().setAttribute(ZKCoreLibConstant.PREV_PAGE, includePanel.getSrc());
		
	}
	
	
	
	
	/**
	 * menampilkan editor
	 * @param zulPath url dari zul untuk edit
	 * @param editedData data yang di edit
	 * @param caller pemanggil data
	 */
	public<DATA extends SingleKeyEntityData<?>> void editData ( String zulPath ,DATA editedData , BaseSimpleController caller ) {
		Map<String, Object> parameter = new HashMap<String, Object>() ; 
		parameter.put(ZKCoreLibConstant.EDITED_DATA_ATTRIBUTE_KEY, editedData); 
		parameter.put(ZKCoreLibConstant.EDITOR_STATE_ATTRIBUTE_KEY, ZKEditorState.EDIT); 
		parameter.put(ZKCoreLibConstant.EDITOR_CALLER_COMPONENT, caller); 
		showHideLatestComponent(false);
		includePanel.setVisible(false);
		editorContainerWindow.setVisible(true); 
		Executions.createComponents(zulPath, editorContainerWindow, parameter); 
	}
	
	
	public<DATA extends SingleKeyEntityData<?>> void addNewData ( String zulPath ,DATA appendedData , BaseSimpleController caller ) {
		Map<String, Object> parameter = new HashMap<String, Object>() ; 
		parameter.put(ZKCoreLibConstant.EDITED_DATA_ATTRIBUTE_KEY, appendedData); 
		parameter.put(ZKCoreLibConstant.EDITOR_STATE_ATTRIBUTE_KEY, ZKEditorState.ADD_NEW); 
		parameter.put(ZKCoreLibConstant.EDITOR_CALLER_COMPONENT, caller);
		showHideLatestComponent(false);
		includePanel.setVisible(false);
		editorContainerWindow.setVisible(true); 
		Executions.createComponents(zulPath, editorContainerWindow, parameter); 
	}
	
	
	/**
	 * worker untuk menaruh editor panel
	 * @param editor url yang perlu di taruh ke dalam editor
	 */
	public void showEditor ( IEditorPanel editor )  {
		showHideLatestComponent(false);
		this.editorContainerWindow.setVisible(true); 
		this.includePanel.setVisible(false); 
		this.editorContainerWindow.appendChild((Component) editor); 
	}
	
	/**
	 * tutup editor terakhir
	 */
	public void closeCurrentEditorPanel () {
		List<Component> components =  editorContainerWindow.getChildren();
		if (! components.isEmpty()){
			editorContainerWindow.removeChild(components.get(components.size()-1));
			showHideLatestComponent(true);
		}
		
		
		
		if ( components.isEmpty()){
			editorContainerWindow.setVisible(false) ; 
			includePanel.setVisible(true); 
			
		}
	}
	
	
	/**
	 * @param editorContainer editor container
	 * @param includePanel included panel
	 * configure manager
	 * 
	 */
	public void configureManager (Window editorContainer , Include includePanel) {
		this.editorContainerWindow = editorContainer ; 
		this.includePanel = includePanel ; 
	}

}
