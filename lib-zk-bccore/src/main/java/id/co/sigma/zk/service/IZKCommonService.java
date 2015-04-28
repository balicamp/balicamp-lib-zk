package id.co.sigma.zk.service;


import java.math.BigInteger;

import id.co.sigma.common.data.query.SimpleQueryFilter;
import id.co.sigma.common.data.query.SimpleSortArgument;
import id.co.sigma.common.server.service.IBaseService;
import id.co.sigma.zk.ui.data.ZKClientSideListDataEditorContainer;

/**
 * service umum untuk spesifik ke ZK
 * @author <a href="mailto:gede.sutarsa@gmail.com">Gede Sutarsa</a>
 */
public interface IZKCommonService extends IBaseService{
	
	
	
	/**
	 * membaca data untuk in memory edit.data di ambil dengan primary key parent. tested untuk : Long, String, BigInteger, Integer  . tidak di rekomendasikan untuk reference ke parent yang bertipe composite
	 * @param dataClass class dari data
	 * @param parentPrimaryKey primary key dari parent
	 * @param parentPKField nama field JPA dari field yang di ambil
	 * 
	 */
	public<DATA> ZKClientSideListDataEditorContainer<DATA> getDataDetails ( Class<DATA> dataClass , String parentPrimaryKey, SimpleSortArgument[] sorts , String parentPKField ) ;
	
	public<DATA> ZKClientSideListDataEditorContainer<DATA> getDataDetails ( Class<DATA> dataClass , Long parentPrimaryKey, SimpleSortArgument[] sorts , String parentPKField ) ;
	
	public<DATA> ZKClientSideListDataEditorContainer<DATA> getDataDetails ( Class<DATA> dataClass , BigInteger parentPrimaryKey, SimpleSortArgument[] sorts , String parentPKField ) ;
	
	public<DATA> ZKClientSideListDataEditorContainer<DATA> getDataDetails ( Class<DATA> dataClass , Integer parentPrimaryKey, SimpleSortArgument[] sorts , String parentPKField ) ;
	
	public<DATA> ZKClientSideListDataEditorContainer<DATA> getDataDetailsWithCustomTableJoinHQL ( String customTableJoinHQL , String tableAliasName ,Long parentPrimaryKey, SimpleSortArgument[] sorts , String parentPKField ) ;
	
	public<DATA> ZKClientSideListDataEditorContainer<DATA> getDataDetailsWithAdditionalFilters ( Class<DATA> dataClass , Long parentPrimaryKey, SimpleSortArgument[] sorts, SimpleQueryFilter[] filters, String parentPKField ) ;
	

}
