package id.co.sigma.zk.ui.controller.base;



import id.co.sigma.common.server.util.ExtendedBeanUtils;
import id.co.sigma.zk.ZKCoreLibConstant;
import id.co.sigma.zk.ui.annotations.ChildGridData;
import id.co.sigma.zk.ui.annotations.HeaderBinder;
import id.co.sigma.zk.ui.annotations.JoinKey;
import id.co.sigma.zk.ui.controller.EditorManager;
import id.co.sigma.zk.ui.controller.IReloadablePanel;
import id.co.sigma.zk.ui.controller.ZKEditorState;
import id.co.sigma.zk.ui.data.ZKClientSideListDataEditorContainer;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zul.Column;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timebox;
import org.zkoss.zul.impl.InputElement;



/**
 * controller sederhana
 * @author <a href='mailto:gede.sutarsa@gmail.com'>Gede Sutarsa</a>
 */
public abstract class BaseSimpleEditor<POJO > extends BaseSimpleController implements IEditorPanel {
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3872226585911174479L;
	
	
	
	/**
	 * object yang di edit
	 */
	protected POJO editedData ; 
	
	
	/**
	 * object tambahan
	 * ini keperluan ketika add tree
	 */
	protected POJO additionalData;
	
	
	/**
	 * state editor
	 */
	private ZKEditorState editorState ; 
	
	/**
	 * reference ke pemanggil dari component
	 * 
	 */
	private BaseSimpleController editorCallerReference ; 
	
	/**
	 * worker untuk save data
	 */
	protected abstract void updateData (POJO data ) throws Exception ; 
	
	/**
	 * insert data ke dalam database. 
	 * override ini kalau anda memerlukan code tersendiri untuk ini
	 */
	protected abstract void insertData (POJO data ) throws Exception ;
	
	public void deleteData(POJO data) throws Exception {
		throw new Exception("Method not supported.");
	}
	
	/**
	 * delete child/detail data
	 * @throws Exception
	 */
	public void deleteChildrenData(List<?> childrenData) throws Exception {
		throw new Exception("Method not supported");
	}

	/**
	 * insert child/detail data
	 * @throws Exception
	 */
	public void insertChildrenData(List<?> childrenData) throws Exception {
		throw new Exception("Method not supported");
	}

	/**
	 * update child/detail data
	 * @throws Exception
	 */
	public void updateChildrenData(List<?> childrenData) throws Exception {
		throw new Exception("Method not supported");
	}
	
	/**
	 * get child/detail container
	 * @param index
	 * @return
	 */
	public ZKClientSideListDataEditorContainer<?> getChildrenContainer(int index) {
		throw new RuntimeException("Method not supported");
	}
	
	/**
	 * delete child data, jika enity ini adalah merupakan master-detail
	 * @param clazz
	 * @param parentId
	 * @param parentKeyName
	 * @throws Exception
	 */
	protected void deleteChildrenData(Class<?> clazz, Object[] parentId, String[] parentKeyName) throws Exception {
		Map<String, Object> parentIdValuePair = new HashMap<String, Object>();
		
		deleteChildrenData(clazz, parentIdValuePair);
	}
	
	/**
	 * delete child data menggunakan parent id value pair (key=value)
	 * @param clazz
	 * @param parentIdValuePair
	 * @throws Exception
	 */
	public void deleteChildrenData(Class<?> clazz, Map<String, Object> parentIdValuePair) throws Exception {
		throw new Exception("Not implemented yet.");
	}
	
	
	/**
	 * worker untuk save data
	 */
	@SuppressWarnings("unchecked")
	public   void updateData (  ) throws Exception {
		
		updateData(getEditedData());
		
		//insert child data (master-detail)
		Object[] parentKeyVals = new Object[]{};
		String[] foreingKeys = new String[]{};
		List<ZKClientSideListDataEditorContainer<?>> children = parseChildGridData(foreingKeys, parentKeyVals);
		if((children != null) && (children.size() > 0)) {
			
			for(ZKClientSideListDataEditorContainer<?> child : children) {
				
				if(child != null) {
					
					for(Object data: child.getNewlyAppendedData()) {
						insertData((POJO)data);
					}

					for(Object data: child.getEditedData()) {
						updateData((POJO)data);
					}

					deleteChildrenData(child.getErasedData());
				}
				
			}
		}
		
		if ( this.editorCallerReference != null && this.editorCallerReference instanceof IReloadablePanel) {
			((IReloadablePanel)editorCallerReference).reload();
		}
		EditorManager.getInstance().closeCurrentEditorPanel();
	}
	
	/**
	 * insert data yang sedang di edit
	 */
	@SuppressWarnings("unchecked")
	public void insertData ()  throws Exception {
		insertData(getEditedData());
		
		Object[] parentKeyVals = new Object[]{};
		String[] foreingKeys = new String[]{};

		//insert child data (master-detail)
		List<ZKClientSideListDataEditorContainer<?>> children = parseChildGridData(foreingKeys, parentKeyVals);
		
		if((children != null) && (children.size() > 0)) {
			
			for(ZKClientSideListDataEditorContainer<?> child : children) {
				
				if(child != null) {
					
					for(Object data: child.getNewlyAppendedData()) {
						
						insertData((POJO)data);
						
					}
					
				}
				
			}
			
		}
		
		if ( this.editorCallerReference != null && this.editorCallerReference instanceof IReloadablePanel) {
			((IReloadablePanel)editorCallerReference).reload();
		}
		EditorManager.getInstance().closeCurrentEditorPanel();
	}
	
	/**
	 * object yang di edit
	 */
	public void setEditedData(POJO editedData) {
		this.editedData = editedData;
	}
	/**
	 * object yang di edit
	 */
	public POJO getEditedData() {
		return editedData;
	}
	
	
	/**
	 * object tambahan, keperluan di tree
	 */
	public POJO getAdditionalData() {
		return additionalData;
	}


	/**
	 * object tambahan, keperluan di tree
	 */
	public void setAdditionalData(POJO additionalData) {
		this.additionalData = additionalData;
	}


	/**
	 * parse data dari client	 
	 * @param comp
	 */
	protected void parseEditedData(Component comp) { 
		IdSpace idSpace = comp.getSpaceOwner();
		Collection<Component> fellows = idSpace.getFellows();
		for(Component fComp : fellows) {
			if(fComp instanceof InputElement) {
				String fId = fComp.getId();
				if(fId.contains(".")) {
					String[] fields = fId.split("\\.");
					Object currObj = editedData;
					try {
						for(int i = 0; i < fields.length - 1; i++) {
							currObj = PropertyUtils.getProperty(currObj, fields[i]);
						}
						if(currObj != null) {
							setProperty(fComp, currObj, fields[fields.length - 1]);
						}
					} catch (Exception e) {
						setProperty(fComp, editedData, fId);
					}
				} else {
					setProperty(fComp, editedData, fId);
				}
			}
		}
	}
	
	/**
	 * parse child grid data
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<ZKClientSideListDataEditorContainer<?>> parseChildGridData(String[] foreingKeys, Object[] parentKeyVals) {
		Field[] fields = getClass().getDeclaredFields();
		List<ZKClientSideListDataEditorContainer<?>> lists = new ArrayList<ZKClientSideListDataEditorContainer<?>>();
		ExtendedBeanUtils beanUtils = ExtendedBeanUtils.getInstance();
		int childIndex = 0;
		for(Field f: fields) {
			if(f.isAnnotationPresent(ChildGridData.class)) {
				ChildGridData ann = f.getAnnotation(ChildGridData.class);
				
				Class<?> eClass = ann.entity();
				String gridId = ann.gridId();
				JoinKey[] joinKeys = ann.joinKeys();
				
				
				HeaderBinder[] hann = ann.headerBinder();
				Map<String, String> hMap = new HashMap<String, String>();
				for(HeaderBinder h : hann) {
					hMap.put(h.headerId(), h.targetField());
				}
				
				parentKeyVals = new Object[joinKeys.length];
				foreingKeys = new String[joinKeys.length];
				
				if(!f.isAccessible()) f.setAccessible(true);
				
				try {
					
					for(int i = 0; i < joinKeys.length; i++) {
						parentKeyVals[i] = beanUtils.getProperty(editedData, joinKeys[i].parentKey());
						foreingKeys[i] = joinKeys[i].childKey();
					}
					
					List<Serializable> children = (List<Serializable>)f.get(this);
					
					lists.add(getChildrenContainer(childIndex));
					
					Component grid = getChildGrid(gridId, fields);
					if(grid instanceof Grid) {
						parseChildGridData((Grid)grid, children, eClass, hMap, parentKeyVals, joinKeys);
					} else if(grid instanceof Listbox) {
						parseListboxGridData((Listbox)grid, children, eClass, hMap, parentKeyVals, joinKeys);
					}
					
				} catch (Exception e) {
				}
				
				childIndex++;
				
			}
		}
		return lists;
	}
	
	/**
	 * get child grid
	 * @param gridId
	 * @param fields
	 * @return
	 */
	private Component getChildGrid(String gridId, Field[] fields) {
		try {
			for(Field f: fields) {
				if(gridId.equals(f.getName())) {
					if(!f.isAccessible()) {
						f.setAccessible(true);
					}
					return (Component)f.get(this);
				}
			}
		} catch (Exception e) {
		}
		return null;
	}
	
	private void parseChildGridData(Grid grid, List<Serializable> children, Class<?> eClass, 
			Map<String, String> hdrBinder, Object[] parentKey, JoinKey[] joinKeys) throws Exception {
		
		if(grid != null) {
			
			List<Row> rows = grid.getRows().getChildren();
			List<Column> cols = grid.getColumns().getChildren();
			
			int r = 0;
			for(Row row : rows) {
				
//				if(r > children.size()) {
//					Serializable data = (Serializable)org.springframework.beans.BeanUtils.instantiate(eClass);
//					children.add(data);
//				}
				
//				Serializable data = children.get(r);
				
				Serializable data = row.getValue();
				List<Component> cells = row.getChildren();
				
				int c = 0;							
				for(Column col : cols) {
					
					Component inp = cells.get(c);
					
					String targetField = col.getId();
					
					if(hdrBinder.containsKey(targetField)) {
						targetField = hdrBinder.get(targetField);
					}
					
					setProperty(inp, data, targetField);
					
					c++;
				}
				
				for(int i = 0; i < parentKey.length; i++) {
					ExtendedBeanUtils.getInstance().setProperty(data, parentKey[i], joinKeys[i].childKey());
				}
				
				r++;
			}
			
		}
		
	}

	private void parseListboxGridData(Listbox listbox, List<Serializable> children, Class<?> eClass, 
			Map<String, String> hdrBinder, Object[] parentKey, JoinKey[] joinKeys) throws Exception {
		if(listbox != null) {
			Collection<Component> headers = listbox.getHeads();
			if(headers.size() > 0) {
				Component[] heads = headers.toArray(new Component[headers.size()]);
				Listhead hds = (Listhead)heads[0];
				
				List<Listheader> hrds = hds.getChildren();
				List<Listitem> items = listbox.getItems();
				
				int r = 0;
				for(Listitem item: items) {
					
//					if(r >= children.size()) {
//						Serializable data = (Serializable)org.springframework.beans.BeanUtils.instantiate(eClass);
//						children.add(data);
//					}
					
//					Serializable data = children.get(r);
					
					Serializable data = item.getValue();
					
					int c = 0;
					
					List<Listcell> cells = item.getChildren();
					
					for(Listheader hdr : hrds) {
						Listcell cell = cells.get(c);
						
						if(!(cell.getChildren().isEmpty())) {
							
							Component inp = cell.getChildren().get(0);
							
							String targetField = hdr.getId();
							
							if(hdrBinder.containsKey(targetField)) {
								targetField = hdrBinder.get(targetField);
							}
							
							if(targetField != null && targetField.trim().length() > 0) {						
								setProperty(inp, data, targetField);
							}
						}
						
						c++;
					}
					
					for(int i = 0; i < parentKey.length; i++) {
						ExtendedBeanUtils.getInstance().setProperty(data, parentKey[i], joinKeys[i].childKey());
					}
					
					r++;
				}
			}
		}
	}

	/**
	 * Set data property dari input client
	 * @param input
	 * @param data
	 * @param fieldName
	 */
	private void setProperty(Component input, Object data, String fieldName) {
		if(input != null) {
			Object val = null; 
			if(input instanceof Datebox) {
				val = ((Datebox)input).getValue();
			} else if(input instanceof Timebox) {
				val = ((Timebox)input).getValue();
			} else if(input instanceof Intbox) {
				val = ((Intbox)input).getValue();
			} else if(input instanceof Decimalbox) {
				val = ((Decimalbox)input).getValue();
			} else if(input instanceof Doublebox) {
				val = ((Doublebox)input).getValue();
			} else if(input instanceof Textbox) {
				val = ((Textbox)input).getValue();
			} else if(input instanceof Longbox) {
				val = ((Longbox)input).getValue();
			}
			
			try {
				ExtendedBeanUtils.getInstance().setProperty(data, val, fieldName);
			} catch (Exception e) {
			}
		}
	}
	
	/**
	 * Set value property class
	 * @param idSpace
	 * @param field
	 */
	protected void setProperty(IdSpace idSpace, Field... fields){
		StringBuffer buffer = new StringBuffer();
		for(Field field : fields){
			if(buffer.length()>0){
				buffer.append("_");
			}
			buffer.append(field.getName());
		}
		Component input = idSpace.getFellowIfAny(buffer.toString());
		if(input != null) {
			Object val = null; 
			if(input instanceof Datebox) {
				val = ((Datebox)input).getValue();
			} else if(input instanceof Timebox) {
				val = ((Timebox)input).getValue();
			} else if(input instanceof Intbox) {
				val = ((Intbox)input).getValue();
			} else if(input instanceof Decimalbox) {
				val = ((Decimalbox)input).getValue();
			} else if(input instanceof Doublebox) {
				val = ((Doublebox)input).getValue();
			} else if(input instanceof Textbox) {
				val = ((Textbox)input).getValue();
			}
			
			try {
				if(fields.length>1){
					Object currObject = editedData;
					for(int i = 0; i < fields.length - 1; i++){
						currObject = PropertyUtils.getProperty(currObject, fields[i].getName());
					}
					if(currObject != null) {
						BeanUtils.setProperty(currObject, fields[fields.length-1].getName(), val);
					}
				}else{
					BeanUtils.setProperty(editedData,fields[0].getName() , val);
				}
			} catch (Exception e) {
			}
		}
	}
	
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public ComponentInfo doBeforeCompose(Page page, Component parent,
			ComponentInfo compInfo) {
		
		Map<?,?> passedParameter  = Executions.getCurrent().getArg();
		
		ComponentInfo info = super.doBeforeCompose(page, parent, compInfo);
		
		if ( passedParameter!= null ) {
			if ( passedParameter.containsKey(ZKCoreLibConstant.EDITED_DATA_ATTRIBUTE_KEY)) {
				editedData = (POJO) passedParameter.get(ZKCoreLibConstant.EDITED_DATA_ATTRIBUTE_KEY);
			}
			if(passedParameter.containsKey(ZKCoreLibConstant.ADDITIONAL_DATA_ATTRIBUTE_KEY)){
				additionalData = (POJO) passedParameter.get(ZKCoreLibConstant.ADDITIONAL_DATA_ATTRIBUTE_KEY);
			}
			if ( passedParameter.containsKey(ZKCoreLibConstant.EDITOR_STATE_ATTRIBUTE_KEY)  ) {
				setEditorState(  (ZKEditorState)passedParameter.get(ZKCoreLibConstant.EDITOR_STATE_ATTRIBUTE_KEY));
			}
			if( passedParameter.containsKey(ZKCoreLibConstant.EDITOR_CALLER_COMPONENT)){
				editorCallerReference = (BaseSimpleController) passedParameter.get(ZKCoreLibConstant.EDITOR_CALLER_COMPONENT); 
			}
			
			runAditionalTaskOnDataRevieved(editedData, editorState, passedParameter);
			
		}
		
		return info;
	}
	
	
	public void setEditorState(ZKEditorState editorState) {
		this.editorState = editorState;
	}
	
	
	
	
	/**
	 * di sini pekerjaan untuk handle task pada saat data di terima
	 */
	protected void runAditionalTaskOnDataRevieved (POJO editedData , ZKEditorState editorState , Map<?,?>   rawDataParameter) {
		
	}
	
	
	/**
	 * menutup panel
	 */
	public void closePanel () {
		EditorManager.getInstance().closeCurrentEditorPanel();
	}

	
	/**
	 * path zul
	 */
	public ZKEditorState getEditorState() {
		return editorState;
	}
	
	
	/**
	 * reference ke pemanggil dari component
	 * 
	 */
	public BaseSimpleController getEditorCallerReference() {
		return editorCallerReference;
	}
}
