package id.co.sigma.zk.ui;

import java.util.List;


/**
 * @author <a href="mailto:raka.sanjaya@sigma.co.id">Raka Sanjaya</a>
 */
public interface MultipleValueLookupReceiver<DATA> {
    
    /**
	 * object di trigger pada saat ada object di pilih
	 */
	public void onDataSelected (List<DATA> selectedDatas); 
	
	
	
	/**
	 * di trigger pada saat user memutuskan cancel/ tidak memilih 
	 */
	public void onNoneSelected () ;

}
