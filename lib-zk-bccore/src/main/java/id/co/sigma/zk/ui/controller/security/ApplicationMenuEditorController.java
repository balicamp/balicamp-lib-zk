/**
 * 
 */
package id.co.sigma.zk.ui.controller.security;

import id.co.sigma.common.data.query.SimpleQueryFilter;
import id.co.sigma.common.data.query.SimpleQueryFilterOperator;
import id.co.sigma.common.data.query.SimpleSortArgument;
import id.co.sigma.common.security.domain.ApplicationMenu;
import id.co.sigma.common.security.domain.PageDefinition;
import id.co.sigma.zk.ui.controller.ZKEditorState;
import id.co.sigma.zk.ui.controller.base.BaseSimpleDirectToDBEditor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Longbox;

/**
 * 
 * @author <a href="mailto:raka.sanjaya@sigma.co.id">Raka Sanjaya</a>
 */
public class ApplicationMenuEditorController extends
		BaseSimpleDirectToDBEditor<ApplicationMenu> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5392589856238025302L;
	static final org.slf4j.Logger logger = org.slf4j.LoggerFactory
			.getLogger(ApplicationMenuEditorController.class.getName());
	
	private static final SimpleSortArgument[] DEF_SORTS ={
		new SimpleSortArgument("id", true)
		}; 
	
	
	@Autowired
	@Qualifier(value="securityApplicationId")
	String applicationId ;
	
	ListModelList<PageDefinition> listModel;
	
	ListModelList<ApplicationMenu> listModelParent;
	
	
	
	@Wire
	Listbox list;
	
	@Wire
	Listbox listParent;
	
	@Wire
	Bandbox urlBox;
	
	@Wire
	Bandbox parentBox;
	
	@Wire
	Longbox pageId;
	
	@Wire
	Longbox functionIdParent;
	
	@Wire
	Intbox treeLevelPosition;


	@Override
	public void insertData() throws Exception {
		Long appId = new Long(applicationId);
		getEditedData().setApplicationId(appId);
		getEditedData().setStatus("A");
		if(getEditedData().getPageId()==0 || urlBox.getValue().equals("") || urlBox.getValue()==null){
			getEditedData().setPageId(null);
		}
		if(getEditedData().getFunctionIdParent()==0 || parentBox.getValue().equals("") || parentBox.getValue()==null){
			getEditedData().setFunctionIdParent(null);
		}
		if(getAdditionalData()!=null){
			Long parentId = getAdditionalData().getId();
			getEditedData().setFunctionIdParent(parentId);
			getEditedData().setTreeLevelPosition(getAdditionalData().getTreeLevelPosition()+1);
			SimpleQueryFilter[] filters = new SimpleQueryFilter[]{
					new SimpleQueryFilter("functionIdParent", SimpleQueryFilterOperator.equal, parentId)
			};
			Long swap = generalPurposeDao.count(ApplicationMenu.class, filters);
			if(swap!=null){
				getEditedData().setSiblingOrder(swap.intValue()+1);
			}
		}else{
			if(getEditedData().getFunctionIdParent()!=null){
				Long idParent = getEditedData().getFunctionIdParent();
				SimpleQueryFilter[] filters = new SimpleQueryFilter[]{
						new SimpleQueryFilter("functionIdParent", SimpleQueryFilterOperator.equal, idParent)
				};
				Long swap = generalPurposeDao.count(ApplicationMenu.class, filters);
				if(swap!=null){
					getEditedData().setSiblingOrder(swap.intValue()+1);
				}
			}else{
				getEditedData().setTreeLevelPosition(1);
				SimpleQueryFilter[] filters = new SimpleQueryFilter[]{
						new SimpleQueryFilter("treeLevelPosition", SimpleQueryFilterOperator.equal, 1)
				};
				Long swap = generalPurposeDao.count(ApplicationMenu.class, filters);
				if(swap!=null){
					getEditedData().setSiblingOrder(swap.intValue()+1);
				}
			}
		}		
		super.insertData();
		if(getEditedData().getTreeLevelPosition()==1){
			getEditedData().setMenuTreeCode(String.valueOf(getEditedData().getId()));
			super.updateData();
		}else{
			Long parentId = getEditedData().getFunctionIdParent();
			ApplicationMenu app = generalPurposeDao.get(ApplicationMenu.class, parentId);
			if(app!=null){
				String menuTreeCode = app.getMenuTreeCode()+"."+String.valueOf(getEditedData().getId());
				getEditedData().setMenuTreeCode(menuTreeCode);
				super.updateData();
			}
		}
	}

	
	
	@Override
	public void updateData() throws Exception {
		
		if(getEditedData().getPageId()==0 || urlBox.getValue().equals("") || urlBox.getValue()==null){
			getEditedData().setPageId(null);
		}
		if(getEditedData().getFunctionIdParent()==0 || parentBox.getValue().equals("") || parentBox.getValue()==null){
			getEditedData().setFunctionIdParent(null);
		}
		if(getEditedData().getFunctionIdParent()!=null){
			Long idParent = getEditedData().getFunctionIdParent();
			SimpleQueryFilter[] filters = new SimpleQueryFilter[]{
					new SimpleQueryFilter("functionIdParent", SimpleQueryFilterOperator.equal, idParent)
			};
			Long swap = generalPurposeDao.count(ApplicationMenu.class, filters);
			if(swap!=null){
				getEditedData().setSiblingOrder(swap.intValue()+1);
			}
			ApplicationMenu app = generalPurposeDao.get(ApplicationMenu.class, idParent);
			if(app!=null){
				getEditedData().setMenuTreeCode(app.getMenuTreeCode()+"."+getEditedData().getId());
			}
		}else{
			getEditedData().setTreeLevelPosition(1);
			getEditedData().setMenuTreeCode(String.valueOf(getEditedData().getId()));
			SimpleQueryFilter[] filters = new SimpleQueryFilter[]{
					new SimpleQueryFilter("treeLevelPosition", SimpleQueryFilterOperator.equal, 1)
			};
			Long swap = generalPurposeDao.count(ApplicationMenu.class, filters);
			if(swap!=null){
				getEditedData().setSiblingOrder(swap.intValue()+1);
			}
		}	
		super.updateData();
	}



	/**
	 * untuk mendapatkan menu label dari parent ketika proses edit
	 * @return
	 */
	public String getLabelParent(){
		if(ZKEditorState.EDIT.equals(getEditorState())){
			Long parentId = getEditedData().getFunctionIdParent();
			if(parentId!=null){
				try {
					ApplicationMenu parent = generalPurposeDao.get(ApplicationMenu.class, parentId);
					if(parent!=null){
						return parent.getFunctionLabel();
					}
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("gagal membaca menu untuk app id : " + applicationId + " , error : " + e.getMessage() , e);
				}
			}else{
				return "";
			}
		}
		return "";
	}
	
	protected List<PageDefinition> getPages () {
		Long appId = new Long(applicationId);
		SimpleQueryFilter[] flt = new SimpleQueryFilter[]{
				new SimpleQueryFilter("applicationId" , SimpleQueryFilterOperator.equal , appId)
		};
		try {
			return generalPurposeDao.list(PageDefinition.class, flt,DEF_SORTS);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("gagal membaca menu untuk app id : " + applicationId + " , error : " + e.getMessage() , e);
			return null ;
		}
	}
	
	public ListModelList<ApplicationMenu> getListParent(){
		return new ListModelList<>(getListMenu());
	}
	
	
	protected List<ApplicationMenu> getListMenu () {
		Long appId = new Long(applicationId);
		SimpleQueryFilter[] flt = new SimpleQueryFilter[]{
				new SimpleQueryFilter("applicationId" , SimpleQueryFilterOperator.equal , appId)
		};
		try {
			//return generalPurposeDao.list(ApplicationMenu.class, flt,DEF_SORTS);
			List l = generalPurposeDao.list( ApplicationMenu.class.getName() +" A left outer join fetch A.application", "A", flt, DEF_SORTS,10000,0);
			return (List<ApplicationMenu>)l;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("gagal membaca menu untuk app id : " + applicationId + " , error : " + e.getMessage() , e);
			return null ;
		}
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		listModel = new ListModelList<>(getPages());
		//listModelParent = new ListModelList<>(getListMenu());
		list.setModel(listModel);
		//listParent.setModel(listModelParent);
	}
	
	@Listen("onSelect=#list")
	public void onItemSelect(){
		PageDefinition selectedItem = list.getSelectedItem().getValue();
		if(selectedItem!=null){
			pageId.setValue(selectedItem.getId());
			urlBox.setValue(selectedItem.getPageUrl());
			urlBox.close();
		}
	}
	
	@Listen("onSelect=#listParent")
	public void onParentSelect(){
		ApplicationMenu selectedItem = listParent.getSelectedItem().getValue();
		if(selectedItem!=null){
			functionIdParent.setValue(selectedItem.getId());
			treeLevelPosition.setValue(selectedItem.getTreeLevelPosition()+1);
			parentBox.setValue(selectedItem.getFunctionLabel());
			parentBox.close();
		}
	}

}
