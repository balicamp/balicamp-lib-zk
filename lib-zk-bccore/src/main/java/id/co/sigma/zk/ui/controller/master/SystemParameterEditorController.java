package id.co.sigma.zk.ui.controller.master;

import id.co.sigma.common.data.app.SystemSimpleParameter;
import id.co.sigma.zk.ui.controller.ZKEditorState;
import id.co.sigma.zk.ui.controller.base.BaseSimpleDirectToDBEditor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;

/**
 * 
 * @author <a href="mailto:rie.anggreani@gmail.com">Arie Anggreani</a>
 */
public class SystemParameterEditorController extends BaseSimpleDirectToDBEditor<SystemSimpleParameter>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -496673852702964145L;
	
	private static final Logger logger = LoggerFactory.getLogger(SystemParameterEditorController.class.getName()); 
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @Wire Textbox id ; 
    @Wire Textbox paramType; 
    @Wire Textbox remark;
    @Wire Textbox paramValue ; 
    @Wire Combobox cmbType;
    @Wire Datebox dateValue;
    @Wire Radiogroup radioValue;
    @Wire Radiogroup editableFlag;
    @Wire Intbox intparamValue;
    @Wire Decimalbox decparamValue;
    
    @Wire
    Row textParamType;

    @Wire
    Row dateParamType;

    @Wire
    Row boolParamType;
    
    @Wire
    Row decParamType;
    
    @Wire
    Row intParamType;


    ListModelList<String> listModelEditable;
    ListModelList<String> listModelParamtype;
    SystemSimpleParameter system;
    
	public ListModelList<String> getListModelParamtype() {
		return listModelParamtype;
	}

	public void setListModelParamtype(ListModelList<String> listModelParamtype) {
		this.listModelParamtype = listModelParamtype;
	}

	public SystemSimpleParameter getSystem() {
		return system;
	}

	public void setSystem(SystemSimpleParameter system) {
		this.system = system;
	}

	public ListModelList<String> getListModelEditable() {
		return listModelEditable;
	}

	public void setListModelEditable(ListModelList<String> listModelEditable) {
		this.listModelEditable = listModelEditable;
	}
	
    public Textbox getId() {
		return id;
	}

	public void setId(Textbox id) {
		this.id = id;
	}

	@Listen("onChange=#cmbType")
    public void onChange(Event event){
		if(cmbType.getValue().equals("java.util.Date")){
			intParamType.setVisible(false);
			textParamType.setVisible(false);
			dateParamType.setVisible(true);
			boolParamType.setVisible(false);
			decParamType.setVisible(false);
		}else if (cmbType.getValue().equals("java.lang.Boolean")){
			intParamType.setVisible(false);
			textParamType.setVisible(false);
			dateParamType.setVisible(false);
			boolParamType.setVisible(true);
			decParamType.setVisible(false);
		} else if (cmbType.getValue().equals(Float.class.getName())){
			intParamType.setVisible(false);
			textParamType.setVisible(false);
			dateParamType.setVisible(false);
			boolParamType.setVisible(false);
			decParamType.setVisible(true);
		}else if (cmbType.getValue().equals(String.class.getName())){
			intParamType.setVisible(false);
			textParamType.setVisible(true);
			dateParamType.setVisible(false);
			boolParamType.setVisible(false);
			decParamType.setVisible(false);
		}else {
			intParamType.setVisible(true);
			decParamType.setVisible(false);
			textParamType.setVisible(false);
			dateParamType.setVisible(false);
			boolParamType.setVisible(false);
			
		}
    }
    
    @Override
    public void doAfterCompose(Component comp) throws Exception {
    	// TODO Auto-generated method stub
    	super.doAfterCompose(comp);
    	
    	listModelParamtype= new ListModelList<String>();
    	listModelParamtype.add(Integer.class.getName());
    	listModelParamtype.add(Long.class.getName());
    	listModelParamtype.add(Float.class.getName());
    	listModelParamtype.add(String.class.getName());
    	listModelParamtype.add(BigInteger.class.getName());
    	listModelParamtype.add(Date.class.getName());
    	listModelParamtype.add(Boolean.class.getName());
    	
    	cmbType.setModel(listModelParamtype);
    	if(getEditorState().equals(ZKEditorState.EDIT)){
	    	if(editedData != null) {
	    		if("java.util.Date".equals(editedData.getParamType())) {
	    			textParamType.setVisible(false);
	    			dateParamType.setVisible(true);
	    			boolParamType.setVisible(false);
	    			decParamType.setVisible(false);
	    			intParamType.setVisible(false);
	    			if(editedData.getValueRaw() != null) {
	    				try {
	    					dateValue.setValue(DATE_FORMAT.parse(editedData.getValueRaw()));
						} catch (Exception e) {}
	    			}
	    		} else if ("java.lang.Boolean".equals(editedData.getParamType())) {
	    			textParamType.setVisible(false);
	    			dateParamType.setVisible(false);
	    			boolParamType.setVisible(true);
	    			decParamType.setVisible(false);
	    			intParamType.setVisible(false);
	    			Boolean bool = Boolean.valueOf(editedData.getValueRaw());
	    			radioValue.setSelectedIndex(bool ? 0 : 1);
	    		}else if (Float.class.getName().equals(editedData.getParamType())){
	    			textParamType.setVisible(false);
	    			dateParamType.setVisible(false);
	    			boolParamType.setVisible(false);
	    			decParamType.setVisible(true);
	    			intParamType.setVisible(false);
	    			decparamValue.setValue(new BigDecimal(editedData.getValueRaw()));
	    		} else if (String.class.getName().equals(editedData.getParamType())){
	    			textParamType.setVisible(true);
	    			dateParamType.setVisible(false);
	    			boolParamType.setVisible(false);
	    			decParamType.setVisible(false);
	    			intParamType.setVisible(false);
	    			paramValue.setValue(editedData.getValueRaw());
	    		} else {
	    			textParamType.setVisible(false);
	    			dateParamType.setVisible(false);
	    			boolParamType.setVisible(false);
	    			decParamType.setVisible(false);
	    			intParamType.setVisible(true);
	    			intparamValue.setValue(new Integer(editedData.getValueRaw()));
	    		}
	     	}
    	}
    	
    	editableFlag.setSelectedIndex(("Yes".equals(editedData.getEditableFlag()) || "Y".equals(editedData.getEditableFlag())) ? 0 : 1);
    	
    /*	if (Date.class.getName().equals(editedData.getParamType())){
    		editedData.setValueRaw(dateValue.getValue().toString());
    	}*/
    }
    
    @Override
    public ComponentInfo doBeforeCompose(Page page, Component parent,
    		ComponentInfo compInfo) {
    	system = (SystemSimpleParameter)Executions.getCurrent().getAttribute("system");
    	return super.doBeforeCompose(page, parent, compInfo);
    }
    
    
    @Override
    protected void parseEditedData(Component comp) {
    	super.parseEditedData(comp);
    	Radio editableRadio = editableFlag.getSelectedItem();
    	if(editableFlag!=null){
    		editedData.setEditableFlag(editableRadio.getValue().toString());
    	}   	
    	editedData.setParamType(cmbType.getValue());
    	if (Date.class.getName().equals(editedData.getParamType())){
    		Date dtValue=dateValue.getValue();
    		if(dtValue!=null){
//	    		SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	            String date = DATE_FORMAT.format(dtValue);
	    		editedData.setValueRaw(date);
    		}
    	} else if (Boolean.class.getName().equals(editedData.getParamType())){
    		Radio valueRadio = radioValue.getSelectedItem();
        	if(radioValue!=null){
        		editedData.setValueRaw(valueRadio.getValue().toString());
        	}
    	} else if (Float.class.getName().equals(editedData.getParamType())){
    		BigDecimal floatValue= decparamValue.getValue();
    		if(floatValue!=null){
    			editedData.setValueRaw(floatValue.toString());
    		}
    	} else if (String.class.getName().equals(editedData.getParamType())){
    		editedData.setValueRaw(paramValue.getValue());
    	} else {
    		Integer integer = intparamValue.getValue();
    		if(integer!=null){
    			editedData.setValueRaw(String.valueOf(integer));
    		}
    	}
    }
}
