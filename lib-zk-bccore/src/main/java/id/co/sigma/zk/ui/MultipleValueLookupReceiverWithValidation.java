package id.co.sigma.zk.ui;

import java.util.List;


/**
 * @author <a href="mailto:raka.sanjaya@sigma.co.id">Raka Sanjaya</a>
 */
public interface MultipleValueLookupReceiverWithValidation<DATA> extends
	MultipleValueLookupReceiver<DATA> {
    
    /**
	 * validasi data. kalau misalnya validasi gagal , throw exception agar data tidak di pilih
	 */
	public void validateSelectedData (List<DATA> selectedDatas) throws Exception ;

}
