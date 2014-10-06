package id.co.sigma.zk.ui.data;

import id.co.sigma.common.security.domain.UserGroup;

import javax.persistence.Transient;

/**
 * 
 * @author <a href="mailto:gusti.darmawan@sigma.co.id">Eka Darmawan</a>
 */
@SuppressWarnings("serial")
public class SelectedUserGroup extends UserGroup {
	static final org.slf4j.Logger logger = org.slf4j.LoggerFactory
			.getLogger(SelectedUserGroup.class.getName());
	
	public SelectedUserGroup() {
		super();
	}
	
	public SelectedUserGroup(UserGroup baseData) {
		super();
		setId(baseData.getId());
		setCreatedBy(baseData.getCreatedBy());
		setCreatedOn(baseData.getCreatedOn());
		setCreatorIPAddress(baseData.getCreatorIPAddress());
		setActiveFlag(baseData.getActiveFlag());
		setApplicationId(baseData.getApplicationId());
		setGroupCode(baseData.getGroupCode());
		setGroupName(baseData.getGroupName());
		
	}
	
	
	@Transient
	private boolean selected = false;

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
}
