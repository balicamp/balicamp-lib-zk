package id.co.sigma.zk.ui;

/**
 * @author <a href="mailto:gede.sutarsa@gmail.com">Gede Sutarsa</a>
 */
public interface SingleValueLookupRecieverWithValidation<DATA> extends SingleValueLookupReciever<DATA> {

	
	/**
	 * validasi data. kalau misalnya validasi gagal , throw exception agar data tidak di pilih
	 */
	public void validateSelectedData (DATA selectedData) throws Exception ; 
}
