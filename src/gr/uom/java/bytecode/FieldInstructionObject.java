package gr.uom.java.bytecode;

public class FieldInstructionObject {

    private String ownerClass;
    private String classType;
    private String name;
    private volatile int hashCode = 0;

    public FieldInstructionObject(String ownerClass, String classType, String name) {
        this.ownerClass = ownerClass;
        this.classType = classType;
        this.name = name;
    }

    public String getOwnerClass() {
        return ownerClass;
    }

    public String getClassType() {
        return classType;
    }

    public String getName() {
        return name;
    }

    public String getSignature() {
    	StringBuilder sb = new StringBuilder();
        sb.append(ownerClass).append("::");
        sb.append(name);
        sb.append(":").append(classType);
        return sb.toString();
    }

    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }

        if (o instanceof FieldInstructionObject) {
            FieldInstructionObject fio = (FieldInstructionObject)o;
            return this.ownerClass.equals(fio.ownerClass) && this.name.equals(fio.name) && this.classType.equals(fio.classType);
        }
        return false;
    }

    public int hashCode() {
    	if(hashCode == 0) {
    		int result = 17;
    		result = 37*result + ownerClass.hashCode();
    		result = 37*result + name.hashCode();
    		result = 37*result + classType.hashCode();
    		hashCode = result;
    	}
    	return hashCode;
    }

}
