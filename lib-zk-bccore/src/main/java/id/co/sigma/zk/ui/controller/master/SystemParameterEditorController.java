package id.co.sigma.zk.ui.controller.master;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;

import id.co.sigma.common.data.app.SystemSimpleParameter;
import id.co.sigma.zk.ui.controller.EditorManager;
import id.co.sigma.zk.ui.controller.ZKEditorState;
import id.co.sigma.zk.ui.controller.base.BaseSimpleDirectToDBEditor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

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
    
/*    Window win;
    AnnotateDataBinder binder;
    boolean textbox, datepicker, radiogrup;
    
    
    private Date dtValue ;
    private Radio editableRadio;
    private Radio valueRadio;
    



	public Radio getValueRadio() {
		return valueRadio;
	}

	public void setValueRadio(Radio valueRadio) {
		this.valueRadio = valueRadio;
	}

	public Radio getEditableRadio() {
		return editableRadio;
	}

	public void setEditableRadio(Radio editableRadio) {
		this.editableRadio = editableRadio;
	}

	public Textbox getRemark() {
		return remark;
	}

	public void setRemark(Textbox remark) {
		this.remark = remark;
	}

	public Date getDtValue() {
		return dtValue;
	}

	public void setDtValue(Date dtValue) {
		this.dtValue = dtValue;
	}



	public Radiogroup getEditableFlag() {
		return editableFlag;
	}

	public void setEditableFlag(Radiogroup editableFlag) {
		this.editableFlag = editableFlag;
	}

	public boolean isRadiogrup() {
		return radiogrup;
	}

	public void setRadiogrup(boolean radiogrup) {
		this.radiogrup = radiogrup;
	}

	public boolean isTextbox() {
		return textbox;
	}

	public void setTextbox(boolean textbox) {
		this.textbox = textbox;
	}

	public boolean isDatepicker() {
		return datepicker;
	}

	public void setDatepicker(boolean datepicker) {
		this.datepicker = datepicker;
	}
*/
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
	
	

	/*@Listen(value="onClick = #saveButton")
    public void simpanClick() {
        if ( ZKEditorState.ADD_NEW.equals(getEditorState())) {
            try {
                insertData();
            } catch (Exception e) {
                logger.error( "" + e.getMessage() , e);
                 Messagebox.show("Gagal input data page. error : " + e.getMessage(), "Gagal Tambah Data", Messagebox.OK, Messagebox.ERROR);
            }

        }else {
            try {
                updateData();
            } catch (Exception e) {
                logger.error("gagal update file. error : " + e.getMessage() , e);
                 Messagebox.show("Gagal input data page. error : " + e.getMessage(), "Gagal Simpan Data", Messagebox.OK, Messagebox.ERROR);
            }
				Date datenya = dateValue.getValue();
				String valStr = datenya.toString();
        }

    }*/
	
	

    public Textbox getId() {
		return id;
	}

	public void setId(Textbox id) {
		this.id = id;
	}

	@Listen("onClick=#btnCancel")
    public void onCancel(){
        EditorManager.getInstance().closeCurrentEditorPanel();
    }


	@Listen("onChange=#cmbType")
    public void onChange(Event event){
		if(cmbType.getValue().equals("java.util.Date")){
//			setTextbox(false);
//			setDatepicker(true);
//			setRadiogrup(false);
			intParamType.setVisible(false);
			textParamType.setVisible(false);
			dateParamType.setVisible(true);
			boolParamType.setVisible(false);
			decParamType.setVisible(false);
		}else if (cmbType.getValue().equals("java.lang.Boolean")){
//			setTextbox(false);
//		    setDatepicker(false);
//		    setRadiogrup(true);
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
    	
//    	if(comp instanceof Window){
//    		win = (Window) comp;
//    		binder = new AnnotateDataBinder(win);
//    		System.out.println("WINDOW_ID : " + win.getId());
//            System.out.println("DESKTOP_ID : " + win.getDesktop().getId());
//    	}
    	
    	
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
