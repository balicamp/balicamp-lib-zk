package id.co.sigma.zk.ui.controller.base;



import id.co.sigma.common.data.SingleKeyEntityData;
import id.co.sigma.common.security.domain.audit.BaseAuditedObject;
import id.co.sigma.zk.ZKCoreLibConstant;
import id.co.sigma.zk.ui.controller.EditorManager;
import id.co.sigma.zk.ui.controller.IReloadablePanel;
import id.co.sigma.zk.ui.controller.ZKEditorState;

import java.lang.reflect.Field;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timebox;



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
	
	
	/**
	 * worker untuk save data
	 */
	public   void updateData (  ) throws Exception {
		updateData(getEditedData());
		if ( this.editorCallerReference != null && this.editorCallerReference instanceof IReloadablePanel) {
			((IReloadablePanel)editorCallerReference).reload();
		}
		EditorManager.getInstance().closeCurrentEditorPanel();
	}
	/**
	 * insert data yang sedang di edit
	 */
	public void insertData ()  throws Exception {
		insertData(getEditedData());
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
	 * parse data dari client	 
	 * @param comp
	 */
	protected void parseEditedData(Component comp) { 
		Field[] fields = editedData.getClass().getDeclaredFields();
		IdSpace idSpace = comp.getSpaceOwner();
		for(Field field : fields) {
			try {
				if(field.getType().isAssignableFrom(SingleKeyEntityData.class)){
					Field[] childFields = field.getClass().getDeclaredFields();
					for(Field f : childFields){
						setProperty(idSpace, field, f);
					}
				}else{
						setProperty(idSpace, field);
}
			} catch (Exception e) {
				e.printStackTrace();
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
					Object childObj = null;
					Object parentObj = editedData;
					for(int i = 0; i < fields.length - 1; i++){
						childObj = PropertyUtils.getProperty(parentObj, fields[i].getName());
						parentObj = childObj;
					}
					System.out.println("Child: " + childObj + ", val: " + val);
					if(childObj != null) {
						BeanUtils.setProperty(childObj, fields[fields.length-1].getName(), val);
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
		
		if ( passedParameter!= null ) {
			if ( passedParameter.containsKey(ZKCoreLibConstant.EDITED_DATA_ATTRIBUTE_KEY)) {
				editedData = (POJO) passedParameter.get(ZKCoreLibConstant.EDITED_DATA_ATTRIBUTE_KEY);
			}
			if ( passedParameter.containsKey(ZKCoreLibConstant.EDITOR_STATE_ATTRIBUTE_KEY)  ) {
				editorState = (ZKEditorState)passedParameter.get(ZKCoreLibConstant.EDITOR_STATE_ATTRIBUTE_KEY);
			}
			if( passedParameter.containsKey(ZKCoreLibConstant.EDITOR_CALLER_COMPONENT)){
				editorCallerReference = (BaseSimpleController) passedParameter.get(ZKCoreLibConstant.EDITOR_CALLER_COMPONENT); 
			}
			
			runAditionalTaskOnDataRevieved(editedData, editorState, passedParameter);
			
		}
		return super.doBeforeCompose(page, parent, compInfo);
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
