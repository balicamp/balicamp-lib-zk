package id.co.sigma.zk.ui.component;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Bandbox;

/**
 * COA Suggestion box
 * @author <a href="mailto:ida.suartama@sigma.co.id">Goesde Rai</a>
 */
public class AutoSuggestBox extends Bandbox implements AfterCompose{
	private static final long serialVersionUID = -5852112816418552173L;
	
	static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AutoSuggestBox.class.getName());
	
	private Serializable selectedData;
	
	private Map<String, Object> additionalData;

	public AutoSuggestBox() {
		super();
		additionalData = new HashMap<String, Object>();
	}

	@Override
	public void afterCompose() {
		// TODO Auto-generated method stub
	}

	public Serializable getSelectedData() throws WrongValueException {
		checkUserError();
		return selectedData;
	}

	public void setSelectedData(Serializable selectedData) {
		this.selectedData = selectedData;
	}
	
	public Map<String, Object> getAdditionalData() {
		return additionalData;
	}
	
	public void setAdditionalData(Map<String, Object> additionalData) {
		this.additionalData = additionalData;
	}
	
	public Object getAdditionalDataItem(String key){
		if(getAdditionalData().containsKey(key)){
			return getAdditionalData().get(key);
		}else{
			return null;
		}
	}
	
	public void setAdditionalDataItem(String key, Object val){
		getAdditionalData().put(key, val);
	}
}
