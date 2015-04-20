package id.co.sigma.zk.ui.component;

import java.io.Serializable;

import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Bandbox;

/**
 * COA Suggestion box
 * @author <a href="mailto:ida.suartama@sigma.co.id">Goesde Rai</a>
 */
public class CoaSuggestionBox extends Bandbox implements AfterCompose{
	private static final long serialVersionUID = -5852112816418552173L;
	
	static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(CoaSuggestionBox.class.getName());
	
	private Serializable selectedData; 

	@Override
	public void afterCompose() {
		// TODO Auto-generated method stub
	}

	public Serializable getSelectedData() {
		return selectedData;
	}

	public void setSelectedData(Serializable selectedData) {
		this.selectedData = selectedData;
	}
	
	
}
