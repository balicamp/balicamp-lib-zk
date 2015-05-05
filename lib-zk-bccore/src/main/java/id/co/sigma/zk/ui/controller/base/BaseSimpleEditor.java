package id.co.sigma.zk.ui.controller.base;



import id.co.sigma.common.data.SingleKeyEntityData;
import id.co.sigma.common.data.lov.CommonLOV;
import id.co.sigma.common.server.util.ExtendedBeanUtils;
import id.co.sigma.zk.ZKCoreLibConstant;
import id.co.sigma.zk.ui.annotations.ChildGridData;
import id.co.sigma.zk.ui.annotations.HeaderBinder;
import id.co.sigma.zk.ui.annotations.JoinKey;
import id.co.sigma.zk.ui.component.CoaSuggestionBox;
import id.co.sigma.zk.ui.controller.EditorManager;
import id.co.sigma.zk.ui.controller.IReloadablePanel;
import id.co.sigma.zk.ui.controller.ZKEditorState;
import id.co.sigma.zk.ui.controller.base.window.EditorWindow;
import id.co.sigma.zk.ui.custom.component.ListOfValueItem;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.util.resource.Labels;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Column;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Constraint;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Spinner;
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
	
	
	private static final Logger logger = LoggerFactory.getLogger(BaseSimpleEditor.class);
	
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
	 * data detail
	 */
	protected List<ZKClientSideListDataEditorContainer<Object>> children;
	
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
	protected abstract void insertData (Object... data ) throws Exception ;
	
	public void deleteData(POJO data) throws Exception {
		throw new Exception("Method not supported.");
	}
	
	/**
	 * delete child/detail data
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public void deleteChildrenData(List<?> childrenData) throws Exception {
		if(childrenData == null || childrenData.isEmpty()) return;
		if(childrenData != null && !childrenData.isEmpty()) {
			if(childrenData.get(0) instanceof SingleKeyEntityData) {
				for(Object child : childrenData) {
					generalPurposeDao.delete(child.getClass(), (Serializable)((SingleKeyEntityData)child).getId(), "id");
				}
				return;
			}
		}
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
		
		ExtendedBeanUtils beanUtils = ExtendedBeanUtils.getInstance();
		
		doBeforeSave(getEditedData());
		
		updateData(getEditedData());
		
		//insert child data (master-detail)
		if((children != null) && (children.size() > 0)) {
			
			for(ZKClientSideListDataEditorContainer<?> child : children) {
				
				if(child != null) {
					
					JoinKey[] keys = getJoinKeys(child);
					
					deleteChildrenData(child.getErasedData());

					for(Object data: child.getNewlyAppendedData()) {
						
						for(JoinKey key : keys) {
							Object val = beanUtils.getProperty(editedData, key.parentKey());
							beanUtils.setProperty(data, val, key.childKey());
						}
						
						insertData((POJO)data);
					}

					for(Object data: child.getEditedData()) {
						updateData((POJO)data);
					}

				}
				
			}
		}
	}
	
	/**
	 * insert data yang sedang di edit
	 */
	@SuppressWarnings("unchecked")
	public void insertData ()  throws Exception {
		
		doBeforeSave(getEditedData());
		
		Serializable[] pojo = new Serializable[]{(Serializable)getEditedData()};
		
		insertData((POJO[])pojo);
		
		//set pojo dari hasil insert ke database
		//hal ini dibutuhkan untuk mendapatkan autoincrement ID
		setEditedData((POJO)pojo[0]);
		
		ExtendedBeanUtils beanUtils = ExtendedBeanUtils.getInstance();
		
		if((children != null) && (children.size() > 0)) {
			
			for(ZKClientSideListDataEditorContainer<?> child : children) {
				
				if(child != null) {
					
					JoinKey[] keys = getJoinKeys(child);
					
					for(Object data: child.getNewlyAppendedData()) {
						
						for(JoinKey key : keys) {
							Object val = beanUtils.getProperty(editedData, key.parentKey());
							beanUtils.setProperty(data, val, key.childKey());
						}
						
						insertData((POJO)data);
						
					}
					
				}
				
			}
			
		}
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
	 * close current editor
	 */
	protected void closeCurrentEditorPanel() {
		if ( this.editorCallerReference != null && this.editorCallerReference instanceof IReloadablePanel) {
			((IReloadablePanel)editorCallerReference).reload();
		}
		EditorManager.getInstance().closeCurrentEditorPanel();
	}
	
	/**
	 * parse data dari client	 
	 * @param comp
	 */
	protected void parseEditedData(Component comp) { 
		IdSpace idSpace = comp.getSpaceOwner();
		Collection<Component> fellows = orderedInputFields;
		if(fellows.isEmpty()) {
			fellows = idSpace.getFellows();
		}
		for(Component fComp : fellows) {
			if(fComp instanceof InputElement || fComp instanceof Checkbox) {
				String fId = fComp.getId();
				if(fId.contains("_")) {
					String[] fields = fId.split("\\_");
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
	 * re-load child/detail data 
	 */
	protected final void reloadChildGridData() {
		parseChildGridData();
	}
	
	/**
	 * parse child grid data
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected final List<ZKClientSideListDataEditorContainer<Object>> parseChildGridData() {
		Field[] fields = getClass().getDeclaredFields();
		List<ZKClientSideListDataEditorContainer<Object>> lists = new ArrayList<ZKClientSideListDataEditorContainer<Object>>();
		for(Field f: fields) {
			if(f.isAnnotationPresent(ChildGridData.class)) {

				try {

					if(!f.isAccessible()) {
						f.setAccessible(true);
					}
					
					ZKClientSideListDataEditorContainer<Object> container = (ZKClientSideListDataEditorContainer<Object>)f.get(this);
					
					parseChildGrid(fields, f, container);
					
					lists.add(container);
					
				} catch(WrongValueException | WrongValuesException e) {
					throw e;
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
				
			}
		}
		return lists;
	}
	
	/**
	 * jika ada proses setting data tambahan sebelum save data
	 * @param editedData
	 */
	protected void doBeforeSave(POJO editedData) {
		
	}
	
	/**
	 * parse child/detail data
	 * @param fields
	 * @param f
	 */
	private void parseChildGrid(Field[] fields, Field f, ZKClientSideListDataEditorContainer<Object> container) {

		ExtendedBeanUtils beanUtils = ExtendedBeanUtils.getInstance();
		
		ChildGridData ann = f.getAnnotation(ChildGridData.class);
		
		Class<?> eClass = ann.entity();
		String gridId = ann.gridId();
		JoinKey[] joinKeys = ann.joinKeys();
		
		
		HeaderBinder[] hann = ann.headerBinder();
		Map<String, String> hMap = new HashMap<String, String>();
		for(HeaderBinder h : hann) {
			hMap.put(h.headerId(), h.targetField());
		}
		
		Object[] parentKeyVals = new Object[joinKeys.length];
		String[] foreingKeys = new String[joinKeys.length];
		
		try {
			
			for(int i = 0; i < joinKeys.length; i++) {
				parentKeyVals[i] = beanUtils.getProperty(editedData, joinKeys[i].parentKey());
				foreingKeys[i] = joinKeys[i].childKey();
			}
			
			Component grid = getChildGrid(gridId, fields);
			if(grid instanceof Grid) {
				parseChildGridData((Grid)grid, eClass, hMap, parentKeyVals, joinKeys, container);
			} else if(grid instanceof Listbox) {
				parseListboxGridData((Listbox)grid, eClass, hMap, parentKeyVals, joinKeys, container);
			}
			
		} catch (WrongValueException wve) {
			throw wve;
		} catch(WrongValuesException wvs) {
			throw wvs;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
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
	
	private void parseChildGridData(Grid grid, Class<?> eClass, 
			Map<String, String> hdrBinder, Object[] parentKey, JoinKey[] joinKeys, 
			ZKClientSideListDataEditorContainer<Object> container) throws Exception {
		
		EditorWindow eWind = getEditorWindow();
		
		if(grid != null) {
			
			List<Row> orgRows = grid.getRows().getChildren();
			List<Row> rows = new ArrayList<Row>(orgRows);
			List<Column> cols = grid.getColumns().getChildren();
			
			for(Row row : rows) {
				
				Serializable data = row.getValue();
				List<Component> cells = row.getChildren();
				
				int c = 0;							
				for(Column col : cols) {
					
					Component inp = cells.get(c);
					
					String targetField = col.getId();
					
					if(hdrBinder.containsKey(targetField)) {
						targetField = hdrBinder.get(targetField);
					}
					
					if(eWind != null) {
						eWind.addRequiredField(inp);						
					}
					
					setProperty(inp, data, targetField);
					
					c++;
				}
				
				
				Component label = row.getLastChild();
				if(label instanceof Label) {
					String sLbl = ((Label)label).getValue();
					if("*".equals(sLbl)) {
						container.modifyItem(data);
					}
				} else if(label instanceof Div) {
					String sclass = ((Div)label).getSclass();
					if("z-icon-pencil".equals(sclass)) {
						container.modifyItem(data);
					}
				}
				
				for(int i = 0; i < parentKey.length; i++) {
					if(parentKey[i] != null) {
						if(parentKey[i] instanceof String) {
							if(((String)parentKey[i]).trim().length() > 0) {
								ExtendedBeanUtils.getInstance().setProperty(data, parentKey[i], joinKeys[i].childKey());
							}
						} else {
							ExtendedBeanUtils.getInstance().setProperty(data, parentKey[i], joinKeys[i].childKey());
						}
					}
				}
				
			}
			
		}
		
	}

	private void parseListboxGridData(Listbox listbox, Class<?> eClass, 
			Map<String, String> hdrBinder, Object[] parentKey, JoinKey[] joinKeys,
			ZKClientSideListDataEditorContainer<Object> container) throws Exception {
		
		EditorWindow eWind = getEditorWindow();
		
		if(listbox != null) {
			Collection<Component> headers = listbox.getHeads();
			if(headers.size() > 0) {
				Component[] heads = headers.toArray(new Component[headers.size()]);
				Listhead hds = (Listhead)heads[0];
				
				List<Listheader> hrds = hds.getChildren();
				List<Listitem> items = new ArrayList<Listitem>(listbox.getItems());
				
				for(Listitem item: items) {
					
					Serializable data = item.getValue();
					
					int c = 0;
					
					List<Listcell> cells = item.getChildren();
					
					for(Listheader hdr : hrds) {
						Listcell cell = cells.get(c);
						
						if(!(cell.getChildren().isEmpty())) {
							
							Component inp = cell.getChildren().get(0);
							
							if( !(inp instanceof Combobox) 
									&& !(inp instanceof CoaSuggestionBox) 
									&& inp.getChildren()!=null 
									&& !inp.getChildren().isEmpty() )
							{
							    inp = inp.getChildren().get(0);
							}
							
							String targetField = hdr.getId();
							
							if(hdrBinder.containsKey(targetField)) {
								targetField = hdrBinder.get(targetField);
							}

							if(eWind != null) {
								eWind.addRequiredField(inp);						
							}
							
							if(targetField != null && targetField.trim().length() > 0) {						
								setProperty(inp, data, targetField);
							}
						}
						
						c++;
					}
					
					Component cell = item.getLastChild();
					if(cell instanceof Listcell) {
//						String sLabel = ((Listcell)cell).getLabel();
						String sLabel = ((Listcell)cell).getIconSclass();
						if("z-icon-pencil".equals(sLabel)) {
							container.modifyItem(data);
						}
						
					}
					
					for(int i = 0; i < parentKey.length; i++) {
						if(parentKey[i] != null) {
							if(parentKey[i] instanceof String) {
								if(((String)parentKey[i]).trim().length() > 0) {
									ExtendedBeanUtils.getInstance().setProperty(data, parentKey[i], joinKeys[i].childKey());
								}
							} else {
								ExtendedBeanUtils.getInstance().setProperty(data, parentKey[i], joinKeys[i].childKey());
							}
						}
					}
				}
			}
		}
	}
	
	private JoinKey[] getJoinKeys(ZKClientSideListDataEditorContainer<?> container) {
		Field[] fields = getClass().getDeclaredFields();
		for(Field f: fields) {
			if(f.isAnnotationPresent(ChildGridData.class)) {

				ChildGridData ann = f.getAnnotation(ChildGridData.class);
				
				try {

					if(!f.isAccessible()) {
						f.setAccessible(true);
					}
					
					ZKClientSideListDataEditorContainer<?> cntr = (ZKClientSideListDataEditorContainer<?>)f.get(this);
					
					if((container != null && cntr != null) && (container.equals(cntr))) {
						return ann.joinKeys();
					}
					
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
				
			}
		}
		return null;
	}

	/**
	 * Set data property dari input client
	 * @param input
	 * @param data
	 * @param fieldName
	 */
	protected void setProperty(Component input, Object data, String fieldName) {
		if(input != null) {
			Object val = null; 
			try {
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
				} else if(input instanceof Label) {
					val = ((Label)input).getValue();
				} else if(input instanceof Combobox)  {
					
					Object testVal = ((Combobox)input).getValue(); // test value
					
					Constraint cons = ((Combobox)input).getConstraint();
					if(cons != null) {
						cons.validate(input, testVal);
					}
					
					if(!Components.isRealVisible(input)) return;
					
					int idx = -1;
					try {
						idx = ((Combobox)input).getSelectedIndex();
					} catch (Exception e) {}
					
					Object cdata = null;
					if(idx >= 0) {
						if(((Combobox)input).getModel() != null) {
							cdata = ((Combobox)input).getModel().getElementAt(idx);
						} else {
							cdata = ((Combobox)input).getSelectedItem().getValue();
						}
					}
					if(cdata instanceof CommonLOV) {
						val = ((CommonLOV)cdata).getDataValue();
					} else if(cdata != null) {
						Comboitem citem = ((Combobox)input).getSelectedItem();
						if(citem != null) {
							val = citem.getValue();
						} else {
							val = ((Combobox)input).getValue();
						}
					}
					
					if(val instanceof ListOfValueItem){
						val = ((ListOfValueItem) val).getValue();
					}
					
				} else if(input instanceof Radiogroup) {
					Radio radio = ((Radiogroup)input).getSelectedItem();
					if(radio != null) {
						val = radio.getValue();
					}
				} else if(input instanceof Radio){
					Radio radio = (Radio)input;
					if(radio.isSelected() || radio.isChecked()) {
						Radiogroup rgroup = radio.getRadiogroup();
						fieldName = rgroup.getId();
						val = radio.getValue();
					}
				} else if(input instanceof Checkbox){
					Checkbox chk = (Checkbox)input;
					if(chk.isChecked()){
						val=1;
					}else{
						val=0;
					}
				} else if(input instanceof CoaSuggestionBox) {
					val = ((CoaSuggestionBox)input).getSelectedData();					
				} else if(input instanceof Textbox){
					val = ((Textbox)input).getValue();
				} else if(input instanceof Longbox) {
					val = ((Longbox)input).getValue();
				} else if(input instanceof Spinner){
				    val = ((Spinner)input).getValue();
				} else if(input instanceof Label){
					val = ((Label)input).getValue();
				}
				
			} catch (WrongValueException e1) {
				((InputElement)input).setFocus(true);
				throw e1;
			}
			
			try {
				if(val instanceof String){
					val = val.toString().trim();
				}
				if((fieldName != null) && !("".equals(fieldName))) {
					ExtendedBeanUtils.getInstance().setProperty(data, val, fieldName);
				}
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
	
	
	@SuppressWarnings("unused")
	private Object getProperty(Object data, String fieldName) {
		return ExtendedBeanUtils.getInstance().getProperty(data, fieldName);
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
	
	protected abstract void saveData(final Event event);
	
	/**
	 * handle button no jika dipilih
	 * default tidak melakukan proses 
	 * @param event
	 */
	protected void handleNoButton(final Event event) {
		
	}
	
	protected final void showInvalidDataMessage(String errMessage){
	    Messagebox.show(
			errMessage, 
			Labels.getLabel("title.msgbox.invalid"),
			new Messagebox.Button[]{Messagebox.Button.OK},
			new String[]{Labels.getLabel("action.button.ok")},
			Messagebox.ERROR,
			Messagebox.Button.OK, null);
	}
	
	protected final void showInformationMessage(String message, String titleMessage){
		Messagebox.show(
				message, 
				titleMessage,
				new Messagebox.Button[]{Messagebox.Button.OK},
				new String[]{Labels.getLabel("action.button.ok")},
				Messagebox.INFORMATION,
				Messagebox.Button.OK, null);
	}

	protected void showSuccesMessage(ZKEditorState state) {
		Messagebox.show(ZKEditorState.ADD_NEW.equals(state) ? Labels.getLabel("msg.save.add.success") : Labels.getLabel("msg.save.edit.success"), 
				Labels.getLabel("title.msgbox.information"),
				new Messagebox.Button[]{Messagebox.Button.OK},
				new String[]{Labels.getLabel("action.button.ok")},
				Messagebox.INFORMATION,
				Messagebox.Button.OK, null);
	}
	
	protected final void showErrorMessage(ZKEditorState state, String errMessage) {
		Messagebox.show(
				(ZKEditorState.ADD_NEW.equals(state) ? Labels.getLabel("msg.save.add.fail") : Labels.getLabel("msg.save.edit.fail"))
				+ ".\n\n" + ((errMessage != null) ? (Labels.getLabel("title.msgbox.error") + ": \n" + errMessage) : ""), 
				Labels.getLabel("title.msgbox.error"),
				new Messagebox.Button[]{Messagebox.Button.OK},
				new String[]{Labels.getLabel("action.button.ok")},
				Messagebox.ERROR,
				Messagebox.Button.OK, null);
	}

	protected final void showInvalidDataMessage(ZKEditorState state, String errMessage) {
		Messagebox.show(
				(ZKEditorState.ADD_NEW.equals(state) ? Labels.getLabel("msg.save.add.invalid") : Labels.getLabel("msg.save.edit.invalid"))
				+ ".\n\n" + ((errMessage != null) ? (errMessage) : ""), 
				Labels.getLabel("title.msgbox.invalid"),
				new Messagebox.Button[]{Messagebox.Button.OK},
				new String[]{Labels.getLabel("action.button.ok")},
				Messagebox.EXCLAMATION,
				Messagebox.Button.OK, null);
	}
	
	protected final EditorWindow getEditorWindow() {
		if(getSelf() instanceof EditorWindow) {
			return ((EditorWindow)getSelf());
		} else {
			return null;
		}
	}
	
	protected final void showCancelConfirmationMessage(String cancelMsg) {
		if(cancelMsg != null && cancelMsg.trim().length() > 0) {
			
			if(getSelf() instanceof EditorWindow) {
				((EditorWindow)getSelf()).clearErrorMessage();
			}
			
			Messagebox.show(cancelMsg, Labels.getLabel("title.msgbox.confirmation"),
					new Messagebox.Button[]{Messagebox.Button.YES, Messagebox.Button.NO},
					new String[]{Labels.getLabel("action.button.yes"), Labels.getLabel("action.button.no")},
					Messagebox.QUESTION,
					Messagebox.Button.YES,
					new EventListener<Messagebox.ClickEvent>() {
				
				@Override
				public void onEvent(Messagebox.ClickEvent event) throws Exception {
					if(Messagebox.Button.YES.equals(event.getButton())) {
						EditorManager.getInstance().closeCurrentEditorPanel();
					}
				}
			});				
		} else EditorManager.getInstance().closeCurrentEditorPanel();
	}
	
	protected void showSaveConfirmationMessage(final Event evt, ZKEditorState state, String confirmMsg) {
		if(confirmMsg != null && confirmMsg.trim().length() > 0) {
			
			Messagebox.show(confirmMsg, Labels.getLabel("title.msgbox.confirmation"),
					new Messagebox.Button[]{Messagebox.Button.YES, Messagebox.Button.NO},
					new String[]{Labels.getLabel("action.button.yes"), Labels.getLabel("action.button.no")},
					Messagebox.QUESTION,
					Messagebox.Button.YES,
					new EventListener<Messagebox.ClickEvent>() {
				
				@Override
				public void onEvent(Messagebox.ClickEvent event) throws Exception {
					if(Messagebox.Button.YES.equals(event.getButton())) {
						saveData(evt);
					} else if(Messagebox.Button.NO.equals(event.getButton())) {
						handleNoButton(evt);
					}
				}
			});				
		} else saveData(evt); 
	}
}
