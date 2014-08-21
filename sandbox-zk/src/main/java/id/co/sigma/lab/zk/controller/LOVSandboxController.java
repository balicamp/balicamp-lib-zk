package id.co.sigma.lab.zk.controller;


import java.util.ArrayList;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.AbstractListModel;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;

import id.co.sigma.common.data.lov.CommonLOV;
import id.co.sigma.zk.ui.annotations.LookupEnabledControl;
import id.co.sigma.zk.ui.controller.base.BaseSimpleController;
import id.co.sigma.zk.ui.lov.CommonLOVWithRenderer;
import id.co.sigma.zk.ui.lov.DefaultLOVRenderer;

/**
 * 
 * @author <a href='mailto:gede.sutarsa@gmail.com'>Gede Sutarsa</a>
 */
public class LOVSandboxController extends BaseSimpleController{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8737390698955598654L;
	@LookupEnabledControl(parameterId="JENIS_KELAMIN")
	@Wire
	Combobox comboBox ; 
	
	@Wire
	Combobox  manualComboBox  ;
	
	@LookupEnabledControl(parameterId="KABUPATEN" , lovDataRenderer=RendererKabupaten.class)
	@Wire
	Combobox  kabupatenComboBox; 
	
	
	@LookupEnabledControl(parameterId="SANDBOX.DEPT" )
	@Wire
	Combobox  customDSComboBox; 
	
	@Wire
	Bandbox bandboxLOV; 
	 
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		CommonLOVWithRenderer l1 = new CommonLOVWithRenderer();
		l1.setDataValue("1");
		l1.setLabel("Label 1");
		
		CommonLOVWithRenderer l2 = new CommonLOVWithRenderer();
		l2.setDataValue("2");
		l2.setLabel("Label 2");
		
		
		ArrayList<CommonLOVWithRenderer> s = new ArrayList<CommonLOVWithRenderer>() ; 
		s.add(l1);
		s.add(l2);
		DefaultLOVRenderer r = new DefaultLOVRenderer(); 
		
		l1.setRenderer(r);
		l2.setRenderer(r);
		
		ListModelList<CommonLOVWithRenderer> ms = new ListModelList<CommonLOVWithRenderer>(s); 
		manualComboBox.setModel(ms);
		
		
		
	}
	
}
