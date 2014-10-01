package id.co.sigma.zk.ui.data;

/**
 * interface untuk data binder
 * @author <a href="mailto:gede.sutarsa@gmail.com">Gede Sutarsa</a>
 */
public interface IFormDataBinder<CONTROL , DATA > {
	
	
	/**
	 * worker untuk menaruh data ke dalam POJO, data di ambil dari source control
	 * @param controlSource control sumber. ini bisa textbox, textarea, combo box , etc
	 * @param target variable yang akan menerima entry dari user
	 * @param targetFieldName field yang dalam <i>target</i>, kemana data akan di taruh
	 */
	public void bindDataFromControl (CONTROL controlSource , DATA target , String targetFieldName) throws Exception; 

}
