/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sql.query.parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;

import dependency.JGraphTDependencyGraphUtils;

/**
 * NOTE that different relational database engine might implements different version of sql thus have different set of reserved symbols;
 * also, different engines have their unique set of built-in functions;
 * 
 * 
 * 
 * @author tanxu
 */
public class SqlReservedStringUtils {

    /**
     * all symbols including operator, keywords,etc
     */
    public static final Set<SqlElementString> SQL_RESERVED_SYMBOLS;
    /**
     * all reserved strings except for keywords;
     */
    public static final Set<SqlElementString> SQL_RESERVED_SEPARATORS;
    
    static {
        //https://www.w3schools.com/sql/sql_operators.asp
        SQL_RESERVED_SYMBOLS = new HashSet<>();
        SQL_RESERVED_SEPARATORS = new HashSet<>();

        SQL_RESERVED_SYMBOLS.add(new SqlElementString(","));
        SQL_RESERVED_SYMBOLS.add(new SqlElementString("."));
        SQL_RESERVED_SYMBOLS.add(new SqlElementString(";"));
        //Arithmetic Operators
        SQL_RESERVED_SYMBOLS.add(new SqlElementString("+"));
        SQL_RESERVED_SYMBOLS.add(new SqlElementString("-"));
        SQL_RESERVED_SYMBOLS.add(new SqlElementString("*"));
        SQL_RESERVED_SYMBOLS.add(new SqlElementString("/"));
        SQL_RESERVED_SYMBOLS.add(new SqlElementString("%"));
        //Bitwise Operators
        SQL_RESERVED_SYMBOLS.add(new SqlElementString("&"));
        SQL_RESERVED_SYMBOLS.add(new SqlElementString("|"));
        SQL_RESERVED_SYMBOLS.add(new SqlElementString("^"));

        //Comparison Operators
        SQL_RESERVED_SYMBOLS.add(new SqlElementString("="));
        SQL_RESERVED_SYMBOLS.add(new SqlElementString(">"));
        SQL_RESERVED_SYMBOLS.add(new SqlElementString("<"));
        SQL_RESERVED_SYMBOLS.add(new SqlElementString(">="));
        SQL_RESERVED_SYMBOLS.add(new SqlElementString("<="));
        SQL_RESERVED_SYMBOLS.add(new SqlElementString("<>"));
        //Compound Operators
        SQL_RESERVED_SYMBOLS.add(new SqlElementString("+="));
        SQL_RESERVED_SYMBOLS.add(new SqlElementString("-="));
        SQL_RESERVED_SYMBOLS.add(new SqlElementString("*="));
        SQL_RESERVED_SYMBOLS.add(new SqlElementString("/="));
        SQL_RESERVED_SYMBOLS.add(new SqlElementString("%="));
        SQL_RESERVED_SYMBOLS.add(new SqlElementString("&="));
        SQL_RESERVED_SYMBOLS.add(new SqlElementString("^-="));
        SQL_RESERVED_SYMBOLS.add(new SqlElementString("|*="));

        //miscan
        SQL_RESERVED_SYMBOLS.add(new SqlElementString("("));
        SQL_RESERVED_SYMBOLS.add(new SqlElementString(")"));
        SQL_RESERVED_SYMBOLS.add(new SqlElementString("'"));
        SQL_RESERVED_SYMBOLS.add(new SqlElementString(String.valueOf('"'))); //double quote
        
        
        //String Concatenation Operator
        SQL_RESERVED_SYMBOLS.add(new SqlElementString("||"));
        
        
        
        for (SqlElementString srs : SQL_RESERVED_SYMBOLS) {
            SQL_RESERVED_SEPARATORS.add(srs);
//            SQL_RESERVED_SEPARATORS.add(new SqlReservedString("\\s+"));
        }
    }

    static {
        //https://db.apache.org/derby/docs/10.2/ref/rrefkeywords29722.html
        //including built in functions

        String DERBY_KEYWORDS = "ADD\n"
                    + "ALL\n"
                    + "ALLOCATE\n"
                    + "ALTER\n"
                    + "AND\n"
                    + "ANY\n"
                    + "ARE\n"
                    + "AS\n"
                    + "ASC\n"
                    + "ASSERTION\n"
                    + "AT\n"
                    + "AUTHORIZATION\n"
                    + "AVG\n"
                    + "BEGIN\n"
                    + "BETWEEN\n"
                    + "BIGINT\n"
                    + "BIT\n"
                    + "BOOLEAN\n"
                    + "BOTH\n"
                    + "BY\n"
                    + "CALL\n"
                    + "CASCADE\n"
                    + "CASCADED\n"
                    + "CASE\n"
                    + "CAST\n"
                    + "CHAR\n"
                    + "CHARACTER\n"
                    + "CHECK\n"
                    + "CLOSE\n"
                    + "COALESCE\n"
                    + "COLLATE\n"
                    + "COLLATION\n"
                    + "COLUMN\n"
                    + "COMMIT\n"
                    + "CONNECT\n"
                    + "CONNECTION\n"
                    + "CONSTRAINT\n"
                    + "CONSTRAINTS\n"
                    + "CONTINUE\n"
                    + "CONVERT\n"
                    + "CORRESPONDING\n"
                    + "COUNT\n"//added by xu
                    + "CREATE\n"
                    + "CURRENT\n"
                    + "CURRENT_DATE\n"
                    + "CURRENT_TIME\n"
                    + "CURRENT_TIMESTAMP\n"
                    + "CURRENT_USER\n"
                    + "CURSOR\n"
                    + "DEALLOCATE\n"
                    + "DEC\n"
                    + "DECIMAL\n"
                    + "DECLARE\n"
                    + "DEFAULT\n"
                    + "DEFERRABLE\n"
                    + "DEFERRED\n"
                    + "DELETE\n"
                    + "DESC\n"
                    + "DESCRIBE\n"
                    + "DIAGNOSTICS\n"
                    + "DISCONNECT\n"
                    + "DISTINCT\n"
                    + "DOUBLE\n"
                    + "DROP\n"
                    + "ELSE\n"
                    + "END\n"
                    + "END-EXEC\n"
                    + "ESCAPE\n"
                    + "EXCEPT\n"
                    + "EXCEPTION\n"
                    + "EXEC\n"
                    + "EXECUTE\n"
                    + "EXISTS\n"
                    + "EXPLAIN\n"
                    + "EXTERNAL\n"
                    + "FALSE\n"
                    + "FETCH\n"
                    + "FIRST\n"
                    + "FLOAT\n"
                    + "FOR\n"
                    + "FOREIGN\n"
                    + "FOUND\n"
                    + "FROM\n"
                    + "FULL\n"
                    + "FUNCTION\n"
                    + "GET\n"
                    + "GETCURRENTCONNECTION\n"
                    + "GLOBAL\n"
                    + "GO\n"
                    + "GOTO\n"
                    + "GRANT\n"
                    + "GROUP\n"
                    + "HAVING\n"
                    + "HOUR\n"
                    + "IDENTITY\n"
                    + "IMMEDIATE\n"
                    + "IN\n"
                    + "INDICATOR\n"
                    + "INITIALLY\n"
                    + "INNER\n"
                    + "INOUT\n"
                    + "INPUT\n"
                    + "INSENSITIVE\n"
                    + "INSERT\n"
                    + "INT\n"
                    + "INTEGER\n"
                    + "INTERSECT\n"
                    + "INTO\n"
                    + "IS\n"
                    + "ISOLATION\n"
                    + "JOIN\n"
                    + "KEY\n"
                    + "LAST\n"
                    + "LEFT\n"
                    + "LIKE\n"
                    + "LOWER\n"
                    + "LTRIM\n"
                    + "MATCH\n"
                    + "MAX\n"
                    + "MIN\n"
                    + "MINUTE\n"
                    + "NATIONAL\n"
                    + "NATURAL\n"
                    + "NCHAR\n"
                    + "NVARCHAR\n"
                    + "NEXT\n"
                    + "NO\n"
                    + "NOT\n"
                    + "NULL\n"
                    + "NULLIF\n"
                    + "NUMERIC\n"
                    + "OF\n"
                    + "ON\n"
                    + "ONLY\n"
                    + "OPEN\n"
                    + "OPTION\n"
                    + "OR\n"
                    + "ORDER\n"
                    + "OUTER\n"
                    + "OUTPUT\n"
                    + "OVERLAPS\n"
                    + "PAD\n"
                    + "PARTIAL\n"
                    + "PREPARE\n"
                    + "PRESERVE\n"
                    + "PRIMARY\n"
                    + "PRIOR\n"
                    + "PRIVILEGES\n"
                    + "PROCEDURE\n"
                    + "PUBLIC\n"
                    + "READ\n"
                    + "REAL\n"
                    + "REFERENCES\n"
                    + "RELATIVE\n"
                    + "RESTRICT\n"
                    + "REVOKE\n"
                    + "RIGHT\n"
                    + "ROLLBACK\n"
                    + "ROWS\n"
                    + "RTRIM\n"
                    + "SCHEMA\n"
                    + "SCROLL\n"
                    + "SECOND\n"
                    + "SELECT\n"
                    + "SESSION_USER\n"
                    + "SET\n"
                    + "SMALLINT\n"
                    + "SOME\n"
                    + "SPACE\n"
                    + "SQL\n"
                    + "SQLCODE\n"
                    + "SQLERROR\n"
                    + "SQLSTATE\n"
                    + "SUBSTR\n"
                    + "SUBSTRING\n"
                    + "SUM\n"
                    + "SYSTEM_USER\n"
                    + "TABLE\n"
                    + "TEMPORARY\n"
                    + "TIMEZONE_HOUR\n"
                    + "TIMEZONE_MINUTE\n"
                    + "TO\n"
                    + "TRANSACTION\n"
                    + "TRANSLATE\n"
                    + "TRANSLATION\n"
                    + "TRUE\n"
                    + "UNION\n"
                    + "UNIQUE\n"
                    + "UNKNOWN\n"
                    + "UPDATE\n"
                    + "UPPER\n"
                    + "USER\n"
                    + "USING\n"
                    + "VALUES\n"
                    + "VARCHAR\n"
                    + "VARYING\n"
                    + "VIEW\n"
                    + "WHENEVER\n"
                    + "WHERE\n"
                    + "WITH\n"
                    + "WORK\n"
                    + "WRITE\n"
                    + "XML\n"
                    + "XMLEXISTS\n"
                    + "XMLPARSE\n"
                    + "XMLQUERY\n"
                    + "XMLSERIALIZE\n"
                    + "YEAR";
        for (String keyWord : DERBY_KEYWORDS.split("\n")) {
//            System.out.println(keyWord);
            SQL_RESERVED_SYMBOLS.add(new SqlElementString(keyWord));
        }
    }
    
    private static List<SqlElementString> reservedSymbolListInCoverOrder;

    /**
     * smaller indexed string are covering the larger indexed string (if exist)
     *
     * @return
     */
    public static List<SqlElementString> listOfReservedSQLSymbolInCoverOrder() {
        if (reservedSymbolListInCoverOrder != null) {
            return reservedSymbolListInCoverOrder;
        }

        SimpleDirectedGraph<SqlElementString, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);;
        reservedSymbolListInCoverOrder = new ArrayList<>();

        for (SqlElementString name : SqlReservedStringUtils.SQL_RESERVED_SYMBOLS) {
            graph.addVertex(name);
        }

        for (SqlElementString name1 : SqlReservedStringUtils.SQL_RESERVED_SYMBOLS) {
            for (SqlElementString name2 : SqlReservedStringUtils.SQL_RESERVED_SYMBOLS) {
                if (name1.equals(name2)) { //the same one
                    continue;
                }
                //name2.getName().toString().toLowerCase().contains(name1.getName().toString().toLowerCase());
                if (name2.toString().toLowerCase().contains(name1.toString().toLowerCase())) {
                    graph.addEdge(name2, name1);
                } else if (name1.toString().toLowerCase().contains(name2.toString().toLowerCase())) {
                    graph.addEdge(name1, name2);
                }

            }
        }

        if (JGraphTDependencyGraphUtils.containsCycle(graph)) {
            throw new IllegalArgumentException("Cycle is detected in the generated ReservedSQLSymbol CoverGraph, debug!");
        }

        Set<SqlElementString> nodesToBeRemoved;
        while (!graph.vertexSet().isEmpty()) {
            nodesToBeRemoved = new HashSet<>();
            for (SqlElementString name : graph.vertexSet()) {
                if (graph.inDegreeOf(name) == 0) {
                    reservedSymbolListInCoverOrder.add(name);
                    nodesToBeRemoved.add(name);
                }
            }

            graph.removeAllVertices(nodesToBeRemoved);
        }

        return reservedSymbolListInCoverOrder;
    }

    private static List<SqlElementString> reservedSqlSeparatorListCoverOrder;

    /**
     * smaller indexed string are covering the larger indexed string (if exist)
     *
     * @return
     */
    public static List<SqlElementString> listOfReservedSQLSeparatorInCoverOrder() {
        if (reservedSqlSeparatorListCoverOrder != null) {
            return reservedSqlSeparatorListCoverOrder;
        }
        
        SimpleDirectedGraph<SqlElementString, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);;
        reservedSqlSeparatorListCoverOrder = new ArrayList<>();
        
        for (SqlElementString name : SqlReservedStringUtils.SQL_RESERVED_SEPARATORS) {
            graph.addVertex(name);
        }

        for (SqlElementString name1 : SqlReservedStringUtils.SQL_RESERVED_SEPARATORS) {
            for (SqlElementString name2 : SqlReservedStringUtils.SQL_RESERVED_SEPARATORS) {
                if (name1.equals(name2)) { //the same one
                    continue;
                }
                //name2.getName().toString().toLowerCase().contains(name1.getName().toString().toLowerCase());
                if (name2.toString().toLowerCase().contains(name1.toString().toLowerCase())) {
                    graph.addEdge(name2, name1);
                } else if (name1.toString().toLowerCase().contains(name2.toString().toLowerCase())) {
                    graph.addEdge(name1, name2);
                }

            }
        }
        
        if (JGraphTDependencyGraphUtils.containsCycle(graph)) {
            throw new IllegalArgumentException("Cycle is detected in the generated ReservedSQLSeparator CoverGraph, debug!");
        }

        Set<SqlElementString> nodesToBeRemoved;
        while (!graph.vertexSet().isEmpty()) {
            nodesToBeRemoved = new HashSet<>();
            for (SqlElementString name : graph.vertexSet()) {
                if (graph.inDegreeOf(name) == 0) {
                    reservedSqlSeparatorListCoverOrder.add(name);
                    nodesToBeRemoved.add(name);
                }
            }

            graph.removeAllVertices(nodesToBeRemoved);
        }

        return reservedSqlSeparatorListCoverOrder;
    }
    
    /**
     * a complicated long sql query that is used to for testing purpose;
     * some of the key words are not necessarily recognized as apache derby sql reserved symbols!!!!!
     * for example built in function TO_CHAR() is not a derby function
     */
    public static final String COMPLICATES_SQL_FOR_TEST = "SELECT\n"
                + "  e.employee_id AS \"Employee#\"\n"
                + "  , e.first_name || ' ' || e.last_name AS \"Name\"\n"
                + "  , e.email AS \"Email\"\n"
                + "  , e.phone_number AS \"Phone\"\n"
                + "  , TO_CHAR(e.hire_date, 'MM/DD/YYYY') AS \"Hire Date\"\n"
                + "  , TO_CHAR(e.salary, 'L99G999D99', 'NLS_NUMERIC_CHARACTERS = ''.,'' NLS_CURRENCY = ''$''') AS \"Salary\"\n"
                + "  , e.commission_pct AS \"Comission %\"\n"
                + "  , 'works as ' || j.job_title || ' in ' || d.department_name || ' department (manager: '\n"
                + "    || dm.first_name || ' ' || dm.last_name || ') and immediate supervisor: ' || m.first_name || ' ' || m.last_name AS \"Current Job\"\n"
                + "  , TO_CHAR(j.min_salary, 'L99G999D99', 'NLS_NUMERIC_CHARACTERS = ''.,'' NLS_CURRENCY = ''$''') || ' - ' ||\n"
                + "      TO_CHAR(j.max_salary, 'L99G999D99', 'NLS_NUMERIC_CHARACTERS = ''.,'' NLS_CURRENCY = ''$''') AS \"Current Salary\"\n"
                + "  , l.street_address || ', ' || l.postal_code || ', ' || l.city || ', ' || l.state_province || ', '\n"
                + "    || c.country_name || ' (' || r.region_name || ')' AS \"Location\"\n"
                + "  , jh.job_id AS \"History Job ID\"\n"
                + "  , 'worked from ' || TO_CHAR(jh.start_date, 'MM/DD/YYYY') || ' to ' || TO_CHAR(jh.end_date, 'MM/DD/YYYY') ||\n"
                + "    ' as ' || jj.job_title || ' in ' || dd.department_name || ' department' AS \"History Job Title\"\n"
                + "  \n"
                + "FROM employees e\n"
                + //"-- to get title of current job_id\n" +
                "  JOIN jobs j \n"
                + "    ON e.job_id = j.job_id\n"
                + //"-- to get name of current manager_id\n" +
                "  LEFT JOIN employees m \n"
                + "    ON e.manager_id = m.employee_id\n"
                + //"-- to get name of current department_id\n" +
                "  LEFT JOIN departments d \n"
                + "    ON d.department_id = e.department_id\n"
                + //"-- to get name of manager of current department\n" +
                //"-- (not equal to current manager and can be equal to the employee itself)\n" +
                "  LEFT JOIN employees dm \n"
                + "    ON d.manager_id = dm.employee_id\n"
                + //"-- to get name of location\n" +
                "  LEFT JOIN locations l\n"
                + "    ON d.location_id = l.location_id\n"
                + "  LEFT JOIN countries c\n"
                + "    ON l.country_id = c.country_id\n"
                + "  LEFT JOIN regions r\n"
                + "    ON c.region_id = r.region_id\n"
                + //"-- to get job history of employee\n" +
                "  LEFT JOIN job_history jh\n"
                + "    ON e.employee_id = jh.employee_id\n"
                + //"-- to get title of job history job_id\n" +
                "  LEFT JOIN jobs jj\n"
                + "    ON jj.job_id = jh.job_id\n"
                + //"-- to get namee of department from job history\n" +
                "  LEFT JOIN departments dd\n"
                + "    ON dd.department_id = jh.department_id\n"
                + "\n"
                + "ORDER BY e.employee_id;";

    public static void main(String[] args) {
//        SQLReservedStrings s = new SQLReservedStrings();
        for (SqlElementString rs : SqlReservedStringUtils.SQL_RESERVED_SYMBOLS) {
            System.out.println(rs.toString());
        }
    }
}
