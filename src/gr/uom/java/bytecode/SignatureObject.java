package gr.uom.java.bytecode;

import java.util.List;

public class SignatureObject {
    private String className;
    private String methodName;
    private List<String> parameterList;
    private String returnType;

    public SignatureObject(String className, String methodName, String returnType, List<String> parameterList) {
        this.className = className;
        this.methodName = methodName;
        this.returnType = returnType;
        this.parameterList = parameterList;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public List<String> getParameterList() {
        return parameterList;
    }

    public String getReturnType() {
        return returnType;
    }

    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }

        if (o instanceof SignatureObject) {
            SignatureObject signatureObject = (SignatureObject)o;

            return className.equals(signatureObject.className) &&
                methodName.equals(signatureObject.methodName) &&
                returnType.equals(signatureObject.returnType) &&
                parameterList.equals(signatureObject.parameterList);
        }
        return false;
    }

    public boolean equalsGeneric(Object o) {
        if(this == o) {
            return true;
        }

        if (o instanceof SignatureObject) {
            SignatureObject signatureObject = (SignatureObject)o;

            return className.equals(signatureObject.className) &&
                methodName.equals(signatureObject.methodName) &&
                equalClassType(returnType, signatureObject.returnType) &&
                equalParameterTypes(parameterList, signatureObject.parameterList);
        }
        return false;
    }

    private boolean equalParameterTypes(List<String> parameterTypes1, List<String> parameterTypes2) {
    	if(parameterTypes1.size() == parameterTypes2.size()) {
			int i = 0;
			for(String type1 : parameterTypes1) {
				String type2 = parameterTypes2.get(i);
				if(!equalClassType(type1, type2))
					return false;
				i++;
			}
		}
		else return false;
    	return true;
    }

    private boolean equalClassType(String thisClassType, String otherClassType) {
    	//this case covers type parameter names, such as E, K, N, T, V, S, U
    	if(thisClassType.length() == 1 || otherClassType.length() == 1)
    		return true;
    	else
    		return thisClassType.equals(otherClassType);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if(!className.equals(methodName))
            sb.append(className).append("::");
        sb.append(methodName);
        sb.append("(");
        if(!parameterList.isEmpty()) {
            for(int i=0; i<parameterList.size()-1; i++)
                sb.append(parameterList.get(i)).append(", ");
            sb.append(parameterList.get(parameterList.size()-1));
        }
        sb.append(")");
        if(returnType != null)
            sb.append(":").append(returnType);
        return sb.toString();
    }
}
