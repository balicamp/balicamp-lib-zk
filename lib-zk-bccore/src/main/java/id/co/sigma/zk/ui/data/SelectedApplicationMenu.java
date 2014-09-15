package id.co.sigma.zk.ui.data;

import id.co.sigma.common.security.domain.ApplicationMenu;
import javax.persistence.Transient;

/**
 * Selected Application Menu
 * @author <a href="mailto:ida.suartama@sigma.co.id">Goesde Rai</a>
 */

public class SelectedApplicationMenu extends ApplicationMenu{
	private static final long serialVersionUID = 2132390938444583319L;
	
	static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(SelectedApplicationMenu.class.getName());
	
	
	public SelectedApplicationMenu() {
		super() ; 
	}
	
	public SelectedApplicationMenu (ApplicationMenu  baseData ) {
		super() ;
		
		setApplication(baseData.getApplication());
		setApplicationId(baseData.getApplicationId()); 
		setFunctionCode(baseData.getFunctionCode());
		setFunctionIdParent(baseData.getFunctionIdParent());
		setFunctionLabel(baseData.getFunctionLabel());
		setId(baseData.getId());
		setMenuTreeCode(baseData.getMenuTreeCode());
		setPageDefinition(baseData.getPageDefinition());
		if(baseData.getPageDefinition()!=null){
			setPageUrlStr(baseData.getPageDefinition().getPageUrl());
		}else{
			setPageUrlStr("-");
		}
		setPageId(baseData.getPageId());
		setSiblingOrder(baseData.getSiblingOrder());
		setStatus(baseData.getStatus());
		setTreeLevelPosition(baseData.getTreeLevelPosition());
	}
	
	@Transient
	private boolean selected;
	
	@Transient
	private String pageUrlStr;

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	public String getPageUrlStr() {
		return pageUrlStr;
	}
	
	public void setPageUrlStr(String pageUrlStr) {
		this.pageUrlStr = pageUrlStr;
	}

}
