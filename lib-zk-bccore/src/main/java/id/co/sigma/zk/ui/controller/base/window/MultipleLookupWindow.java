package id.co.sigma.zk.ui.controller.base.window;

import id.co.sigma.zk.ui.controller.base.BaseSimpleController;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Panelchildren;
import org.zkoss.zul.Window;


/**
 * lookup untuk multiple selection
 * @author <a href="mailto:raka.sanjaya@sigma.co.id">Raka Sanjaya</a>
 */
public class MultipleLookupWindow extends Window implements AfterCompose {
    /**
     * 
     */
    private static final long serialVersionUID = -2496877906904431145L;
    static final org.slf4j.Logger logger = org.slf4j.LoggerFactory
	    .getLogger(MultipleLookupWindow.class.getName());

    @Wire
    private Panelchildren panelLookup;


    BaseSimpleController controller;

    /**
     * jumlah child component window template 
     */
    private int childrenCount = 4;

    public MultipleLookupWindow() {
	Executions.createComponents("~./zul/pages/common/MultipleLookupWindow.zul", this, null);
	Selectors.wireComponents(this, this, false);
	Selectors.wireVariables(this, this, null);
	Selectors.wireEventListeners(this, this);
	childrenCount = this.getChildren().size(); 
    }

    @Override
    public void afterCompose() {
	try {
	    controller = (BaseSimpleController)getAttribute(getId() + "$composer");
	} catch (Exception e) {
	    logger.error("gagal membaca controller class : " + getClass().getName() +", error : " + e.getMessage()   , e);
	}
	List<Component> children = getChildren();
	int dynaChildren = children.size() - childrenCount;
	for(int i = 0; i < dynaChildren; i++) {
	    Component child = children.get(childrenCount);
	    panelLookup.appendChild(child);
	}
    }
}
