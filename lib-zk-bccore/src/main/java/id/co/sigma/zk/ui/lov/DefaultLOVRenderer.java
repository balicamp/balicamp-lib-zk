package id.co.sigma.zk.ui.lov;

import id.co.sigma.common.data.CustomDataFormatter;
import id.co.sigma.common.data.lov.CommonLOV;

/**
 * 
 * @author <a href='mailto:gede.sutarsa@gmail.com'>Gede Sutarsa</a>
 */
public class DefaultLOVRenderer implements CustomDataFormatter<CommonLOV>{

	@Override
	public String getFormattedData(CommonLOV data) {
		return data.getLabel(); 
	}

	@Override
	public String getStringForValue(CommonLOV data) {
		return data.getDataValue();
	}
	
	

}
