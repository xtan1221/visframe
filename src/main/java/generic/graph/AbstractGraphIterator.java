package generic.graph;

public abstract class AbstractGraphIterator implements GraphIterator{
	
	protected boolean vertexDone = false;
	protected boolean edgeDone = false;
	
	
	@Override
	public boolean vertexAreDone() {
		return vertexDone;
	}

	@Override
	public boolean edgeAreDone() {
		return edgeDone;
	}
}
