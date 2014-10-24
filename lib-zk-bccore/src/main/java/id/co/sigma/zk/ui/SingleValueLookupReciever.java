package id.co.sigma.zk.ui;



/**
 * interface untuk selector dengan data hasil tunggal
 * @author <a href='mailto:gede.sutarsa@gmail.com'>Gede Sutarsa</a>
 */
public interface SingleValueLookupReciever<DATA> {
	
	
	
	
	/**
	 * object di trigger pada saat ada object di pilih
	 */
	public void onDataSelected (DATA selectedData ); 
	
	
	
	/**
	 * di trigger pada saat user memutuskan cancel/ tidak memilih 
	 */
	public void onNoneSelected () ; 

}
