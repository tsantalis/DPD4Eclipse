package gr.uom.java.bytecode;

import java.util.ListIterator;

public interface AbstractMethodDeclaration {
	public String getName();

	public Access getAccess();
    
    public ListIterator<TypeObject> getParameterListIterator();

    public ListIterator<MethodInvocationObject> getMethodInvocationIterator();

    public ListIterator<String> getObjectInstantiationIterator();

    public ListIterator<LoopObject> getLoopIterator();

    public ListIterator<FieldInstructionObject> getFieldInstructionIterator();

    public boolean containsFieldInstruction(FieldObject field);

    public boolean hasParameterType(String className);
    
    public SignatureObject getSignature();
}
