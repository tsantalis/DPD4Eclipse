package dpd4eclipse.views;

import dpd4eclipse.visualization.DesignPatternVisualizationData;

public class VisualizationDataSingleton {
	private static DesignPatternVisualizationData data;

	public static DesignPatternVisualizationData getData() {
		return data;
	}

	public static void setData(DesignPatternVisualizationData data) {
		VisualizationDataSingleton.data = data;
	}
}
