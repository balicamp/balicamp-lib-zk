package id.co.sigma.lab.zk.controller;

import id.co.sigma.common.data.CustomDataFormatter;
import id.co.sigma.common.data.lov.CommonLOV;

/**
 * 
 * @author <a href='mailto:gede.sutarsa@gmail.com'>Gede Sutarsa</a>
 */
public class RendererKabupaten implements  CustomDataFormatter<CommonLOV>{

	@Override
	public String getFormattedData(CommonLOV data) {
		return data.getDataValue() + "-" + data.getLabel();
	}

	@Override
	public String getStringForValue(CommonLOV data) {
		return data.getDataValue();
	}

}
