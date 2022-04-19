package operation.sql.predefined.utils;

import basic.reproduce.SimpleReproducible;

/**
 * 
 * @author tanxu
 *
 */
public enum SqlSortOrderType implements SimpleReproducible{
	DESCEND("DESC", "descending"),
	ASCEND("ASC", "ascending");
	
	private final String sqlSymbol;
	private final String description;
	
	SqlSortOrderType(String sqlSymbol, String description){
		this.sqlSymbol = sqlSymbol;
		this.description = description;
	}

	public String getSqlSymbol() {
		return sqlSymbol;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public SqlSortOrderType reproduce() {
		return this;
	}

	
}

