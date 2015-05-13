package id.co.sigma.zk.ui.controller.base;

import id.co.sigma.common.data.SingleKeyEntityData;
import id.co.sigma.common.data.query.SimpleQueryFilter;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Map;

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
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Timer;

/**
 * 
 * 
 * @author <a href='mailto:gede.sutarsa@gmail.com'>Gede Sutarsa</a>
 */
public abstract class BaseSimpleListController<DATA extends Serializable> extends BaseHaveListboxController<DATA> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1981062461544583336L;

	private Logger logger = LoggerFactory.getLogger(BaseSimpleListController.class);

	@Autowired
	@Qualifier(value = "transactionManager")
	protected PlatformTransactionManager transactionManager;

	protected Map<Class<?>, SimpleQueryFilter[]> getReferenceEntities(Object parentId, String... errMessage) {
		return null;
	}

	private void checkDataInUse(Object parentId) {
		String[] errMessage = new String[1];
		Map<Class<?>, SimpleQueryFilter[]> map = getReferenceEntities(parentId, errMessage);
		if (map != null && !map.isEmpty()) {
			Class<?>[] classes = map.keySet().toArray(new Class[map.size()]);
			Long count = 0L;
			for (Class<?> clazz : classes) {
				SimpleQueryFilter[] filters = map.get(clazz);
				count = count + generalPurposeDao.count(clazz, filters);
			}
			if (count != null && count > 0) {
				String errMsg = "Error entity is in use";
				if (errMessage != null && errMessage.length == 1) {
					errMsg = errMessage[0];
				}
				throw new RuntimeException(errMsg);
			}
		}
	}

	/**
	 * delete data
	 * 
	 * @param data
	 * @param pk
	 * @param pkFieldName
	 */
	protected void deleteData(final DATA data, final Serializable pk, final String pkFieldName) {

		TransactionTemplate tmpl = new TransactionTemplate(this.transactionManager);

		try {

			checkDataInUse(pk);

			tmpl.execute(new TransactionCallback<Integer>() {

				@Override
				public Integer doInTransaction(TransactionStatus status) {
					Object obj = null;
					try {
						obj = status.createSavepoint();
					} catch (Exception e) {
						logger.warn(e.getMessage());
					}

					try {
						Map<String, Class<?>> children = getChildrenParentKeyAndEntiy();
						if (children != null && !children.isEmpty()) {
							String[] prntKeys = children.keySet().toArray(new String[children.keySet().size()]);
							for (String pKey : prntKeys) {
								Class<?> clazz = children.get(pKey);
								generalPurposeService.delete(clazz, pk, pKey);
							}
						}

						generalPurposeService.delete(data.getClass(), pk, pkFieldName);

						if (obj != null) {
							status.releaseSavepoint(obj);
						}

					} catch (Exception e) {
						logger.error(e.getMessage(), e);
						if (obj != null) {
							status.rollbackToSavepoint(obj);
						} else {
							status.setRollbackOnly();
						}
						throw e;
					}

					return 1;
				}
			});

			Messagebox.show(Labels.getLabel("msg.save.delete.success"), Labels.getLabel("title.msgbox.information"),
					new Messagebox.Button[] { Messagebox.Button.OK },
					new String[] { Labels.getLabel("action.button.ok") }, Messagebox.INFORMATION, Messagebox.Button.OK,
					null);

			refreshList();

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			if (e instanceof javax.persistence.PersistenceException) {
				Messagebox.show(Labels.getLabel("msg.save.delete.fail") + ".\n\n "
						+ "Data tidak bisa dihapus, karena masih dipakai di transaksi lain",
						Labels.getLabel("title.msgbox.error"), new Messagebox.Button[] { Messagebox.Button.OK },
						new String[] { Labels.getLabel("action.button.ok") }, Messagebox.ERROR, Messagebox.Button.OK,
						null);
			} else {
				Messagebox.show(
						Labels.getLabel("msg.save.delete.fail") + ".\n\n " + Labels.getLabel("title.msgbox.error")
						+ " \n" + e.getMessage(), Labels.getLabel("title.msgbox.error"),
						new Messagebox.Button[] { Messagebox.Button.OK },
						new String[] { Labels.getLabel("action.button.ok") }, Messagebox.ERROR, Messagebox.Button.OK,
						null);
			}
		}

	}

	/**
	 * get child/detail data info parent key dan child class
	 * 
	 * @return
	 */
	protected Map<String, Class<?>> getChildrenParentKeyAndEntiy() {
		return null;
	}

	@SuppressWarnings("unchecked")
	public DATA addNewData() {
		try {
			ParameterizedType genericClass = (ParameterizedType) getClass().getGenericSuperclass();
			Class<DATA> clazz = (Class<DATA>) genericClass.getActualTypeArguments()[0];
			return clazz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("rawtypes")
	public void deleteData(DATA data) {
		if (data instanceof SingleKeyEntityData) {
			deleteData(data, (Serializable) ((SingleKeyEntityData) data).getId(), "id");
		} else {
			throw new RuntimeException("Method not supported.");
		}
	}

	public void refreshList() {
		Component timer = getSelf().getFellowIfAny("listTimer");
		if (timer instanceof Timer) {
			((Timer) timer).start();
		}
	}





}
