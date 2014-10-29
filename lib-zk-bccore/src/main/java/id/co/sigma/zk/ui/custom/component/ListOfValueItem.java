package id.co.sigma.zk.ui.custom.component;

import java.util.Date;

public final class ListOfValueItem {
	
	private String code;
	
	private Object value;
	
	private String label;
	
	private String separator;
	
	public ListOfValueItem(Object value, String label, String separator) {
		super();
		this.value = value;
		this.label = label;
		this.separator = separator;
		this.code = String.valueOf(value);
	}

	public ListOfValueItem(String code, Object value, String label, String separator) {
		super();
		this.value = value;
		this.label = label;
		this.separator = separator;
		this.code = code;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer sBuf = new StringBuffer();
		sBuf.append("{");
		if(this.value instanceof String || this.value instanceof Date) {
			sBuf.append("value: \"").append(this.value).append("\", ");
			sBuf.append("_id: \"").append(this.value).append("\", ");
		} else {
			sBuf.append("value: ").append(this.value).append(", ");
			sBuf.append("_id: ").append(this.value).append(", ");
		}
		sBuf.append("label: \"").append(this.code).append(separator).append(label).append("\"")
		.append("}");			
		return sBuf.toString();
	}
	
	
}