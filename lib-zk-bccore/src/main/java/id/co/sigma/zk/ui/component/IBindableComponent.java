package id.co.sigma.zk.ui.component;

import id.co.sigma.zk.ui.data.IFormDataBinder;

/**
 * interface untuk komponen input yang bisa di bind otomatis di contorller
 *  
 * @author <a href="mailto:gede.sutarsa@gmail.com">Gede Sutarsa</a>
 */
public interface IBindableComponent {
	
	
	/**
	 * field yang menjadi target bind. ini nama pojo field
	 */
	public String getTargetBindField()  ;
	
	/**
	 * field yang menjadi target bind. ini nama pojo field
	 */
	public void setTargetBindField(String targetBindField)  ;
	
	
	/**
	 * class untuk binder. ini hanya di isi kalau class memerlukan custom binder. FQCN harus implement {@link IFormDataBinder}
	 */
	public void setBinderClassFQCN (String fqcn ) ;
	
	
	
	/**
	 * get FQCN dari class binder
	 */
	public String getBinderClassFQCN () ;
	

}
