package id.co.sigma.zk.ui.data;

import com.google.gson.Gson;

import id.co.sigma.common.data.app.SimpleDualControlData;
import id.co.sigma.common.util.json.ParsedJSONContainer;

/**
 * 
 * @author <a href='mailto:gede.sutarsa@gmail.com'>Gede Sutarsa</a>
 */
public abstract class BaseServerOnlySimpleDualControlData<POJO extends BaseServerOnlySimpleDualControlData<?>> extends SimpleDualControlData<POJO>{

	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5523875265411355634L;

	@Override
	protected final void extractDataFromJSON(POJO targetObject,
			ParsedJSONContainer jsonContainer) {
		
		
	}
	
	@Override
	public final void translateToJSON(ParsedJSONContainer jsonContainerData) {
		Gson gson = new Gson() ; 
		String jsonString =  gson.toJson(this);
		jsonContainerData.injectJSONValue(jsonString);
	}
}
