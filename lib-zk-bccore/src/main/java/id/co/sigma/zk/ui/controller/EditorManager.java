package id.co.sigma.zk.ui.controller;

import id.co.sigma.common.data.SingleKeyEntityData;
import id.co.sigma.common.data.query.SimpleQueryFilter;
import id.co.sigma.common.data.query.SimpleSortArgument;
import id.co.sigma.zk.ZKCoreLibConstant;
import id.co.sigma.zk.spring.security.SecurityUtil;
import id.co.sigma.zk.ui.MultipleValueLookupReceiver;
import id.co.sigma.zk.ui.SingleValueLookupReciever;
import id.co.sigma.zk.ui.controller.base.BaseSimpleController;
import id.co.sigma.zk.ui.controller.base.IEditorPanel;
import id.co.sigma.zk.ui.custom.component.ListWindow;
import id.co.sigma.zk.ui.data.ZKClientSideListDataEditorContainer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Include;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Window;

/**
 * editor manager
 * @author <a href='mailto:gede.sutarsa@gmail.com'>Gede Sutarsa</a>
 */
public final class EditorManager {
	
	private Window editorContainerWindow ; 
	
	private Include includePanel ; 
	
	private Long createdTime = Calendar.getInstance().getTimeInMillis();
	
	private List<Window> winModals = new ArrayList<Window>();
	
	private EditorManager() {}
	
	
	private static Map<String, EditorManager> instances ; 
	
	public static EditorManager getInstance() {
		String sessionId = SecurityUtil.getAuthDetails().getSessionId();
		if(instances == null) {
			instances = new HashMap<String, EditorManager>();
		} else {
			checkExpiredInstances();
		}
		EditorManager instance = instances.get(sessionId);
		if ( instance== null) {
			instance = new EditorManager();			
			instances.put(sessionId, instance);			
		}
		return instance;
	}
	
	private static void checkExpiredInstances() {
		if(instances != null && instances.isEmpty()) {
			Iterator<EditorManager> iters = instances.values().iterator();
			for(;iters.hasNext();) {
				EditorManager manager = iters.next();
				Long liveTime = Calendar.getInstance().getTimeInMillis() - manager.createdTime;
				
				if(liveTime > (24*60*60)) {
					instances.remove(manager);
					manager = null;
				}
			}
		}
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
				if ( cmpn!= null) {
					cmpn.detach();
					editorContainerWindow.removeChild(cmpn);
				}
			}
		}
		editorContainerWindow.setVisible(false); 
		includePanel.setSclass(null);
		includePanel.setVisible(true);
		includePanel.setSrc(zulPath);
		Executions.getCurrent().getSession().setAttribute(ZKCoreLibConstant.PREV_PAGE, includePanel.getSrc());
		
	}
	
	/**
	 * ini untuk menampilkan halaman view data, data yang ditampilkan tidak bisa diubah (readonly)
	 * @param zulPath zul path dari data
	 * @param viewedData data yang ditampilkan
	 * @param caller pemanggil
	 * @param isPopup
	 */
	public<DATA extends SingleKeyEntityData<?>> void viewData ( String zulPath, DATA viewedData, BaseSimpleController caller, boolean... isPopup ) {

	    Map<String, Object> parameter = new HashMap<String, Object>() ; 
	    parameter.put(ZKCoreLibConstant.EDITED_DATA_ATTRIBUTE_KEY, viewedData); 
	    parameter.put(ZKCoreLibConstant.EDITOR_STATE_ATTRIBUTE_KEY, ZKEditorState.VIEW_READONLY); 
	    parameter.put(ZKCoreLibConstant.EDITOR_CALLER_COMPONENT, caller); 

	    if(!isShowModal(isPopup)) {
		showHideLatestComponent(false);
		includePanel.setVisible(false);
		editorContainerWindow.setVisible(true); 
		Executions.createComponents(zulPath, editorContainerWindow, parameter);
	    } else {
		Window wModal = (Window)Executions.createComponents(zulPath, null, parameter);
		wModal.setClosable(false);			
		wModal.doModal();
		winModals.add(0, wModal);
	    }
	}
	
	
	/**
	 * ini untuk menampilkan editor dengan tipe data tidak di simpan ke database langsung
	 * @param zulPath zul path dari data
	 * @param dataContainer container dari data
	 * @param editedData data yang di edit
	 * @param caller pemanggil
	 * @param isPopup TODO
	 */
	public<DATA extends SingleKeyEntityData<?>> void editData ( String zulPath ,ZKClientSideListDataEditorContainer<DATA> dataContainer ,DATA editedData , BaseSimpleController caller, boolean... isPopup ) {
		
		Map<String, Object> parameter = new HashMap<String, Object>() ; 
		parameter.put(ZKCoreLibConstant.EDITED_DATA_ATTRIBUTE_KEY, editedData); 
		parameter.put(ZKCoreLibConstant.EDITOR_STATE_ATTRIBUTE_KEY, ZKEditorState.EDIT); 
		parameter.put(ZKCoreLibConstant.EDITOR_CALLER_COMPONENT, caller); 
		parameter.put(ZKCoreLibConstant.EDITED_DATA_CLIENT_CONTAINER_KEY, dataContainer);
		
		if(!isShowModal(isPopup)) {
			showHideLatestComponent(false);
			includePanel.setVisible(false);
			editorContainerWindow.setVisible(true); 
			Executions.createComponents(zulPath, editorContainerWindow, parameter);
		} else {
			Window wModal = (Window)Executions.createComponents(zulPath, null, parameter);
//			Toolbar tb = (Toolbar) wModal.getFellowIfAny("buttonToolbar");
//			if(tb != null) {
//				tb.setAlign("center");
//			}
			wModal.setClosable(false);			
			wModal.doModal();
			winModals.add(0, wModal);
		}
	}
	
	/**
	 * menampilkan editor
	 * @param zulPath url dari zul untuk edit
	 * @param editedData data yang di edit
	 * @param caller pemanggil data
	 */
	public<DATA extends SingleKeyEntityData<?>> void editData ( String zulPath ,DATA editedData , BaseSimpleController caller, boolean... isPopup) {
		Map<String, Object> parameter = new HashMap<String, Object>() ; 
		parameter.put(ZKCoreLibConstant.EDITED_DATA_ATTRIBUTE_KEY, editedData); 
		parameter.put(ZKCoreLibConstant.EDITOR_STATE_ATTRIBUTE_KEY, ZKEditorState.EDIT); 
		parameter.put(ZKCoreLibConstant.EDITOR_CALLER_COMPONENT, caller); 
		
		if(!isShowModal(isPopup)) {		
			showHideLatestComponent(false);
			includePanel.setVisible(false);
			editorContainerWindow.setVisible(true); 
			Executions.createComponents(zulPath, editorContainerWindow, parameter);
		} else {
			Window wModal = (Window)Executions.createComponents(zulPath, null, parameter);
//			Toolbar tb = (Toolbar) wModal.getFellowIfAny("buttonToolbar");
//			if(tb != null) {
//				tb.setAlign("center");
//			}
			wModal.setClosable(false);			
			wModal.doModal();
			winModals.add(0, wModal);
		}
	}
	
	
	
	public<DATA > void addNewData ( String zulPath, ZKClientSideListDataEditorContainer<DATA> container ,DATA appendedData , BaseSimpleController caller, boolean... isPopup ) {
		Map<String, Object> parameter = new HashMap<String, Object>() ; 
		parameter.put(ZKCoreLibConstant.EDITED_DATA_ATTRIBUTE_KEY, appendedData); 
		parameter.put(ZKCoreLibConstant.EDITOR_STATE_ATTRIBUTE_KEY, ZKEditorState.ADD_NEW);
		parameter.put(ZKCoreLibConstant.EDITED_DATA_CLIENT_CONTAINER_KEY, container);
		parameter.put(ZKCoreLibConstant.EDITOR_CALLER_COMPONENT, caller); 
		
		if(!isShowModal(isPopup)) {
			showHideLatestComponent(false);
			includePanel.setVisible(false);
			editorContainerWindow.setVisible(true); 
			Executions.createComponents(zulPath, editorContainerWindow, parameter);
		} else {
			Window wModal = (Window)Executions.createComponents(zulPath, null, parameter);
//			Toolbar tb = (Toolbar) wModal.getFellowIfAny("buttonToolbar");
//			if(tb != null) {
//				tb.setAlign("center");
//			}
			wModal.setClosable(false);			
			wModal.doModal();
			winModals.add(0, wModal);
		}
	}
	
	
	/**
	 * menampilkan editor data
	 * @param zulPath path dari zul
	 * @param appendedData data yang di add
	 * @param caller caller dari editor. biasanay this 
	 */
	public<DATA extends SingleKeyEntityData<?>> void addNewData ( String zulPath ,DATA appendedData , BaseSimpleController caller, boolean... isPopup ) {
		Map<String, Object> parameter = new HashMap<String, Object>() ; 
		parameter.put(ZKCoreLibConstant.EDITED_DATA_ATTRIBUTE_KEY, appendedData); 
		parameter.put(ZKCoreLibConstant.EDITOR_STATE_ATTRIBUTE_KEY, ZKEditorState.ADD_NEW); 
		parameter.put(ZKCoreLibConstant.EDITOR_CALLER_COMPONENT, caller);
		
		if(!isShowModal(isPopup)) {		
			showHideLatestComponent(false);
			includePanel.setVisible(false);
			editorContainerWindow.setVisible(true); 
			Executions.createComponents(zulPath, editorContainerWindow, parameter);
		} else {
			Window wModal = (Window)Executions.createComponents(zulPath, null, parameter);
//			Toolbar tb = (Toolbar) wModal.getFellowIfAny("buttonToolbar");
//			if(tb != null) {
//				tb.setAlign("center");
//			}
			wModal.setClosable(false);			
			wModal.doModal();
			winModals.add(0, wModal);
		}
	}
	
	
	/**
	 * menampilkan editor data dengan data tambahan, keperluan tree
	 * @param zulPath
	 * @param appendedData
	 * @param additionalData
	 * @param caller
	 */
	public<DATA extends SingleKeyEntityData<?>> void addNewData ( String zulPath ,DATA appendedData , DATA additionalData, BaseSimpleController caller, boolean... isPopup ) {
		Map<String, Object> parameter = new HashMap<String, Object>() ; 
		parameter.put(ZKCoreLibConstant.EDITED_DATA_ATTRIBUTE_KEY, appendedData); 
		parameter.put(ZKCoreLibConstant.ADDITIONAL_DATA_ATTRIBUTE_KEY, additionalData);
		parameter.put(ZKCoreLibConstant.EDITOR_STATE_ATTRIBUTE_KEY, ZKEditorState.ADD_NEW); 
		parameter.put(ZKCoreLibConstant.EDITOR_CALLER_COMPONENT, caller);
		
		if(!isShowModal(isPopup)) {
			showHideLatestComponent(false);
			includePanel.setVisible(false);
			editorContainerWindow.setVisible(true); 
			Executions.createComponents(zulPath, editorContainerWindow, parameter);
		} else {
			Window wModal = (Window)Executions.createComponents(zulPath, null, parameter);
//			Toolbar tb = (Toolbar) wModal.getFellowIfAny("buttonToolbar");
//			if(tb != null) {
//				tb.setAlign("center");
//			}
			wModal.setClosable(false);			
			wModal.doModal();
			winModals.add(0, wModal);
		}
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
	 * menapilkan dialog dengan single result
	 */
	public <DATA> void showSingleResultLookup (String zulPath , SingleValueLookupReciever<DATA> dataSelectionHandler ) {
		Map<String, Object> parameter = new HashMap<String, Object>() ; 
		parameter.put(ZKCoreLibConstant.AFTER_SELECTION_HANDLER, dataSelectionHandler); 
		final Window w = (Window) Executions.createComponents(zulPath, null, parameter);
		w.setClosable(true);
		w.setMaximizable(true);
		w.doModal(); 
	}
	
	/**
	 * menampilkan dialog lookup dengan single result, custom filter, dan custom sort 
	 */
	public <DATA> void showSingleResultLookup (String zulPath , SingleValueLookupReciever<DATA> dataSelectionHandler, SimpleQueryFilter[] filters, SimpleSortArgument[] sorts) {
		Map<String, Object> parameter = new HashMap<String, Object>() ; 
		parameter.put(ZKCoreLibConstant.AFTER_SELECTION_HANDLER, dataSelectionHandler);
		parameter.put(ZKCoreLibConstant.LOOKUP_LIST_FILTERS, filters);
		parameter.put(ZKCoreLibConstant.LOOKUP_LIST_SORTS, sorts);
		final Window w = (Window) Executions.createComponents(zulPath, null, parameter);
		w.setClosable(true);
		w.setMaximizable(true);
		w.doModal(); 
	}
	
	/**
	 * menampilkan dialog dengan multiple result
	 */
	public <DATA> void showMultipleResultLookup (String zulPath , MultipleValueLookupReceiver<DATA> dataSelectionHandler ) {
		Map<String, Object> parameter = new HashMap<String, Object>() ; 
		parameter.put(ZKCoreLibConstant.AFTER_SELECTION_HANDLER, dataSelectionHandler); 
		final Window w = (Window) Executions.createComponents(zulPath, null, parameter);
		w.setClosable(true);
		w.setMaximizable(true);
		w.doModal(); 
	}
	
	/**
	 * tutup editor terakhir
	 */
	public void closeCurrentEditorPanel () {
		
		if(winModals.isEmpty()) {
			
			List<Component> components =  editorContainerWindow.getChildren();
			if (! components.isEmpty()){
				editorContainerWindow.removeChild(components.get(components.size()-1));
				showHideLatestComponent(true);
			}
			
			
			
			if ( components.isEmpty()){
				editorContainerWindow.setVisible(false) ; 
				includePanel.setVisible(true); 
				Events.postEvent(new ReloadEvent(includePanel.getFirstChild(), reloadData()));
			}
			
		} else {
			Window w = winModals.get(0);
			w.detach();
			winModals.clear();
			w = null;
			if(includePanel.isVisible()) {
				Events.postEvent(new ReloadEvent(includePanel.getFirstChild(), reloadData()));
			}
		}
	}
	
	/**
	 * tutup editor terakhir tanpa reload data di list, ini untuk keperluan
	 * apabila panel yang dibuka lebih dari 2 level dan ketika menuju panel berikutnya tidak diharuskan mereload
	 * list yang ada di halaman searching, cukup diremove panel terakhir dari container
	 */
	public void removeCurrentEditorPanelFromContainer(){
	    	List<Component> components =  editorContainerWindow.getChildren();
		if (! components.isEmpty()){
			editorContainerWindow.removeChild(components.get(components.size()-1));
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
	
	private Long reloadData() {
		Component listWindow = includePanel.getFirstChild();
		Object composer = null;
		if(listWindow instanceof ListWindow) {
			composer = ((ListWindow)listWindow).getAttribute(listWindow.getId() + "$composer");
			Timer timer = (Timer)((ListWindow)listWindow).getFellowIfAny("listTimer");
			if(timer != null) {
				timer.start();
			}
		} else if(listWindow instanceof Window) {
			composer = ((Window)listWindow).getAttribute(listWindow.getId() + "$composer");
			if(composer instanceof IReloadablePanel) {
				((IReloadablePanel)composer).reload();
			}
			Timer timer = (Timer)((Window)listWindow).getFellowIfAny("listTimer");
			if(timer != null) {
				timer.start();
			}
		}
		return System.currentTimeMillis();
	}

	@SuppressWarnings("serial")
	public class ReloadEvent extends Event {
		public ReloadEvent(Component target, Object data) {
			super("onReload", target, data);
		}
		
	}
	
	private boolean isShowModal(boolean... isPopup) {
		boolean showModal = false;
		if(isPopup != null && isPopup.length > 0) {
			showModal = isPopup[0];
		}
		return showModal;
	}
}
