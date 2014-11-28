package id.co.sigma.zk.service;

/**
 *
 * @author <a href='mailto:gede.sutarsa@gmail.com'>Gede Sutarsa</a>
 */
public interface SubPanelZullProvider {
	
	
	
	public String getSubPanelZullPath (Class<?> parentComposer )   ; 

	
	
	public void registerSubPanel (Class<?> parentComposer , String zulPath  ) ; 

}
