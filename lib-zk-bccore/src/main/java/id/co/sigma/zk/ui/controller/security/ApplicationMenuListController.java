package id.co.sigma.zk.ui.controller.security;

import id.co.sigma.common.data.SingleKeyEntityData;
import id.co.sigma.common.data.query.SimpleQueryFilter;
import id.co.sigma.common.data.query.SimpleQueryFilterOperator;
import id.co.sigma.common.data.query.SimpleSortArgument;
import id.co.sigma.common.security.domain.ApplicationMenu;
import id.co.sigma.common.security.domain.PageDefinition;
import id.co.sigma.zk.tree.MenuTreeNode;
import id.co.sigma.zk.tree.MenuTreeNodeCollection;
import id.co.sigma.zk.ui.CustomQueryDrivenTreeModel;
import id.co.sigma.zk.ui.controller.EditorManager;
import id.co.sigma.zk.ui.controller.IReloadablePanel;
import id.co.sigma.zk.ui.controller.base.BaseSimpleTreeController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.zkoss.zk.ui.Component;
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
public class ApplicationMenuListController extends BaseSimpleTreeController<ApplicationMenu> implements IReloadablePanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4321464574596835605L;



	static final org.slf4j.Logger logger = org.slf4j.LoggerFactory
			.getLogger(ApplicationMenuListController.class.getName());
	
	private static final SimpleSortArgument[] DEF_SORTS ={
		new SimpleSortArgument("id", true)
		}; 
			
	
	@Wire
	Tree tree;
	
	
	@Autowired
	@Qualifier(value="securityApplicationId")
	String applicationId ; 
	

	@Override
	public Tree getTree() {
		return tree;
	}


	@Override
	protected Class<? extends ApplicationMenu> getHandledClass() {
		return ApplicationMenu.class;
	}

	
	public Class<? extends SingleKeyEntityData<Long>> getEntityClass() {
		return ApplicationMenu.class;
	}

	
	public TreeModel<TreeNode<ApplicationMenu>> getTreeModel(){
	  return constructTree(getMenus());
	}
	
	@Override
	public TreeModel<TreeNode<ApplicationMenu>> constructTree(List<ApplicationMenu> data){
		
		Map<Long, List<TreeNode<ApplicationMenu>>> mapsOfChildCol = new HashMap<>();
		MenuTreeNodeCollection<ApplicationMenu> col = new MenuTreeNodeCollection<>();
		List<MenuTreeNode<ApplicationMenu>> listOfTreeMenu = new ArrayList<>();
		
		for(ApplicationMenu menu : data){
			MenuTreeNode<ApplicationMenu> treeMenu=null;
			if(menuHasChild(menu.getId(), data)){
				treeMenu = new MenuTreeNode<ApplicationMenu>(menu, new MenuTreeNodeCollection<ApplicationMenu>());
				mapsOfChildCol.put(menu.getId(), treeMenu.getChildren());
			}else{
				treeMenu = new MenuTreeNode<ApplicationMenu>(menu);
			}
			listOfTreeMenu.add(treeMenu);
		}
		
		for(MenuTreeNode<ApplicationMenu> tree : listOfTreeMenu){
			if((tree.getData().getPageId()==null && tree.getData().getFunctionIdParent()==null) ||
					(tree.getData().getFunctionIdParent()==null && tree.getData().getPageId()!=null)){
				col.add(tree);
			}else if(tree.getData().getFunctionIdParent()!=null && tree.getData().getPageId()==null){
				mapsOfChildCol.get(tree.getData().getFunctionIdParent()).add(tree);
			} else{
				mapsOfChildCol.get(tree.getData().getFunctionIdParent()).add(tree);
			}
		}
		
		return new DefaultTreeModel<ApplicationMenu>(new MenuTreeNode<ApplicationMenu>(null, col));
		
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
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("gagal membaca menu untuk app id : " + applicationId + " , error : " + e.getMessage() , e);
			return null ;
		}
	}
	
	
	/**
	 * check if menu has child
	 * @param menuId
	 * @param listOfMenu
	 * @return
	 */
	private boolean menuHasChild(Long menuId, List<ApplicationMenu> listOfMenu){
		for(ApplicationMenu menu : listOfMenu){
			if(menu.getFunctionIdParent()!=null && menu.getFunctionIdParent()==menuId){
				return true;
			}
		}
		return false;
	}
	

	@Override
	protected String getCustomQuery() {
		return getHandledClass().getName()+" A left outer join fetch A.pageDefinition";
	}

	@Override
	protected String getInitial() {
		return "A";
	}
	
	@Listen("onClick=#btnAdd")
	public void onClickAdd(){
		ApplicationMenu appMenu = new ApplicationMenu();
		EditorManager.getInstance().addNewData("~./zul/pages/master/security/MenuEditor.zul", appMenu, this);
		
	}

	@Override
	public void reload() {
		//invokeSearch();
		tree.clear();
		tree.setModel(constructTree(getMenus()));
	}


	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		tree.clear();
		tree.setModel(constructTree(getMenus()));
	}


	@Override
	protected CustomQueryDrivenTreeModel<ApplicationMenu> generateTreeModel(
			final SimpleQueryFilter[] filters,final  SimpleSortArgument[] sorts) {
		final String customQuery = getCustomQuery();
		final String initial = getInitial();
		return new CustomQueryDrivenTreeModel<ApplicationMenu>() {
			@Override
			public Class<ApplicationMenu> getHandledClass() {
				return ApplicationMenu.class;
			}
			@Override
			protected SimpleQueryFilter[] getFilters() {
				return filters;
			}
			@Override
			protected SimpleSortArgument[] getSorts() {
				return sorts;
			}
			@Override
			protected String getCustomQuery() {
				return customQuery;
			}
			@Override
			protected String getInitial() {
				return initial;
			}
			@Override
			public List<ApplicationMenu> selectFromDB(int pageSize,
					int firstRowPosition) {
				// TODO Auto-generated method stub
				List<ApplicationMenu> swap  =  super.selectFromDB(pageSize, firstRowPosition);
				if ( swap != null && !swap.isEmpty()){
					Map<Long , ArrayList<ApplicationMenu>> idxMenus = new HashMap<>();
					for (ApplicationMenu scn : swap ){
						if ( !idxMenus.containsKey(scn.getPageId())){
							idxMenus.put(scn.getPageId() , new ArrayList<ApplicationMenu>());
						}
						idxMenus.get(scn.getPageId()).add(scn);
					}
					
					Number[] n = new Number[idxMenus.size()]; 
					int i = 0  ; 
					for ( Long scn :idxMenus.keySet() ){
						n[i] = scn ; 
						i++ ; 
					}
					SimpleQueryFilter s = new SimpleQueryFilter(); 
					
					
					s.setField("id");
					s.setFilter(n) ; 
					s.setOperator(SimpleQueryFilterOperator.fieldIn);
					SimpleQueryFilter[] arr = new SimpleQueryFilter[]{
							s
					};
					try{
						List<PageDefinition> pgs =  generalPurposeDao.list(PageDefinition.class, arr, null);
						for  ( PageDefinition scn : pgs ) {
							List<ApplicationMenu > mnus =  idxMenus.get(scn.getId());
							for(ApplicationMenu mnu : mnus ){
								mnu.setPageDefinition(scn);
							}
						}
					}catch ( Exception e){
						logger.error("Application Menu List, baca pagedefinition,"+e.getMessage(),e);
					}
					
				}
				return swap;
			}
			
			
		};
	}
	
}
