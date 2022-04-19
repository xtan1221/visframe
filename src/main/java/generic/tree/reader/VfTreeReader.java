package generic.tree.reader;

import java.io.IOException;
import java.sql.SQLException;

import generic.tree.VfTree;


/**
 * base class that read from a data source to build a VfTree object;
 * 
 * @author tanxu
 *
 */
public abstract class VfTreeReader implements VfTree{
	/**
	 * perform reading from data source and building the vftree
	 * @throws IOException
	 * @throws SQLException 
	 */
	protected abstract void perform() throws IOException, SQLException;
}
