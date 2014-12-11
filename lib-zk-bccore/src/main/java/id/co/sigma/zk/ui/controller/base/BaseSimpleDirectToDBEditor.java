package id.co.sigma.zk.ui.controller.base;

import id.co.sigma.common.server.dao.IGeneralPurposeDao;
import id.co.sigma.common.server.service.IGeneralPurposeService;
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
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.annotation.Listen;

/**
 * 
 * @author <a href='mailto:gede.sutarsa@gmail.com'>Gede Sutarsa</a>
 */
public abstract class BaseSimpleDirectToDBEditor<POJO extends Serializable>
extends BaseSimpleEditor<POJO> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5283385493029490963L;

	private static final Logger logger = LoggerFactory
			.getLogger(BaseSimpleDirectToDBEditor.class);

	@Autowired
	protected IGeneralPurposeService generalPurposeService;

	@Autowired
	protected IGeneralPurposeDao generalPurposeDao;

	@Autowired
	@Qualifier(value = "transactionManager")
	protected PlatformTransactionManager transactionManager;

	@Override
	protected void insertData(Object... data) throws Exception {
		if (data != null && data.length > 0) {
			data[0] = generalPurposeService.merge((Serializable) data[0]);
		}

	}

	@Override
	protected void updateData(POJO data) throws Exception {
		generalPurposeService.update(data);
	}

	@Listen("onClick = #btnSave")
	public void saveClick(final Event evt) {

		// trigger validasi input data sebelum message konfirmasi
		parseEditedData(evt.getTarget());
		try {
			bindValueFromControl(getEditedData());
			children = parseChildGridData();
		} catch (Exception e) {
			logger.error("gagal simpam data. error : " + e.getMessage(), e);
			showErrorMessage(getEditorState(), e.getMessage());
			return;
		}
		// -->end

		try {
		    bindValueFromControl(getEditedData());
		    children = parseChildGridData();
		} catch(WrongValueException | WrongValuesException e) {
			throw e; // pass error to zul
		} catch (Exception e) {
		    logger.error("gagal simpam data. error : " + e.getMessage(), e);
		    showErrorMessage(getEditorState(), e.getMessage());
		    return;
		}

	}

	protected void validateData() throws Exception {

	}

	@Override
	protected void saveData(final Event event) {

		TransactionTemplate tmpl = new TransactionTemplate(
				this.transactionManager);

		try {

			tmpl.execute(new TransactionCallback<Integer>() {
				@Override
				public Integer doInTransaction(TransactionStatus stts) {

					Object obj = null;
					try {
						obj = stts.createSavepoint();
					} catch (Exception e) {
						logger.warn(e.getMessage());
					}

					try {
						if (ZKEditorState.ADD_NEW.equals(getEditorState())) {
							insertData();
						} else {
							updateData();
						}

						if (obj != null) {
							stts.releaseSavepoint(obj);
						}

					} catch (Exception e) {
						logger.error(e.getMessage(), e);
						if (obj != null) {
							stts.rollbackToSavepoint(obj);
						} else {
							stts.setRollbackOnly();
						}
						throw new RuntimeException(e);
					}

					return 1;
				}
			});

			showSuccesMessage(getEditorState());
			closeCurrentEditorPanel();

		} catch (Exception e) {
			logger.error("gagal update file. error : " + e.getMessage(), e);
			showErrorMessage(getEditorState(), e.getMessage());
		}
	}

	@Listen("onClick = #btnCancel")
	public void cancelClick() {
		String cancelMsg = (String) getSelf().getAttribute("cancellationMsg");
		showCancelConfirmationMessage(cancelMsg);
	}

	/**
	 * delete data dari child/detail container
	 * 
	 * @param data
	 */
	public void deleteChildData(Object data,
			ZKClientSideListDataEditorContainer<Object> container) {
		container.eraseData(data);
	}
}
