package ctdownloader.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ClassElement implements Element {

private String className;
	
	public ClassElement(String cName){
		this.className = cName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}
	
	
	
	 public boolean equals(Object obj) {
	        if (obj == null)
	            return false;
	        if (obj == this)
	            return true;
	        if (!(obj instanceof ClassElement))
	            return false;

	        ClassElement rhs = (ClassElement) obj;
	        return new EqualsBuilder().
	            // if deriving: appendSuper(super.equals(obj)).
	            append(className, rhs.className).
	            isEquals();
	    }
	 
	 public int hashCode() {
	        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
	            // if deriving: appendSuper(super.hashCode()).
	            append(className).
	            toHashCode();
	    }
	
	 @Override
	 public String toString(){
		 return className;
	 }


}
