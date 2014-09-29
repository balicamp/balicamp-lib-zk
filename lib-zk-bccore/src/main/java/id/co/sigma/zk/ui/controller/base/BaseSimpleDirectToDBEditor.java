
package id.co.sigma.zk.ui.controller.base;

import id.co.sigma.common.server.dao.IGeneralPurposeDao;
import id.co.sigma.common.server.service.IGeneralPurposeService;
import id.co.sigma.zk.ui.controller.EditorManager;
import id.co.sigma.zk.ui.controller.ZKEditorState;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.annotation.Listen;

/**
 * 
 * @author <a href='mailto:gede.sutarsa@gmail.com'>Gede Sutarsa</a>
 */
public abstract class BaseSimpleDirectToDBEditor<POJO extends Serializable> extends BaseSimpleEditor<POJO>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5283385493029490963L;
	
	private static final Logger logger = LoggerFactory.getLogger(BaseSimpleDirectToDBEditor.class);
	
	@Autowired
	protected IGeneralPurposeService generalPurposeService ;  
	
	@Autowired
	protected IGeneralPurposeDao  generalPurposeDao ; 
	
	@Autowired
	@Qualifier(value="transactionManager")
	protected PlatformTransactionManager  transactionManager ; 
	
	
	@Override
	protected void insertData(POJO data) throws Exception {
		generalPurposeService.insert(data);
		
	}
	
	@Override
	protected void updateData(POJO data) throws Exception {
		generalPurposeService.update(data);
	}
	
	@Listen("onClick = #btnSave")
	public void saveClick(final Event evt) {

		String confirmMsg = (String)getSelf().getAttribute("confirmationMsg");
		if(confirmMsg != null && confirmMsg.trim().length() > 0) {
			
			Messagebox.show(confirmMsg, "Prompt", 
					Messagebox.YES|Messagebox.NO, 
					Messagebox.QUESTION, 
			new EventListener<Event>() {
				
				@Override
				public void onEvent(Event event) throws Exception {
					switch(((Integer)event.getData()).intValue()) {
					case Messagebox.YES:
						saveData(evt);
						break;
					case Messagebox.NO:
						break;
					}
				}
			});				
		} else saveData(evt); 
	}
	
	private final void saveData(final Event event) {
		parseEditedData(event.getTarget());
		try {
			bindValueFromControl(getEditedData());
		} catch (Exception e) {
			 Messagebox.show("Gagal Bind data. error : " + e.getMessage(), "Gagal Bind Data", Messagebox.OK, Messagebox.ERROR);
			return  ; 
		}
		
		TransactionTemplate tmpl = new TransactionTemplate(this.transactionManager);
		
		tmpl.execute(new TransactionCallback<Integer>() {
			@Override
			public Integer doInTransaction(TransactionStatus stts) {
				
				Object obj = null;
				try {
					obj = stts.createSavepoint();
				} catch (Exception e) {
					logger.warn(e.getMessage());
				}
				
				boolean saveCommit = true ; 
				
				
				if ( ZKEditorState.ADD_NEW.equals(getEditorState())) {
					try {
						insertData();
					} catch (Exception e) {
						saveCommit = false ; 
						logger.error( "" + e.getMessage() , e);
						 Messagebox.show("Gagal input data page. error : " + e.getMessage(), "Gagal Tambah Data", Messagebox.OK, Messagebox.ERROR);
					}
				}else {
					try {
						updateData();
					} catch (Exception e) {
						saveCommit = false ; 
						logger.error("gagal update file. error : " + e.getMessage() , e);
						 Messagebox.show("Gagal input data page. error : " + e.getMessage(), "Gagal Simpan Data", Messagebox.OK, Messagebox.ERROR);
					}
					
				}
				
				if(obj != null) { //jika support savepoint
					if ( saveCommit){
						stts.releaseSavepoint(obj);
					}
					else {
						stts.rollbackToSavepoint(obj);
					}
				} else { //jika tidak support savepoint
					if(!saveCommit) {
						stts.setRollbackOnly();
					}
				}
				
				return 1;
			}
		});
		
		
		
		
	}
	
	@Listen("onClick = #btnCancel")
	public void cancelClick() {
		String cancelMsg = (String)getSelf().getAttribute("cancellationMsg");
		if(cancelMsg != null && cancelMsg.trim().length() > 0) {
			
			Messagebox.show(cancelMsg, "Prompt", 
					Messagebox.YES|Messagebox.NO, 
					Messagebox.QUESTION, 
			new EventListener<Event>() {
				
				@Override
				public void onEvent(Event event) throws Exception {
					switch(((Integer)event.getData()).intValue()) {
					case Messagebox.YES:
						EditorManager.getInstance().closeCurrentEditorPanel();
						break;
					case Messagebox.NO:
						break;
					}
				}
			});				
		} else EditorManager.getInstance().closeCurrentEditorPanel();
	}
	
}

