package gr.uom.java.bytecode;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Map;

public class ClassObject {

	private String name;
	private List<ConstructorObject> constructorList;
	private List<MethodObject> methodList;
	private List<FieldObject> fieldList;
	private String superclass;
	private List<String> interfaceList;
	private boolean _abstract;
    private boolean _interface;
    private boolean _static;
    private boolean _enum;
    private Access access;

    public ClassObject() {
		this.constructorList = new ArrayList<ConstructorObject>();
		this.methodList = new ArrayList<MethodObject>();
		this.interfaceList = new ArrayList<String>();
		this.fieldList = new ArrayList<FieldObject>();
		this._abstract = false;
        this._interface = false;
        this._static = false;
        this.access = Access.NONE;
    }
    
    public MethodObject getMethod(SignatureObject signature) {
        ListIterator<MethodObject> mi = getMethodIterator();
        while(mi.hasNext()) {
            MethodObject mo = mi.next();
            if(mo.getSignature().equals(signature))
                return mo;
        }
        return getMethodGenericMatch(signature);
    }

    private MethodObject getMethodGenericMatch(SignatureObject signature) {
    	ListIterator<MethodObject> mi = getMethodIterator();
        while(mi.hasNext()) {
            MethodObject mo = mi.next();
            if(mo.getSignature().equalsGeneric(signature))
                return mo;
        }
        return null;
    }

    public MethodObject findMethodIncludingSuperTypes(SignatureObject signature) {
    	MethodObject method = getMethod(signature);
    	if(method != null) {
    		return method;
    	}
    	else {
    		ListIterator<String> superClassIterator = getSuperclassIterator();
    		while(superClassIterator.hasNext()) {
    			String superClassName = superClassIterator.next();
    			ClassObject superClass = BytecodeReader.getSystemObject().getClassObject(superClassName);
    			if(superClass != null) {
    				SignatureObject updatedSignature = new SignatureObject(superClassName,
    						signature.getMethodName(), signature.getReturnType(), signature.getParameterList());
    				MethodObject superMethod = superClass.findMethodIncludingSuperTypes(updatedSignature);
    				if(superMethod != null) {
    					return superMethod;
    				}
    			}
    		}
    	}
    	return null;
    }

    public FieldObject findFieldIncludingSuperTypes(FieldInstructionObject fio) {
    	FieldObject field = getField(fio);
    	if(field != null) {
    		return field;
    	}
    	else {
    		ListIterator<String> superClassIterator = getSuperclassIterator();
    		while(superClassIterator.hasNext()) {
    			String superClassName = superClassIterator.next();
    			ClassObject superClass = BytecodeReader.getSystemObject().getClassObject(superClassName);
    			if(superClass != null) {
    				FieldInstructionObject updatedSignature = new FieldInstructionObject(superClassName, fio.getClassType(), fio.getName());
    				FieldObject superField = superClass.findFieldIncludingSuperTypes(updatedSignature);
    				if(superField != null) {
    					return superField;
    				}
    			}
    		}
    	}
    	return null;
    }

    public FieldObject getField(FieldInstructionObject fio) {
    	ListIterator<FieldObject> fi = getFieldIterator();
    	while(fi.hasNext()) {
    		FieldObject fo = fi.next();
    		if(fo.getSignature().equals(fio.getSignature()))
    			return fo;
    	}
    	return getFieldGenericMatch(fio);
    }

    public FieldObject getFieldGenericMatch(FieldInstructionObject fio) {
    	ListIterator<FieldObject> fi = getFieldIterator();
    	while(fi.hasNext()) {
    		FieldObject fo = fi.next();
    		if(fo.equalsGeneric(fio))
    			return fo;
    	}
    	return null;
    }

    public boolean isSubclassOf(String className) {
    	if(this.name.equals(className)) {
    		return true;
    	}
    	else {
    		ListIterator<String> superClassIterator = getSuperclassIterator();
    		while(superClassIterator.hasNext()) {
    			String superClassName = superClassIterator.next();
    			ClassObject superClass = BytecodeReader.getSystemObject().getClassObject(superClassName);
    			if(superClass != null) {
    				boolean isSubclass = superClass.isSubclassOf(className);
    				if(isSubclass) {
    					return true;
    				}
    			}
    		}
    	}
    	return false;
    }

    public boolean hasFieldType(String className) {
        ListIterator<FieldObject> fi = getFieldIterator();
        while(fi.hasNext()) {
            FieldObject fo = fi.next();
            if(fo.getType().getClassType().equals(className))
                return true;
        }
        return false;
    }

    public Map<MethodObject, List<MethodInvocationObject>> iterativeMethodInvocations(String origin) {
    	Map<MethodObject, List<MethodInvocationObject>> methodInvocationMap =
    		new LinkedHashMap<MethodObject, List<MethodInvocationObject>>();
        ListIterator<MethodObject> mi = getMethodIterator();
        while(mi.hasNext()) {
            MethodObject method = mi.next();
            List<MethodInvocationObject> iterativeMethodInvocations = method.iterativeMethodInvocations(origin);
            if(!iterativeMethodInvocations.isEmpty())
            	methodInvocationMap.put(method, iterativeMethodInvocations);
        }
        return methodInvocationMap;
    }

    public void setAccess(Access access) {
        this.access = access;
    }

    public Access getAccess() {
        return access;
    }

    public void setSuperclass(String superclass) {
		this.superclass = superclass;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public boolean addMethod(MethodObject method) {
		return methodList.add(method);
	}
	
	public boolean addInterface(String i) {
		return interfaceList.add(i);
	}
	
	public boolean addConstructor(ConstructorObject c) {
		return constructorList.add(c);
	}
	
	public boolean addField(FieldObject f) {
		return fieldList.add(f);
	}
	
	public ListIterator<ConstructorObject> getConstructorIterator() {
		return constructorList.listIterator();
	}
	
	public ListIterator<MethodObject> getMethodIterator() {
		return methodList.listIterator();
	}
	
	public ListIterator<String> getInterfaceIterator() {
		return interfaceList.listIterator();
	}

    public ListIterator<String> getSuperclassIterator() {
		List<String> superclassList = new ArrayList<String>(interfaceList);
		if(superclass != null)
			superclassList.add(superclass);
		return superclassList.listIterator();
	}

	public ListIterator<FieldObject> getFieldIterator() {
		return fieldList.listIterator();
	}

	public String getName() {
		return name;
	}

	public String getSuperclass() {
		return superclass;
	}
	
	public void setAbstract(boolean abstr) {
		this._abstract = abstr;
	}
	
	public boolean isAbstract() {
		return this._abstract;
	}

    public void setInterface(boolean i) {
        this._interface = i;
    }

    public boolean isInterface() {
        return this._interface;
    }

    public boolean isStatic() {
        return this._static;
    }

    public void setStatic(boolean s) {
        this._static = s;
    }
    
    public boolean isEnum() {
    	return this._enum;
    }
    
    public boolean isEnumWithMultipleConstants() {
    	if(this._enum) {
	    	int namedConstantsCount = 0;
	    	for(FieldObject field : fieldList) {
	    		if(!field.getName().equals("ENUM$VALUES") &&
	    				field.getType().getClassType().equals(this.getName())) {
	    			namedConstantsCount++;
	    		}
	    	}
	    	return namedConstantsCount > 1;
    	}
    	return false;
    }
    
    public void setEnum(boolean e) {
    	this._enum = e;
    }
    
    public boolean implementsInterface(String i) {
		return interfaceList.contains(i);
	}
    
    public boolean extendsClassType(String classType) {
    	if((superclass != null && superclass.equals(classType)) || interfaceList.contains(classType))
    		return true;
    	return false;
    }

    public boolean isInner() {
    	return name.contains("$");
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if(!access.equals(Access.NONE))
            sb.append(access.toString()).append(" ");
        if(_static)
            sb.append("static").append(" ");
        if(_interface)
            sb.append("interface").append(" ");
        else if(_abstract)
            sb.append("abstract class").append(" ");
        else
            sb.append("class").append(" ");
        sb.append(name).append(" ");
        sb.append("extends ").append(superclass);
        if(!interfaceList.isEmpty()) {
            sb.append(" ").append("implements ");
            for(int i=0; i<interfaceList.size()-1; i++)
                sb.append(interfaceList.get(i)).append(", ");
            sb.append(interfaceList.get(interfaceList.size()-1));
        }
        sb.append("\n\n").append("Fields:");
        for(FieldObject field : fieldList)
            sb.append("\n").append(field.toString());

        sb.append("\n\n").append("Constructors:");
        for(ConstructorObject constructor : constructorList)
            sb.append("\n").append(constructor.toString());

        sb.append("\n\n").append("Methods:");
        for(MethodObject method : methodList)
            sb.append("\n").append(method.toString());

        return sb.toString();
    }
}