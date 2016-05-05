package dpd4eclipse.visualization;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import gr.uom.java.bytecode.BytecodeReader;
import gr.uom.java.bytecode.ClassObject;
import gr.uom.java.bytecode.FieldInstructionObject;
import gr.uom.java.bytecode.FieldObject;
import gr.uom.java.bytecode.MethodInvocationObject;
import gr.uom.java.bytecode.MethodObject;
import gr.uom.java.bytecode.SignatureObject;
import gr.uom.java.bytecode.SystemObject;
import gr.uom.java.pattern.PatternInstance;
import gr.uom.java.pattern.PatternInstance.RoleType;

public class DesignPatternVisualizationData {
	private Map<MethodObject, Map<MethodObject, Integer>> internalMethodInvocationMap;
	private Map<MethodObject, Map<MethodObject, Integer>> externalMethodInvocationMap;
	private Map<MethodObject, Map<FieldObject, Integer>> internalFieldReadMap;
	//the key is the role in the design pattern
	private Map<String, ClassObject> classMap;
	private Map<String, Set<MethodObject>> methodMap;
	private Map<String, Set<FieldObject>> fieldMap;
	
	public DesignPatternVisualizationData(PatternInstance patternInstance) {
		this.internalMethodInvocationMap = new LinkedHashMap<MethodObject, Map<MethodObject, Integer>>();
		this.externalMethodInvocationMap = new LinkedHashMap<MethodObject, Map<MethodObject, Integer>>();
		this.internalFieldReadMap = new LinkedHashMap<MethodObject, Map<FieldObject, Integer>>();
		this.classMap = new LinkedHashMap<String, ClassObject>();
		this.methodMap = new LinkedHashMap<String, Set<MethodObject>>();
		this.fieldMap = new LinkedHashMap<String, Set<FieldObject>>();
		SystemObject systemObject = BytecodeReader.getSystemObject();
		
		for(PatternInstance.Entry entry : patternInstance.getEntrySet()) {
			if(entry.getRoleType().equals(RoleType.CLASS)) {
				ClassObject classObject = systemObject.getClassObject(entry.getElementName());
				if(classObject != null) {
					classMap.put(entry.getRoleName(), classObject);
				}
			}
			else if(entry.getRoleType().equals(RoleType.METHOD)) {
				String elementFullName = entry.getElementName();
				String qualifiedClassName = elementFullName.substring(0, elementFullName.indexOf("::"));
				String methodFullSignature = elementFullName.substring(elementFullName.indexOf("::")+2, elementFullName.length());
				
				String methodName = methodFullSignature.substring(0, methodFullSignature.indexOf("("));
				String parameters = methodFullSignature.substring(methodFullSignature.indexOf("(")+1, methodFullSignature.indexOf(")"));
				String[] qualifiedParameterTypes;
				if(parameters.equals("")) {
					qualifiedParameterTypes = new String[0];
				}
				else {
					qualifiedParameterTypes = parameters.split(", ");
				}
				List<String> qualifiedParameterTypeList = Arrays.asList(qualifiedParameterTypes);
				String qualifiedReturnType = methodFullSignature.substring(methodFullSignature.indexOf(":")+1, methodFullSignature.length());
				
				ClassObject classObject = systemObject.getClassObject(qualifiedClassName);
				if(classObject != null) {
					SignatureObject signature = new SignatureObject(qualifiedClassName, methodName, qualifiedReturnType, qualifiedParameterTypeList);
					MethodObject methodObject = classObject.getMethod(signature);
					if(methodObject != null) {
						if(methodMap.containsKey(entry.getRoleName())) {
							methodMap.get(entry.getRoleName()).add(methodObject);
						}
						else {
							LinkedHashSet<MethodObject> methods = new LinkedHashSet<MethodObject>();
							methods.add(methodObject);
							methodMap.put(entry.getRoleName(), methods);
						}
						ListIterator<MethodInvocationObject> methodInvocationIterator = methodObject.getMethodInvocationIterator();
						while(methodInvocationIterator.hasNext()) {
							MethodInvocationObject methodInvocation = methodInvocationIterator.next();
							if(methodInvocation.getOriginClassName().equals(methodObject.getClassName())) {
								MethodObject invokedMethod = classObject.findMethodIncludingSuperTypes(methodInvocation.getSignature());
								if(invokedMethod != null) {
									insertToMap(methodObject, invokedMethod, internalMethodInvocationMap);
								}
							}
							else if(getClassNames().contains(methodInvocation.getOriginClassName())) {
								MethodObject invokedMethod = null;
								for(ClassObject classObject2 : classMap.values()) {
									if(!classObject2.equals(classObject)) {
										MethodObject invokedMethod2 = classObject2.findMethodIncludingSuperTypes(methodInvocation.getSignature());
										if(invokedMethod2 != null) {
											invokedMethod = invokedMethod2;
											break;
										}
									}
								}
								if(invokedMethod != null) {
									insertToMap(methodObject, invokedMethod, externalMethodInvocationMap);
								}
							}
						}
						ListIterator<FieldInstructionObject> fieldInstructionIterator = methodObject.getFieldInstructionIterator();
						while(fieldInstructionIterator.hasNext()) {
							FieldInstructionObject fieldInstruction = fieldInstructionIterator.next();
							if(fieldInstruction.getOwnerClass().equals(methodObject.getClassName())) {
								FieldObject accessedField = classObject.findFieldIncludingSuperTypes(fieldInstruction);
								if(accessedField != null) {
									insertToMap(methodObject, accessedField, internalFieldReadMap, 1);
								}
							}
						}
					}
				}
			}
			else if(entry.getRoleType().equals(RoleType.FIELD)) {
				String elementFullName = entry.getElementName();
				String qualifiedClassName = elementFullName.substring(0, elementFullName.indexOf("::"));
				String fieldFullSignature = elementFullName.substring(elementFullName.indexOf("::")+2, elementFullName.length());
				String fieldName = fieldFullSignature.substring(0, fieldFullSignature.indexOf(":"));
				String qualifiedType = fieldFullSignature.substring(fieldFullSignature.indexOf(":")+1, fieldFullSignature.length());
				ClassObject classObject = systemObject.getClassObject(qualifiedClassName);
				if(classObject != null) {
					FieldInstructionObject fieldSignature = new FieldInstructionObject(qualifiedClassName, qualifiedType, fieldName);
					FieldObject fieldObject = classObject.getField(fieldSignature);
					if(fieldObject != null) {
						if(fieldMap.containsKey(entry.getRoleName())) {
							fieldMap.get(entry.getRoleName()).add(fieldObject);
						}
						else {
							LinkedHashSet<FieldObject> fields = new LinkedHashSet<FieldObject>();
							fields.add(fieldObject);
							fieldMap.put(entry.getRoleName(), fields);
						}
					}
				}
			}
		}
	}

	private Set<String> getClassNames() {
		Set<String> classNames = new LinkedHashSet<String>();
		for(String key : classMap.keySet()) {
			ClassObject classObject = classMap.get(key);
			classNames.add(classObject.getName());
		}
		return classNames;
	}

	public Map<String, ClassObject> getClassMap() {
		return classMap;
	}

	public Set<FieldObject> getPatternFieldsForClass(ClassObject classObject) {
		Set<FieldObject> fields = new LinkedHashSet<FieldObject>();
		for(String roleName : this.fieldMap.keySet()) {
			Set<FieldObject> fieldObjects = this.fieldMap.get(roleName);
			for(FieldObject fieldObject : fieldObjects) {
				if(fieldObject.getClassName().equals(classObject.getName())) {
					fields.add(fieldObject);
				}
			}
		}
		return fields;
	}

	public Set<MethodObject> getPatternMethodsForClass(ClassObject classObject) {
		Set<MethodObject> methods = new LinkedHashSet<MethodObject>();
		for(String roleName : this.methodMap.keySet()) {
			Set<MethodObject> methodObjects = this.methodMap.get(roleName);
			for(MethodObject methodObject : methodObjects) {
				if(methodObject.getClassName().equals(classObject.getName())) {
					methods.add(methodObject);
				}
			}
		}
		return methods;
	}

	public Set<MethodObject> getMethodsForClass(ClassObject classObject) {
		Set<MethodObject> methods = new LinkedHashSet<MethodObject>();
		methods.addAll(getPatternMethodsForClass(classObject));
		
		for(MethodObject methodObject : this.internalMethodInvocationMap.keySet()) {
			if(methodObject.getClassName().equals(classObject.getName())) {
				Map<MethodObject, Integer> methodInvocationMap = this.internalMethodInvocationMap.get(methodObject);
				for(MethodObject methodInvocation : methodInvocationMap.keySet()) {
					methods.add(methodInvocation);
				}
			}
		}
		for(MethodObject methodObject : this.externalMethodInvocationMap.keySet()) {
			Map<MethodObject, Integer> methodInvocationMap = this.externalMethodInvocationMap.get(methodObject);
			if(!methodObject.getClassName().equals(classObject.getName())) {
				for(MethodObject methodInvocation : methodInvocationMap.keySet()) {
					if(methodInvocation.getClassName().equals(classObject.getName()) || classObject.isSubclassOf(methodInvocation.getClassName())) {
						methods.add(methodInvocation);
					}
				}
			}
		}
		return methods;
	}

	public Set<FieldObject> getFieldsForClass(ClassObject classObject) {
		Set<FieldObject> fields = new LinkedHashSet<FieldObject>();
		fields.addAll(getPatternFieldsForClass(classObject));
		
		for(MethodObject methodObject : this.internalFieldReadMap.keySet()) {
			if(methodObject.getClassName().equals(classObject.getName())) {
				Map<FieldObject, Integer> fieldReadMap = this.internalFieldReadMap.get(methodObject);
				for(FieldObject fieldInstruction : fieldReadMap.keySet()) {
					fields.add(fieldInstruction);
				}
			}
		}
		return fields;
	}

	public Map<MethodObject, Map<MethodObject, Integer>> getInternalMethodInvocationMapForClass(ClassObject classObject) {
		Map<MethodObject, Map<MethodObject, Integer>> internalMethodInvocationMap = new LinkedHashMap<MethodObject, Map<MethodObject, Integer>>();
		for(MethodObject methodObject : this.internalMethodInvocationMap.keySet()) {
			if(methodObject.getClassName().equals(classObject.getName())) {
				internalMethodInvocationMap.put(methodObject, this.internalMethodInvocationMap.get(methodObject));
			}
		}
		return internalMethodInvocationMap;
	}

	public Map<MethodObject, Map<MethodObject, Integer>> getExternalMethodInvocationMapForClass(ClassObject classObject) {
		Map<MethodObject, Map<MethodObject, Integer>> externalMethodInvocationMap = new LinkedHashMap<MethodObject, Map<MethodObject, Integer>>();
		for(MethodObject methodObject : this.externalMethodInvocationMap.keySet()) {
			if(methodObject.getClassName().equals(classObject.getName())) {
				externalMethodInvocationMap.put(methodObject, this.externalMethodInvocationMap.get(methodObject));
			}
		}
		return externalMethodInvocationMap;
	}

	public Map<MethodObject, Map<FieldObject, Integer>> getInternalFieldReadMapForClass(ClassObject classObject) {
		Map<MethodObject, Map<FieldObject, Integer>> internalFieldReadMap = new LinkedHashMap<MethodObject, Map<FieldObject, Integer>>();
		for(MethodObject methodObject : this.internalFieldReadMap.keySet()) {
			if(methodObject.getClassName().equals(classObject.getName())) {
				internalFieldReadMap.put(methodObject, this.internalFieldReadMap.get(methodObject));
			}
		}
		return internalFieldReadMap;
	}

	private void insertToMap(MethodObject method, MethodObject methodInvocation, Map<MethodObject, Map<MethodObject, Integer>> map) {
		if(map.containsKey(method)) {
			Map<MethodObject, Integer> invocationMap = map.get(method);
			if(invocationMap.containsKey(methodInvocation)) {
				invocationMap.put(methodInvocation, invocationMap.get(methodInvocation) + 1);
			}
			else {
				invocationMap.put(methodInvocation, 1);
			}
		}
		else {
			Map<MethodObject, Integer> invocationMap = new LinkedHashMap<MethodObject, Integer>();
			invocationMap.put(methodInvocation, 1);
			map.put(method, invocationMap);
		}
	}

	private void insertToMap(MethodObject method, FieldObject fieldInstruction, Map<MethodObject, Map<FieldObject, Integer>> map, int count) {
		if(map.containsKey(method)) {
			Map<FieldObject, Integer> fieldAccessMap = map.get(method);
			if(fieldAccessMap.containsKey(fieldInstruction)) {
				fieldAccessMap.put(fieldInstruction, fieldAccessMap.get(fieldInstruction) + count);
			}
			else {
				fieldAccessMap.put(fieldInstruction, count);
			}
		}
		else {
			Map<FieldObject, Integer> fieldAccessMap = new LinkedHashMap<FieldObject, Integer>();
			fieldAccessMap.put(fieldInstruction, count);
			map.put(method, fieldAccessMap);
		}
	}
}
