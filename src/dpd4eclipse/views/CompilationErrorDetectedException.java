package dpd4eclipse.views;

import java.util.List;

import org.eclipse.core.resources.IMarker;

public class CompilationErrorDetectedException extends Exception {

	private static final long serialVersionUID = 1L;
	private List<IMarker> markers;

	public CompilationErrorDetectedException(List<IMarker> markers) {
		this.markers = markers;
	}

	public List<IMarker> getMarkers() {
		return markers;
	}
}
