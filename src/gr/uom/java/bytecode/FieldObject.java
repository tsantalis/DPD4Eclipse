package gr.uom.java.bytecode;

public class FieldObject {

    private String name;
    private TypeObject type;
    private boolean _static;
    private Access access;
    private String className;

    public FieldObject(TypeObject type, String name) {
        this.type = type;
        this.name = name;
        this._static = false;
        this.access = Access.NONE;
    }

    public void setAccess(Access access) {
        this.access = access;
    }

    public Access getAccess() {
        return access;
    }

    public String getName() {
        return name;
    }

    public TypeObject getType() {
        return type;
    }

    public boolean isStatic() {
        return _static;
    }

    public void setStatic(boolean s) {
        _static = s;
    }

    public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }

        if (o instanceof FieldObject) {
            FieldObject fieldObject = (FieldObject)o;
            return this.name.equals(fieldObject.name) && this.type.equals(fieldObject.type);
        }
        return false;
    }

    public boolean equals(FieldInstructionObject fio) {
        return this.name.equals(fio.getName()) && this.type.getClassType().equals(fio.getClassType());
    }

    public String getSignature() {
    	StringBuilder sb = new StringBuilder();
        sb.append(className).append("::");
        sb.append(name);
        sb.append(":").append(type);
        return sb.toString();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if(!access.equals(Access.NONE))
            sb.append(access.toString()).append(" ");
        if(_static)
            sb.append("static").append(" ");
        sb.append(type.toString()).append(" ");
        sb.append(name);
        return sb.toString();
    }
}