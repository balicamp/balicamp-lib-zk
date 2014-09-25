package id.co.sigma.zk.ui.component;

import org.zkoss.zhtml.Textarea;

/**
 * 
 * @author <a href="mailto:gede.sutarsa@gmail.com">Gede Sutarsa</a>
 */
public class TextareaBindable extends Textarea implements IBindableComponent{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5039658316141505415L;
	static final org.slf4j.Logger logger = org.slf4j.LoggerFactory
			.getLogger(TextareaBindable.class.getName());
	
	private String targetBindField ;
	 
	private String  binderClassFQCN ;  
	 
	@Override 
	public String getTargetBindField() {
		return targetBindField;
	}
	@Override
	public void setTargetBindField(String targetBindField) {
		this.targetBindField = targetBindField;
	}
	@Override
	public void setBinderClassFQCN(String fqcn) {
		this.binderClassFQCN = fqcn  ; 
		
	}
	@Override
	public String getBinderClassFQCN() {
		return binderClassFQCN;
	} 
}
