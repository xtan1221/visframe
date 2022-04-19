/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sql.query.parser;

import java.util.Objects;

/**
 * basic sql string components that can not be further decomposed;
 * 
 * 1. sql reserved symbol;
 * 2. user provided variable
 * 3. schema name/table name/column name
 * 4. constant value;
 * 
 * @author tanxu
 */
public class SqlElementString {
    private final String inputString;
    
    public SqlElementString(String inputString){
        if(inputString == null||inputString.isEmpty()){
            throw new IllegalArgumentException("given visframe name is null or empty");
        }
        
        this.inputString = inputString;
    }
    
    @Override
    public String toString(){
        return inputString;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.inputString.toUpperCase());
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SqlElementString other = (SqlElementString) obj;
        if (!Objects.equals(this.inputString.toUpperCase(), other.inputString.toUpperCase())) {
            return false;
        }
        return true;
    }
}
