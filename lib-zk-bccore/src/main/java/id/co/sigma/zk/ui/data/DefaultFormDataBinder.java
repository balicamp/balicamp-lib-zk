package id.co.sigma.zk.ui.data;

import id.co.sigma.common.data.lov.CommonLOV;
import id.co.sigma.common.server.util.ExtendedBeanUtils;

import org.zkoss.zhtml.Textarea;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Components;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Spinner;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timebox;

/**
 * 
 * @author <a href="mailto:gede.sutarsa@gmail.com">Gede Sutarsa</a>
 */
public class DefaultFormDataBinder<DATA> implements IFormDataBinder<AbstractComponent , DATA>{
	static final org.slf4j.Logger logger = org.slf4j.LoggerFactory
			.getLogger(DefaultFormDataBinder.class.getName());

	@Override
	public void bindDataFromControl(AbstractComponent controlSource, DATA target,
			String targetFieldName) throws Exception{
		Object val = null ; 
		if ( controlSource instanceof Combobox) {

			int idx = -1;
			try {
				idx = ((Combobox)controlSource).getSelectedIndex();
			} catch (Exception e) {}
			
			Object cdata = null;
			if(idx >= 0) {
				if(((Combobox)controlSource).getModel() != null) {
					cdata = ((Combobox)controlSource).getModel().getElementAt(idx);
				} else {
					cdata = ((Combobox)controlSource).getSelectedItem().getValue();
				}
			}
			if(cdata instanceof CommonLOV) {
				val = ((CommonLOV)cdata).getDataValue();
			} else {
				Comboitem citem = ((Combobox)controlSource).getSelectedItem();
				if(citem != null) {
					val = citem.getValue();
				} else {
					val = ((Combobox)controlSource).getValue();
				}
			}
			
		}
		else if ( controlSource instanceof Intbox) {
			Intbox txt = (Intbox) controlSource;
			val = txt.getValue();
		}
		else if ( controlSource instanceof Decimalbox) {
			Decimalbox txt = (Decimalbox) controlSource;
			val = txt.getValue();
		}
		else if ( controlSource instanceof Datebox) {
			Datebox txt = (Datebox) controlSource;
			val = txt.getValue();
		}
		else if ( controlSource instanceof Timebox) {
			Timebox txt = (Timebox) controlSource;
			val = txt.getValue();
		}
		else if ( controlSource instanceof Spinner) {
			Spinner txt = (Spinner) controlSource;
			val = txt.getValue();
		}
		else if ( controlSource instanceof Textbox) {
			Textbox txt = (Textbox) controlSource; 
			val = txt.getValue(); 
		}
		else if ( controlSource instanceof Doublebox ) {
			Doublebox txt = (Doublebox) controlSource;
			val = txt.getValue();
		}
		else if  ( controlSource instanceof Textarea) {
			Textarea txt = (Textarea) controlSource;
			val = txt.getValue();
		} else if(controlSource instanceof Checkbox){
			Checkbox chk = (Checkbox)controlSource;
			if(chk.isChecked()){
				val=1;
			}else{
				val=0;
			}
		} else if(controlSource instanceof Radiogroup) {
			Radio radio = ((Radiogroup)controlSource).getSelectedItem();
			if(radio != null) {
				val = radio.getValue();
			}
		} else if(controlSource instanceof Radio){
			Radio radio = (Radio)controlSource;
			if(radio.isSelected() || radio.isChecked()) {
				val = radio.getValue();
			}
		}
		ExtendedBeanUtils.getInstance().setProperty(target, val, targetFieldName);
		
		
		
	}
	
}
