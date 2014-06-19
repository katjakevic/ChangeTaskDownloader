package ctdownloader.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class MethodElement implements Element {
	
	private String className;
	private String methodName;
	
	
	public MethodElement(String cName, String mName){
		this.className = cName;
		this.methodName = mName;
	}
	
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	} 
	
	 public boolean equals(Object obj) {
	        if (obj == null)
	            return false;
	        if (obj == this)
	            return true;
	        if (!(obj instanceof MethodElement))
	            return false;

	        MethodElement rhs = (MethodElement) obj;
	        return new EqualsBuilder().
	            // if deriving: appendSuper(super.equals(obj)).
	            append(className, rhs.className).
	            append(methodName, rhs.methodName).
	            isEquals();
	    }
	 
	 public int hashCode() {
	        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
	            // if deriving: appendSuper(super.hashCode()).
	            append(className).
	            append(methodName).
	            toHashCode();
	    }
	 
	 @Override
	 public String toString(){
		 return className+"\t"+methodName;
	 }
	
	

}

