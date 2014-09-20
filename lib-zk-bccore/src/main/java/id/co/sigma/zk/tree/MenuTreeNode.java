/**
 * 
 */
package id.co.sigma.zk.tree;

import org.zkoss.zul.DefaultTreeNode;


/**
 * Tree model yang akan di render
 * @author <a href="mailto:raka.sanjaya@sigma.co.id">Raka Sanjaya</a>
 */
public class MenuTreeNode<T> extends DefaultTreeNode<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8820428139444999096L;
	
	private boolean open =false;
	private MenuTreeNodeCollection<T> menuTreeNodeCollection;

	public MenuTreeNode(T data, MenuTreeNodeCollection<T> children,
			boolean open) {
		super(data, children, open);
		this.open=open;
		this.menuTreeNodeCollection=children;
	}

	public MenuTreeNode(T data, MenuTreeNodeCollection<T> children) {
		super(data, children);
		this.menuTreeNodeCollection=children;
	}

	public MenuTreeNode(T data) {
		super(data);
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public MenuTreeNodeCollection<T> getMenuTreeNodeCollection() {
		return menuTreeNodeCollection;
	}

	public void setMenuTreeNodeCollection(
			MenuTreeNodeCollection<T> menuTreeNodeCollection) {
		this.menuTreeNodeCollection = menuTreeNodeCollection;
	}
	
}
