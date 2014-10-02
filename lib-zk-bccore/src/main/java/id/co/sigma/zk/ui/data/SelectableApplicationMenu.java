package id.co.sigma.zk.ui.data;

import id.co.sigma.common.security.domain.ApplicationMenu;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.Transient;
import org.zkoss.json.JSONAware;
import org.zkoss.json.JSONObject;

/**
 * Class turunan dari ApplicationMenu dgn penambahan beberapa field
 * @author <a href="mailto:ida.suartama@sigma.co.id">Goesde Rai</a>
 */
public class SelectableApplicationMenu extends ApplicationMenu implements JSONAware{
	private static final long serialVersionUID = 1L;
	
	static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(SelectableApplicationMenu.class.getName());
	
	@Transient
	private SelectableApplicationMenuState state;
	
	public SelectableApplicationMenu(ApplicationMenu menu, boolean selected) {
		super();
		setId(menu.getId());
		setFunctionIdParent(menu.getFunctionIdParent());
		setFunctionCode(menu.getFunctionCode());
		setFunctionLabel(menu.getFunctionLabel());
		this.state = new SelectableApplicationMenuState(false, false, selected);
	}
	
	public String toJSONString(){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", getId());
		map.put("parent", (getFunctionIdParent()==null)? "#" : getFunctionIdParent());
		map.put("text", "["+getFunctionCode()+"]-"+getFunctionLabel());
		map.put("state", getState());
		return JSONObject.toJSONString(map);
	}
	
	public SelectableApplicationMenuState getState() {
		return state;
	}

	public void setState(SelectableApplicationMenuState state) {
		this.state = state;
	}	
	
}
