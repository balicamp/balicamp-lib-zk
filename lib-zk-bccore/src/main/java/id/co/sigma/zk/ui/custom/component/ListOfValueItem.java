package id.co.sigma.zk.ui.custom.component;

import java.util.Date;

public final class ListOfValueItem {
	
	private String code;
	
	private Object value;
	
	private String label;
	
	private String separator;
	
	private Integer sequence = 0;
	
	private boolean serverObject = false;
	
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

	public ListOfValueItem(String code, Object value, String label, String separator, Integer sequence) {
		super();
		this.value = value;
		this.label = label;
		this.separator = separator;
		this.code = code;
		this.sequence = sequence;
	}

	public ListOfValueItem(Object value, String label, String separator, Integer sequence) {
		super();
		this.value = value;
		this.label = label;
		this.separator = separator;
		this.code = String.valueOf(value);
		this.sequence = sequence;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer sBuf = new StringBuffer();
		if(!serverObject) {
			sBuf.append("{");
			if(this.value instanceof String || this.value instanceof Date) {
				sBuf.append("value: \"").append(this.value).append("\", ");
				sBuf.append("_id: \"").append(this.sequence).append("_").append(this.value).append("\", ");
			} else {
				sBuf.append("value: ").append(this.value).append(", ");
				sBuf.append("_id: ").append(this.sequence).append("_").append(this.value).append(", ");
			}
			if(!"".equals(separator)) {
				//gede sutarsa 1-12-2014 --> untuk value = "" tidak di tampilkan dalam data LOV
				if ( this.value instanceof String && ((String)this.value).isEmpty()){
					sBuf.append("label: \"").append(label).append("\"").append("}");
				}else{
					sBuf.append("label: \"").append(this.code).append(separator).append(label).append("\"").append("}");
				}
				
				
				
						
			} else {
				sBuf.append("label: \"").append(label).append("\"").append("}");
			}
		} else {
			if(!"".equals(separator)) {
				sBuf.append(code).append(separator).append(label);
			} else {
				sBuf.append(label);
			}
		}
		return sBuf.toString();
	}

	public boolean isServerObject() {
		return serverObject;
	}

	public void setServerObject(boolean serverObject) {
		this.serverObject = serverObject;
	}

	/**
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(Object value) {
		this.value = value;
	}
	
	/**
	 * @return the label
	 */
	public String getDescription() {
		return label;
	}

	/**
	 * @return the sequence
	 */
	public Integer getSequence() {
		return sequence;
	}

	/**
	 * @param sequence the sequence to set
	 */
	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
}