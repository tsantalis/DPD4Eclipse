package dpd4eclipse.views;

import gr.uom.java.bytecode.ClassObject;
import gr.uom.java.bytecode.FieldObject;
import gr.uom.java.bytecode.MethodObject;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.viewsupport.JavaElementImageProvider;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

@SuppressWarnings("restriction")
public class IJavaElementRecovery {

	private static ICompilationUnit getICompilationUnit(IJavaProject iJavaProject, String fullName) {
		try {
			IClasspathEntry[] classpathEntries = iJavaProject.getResolvedClasspath(true);
			for(int i = 0; i < classpathEntries.length; i++){
				IClasspathEntry entry = classpathEntries[i];

				if(entry.getContentKind() == IPackageFragmentRoot.K_SOURCE){
					IPath path = entry.getPath();  
					if (path.toString().length() > iJavaProject.getProject().getName().length() + 2) {
						String fullPath = path.toString().substring(iJavaProject.getProject().getName().length() + 2) + "/" + fullName;

						ICompilationUnit iCompilationUnit = (ICompilationUnit)JavaCore.create(iJavaProject.getProject().getFile(fullPath));
						if (iCompilationUnit != null && iCompilationUnit.exists())
							return iCompilationUnit;
					}
				}
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static IField getIField(IType iType, String fieldFullSignature) {
		try {
			String fieldName = fieldFullSignature.substring(0, fieldFullSignature.indexOf(":"));
			String qualifiedType = fieldFullSignature.substring(fieldFullSignature.indexOf(":")+1, fieldFullSignature.length());
			for(IField iField : iType.getFields()) {
				if(iField.getElementName().equals(fieldName)) {
					String nonQualifiedType = Signature.toString(iField.getTypeSignature());
					if(equalTypes(qualifiedType, nonQualifiedType)) {
						return iField;
					}
				}
			}
		} catch(JavaModelException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static IMethod getIMethod(IType iType, String methodFullSignature) {
		try {
			String methodName = methodFullSignature.substring(0, methodFullSignature.indexOf("("));
			String parameters = methodFullSignature.substring(methodFullSignature.indexOf("(")+1, methodFullSignature.indexOf(")"));
			String[] qualifiedParameterTypes;
			if(parameters.equals("")) {
				qualifiedParameterTypes = new String[0];
			}
			else {
				qualifiedParameterTypes = parameters.split(", ");
			}
			String qualifiedReturnType = methodFullSignature.substring(methodFullSignature.indexOf(":")+1, methodFullSignature.length());
			for(IMethod iMethod : iType.getMethods()) {
				if(iMethod.getElementName().equals(methodName)) {
					String returnType = iMethod.getReturnType();
					String[] parameterTypes = iMethod.getParameterTypes();
					String nonQualifiedReturnType = Signature.toString(returnType);
					boolean returnTypeMatch = false;
					if(equalTypes(qualifiedReturnType, nonQualifiedReturnType)) {
						returnTypeMatch = true;
					}
					boolean parameterTypesMatch = true;
					if(parameterTypes.length == qualifiedParameterTypes.length) {
						for(int i=0; i<qualifiedParameterTypes.length; i++) {
							String nonQualifiedParameterType = Signature.toString(parameterTypes[i]);
							String qualifiedParameterType = qualifiedParameterTypes[i];
							if(!equalTypes(qualifiedParameterType, nonQualifiedParameterType)) {
								parameterTypesMatch = false;
							}
						}
					}
					else {
						parameterTypesMatch = false;
					}
					if(returnTypeMatch && parameterTypesMatch)
						return iMethod;
				}
			}
		} catch(JavaModelException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static boolean equalTypes(String qualifiedType, String nonQualifiedType) {
		qualifiedType = qualifiedType.replaceAll("\\$", ".");
		if(nonQualifiedType.contains("<") && nonQualifiedType.contains(">")) {
			String nonQualifiedTypeWithoutGeneric = nonQualifiedType.substring(0, nonQualifiedType.indexOf("<")) +
					nonQualifiedType.substring(nonQualifiedType.lastIndexOf(">")+1, nonQualifiedType.length());
			return qualifiedType.endsWith(nonQualifiedTypeWithoutGeneric);
		}
		return qualifiedType.endsWith(nonQualifiedType);
	}

	public static IType getIType(ICompilationUnit iCompilationUnit, String qualifiedClassName) {
		List<IType> allTypes = getAllTypesIncludingAnonymous(iCompilationUnit);
		for(IType iType : allTypes) {
			if(iType.getFullyQualifiedName().equals(qualifiedClassName)) {
				return iType;
			}
		}
		return null;
	}

	private static List<IType> getAllTypesIncludingAnonymous(ICompilationUnit iCompilationUnit) {
		List<IType> allTypes = new ArrayList<IType>();
		try {
			IType[] topLevelAndNestedTypes = iCompilationUnit.getAllTypes();
			for(IType iType : topLevelAndNestedTypes) {
				allTypes.add(iType);
				for(IField iField : iType.getFields()) {
					IJavaElement[] children = iField.getChildren();
					for(IJavaElement element : children) {
						if(element instanceof IType) {
							IType type = (IType)element;
							if(type.isAnonymous()) {
								allTypes.add(type);
							}
						}
					}
				}
				for(IMethod iMethod : iType.getMethods()) {
					IJavaElement[] children = iMethod.getChildren();
					for(IJavaElement element : children) {
						if(element instanceof IType) {
							IType type = (IType)element;
							if(type.isAnonymous()) {
								allTypes.add(type);
							}
						}
					}
				}
			}
		} catch(JavaModelException e) {
			e.printStackTrace();
		}
		return allTypes;
	}

	public static ICompilationUnit getICompilationUnitFromFullyQualifiedClassName(IJavaProject iJavaProject, String qualifiedClassName) {
		String compilationUnitName = null;
		if(qualifiedClassName.contains("$")) {
			//inner class
			String enclosingClass = qualifiedClassName.substring(0, qualifiedClassName.indexOf("$"));
			compilationUnitName = enclosingClass.replace(".", "/") + ".java";
		}
		else {
			compilationUnitName = qualifiedClassName.replace(".", "/") + ".java";
		}
		ICompilationUnit sourceJavaElement = getICompilationUnit(iJavaProject, compilationUnitName);
		return sourceJavaElement;
	}

	public static IType getIType(IJavaProject iJavaProject, ClassObject classObject) {
		IType iType = null;
		String elementFullName = classObject.getName();
		ICompilationUnit sourceJavaElement = getICompilationUnitFromFullyQualifiedClassName(iJavaProject, elementFullName);
		if(sourceJavaElement != null) {
			iType = getIType(sourceJavaElement, elementFullName);
		}
		return iType;
	}

	public static IMethod getIMethod(IJavaProject iJavaProject, MethodObject method) {
		IMethod iMethod = null;
		String elementFullName = method.getSignature().toString();
		String qualifiedClassName = elementFullName.substring(0, elementFullName.indexOf("::"));
		String methodFullSignature = elementFullName.substring(elementFullName.indexOf("::")+2, elementFullName.length());
		ICompilationUnit sourceJavaElement = getICompilationUnitFromFullyQualifiedClassName(iJavaProject, qualifiedClassName);
		if(sourceJavaElement != null) {
			IType type = getIType(sourceJavaElement, qualifiedClassName);
			if(type != null) {
				iMethod = getIMethod(type, methodFullSignature);
			}
		}
		return iMethod;
	}

	public static IField getIField(IJavaProject iJavaProject, FieldObject field) {
		IField iField = null;
		String elementFullName = field.getSignature();
		String qualifiedClassName = elementFullName.substring(0, elementFullName.indexOf("::"));
		String fieldFullSignature = elementFullName.substring(elementFullName.indexOf("::")+2, elementFullName.length());
		ICompilationUnit sourceJavaElement = getICompilationUnitFromFullyQualifiedClassName(iJavaProject, qualifiedClassName);
		if(sourceJavaElement != null) {
			IType type = getIType(sourceJavaElement, qualifiedClassName);
			if(type != null) {
				iField = getIField(type, fieldFullSignature);
			}
		}
		return iField;
	}

	public static ImageDescriptor getImageDescriptor(IJavaElement javaElement) {
		return new JavaElementImageProvider().getJavaImageDescriptor(javaElement, JavaElementImageProvider.OVERLAY_ICONS | JavaElementImageProvider.SMALL_ICONS);
	}
	
	public static Image getImage(IJavaElement javaElement) {
		return JavaPlugin.getImageDescriptorRegistry().get(getImageDescriptor(javaElement));
	}
}
