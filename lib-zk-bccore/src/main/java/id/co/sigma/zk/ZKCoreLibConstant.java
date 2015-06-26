package id.co.sigma.zk;

/**
 * constant untuk ZK
 * @author <a href='mailto:gede.sutarsa@gmail.com'>Gede Sutarsa</a>
 */
public final class ZKCoreLibConstant  {
	
	
	/**
	 * key untuk passing attribute data yang di edit
	 */
	public static final String EDITED_DATA_ATTRIBUTE_KEY="editedData";
	
	
	
	/**
	 * key untuk passing data tambahan ke controller
	 */
	public static final String ADDITIONAL_DATA_ATTRIBUTE_KEY="additionalData";
	
	/**
	 * key untuk passing component tambahan ke controller
	 */
	public static final String COMPONENT_VALUE_HOLDER_KEY="componentValueHolder";
	
	/**
	 * key untuk passing filter list lookup
	 */
	public static final String LOOKUP_LIST_FILTERS="lookupListFilters";
	
	/**
	 * key untuk passing sort list lookup
	 */
	public static final String LOOKUP_LIST_SORTS="lookupListSorts";
	
	/**
	 * container data.key
	 */
	public static final String EDITED_DATA_CLIENT_CONTAINER_KEY="editedDataContainer";
	
	
	/**
	 * state dari editor
	 */
	public static final String EDITOR_STATE_ATTRIBUTE_KEY="editorState";
	
	
	
	
	/**
	 * caller dari component. siapa yang memanggil component. incase perlu proses reload dsb
	 */
	public static final String EDITOR_CALLER_COMPONENT="editorCaller";
	
	
	/**
	 * path sebelumnya
	 */
	public static final String  PREV_PAGE = "FROM_PAGE"; 
	
	
	
	/**
	 * reference dari object dalam menu item
	 */
	public static final String MENUITEM_OBJECT_REF_KEY="menObjectRef";
	
	
	
	
	/**
	 * key untuk menaruh key pada saat item di pilih.
	 * untuk lookup dialog
	 */
	public static final String AFTER_SELECTION_HANDLER ="onLookupDoneHandler"; 

}

