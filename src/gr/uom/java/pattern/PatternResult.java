package gr.uom.java.pattern;

import java.util.ArrayList;
import java.util.List;

public class PatternResult {
	private String patternName;
	private List<PatternInstance> instances;
	
	public PatternResult(String patternName) {
		this.patternName = patternName;
		this.instances = new ArrayList<PatternInstance>();
	}

	public void addInstance(PatternInstance instance) {
		instances.add(instance);
		instance.setInstanceCounter(instances.size());
	}
	
	public boolean containsInstance(PatternInstance instance) {
		return instances.contains(instance);
	}

	public String getPatternName() {
		return patternName;
	}

	public List<PatternInstance> getPatternInstances() {
		return instances;
	}
}
