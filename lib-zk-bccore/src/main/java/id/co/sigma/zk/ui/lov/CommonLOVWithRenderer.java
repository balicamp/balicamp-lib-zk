package id.co.sigma.zk.ui.lov;

import id.co.sigma.common.data.CustomDataFormatter;
import id.co.sigma.common.data.lov.CommonLOV;

/**
 * 
 * @author <a href='mailto:gede.sutarsa@gmail.com'>Gede Sutarsa</a>
 */
public class CommonLOVWithRenderer extends CommonLOV{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6196595756971078276L;
	CustomDataFormatter<CommonLOV> renderer ; 
	
	
	public CommonLOVWithRenderer() {
		super(); 
	}
	
	public CommonLOVWithRenderer(CommonLOV baseData  ,CustomDataFormatter<CommonLOV> renderer  ) {
		super() ; 
		this.renderer = renderer; 
		this.additionalData1 = baseData.getAdditionalData1() ; 
		this.additionalData2 = baseData.getAdditionalData2() ; 
		this.dataValue = baseData.getDataValue() ; 
		this.parentId = baseData.getParentId() ; 
		this.label = baseData.getLabel(); 
		
	}
	
	public CustomDataFormatter<CommonLOV> getRenderer() {
		return renderer;
	}
	
	public void setRenderer(CustomDataFormatter<CommonLOV> renderer) {
		this.renderer = renderer;
	}
	
	@Override
	public String toString() {
		if ( renderer == null)
			return label ; 
		return renderer.getFormattedData(this); 
	}

}
