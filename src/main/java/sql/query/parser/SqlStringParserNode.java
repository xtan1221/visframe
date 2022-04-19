/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sql.query.parser;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tanxu
 */
public class SqlStringParserNode {
    private final String nodeString;

    /**
     * the index of the alias name that is to be parsed for next level nodes (if exist)
     */
    private int reservedSymbolListIndex;
    
    private List<SqlStringParserNode> children;
    
    boolean isLeaf;
    
    
    public SqlStringParserNode(String nodeString, int reservedSymbolListIndex, boolean isLeaf) {
        if (nodeString == null || nodeString.isEmpty()) {
            throw new IllegalArgumentException("given nodeString is null or empty");
        }
        if (reservedSymbolListIndex < 0 || reservedSymbolListIndex > SqlReservedStringUtils.listOfReservedSQLSymbolInCoverOrder().size()) {
            throw new IllegalArgumentException("given reservedSymbolListIndex is out of boundary of aliasNameList of SQLReservedStringUtils.listOfReservedSQLSymbolInCoverOrder:"+reservedSymbolListIndex);
        }

        this.nodeString = nodeString.trim();
        this.reservedSymbolListIndex = reservedSymbolListIndex;
        this.isLeaf = isLeaf;
        
        if (!this.isLeaf) {
            this.parseChildrenNodes();
        }
    }
    
    
    /**
     * parse the separators
     */
    private void parseChildrenNodes() {
        String nodeStringUpperCase = this.nodeString.toUpperCase();
        if(reservedSymbolListIndex > SqlReservedStringUtils.listOfReservedSQLSeparatorInCoverOrder().size() - 1){//node string contains none of the alias name
            this.isLeaf = true;
            return;
        }
        
        while (!nodeStringUpperCase.contains(SqlReservedStringUtils.listOfReservedSQLSeparatorInCoverOrder().get(this.reservedSymbolListIndex).toString().toUpperCase())) {
            reservedSymbolListIndex++;
            if (reservedSymbolListIndex > SqlReservedStringUtils.listOfReservedSQLSeparatorInCoverOrder().size() - 1) {
                this.isLeaf = true;
                return;
            }
        }
        
        String aliasNameUpperCase = SqlReservedStringUtils.listOfReservedSQLSeparatorInCoverOrder().get(this.reservedSymbolListIndex).toString().toUpperCase();
        
        //node string contains a single alias name
        if(nodeStringUpperCase.equals(aliasNameUpperCase)){
            this.isLeaf = true;
            return;
        }
        
        //first alias name with the updated aliasNameListIndex is found in the node string
        
        this.children = new ArrayList<>();
        
        String[] splits = nodeStringUpperCase.split("\\".concat(aliasNameUpperCase));
        
        int startSplitIndex;
        if (nodeStringUpperCase.startsWith(aliasNameUpperCase)) { //start with a alias name, add a leaf node
            
            this.children.add(new SqlStringParserNode(
//                        this.tree,
                        SqlReservedStringUtils.listOfReservedSQLSeparatorInCoverOrder().get(reservedSymbolListIndex).toString(),
                        this.reservedSymbolListIndex + 1,
                        true
            ));
            startSplitIndex = 1; //ignore the first split string which is empty string
        } else {
            startSplitIndex = 0;
        }
        
        //note that splits method will ignore trailing empty string
        for (int i = startSplitIndex; i < splits.length; i++) {//add the split strings for further parsing
            if(!splits[i].isEmpty()){//only add non-empty
                this.children.add(new SqlStringParserNode(
//                            this.tree,
                            splits[i],
                            this.reservedSymbolListIndex + 1,
                            false
                ));
            }
            
            if (i < splits.length - 1) {//add leaf between non-leaf non-empty split strings
                this.children.add(new SqlStringParserNode(
//                            this.tree,
                            SqlReservedStringUtils.listOfReservedSQLSeparatorInCoverOrder().get(reservedSymbolListIndex).toString(),
                            this.reservedSymbolListIndex + 1,
                            true
                ));
            }
        }

        if (nodeStringUpperCase.endsWith(aliasNameUpperCase)) { //start with a alias name, add a leaf node
            this.children.add(new SqlStringParserNode(
//                        this.tree,
                        SqlReservedStringUtils.listOfReservedSQLSeparatorInCoverOrder().get(reservedSymbolListIndex).toString(),
                        this.reservedSymbolListIndex + 1,
                        true
            ));
        }
    }
    
    
    
    
    
    public List<SqlStringParserNode> getOrderedListOfDescendantLeafNodes(){
        List<SqlStringParserNode> ret = new ArrayList<>();
        
        if(this.isLeaf){
            ret.add(this);
        }else{
            for(SqlStringParserNode child:this.children){
                ret.addAll(child.getOrderedListOfDescendantLeafNodes());
            }
        }
        
        return ret;
    }
    
    
    ////////////////////////////////////////////////
    public String getNodeString() {
        return nodeString;
    }

    public int getReservedSymbolListIndex() {
        return reservedSymbolListIndex;
    }

    public List<SqlStringParserNode> getChildren() {
        return children;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public boolean isSqlReserved() {
        return SqlReservedStringUtils.SQL_RESERVED_SYMBOLS.contains(new SqlElementString(this.nodeString));
    }

}
