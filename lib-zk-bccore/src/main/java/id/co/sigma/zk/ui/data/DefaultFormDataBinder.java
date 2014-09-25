package id.co.sigma.zk.ui.data;

import id.co.sigma.common.server.util.ExtendedBeanUtils;

import org.zkoss.zhtml.Textarea;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Intbox;
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
		if ( controlSource instanceof Textbox) {
			Textbox txt = (Textbox) controlSource; 
			val = txt.getValue(); 
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
		else if ( controlSource instanceof Combobox) {
			Combobox txt = (Combobox) controlSource;
			val = txt.getValue();
		}
		else if ( controlSource instanceof Doublebox ) {
			Doublebox txt = (Doublebox) controlSource;
			val = txt.getValue();
		}
		else if  ( controlSource instanceof Textarea) {
			Textarea txt = (Textarea) controlSource;
			val = txt.getValue();
		}
		ExtendedBeanUtils.getInstance().setProperty(target, val, targetFieldName);
		
		
		
	}
	
}
