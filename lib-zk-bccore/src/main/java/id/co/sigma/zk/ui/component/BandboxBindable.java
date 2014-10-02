package id.co.sigma.zk.ui.component;

import org.zkoss.zul.Bandbox;

/**
 * 
 * @author <a href="mailto:gede.sutarsa@gmail.com">Gede Sutarsa</a>
 */
public class BandboxBindable extends Bandbox implements IBindableComponent{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4724618993331578103L;


	static final org.slf4j.Logger logger = org.slf4j.LoggerFactory
			.getLogger(BandboxBindable.class.getName());
	
	
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
