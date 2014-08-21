package id.co.sigma.zk.ui.lov;

import id.co.sigma.common.data.lov.StrongTypedCustomLOVID;
import id.co.sigma.common.server.lov.GenericCustomLOVProvider;
import id.co.sigma.common.util.json.ParsedJSONContainer;

/**
 * Base LOV custom untuk ZK
 * @author <a href='mailto:gede.sutarsa@gmail.com'>Gede Sutarsa</a>
 */
public abstract class ZKGenericCustomLOVProvider<KEY, DATA> extends GenericCustomLOVProvider<KEY, DATA> {

	@SuppressWarnings("rawtypes")
	@Override
	public StrongTypedCustomLOVID getType() {
		return new StrongTypedCustomLOVID() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 4137718343165545790L;
			@Override
			public String getId() {
				return getParameterId();
			}
			@Override
			public String getModulGroupId() {
				return getModulCode();
			}
			@Override
			public void translateToJSON(ParsedJSONContainer jsonContainer) {
				
			}
			@Override
			public Object instantiateFromJSON(ParsedJSONContainer jsonContainer) {
				return this;
			}
		};
	}
	
	
	/**
	 * kode modul. id parameter = modul.paramterid
	 */
	protected abstract String getModulCode () ; 
	
	
	/**
	 * id paramter
	 */
	protected abstract String getParameterId ( ) ; 
	
	
	
}
