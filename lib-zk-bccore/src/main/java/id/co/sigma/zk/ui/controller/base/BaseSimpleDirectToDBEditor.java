
package id.co.sigma.zk.ui.controller.base;

import id.co.sigma.common.server.dao.IGeneralPurposeDao;
import id.co.sigma.common.server.service.IGeneralPurposeService;
import id.co.sigma.zk.ui.controller.EditorManager;
import id.co.sigma.zk.ui.controller.ZKEditorState;
import id.co.sigma.zk.ui.data.ZKClientSideListDataEditorContainer;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.zkoss.util.resource.Labels;
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
	protected void insertData(Object... data) throws Exception {
		if(data != null && data.length > 0){
			data[0] = generalPurposeService.merge((Serializable)data[0]);
		}
		
	}
	
	@Override
	protected void updateData(POJO data) throws Exception {
		generalPurposeService.update(data);
	}
	
	@Listen("onClick = #btnSave")
	public void saveClick(final Event evt) {

		//trigger validasi input data sebelum message konfirmasi		
		parseEditedData(evt.getTarget());
		try {
			bindValueFromControl(getEditedData());
		} catch (Exception e) {
			if ( ZKEditorState.ADD_NEW.equals(getEditorState())) {
				Messagebox.show(Labels.getLabel("msg.save.edit.fail"), 
						Labels.getLabel("title.msgbox.error"),
						new Messagebox.Button[]{Messagebox.Button.OK},
						new String[]{Labels.getLabel("action.button.ok")},
						Messagebox.ERROR,
						Messagebox.Button.OK, null);
			} else {
				Messagebox.show(Labels.getLabel("msg.save.edit.fail"), 
						Labels.getLabel("title.msgbox.error"),
						new Messagebox.Button[]{Messagebox.Button.OK},
						new String[]{Labels.getLabel("action.button.ok")},
						Messagebox.ERROR,
						Messagebox.Button.OK, null);
			}
			return  ; 
		}
		//-->end
		
		String confirmMsg = (String)getSelf().getAttribute("confirmationMsg");
		if(confirmMsg != null && confirmMsg.trim().length() > 0) {
			
			Messagebox.show(confirmMsg, Labels.getLabel("title.msgbox.confirmation"),
					new Messagebox.Button[]{Messagebox.Button.YES, Messagebox.Button.NO},
					new String[]{Labels.getLabel("action.button.yes"), Labels.getLabel("action.button.no")},
					Messagebox.QUESTION,
					Messagebox.Button.YES,
					new EventListener<Messagebox.ClickEvent>() {
				
				@Override
				public void onEvent(Messagebox.ClickEvent event) throws Exception {
					if(Messagebox.Button.YES.equals(event.getButton())) {
						saveData(evt);
					}
				}
			});				
		} else saveData(evt); 
	}
	
	private final void saveData(final Event event) {
//		parseEditedData(event.getTarget());
//		try {
//			bindValueFromControl(getEditedData());
//		} catch (Exception e) {
//			 Messagebox.show("Gagal Bind data. error : " + e.getMessage(), "Gagal Bind Data", Messagebox.OK, Messagebox.ERROR);
//			return  ; 
//		}
		
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
						
						Messagebox.show(Labels.getLabel("msg.save.add.success"), 
								Labels.getLabel("title.msgbox.information"),
								new Messagebox.Button[]{Messagebox.Button.OK},
								new String[]{Labels.getLabel("action.button.ok")},
								Messagebox.INFORMATION,
								Messagebox.Button.OK, null);
						
					} catch (Exception e) {
						saveCommit = false ; 
						logger.error( "" + e.getMessage() , e);

						Messagebox.show(Labels.getLabel("msg.save.add.fail"), 
								Labels.getLabel("title.msgbox.error"),
								new Messagebox.Button[]{Messagebox.Button.OK},
								new String[]{Labels.getLabel("action.button.ok")},
								Messagebox.ERROR,
								Messagebox.Button.OK, null);
					}
				}else {
					try {
						
						updateData();
						
						Messagebox.show(Labels.getLabel("msg.save.edit.success"), 
								Labels.getLabel("title.msgbox.information"),
								new Messagebox.Button[]{Messagebox.Button.OK},
								new String[]{Labels.getLabel("action.button.ok")},
								Messagebox.INFORMATION,
								Messagebox.Button.OK, null);
						
					} catch (Exception e) {
						saveCommit = false ; 
						logger.error("gagal update file. error : " + e.getMessage() , e);

						Messagebox.show(Labels.getLabel("msg.save.edit.fail"), 
								Labels.getLabel("title.msgbox.error"),
								new Messagebox.Button[]{Messagebox.Button.OK},
								new String[]{Labels.getLabel("action.button.ok")},
								Messagebox.ERROR,
								Messagebox.Button.OK, null);
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
			
			Messagebox.show(cancelMsg, Labels.getLabel("title.msgbox.confirmation"),
					new Messagebox.Button[]{Messagebox.Button.YES, Messagebox.Button.NO},
					new String[]{Labels.getLabel("action.button.yes"), Labels.getLabel("action.button.no")},
					Messagebox.QUESTION,
					Messagebox.Button.YES,
					new EventListener<Messagebox.ClickEvent>() {
				
				@Override
				public void onEvent(Messagebox.ClickEvent event) throws Exception {
					if(Messagebox.Button.YES.equals(event.getButton())) {
						EditorManager.getInstance().closeCurrentEditorPanel();
					}
				}
			});				
		} else EditorManager.getInstance().closeCurrentEditorPanel();
	}

	/**
	 * delete data dari child/detail container
	 * @param data
	 */
	public void deleteChildData(Object data, ZKClientSideListDataEditorContainer<Object> container) {
		container.eraseData(data);
	}
}

