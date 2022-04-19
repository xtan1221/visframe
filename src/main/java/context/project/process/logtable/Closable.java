package context.project.process.logtable;

/**
 * a process should be closeable;
 * once it is closed, it cannot have any impact on the original host VisProjectDBContext
 * @author tanxu
 */
public interface Closable {
	/**
	 * close this {@link #Closable} object so that it can no longer impact the original host VisProjectDBContext;
	 * invoked when this object is finished or aborted;
	 */
	void close();
}
