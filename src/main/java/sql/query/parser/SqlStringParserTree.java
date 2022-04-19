package sql.query.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import basic.SimpleName;
import basic.VfNameString;


/**
 * this api is used to replace a sql string with alias name of columns and tables with the real column and table name;
 * 
 * no 'AS' key word is allowed in the raw sql query string, all the non-reserved symbols are considered as alias names;
 * 
 * string that are used as constant value is not dealt with yet???
 * 
 * numeric constant value????
 * 
 * the major difficulty is:
 * 1. distinguish alias name and reserved sql syntax characters/symbols
 * 2. alias names that are overlapping with each other should be dealt with with caution;
 *  for example, for alias names 'ABCD' and 'ABC', 'ABCD' should be replaced before 'ABC';
 * 3. real column names and table names might overlapping with each other and the alias names;
 * 
 * 
 * ***********************************************************************************************
 * visframe relational data table related name can not contain empty
 * space!!!!!!!!!!!!!!!! no AS can be used, variable names are already alias; 
 * 
 * @author tanxu
 */
public class SqlStringParserTree {

    protected final String sqlString;
    
    protected List<SqlStringParserNode> childNodeList;
    
    protected SqlStringParserTree(String sqlString) {
        this.sqlString = sqlString.trim();

        String[] splits = this.sqlString.split("\\s+");
        
        this.childNodeList = new ArrayList<>();
        
        for (String split : splits) {
            this.childNodeList.add(
                        new SqlStringParserNode(
                                    split,
                                    0,
                                    false
                        ));
        }
        
    }

    /**
     * todo list:
     *
     * if a string is used as a constant, need to exclude it from replacing; for
     * example, 'table1' is used as a string parameter, and table1 is the alias
     * name of a variable; SELECT Test FROM user WHERE login LIKE 'Test'; //Test
     * is a table column name and also used as a string parameter
     *
     * @return
     */
    public Set<SqlElementString> getParsedVariableSet() {
        Set<SqlElementString> parsedVariableNameSet = new HashSet<>();

        for (SqlStringParserNode node : this.childNodeList) {
            for (int i = 0; i < node.getOrderedListOfDescendantLeafNodes().size(); i++) {
                SqlStringParserNode leaf = node.getOrderedListOfDescendantLeafNodes().get(i);
//                System.out.println(leaf.getNodeString() + "\tisSqlReserved==" + leaf.isSqlReserved());
                
                if (!isQuotedString(node.getOrderedListOfDescendantLeafNodes(),i) && !leaf.isSqlReserved()) {
                    parsedVariableNameSet.add(new SqlElementString(leaf.getNodeString()));
                }
            }
        }

        return parsedVariableNameSet;
    }

    /**
     * 
     * @param variableNameReplacingStringMap
     * @return 
     */
    public String getSqlStringWithReplacedVariableNames(Map<VfNameString, String> variableNameReplacingStringMap) {
        String ret = "";

        for (SqlStringParserNode node : this.childNodeList) {
            if (!ret.isEmpty()) {
                ret = ret.concat(" ");
            }

            for (int i = 0; i < node.getOrderedListOfDescendantLeafNodes().size(); i++) {
                SqlStringParserNode leaf = node.getOrderedListOfDescendantLeafNodes().get(i);
                boolean leafCanBeVfName = false;
                try {
                    SimpleName leafV = new SimpleName(leaf.getNodeString());
                    leafCanBeVfName = true;
                } catch (IllegalArgumentException e) {
                    //
                }

//                System.out.println(leaf.getNodeString() + "\tisSqlReserved==" + leaf.isSqlReserved());
                if (leafCanBeVfName && !isQuotedString(node.getOrderedListOfDescendantLeafNodes(),i) && variableNameReplacingStringMap.keySet().contains(new SimpleName(leaf.getNodeString()))) {
                    ret = ret.concat(variableNameReplacingStringMap.get((new SimpleName(leaf.getNodeString()))));
                } else {
                    ret = ret.concat(leaf.getNodeString());
                }
            }
        }

        return ret;
    }
    
    
    /**
     * check if the given leaf at the given index position is a quoted string or not
     * @param orderedLeafNodeList
     * @param nodeIndex
     * @return 
     */
    boolean isQuotedString(List<SqlStringParserNode> orderedLeafNodeList, int nodeIndex) {
        //check if the leaf node is a quoted string paramater rather than a variable name
        if (nodeIndex > 0 && nodeIndex < orderedLeafNodeList.size() - 1) {
            SqlStringParserNode previousNode = orderedLeafNodeList.get(nodeIndex - 1);
            SqlStringParserNode nextNode = orderedLeafNodeList.get(nodeIndex + 1);
            if (previousNode.getNodeString().equals("'") && nextNode.getNodeString().equals("'")
                        || previousNode.getNodeString().equals("\"") && nextNode.getNodeString().equals("\"")) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
//        String sqlString = "SELECT a, b, c from table1, table2";
//        String sqlString = "CREATE TABLE Persons ID int NOT NULL,LastName varchar(255) NOT NULL,FirstName varchar(255) NOT NULL,Age int);";

//        String sqlString = "SELECT COUNT(CustomerID), Country\n"
//                    + "FROM Customers\n"
//                    + "GROUP BY Country\n"
//                    + "ORDER BY COUNT(CustomerID) DESC;";
        String sqlString = "SELECT Test FROM user WHERE login LIKE \"Test\"";

//        String sqlString = "SELECT Shippers.ShipperName, COUNT(Orders.OrderID) AS NumberOfOrders FROM Orders\n"
//                    + "LEFT JOIN Shippers ON Orders.ShipperID = Shippers.ShipperID\n"
//                    + "GROUP BY ShipperName;";
        SqlStringParserTree tree = new SqlStringParserTree(sqlString);
//        SqlStringParserTree tree = new SqlStringParserTree(SqlReservedStringUtils.COMPLICATES_SQL_FOR_TEST);

//        for (SqlStringParserNode node : tree.childNodeList) {
//            for (SqlStringParserNode leaf : node.getOrderedListOfDescendantLeafNodes()) {
//                System.out.println(leaf.getNodeString() + "\tisSqlReserved==" + leaf.isSqlReserved());
//            }
//        }
        for (SqlElementString v : tree.getParsedVariableSet()) {
            System.out.println(v.toString());
        }

//        Map<VfNameString,VfNameString> variableNameReplacingStringMap = new HashMap<>();
//        variableNameReplacingStringMap.put(new VfNameString("SHIPPERID"), new VfNameString("A"));
//        variableNameReplacingStringMap.put(new VfNameString("ORDERID"), new VfNameString("B"));
//        variableNameReplacingStringMap.put(new VfNameString("SHIPPERS"), new VfNameString("C"));
//        variableNameReplacingStringMap.put(new VfNameString("ORDERS"), new VfNameString("D"));
//        variableNameReplacingStringMap.put(new VfNameString("SHIPPERNAME"), new VfNameString("E"));
//        variableNameReplacingStringMap.put(new VfNameString("NumberOfOrders"), new VfNameString("F"));
        Map<VfNameString, String> variableNameReplacingStringMap = new HashMap<>();
        variableNameReplacingStringMap.put(new SimpleName("login"), "A");
        variableNameReplacingStringMap.put(new SimpleName("Test"), "B");
        System.out.println(tree.getSqlStringWithReplacedVariableNames(variableNameReplacingStringMap));
    }

}
