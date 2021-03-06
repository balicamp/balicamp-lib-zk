package id.co.sigma.zk.service.impl;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;

import id.co.sigma.zk.service.IZKCommonService;

import id.co.sigma.zk.ui.data.ZKClientSideListDataEditorContainer;
import id.co.sigma.common.data.query.SimpleQueryFilter;
import id.co.sigma.common.data.query.SimpleQueryFilterOperator;
import id.co.sigma.common.data.query.SimpleSortArgument;
import id.co.sigma.common.server.dao.IGeneralPurposeDao;
import id.co.sigma.common.server.service.AbstractService;

/**
 * 
 * @author <a href="mailto:gede.sutarsa@gmail.com">Gede Sutarsa</a>
 */
public class ZKCommonServiceImpl extends AbstractService implements IZKCommonService  {
	static final org.slf4j.Logger logger = org.slf4j.LoggerFactory
			.getLogger(ZKCommonServiceImpl.class.getName());
	
	
	
	@Autowired
	IGeneralPurposeDao generalPurposeDao ;

	@Override
	public <DATA> ZKClientSideListDataEditorContainer<DATA> getDataDetails(
			Class<DATA> dataClass, Long parentPrimaryKey,
			SimpleSortArgument[] sorts, String parentPKField) {
		SimpleQueryFilter [] flt = new SimpleQueryFilter []{
				new 	SimpleQueryFilter( parentPKField , SimpleQueryFilterOperator.equal , parentPrimaryKey )
		}; 
		try {
			List<DATA> d = generalPurposeDao.list(dataClass, flt ,  sorts);
			return transposeToContainer(d);
		} catch (Exception e) {
			logger.error("gagal membaca data. error di laporkan : " + e.getMessage() , e);
			return null;
		}
	}
	@Override
	public <DATA> ZKClientSideListDataEditorContainer<DATA> getDataDetails(
			Class<DATA> dataClass, Integer parentPrimaryKey,
			SimpleSortArgument[] sorts, String parentPKField) {
		SimpleQueryFilter [] flt = new SimpleQueryFilter []{
				new 	SimpleQueryFilter( parentPKField , SimpleQueryFilterOperator.equal , parentPrimaryKey )
		}; 
		try {
			List<DATA> d = generalPurposeDao.list(dataClass, flt ,  sorts);
			return transposeToContainer(d);
		} catch (Exception e) {
			logger.error("gagal membaca data. error di laporkan : " + e.getMessage() , e);
			return null;
		}
	}

	@Override
	public <DATA> ZKClientSideListDataEditorContainer<DATA> getDataDetails(
			Class<DATA> dataClass, String parentPrimaryKey,
			SimpleSortArgument[] sorts, String parentPKField) {
		SimpleQueryFilter [] flt = new SimpleQueryFilter []{
				new 	SimpleQueryFilter( parentPKField , SimpleQueryFilterOperator.equal , parentPrimaryKey )
		}; 
		try {
			List<DATA> d = generalPurposeDao.list(dataClass, flt ,  sorts);
			return transposeToContainer(d);
		} catch (Exception e) {
			logger.error("gagal membaca data. error di laporkan : " + e.getMessage() , e);
			return null;
		}
	}
	@Override
	public <DATA> ZKClientSideListDataEditorContainer<DATA> getDataDetails(
			Class<DATA> dataClass, BigInteger parentPrimaryKey,
			SimpleSortArgument[] sorts, String parentPKField) {
		SimpleQueryFilter [] flt = new SimpleQueryFilter []{
				new 	SimpleQueryFilter( parentPKField , SimpleQueryFilterOperator.equal , parentPrimaryKey )
		}; 
		try {
			List<DATA> d = generalPurposeDao.list(dataClass, flt ,  sorts);
			return transposeToContainer(d);
		} catch (Exception e) {
			logger.error("gagal membaca data. error di laporkan : " + e.getMessage() , e);
			return null;
		}
	}
	
	
	private<DATA> ZKClientSideListDataEditorContainer<DATA> transposeToContainer (List<DATA> datas ) {
		ZKClientSideListDataEditorContainer<DATA> retval = new ZKClientSideListDataEditorContainer<DATA>(); 
		retval.initiateAndFillData(datas);
		return retval ; 
	}
	@Override
	public <DATA> ZKClientSideListDataEditorContainer<DATA> getDataDetailsWithCustomTableJoinHQL(
			String customTableJoinHQL,String tableAliasName, Long parentPrimaryKey,
			SimpleSortArgument[] sorts, String parentPKField) {


		SimpleQueryFilter [] flt = new SimpleQueryFilter []{
				new 	SimpleQueryFilter( parentPKField , SimpleQueryFilterOperator.equal , parentPrimaryKey )
		}; 
		try {
			List<DATA> d = generalPurposeDao.list(customTableJoinHQL, tableAliasName , flt ,  sorts);
			return transposeToContainer(d);
		} catch (Exception e) {
			logger.error("gagal membaca data. error di laporkan : " + e.getMessage() , e);
			return null;
		}
	}
	
	@Override
	public <DATA> ZKClientSideListDataEditorContainer<DATA> getDataDetailsWithAdditionalFilters(
			Class<DATA> dataClass, Long parentPrimaryKey,
			SimpleSortArgument[] sorts, SimpleQueryFilter[] filters,
			String parentPKField) {
		
		List<SimpleQueryFilter> filterList = new ArrayList<>();
		filterList.add(new 	SimpleQueryFilter( parentPKField , SimpleQueryFilterOperator.equal , parentPrimaryKey ));
		
		if(filters != null && filters.length > 0){
			int i = 0;
			for(SimpleQueryFilter f : filters){
				filterList.add(f);
				i++;
			}
		}
		
		SimpleQueryFilter [] flt = new SimpleQueryFilter [filterList.size()];
		filterList.toArray(flt);
		try {
			List<DATA> d = generalPurposeDao.list(dataClass, flt ,  sorts);
			return transposeToContainer(d);
		} catch (Exception e) {
			logger.error("gagal membaca data. error di laporkan : " + e.getMessage() , e);
			return null;
		}
	}
	@Override
	public <DATA> ZKClientSideListDataEditorContainer<DATA> getDataDetailsWithCustomTableJoinHQL(
			String customTableJoinHQL, String tableAliasName,
			String parentPrimaryKey, SimpleSortArgument[] sorts,
			String parentPKField) {
		
		SimpleQueryFilter [] flt = new SimpleQueryFilter []{
				new 	SimpleQueryFilter( parentPKField , SimpleQueryFilterOperator.equal , parentPrimaryKey )
		}; 
		try {
			List<DATA> d = generalPurposeDao.list(customTableJoinHQL, tableAliasName , flt ,  sorts);
			return transposeToContainer(d);
		} catch (Exception e) {
			logger.error("gagal membaca data. error di laporkan : " + e.getMessage() , e);
			return null;
		}
	}
}
