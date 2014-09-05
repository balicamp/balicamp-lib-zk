package id.co.sigma.zk.ui.controller.security;

import id.co.sigma.common.data.query.SimpleQueryFilter;
import id.co.sigma.common.data.query.SimpleQueryFilterOperator;
import id.co.sigma.common.data.query.SimpleSortArgument;
import id.co.sigma.common.security.domain.ApplicationMenu;
import id.co.sigma.zk.tree.MenuTreeNode;
import id.co.sigma.zk.tree.MenuTreeNodeCollection;
import id.co.sigma.zk.ui.controller.base.BaseSimpleController;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.Tree;
import org.zkoss.zul.TreeModel;
import org.zkoss.zul.TreeNode;

/**
 * 
 * @author <a href="mailto:raka.sanjaya@sigma.co.id">Raka Sanjaya</a>
 */
public class ApplicationMenuListController extends BaseSimpleController{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4321464574596835605L;



	static final org.slf4j.Logger logger = org.slf4j.LoggerFactory
			.getLogger(ApplicationMenuListController.class.getName());
	
	
	
	private static final SimpleSortArgument[] DEF_SORTS ={
		new SimpleSortArgument("siblingOrder", true)
	}; 
			
	
	@Wire
	Tree tree;
	
	
	@Autowired
	@Qualifier(value="securityApplicationId")
	String applicationId ; 
	
	
	public TreeModel<TreeNode<ApplicationMenu>> getTreeModel(){
		
		List<ApplicationMenu> retVal = getMenus();
		Map<Long, ApplicationMenu> mapParent = new HashMap<>();
		Map<Long, ApplicationMenu> mapChild = new HashMap<>();
		if(retVal!=null && !retVal.isEmpty()){
			for(ApplicationMenu app : retVal){
				if(app.getFunctionIdParent()==null)
					mapParent.put(app.getId(), app);
				else
					mapChild.put(app.getId(), app);
			}
		}
		return new DefaultTreeModel<ApplicationMenu>(generateTreeModel(mapParent, mapChild));
	}

	
	protected MenuTreeNode<ApplicationMenu> generateTreeModel(Map<Long,ApplicationMenu> parent, Map<Long,ApplicationMenu> child){	
		Set<Long> key = parent.keySet();
		Collection<ApplicationMenu> value = child.values();
		MenuTreeNodeCollection<ApplicationMenu> menuTree = new MenuTreeNodeCollection<>();
		for(Long k : key){
			MenuTreeNodeCollection<ApplicationMenu> children = new MenuTreeNodeCollection<>();
			MenuTreeNode<ApplicationMenu> menu;
			for(ApplicationMenu app : value){
				if(app.getFunctionIdParent()==k){
					children.add(new MenuTreeNode<ApplicationMenu>(app));
				}
			}
			if(!children.isEmpty()){
				menu = new MenuTreeNode<ApplicationMenu>(parent.get(k), children,true);
			}else{
				menu = new MenuTreeNode<ApplicationMenu>(parent.get(k));
			}
			menuTree.add(menu);
		}
		return new MenuTreeNode<ApplicationMenu>(null,menuTree,true);
	}
	
	@SuppressWarnings("unchecked")
	protected List<ApplicationMenu> getMenus () {
		Long appId = new Long(applicationId); 
		SimpleQueryFilter[] flt = new SimpleQueryFilter[]{
			 new SimpleQueryFilter("applicationId" , SimpleQueryFilterOperator.equal , appId)
		}; 
		try {
			List l = generalPurposeDao.list( ApplicationMenu.class.getName() +" A left outer join fetch A.pageDefinition", "A", flt, DEF_SORTS,10000,0);
			return (List<ApplicationMenu>)l;
			//return generalPurposeDao.list(ApplicationMenu.class, flt,DEF_SORTS);
		} catch (Exception e) {
			
			e.printStackTrace();
			logger.error("gagal membaca menu untuk app id : " + applicationId + " , error : " + e.getMessage() , e);
			return null ;
		} 
	}
	
	
	@Listen("onClick=#searchButton")
	public void onSearch(){
		
	}
}
