package symja;

import basic.SimpleName;
import basic.reproduce.SimpleReproducible;

/**
 * 
 * 
 * 
 * 
 * @author tanxu
 *
 */
public class SymjaBasedDomain implements SimpleReproducible{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7790030940195435555L;
	
	////////////////////
	public static final String VARIABLE_ALIAS = "SYMJAVA";
    
    /**
     * name of the variable that appear in the domain string (if applicable);
     * note that for a set type domain, the variable name should not appear in the domain string;
     */
    private final SimpleName primitiveGraphicsPropertyName;
    
    /**
     * if true, the domain is composed of numeric value range possibly linked by logical operators recognizable by symja including 'AND', 'OR', 'NOT'; 
     * and the property name string MUST appear in the domain string;
     * otherwise, the domain is a set of values and the property name string will not appear in the domain string and a pair of bracket parenthesis will be used {}
     */
    private final boolean numericRange;
    
    /**
     * the domain string;
     * if isNumericRange, the given propertyName will appear in it;
     */
    private final String domainString;
    
    /**
     * constructor
     * @param propertyName
     * @param isNumericRange
     * @param domainString 
     */
    public SymjaBasedDomain(SimpleName propertyName, boolean isNumericRange, String domainString){
        if(propertyName.toString().toUpperCase().contains(VARIABLE_ALIAS)){
            throw new IllegalArgumentException("reserved characters:"+VARIABLE_ALIAS+" are found in the given property name:"+propertyName.toString());
        }
        
        if(domainString == null || domainString.isEmpty()){
            throw new IllegalArgumentException("given domain string is null or empty");
        }
        
        if(!isNumericRange){//a set of values
            if(!(domainString.startsWith("\\{") && domainString.endsWith("\\}"))){
                throw new IllegalArgumentException("given domain string is not a set of values contained in a bracket parenthesis when given isNumericRange is false");
            }
        }
        
        this.primitiveGraphicsPropertyName = propertyName;
        this.numericRange = isNumericRange;
        this.domainString = domainString;
    }
    
    
    
    SimpleName getPropertyName() {
        return primitiveGraphicsPropertyName;
    }
    
    boolean isNumericRange() {
        return numericRange;
    }
    
    String getDomainString() {
        return domainString;
    }
    
    
    boolean validate(String stringValue) {
    	//TODO
    	return true;
    }
    
    /**
     * shallow check whether the two given domain strings are of the same despite of the variable name string;
     * 
     * note that it does not check logically whether the two domains are the same in terms of the range;
     * for example, a<10 and a>0 will not be equal to  a>0 and a<10;
     * 
     * @param domain1
     * @param domain2
     * @return 
     */
    public static boolean domainsStringsAreEqual(SymjaBasedDomain domain1, SymjaBasedDomain domain2){
    	//TODO
        if(domain1.isNumericRange() && domain2.isNumericRange()){//both are numeric type
            String d1 = domain1.domainString.toUpperCase().trim().replace(domain1.primitiveGraphicsPropertyName.toString().toUpperCase(), VARIABLE_ALIAS);
            String d2 = domain2.domainString.toUpperCase().trim().replace(domain2.primitiveGraphicsPropertyName.toString().toUpperCase(), VARIABLE_ALIAS);
            return d1.equals(d2);
            
        }else if(!domain1.isNumericRange() && !domain2.isNumericRange()){//both are discrete set
            
            return domain1.domainString.trim().equals(domain2.domainString.trim());
            
        }else{
            return false;
        }
    }
    

	@Override
	public SimpleReproducible reproduce() {
		// TODO Auto-generated method stub
		return null;
	}
    
	
	//TODO equals and hashcode
}

