package gr.uom.java.bytecode;

import java.io.*;
import java.util.*;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.IJavaProject;
import org.objectweb.asm.*;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.util.TraceSignatureVisitor;
import org.objectweb.asm.tree.*;

public class BytecodeReader {

	private static SystemObject so;
	private static IJavaProject examinedProject;

	public BytecodeReader(IJavaProject iJavaProject, File dir, IProgressMonitor monitor) {
		so = new SystemObject();
		examinedProject = iJavaProject;
		if(monitor != null)
			monitor.beginTask("Parsing selected Java Project", getNumberOfClassFiles(dir));
		recurse(dir, monitor);
		if(monitor != null)
			monitor.done();
	}

	private void recurse(File file, IProgressMonitor monitor) {
		if(file.isDirectory()) {
			File files[] = file.listFiles();
            for (File afile : files) {
                if (afile.isDirectory())
                    recurse(afile, monitor);
                else if (afile.getName().toLowerCase().endsWith(".class")) {
                	if(monitor != null && monitor.isCanceled())
		    			throw new OperationCanceledException();
                    parseBytecode(afile);
                    if(monitor != null)
						monitor.worked(1);
                }
            }
		}
		else if(file.getName().toLowerCase().endsWith(".class")) {
			if(monitor != null && monitor.isCanceled())
    			throw new OperationCanceledException();
			parseBytecode(file);
			if(monitor != null)
				monitor.worked(1);
		}
	}

	private static int getNumberOfClassFiles(File file) {
		int count = 0;
		if(file.isDirectory()) {
			File files[] = file.listFiles();
            for (File afile : files) {
                if (afile.isDirectory())
                	count += getNumberOfClassFiles(afile);
                else if (afile.getName().toLowerCase().endsWith(".class")) {
                    count++;
                }
            }
		}
		else if(file.getName().toLowerCase().endsWith(".class"))
			count++;
		return count;
	}

	private void parseBytecode(File file) {
		final ClassObject co = new ClassObject();
		try {
			FileInputStream fin = new FileInputStream(file);
			ClassReader cr = new ClassReader(new DataInputStream(fin));
			ClassNode cn = new ClassNode();
    		cr.accept(cn, ClassReader.SKIP_DEBUG);
    		
    		String name = cn.name;
    		co.setName(name.replaceAll("/", "."));

            if ((cn.access & Opcodes.ACC_INTERFACE) != 0)
    			co.setInterface(true);
            else if ((cn.access & Opcodes.ACC_ABSTRACT) != 0)
    			co.setAbstract(true);
            else if ((cn.access & Opcodes.ACC_ENUM) != 0)
            	co.setEnum(true);

            if ((cn.access & Opcodes.ACC_PUBLIC) != 0)
                co.setAccess(Access.PUBLIC);
            else if ((cn.access & Opcodes.ACC_PROTECTED) != 0)
                co.setAccess(Access.PROTECTED);
            else if ((cn.access & Opcodes.ACC_PRIVATE) != 0)
                co.setAccess(Access.PRIVATE);
            if ((cn.access & Opcodes.ACC_STATIC) != 0)
                    co.setStatic(true);

            String superClass = cn.superName;
            boolean extendsTestCase = false;
            if(superClass != null) {
            	co.setSuperclass(superClass.replaceAll("/", "."));
            	if(superClass.equals("junit/framework/TestCase")) {
            		extendsTestCase = true;
            	}
            }
    		
    		List interfaces = cn.interfaces;
            for (Object anInterface : interfaces) {
                String interfaceString = (String)anInterface;
                co.addInterface(interfaceString.replaceAll("/", "."));
            }
            
    		List fields = cn.fields;
            for (Object field : fields) {
                FieldNode fieldNode = (FieldNode) field;
                Type fieldType = Type.getType(fieldNode.desc);
                TypeObject typeObject = new TypeObject(fieldType.getClassName());
                if(fieldNode.signature != null) {
                    TraceSignatureVisitor v = new TraceSignatureVisitor(ClassReader.SKIP_DEBUG);
                    SignatureReader r = new SignatureReader(fieldNode.signature);
                    r.accept(v);
                    String declaration = v.getDeclaration();
                    if(declaration.contains("<") && declaration.contains(">"))
                        typeObject.setGeneric(declaration.substring(declaration.indexOf("<")+1,declaration.lastIndexOf(">")));
                }
                FieldObject fo = new FieldObject(typeObject, fieldNode.name);
                fo.setClassName(co.getName());

                if ((fieldNode.access & Opcodes.ACC_PUBLIC) != 0)
                    fo.setAccess(Access.PUBLIC);
                else if ((fieldNode.access & Opcodes.ACC_PROTECTED) != 0)
                    fo.setAccess(Access.PROTECTED);
                else if ((fieldNode.access & Opcodes.ACC_PRIVATE) != 0)
                    fo.setAccess(Access.PRIVATE);
                if ((fieldNode.access & Opcodes.ACC_STATIC) != 0)
                    fo.setStatic(true);
                co.addField(fo);
            }
    		
    		List methods = cn.methods;
    		boolean containsTestMethod = false;
            for (Object method : methods) {
                MethodNode methodNode = (MethodNode) method;
                MethodAnnotationScanner methodAnnotationScanner = new MethodAnnotationScanner();
				methodNode.accept(methodAnnotationScanner);
				if(methodAnnotationScanner.isTestMethod()) {
					containsTestMethod = true;
				}
                final ConstructorObject constructorObject = new ConstructorObject();

                if ((methodNode.access & Opcodes.ACC_PUBLIC) != 0)
                    constructorObject.setAccess(Access.PUBLIC);
                else if ((methodNode.access & Opcodes.ACC_PROTECTED) != 0)
                    constructorObject.setAccess(Access.PROTECTED);
                else if ((methodNode.access & Opcodes.ACC_PRIVATE) != 0)
                    constructorObject.setAccess(Access.PRIVATE);

                if(methodNode.signature != null) {
                    TraceSignatureVisitor v = new TraceSignatureVisitor(ClassReader.SKIP_DEBUG);
                    SignatureReader r = new SignatureReader(methodNode.signature);
                    r.accept(v);
                    String declaration = v.getDeclaration();
                    String temp = declaration;
                    if(temp.startsWith("<K, V>"))
                    	temp = temp.substring(6, temp.length());
                    if(temp.startsWith("<T>"))
                    	temp = temp.substring(3, temp.length());
                    if(temp.startsWith("("))
                    	temp = temp.substring(1, temp.length());
                    if(temp.endsWith(""))
                    	temp = temp.substring(0, temp.length()-1);
                    if(!temp.equals("")) {
                        ParameterAnalyzer analyzer = new ParameterAnalyzer(temp);
                        for (String token : analyzer.getParameters()) {
                            if(token.contains("<") && token.contains(">")) {
                                TypeObject typeObject = new TypeObject(token.substring(0,token.indexOf("<")));
                                typeObject.setGeneric(token.substring(token.indexOf("<")+1,token.lastIndexOf(">")));
                                constructorObject.addParameter(typeObject);
                            }
                            else {
                                constructorObject.addParameter(new TypeObject(token));
                            }
                        }
                    }
                }
                else {
                    Type[] argumentTypes = Type.getArgumentTypes(methodNode.desc);
                    for (Type argumentType : argumentTypes)
                        constructorObject.addParameter(new TypeObject(argumentType.getClassName()));
                }
                if (methodNode.instructions.size() > 0) {
                    Map<String, Integer> labelIndexMap = new HashMap<String, Integer>();
                    List<LoopObject> activeLoops = new ArrayList<LoopObject>();

                    Iterator insnIt = methodNode.instructions.iterator();
                    int index = 0;
                    while(insnIt.hasNext()) {
                        AbstractInsnNode ainsn = (AbstractInsnNode)insnIt.next();

                        if(ainsn instanceof LabelNode) {
                            LabelNode labelNode = (LabelNode) ainsn;
                            Label label = labelNode.getLabel();
                            LoopObject loop = new LoopObject(label.toString());
                            activeLoops.add(loop);
                            labelIndexMap.put(label.toString(), index);
                        }

                        if(ainsn instanceof JumpInsnNode) {
                            JumpInsnNode jumpNode = (JumpInsnNode) ainsn;
                            Label label = jumpNode.label.getLabel();
                            if(labelIndexMap.containsKey(label.toString())) {
                            	LoopObject matchingLoop = null;
                            	for(LoopObject loop : activeLoops) {
                            		if(loop.getLabel().equals(label.toString())) {
                            			matchingLoop = loop;
                            			break;
                            		}
                            	}
                            	if(matchingLoop != null) {
                            		constructorObject.addLoop(matchingLoop);
                            		activeLoops.remove(matchingLoop);
                            	}
                            }
                        }

                        if(ainsn instanceof FieldInsnNode) {
                            FieldInsnNode fieldInsnNode = (FieldInsnNode) ainsn;
                            Type fieldType = Type.getType(fieldInsnNode.desc);
                            FieldInstructionObject fieldObject = new FieldInstructionObject(fieldInsnNode.owner.replaceAll("/", "."),fieldType.getClassName(),fieldInsnNode.name);
                            constructorObject.addFieldInstruction(fieldObject);
                            for(LoopObject loop : activeLoops) {
                                loop.addFieldInstruction(fieldObject);
                            }
                        }

                        if ((ainsn.getOpcode() == Opcodes.INVOKEVIRTUAL) ||
                                (ainsn.getOpcode() == Opcodes.INVOKESTATIC) ||
                                (ainsn.getOpcode() == Opcodes.INVOKESPECIAL) ||
                                (ainsn.getOpcode() == Opcodes.INVOKEINTERFACE)) {

                        	MethodInvocationType type = null;
                        	if((ainsn.getOpcode() == Opcodes.INVOKEVIRTUAL))
                        		type = MethodInvocationType.INVOKE_VIRTUAL;
                        	else if((ainsn.getOpcode() == Opcodes.INVOKESTATIC))
                        		type = MethodInvocationType.INVOKE_STATIC;
                        	else if((ainsn.getOpcode() == Opcodes.INVOKESPECIAL))
                        		type = MethodInvocationType.INVOKE_SPECIAL;
                        	else if((ainsn.getOpcode() == Opcodes.INVOKEINTERFACE))
                        		type = MethodInvocationType.INVOKE_INTERFACE;
                            MethodInsnNode minsn = (MethodInsnNode) ainsn;
                            MethodInvocationObject mio = new MethodInvocationObject(
                                    minsn.owner.replaceAll("/", "."), minsn.name, Type.getReturnType(minsn.desc).getClassName(), type);
                            Type[] argTypes = Type.getArgumentTypes(minsn.desc);
                            for (Type argType : argTypes)
                                mio.addParameter(argType.getClassName());

                            constructorObject.addMethodInvocation(mio);
                            for(LoopObject loop : activeLoops) {
                                loop.addMethodInvocation(mio);
                            }
                        }

                        if ((ainsn.getOpcode() == Opcodes.NEW) ||
                                (ainsn.getOpcode() == Opcodes.ANEWARRAY)) {

                            TypeInsnNode tinsn = (TypeInsnNode) ainsn;
                            constructorObject.addObjectInstantiation(tinsn.desc.replaceAll("/", "."));
                        }
                        index++;
                    }
                }

                if (methodNode.name.equals("<init>")) {
                    constructorObject.setName(co.getName());
                    co.addConstructor(constructorObject);
                } else {
                    Type returnType = Type.getReturnType(methodNode.desc);
                    constructorObject.setName(methodNode.name);
                    MethodObject methodObject = new MethodObject(constructorObject);
                    TypeObject typeObject = new TypeObject(returnType.getClassName());
                    if(methodNode.signature != null) {
                        TraceSignatureVisitor v = new TraceSignatureVisitor(ClassReader.SKIP_DEBUG);
                        SignatureReader r = new SignatureReader(methodNode.signature);
                        r.accept(v);
                        String genericReturnType = v.getReturnType();
                        if(genericReturnType.contains("<") && genericReturnType.contains(">"))
                            typeObject.setGeneric(genericReturnType.substring(genericReturnType.indexOf("<")+1,genericReturnType.lastIndexOf(">")));
                    }

                    methodObject.setReturnType(typeObject);
                    methodObject.setClassName(co.getName());
                    if ((methodNode.access & Opcodes.ACC_ABSTRACT) != 0)
                        methodObject.setAbstract(true);
                    if ((methodNode.access & Opcodes.ACC_STATIC) != 0)
                        methodObject.setStatic(true);
                    co.addMethod(methodObject);
                }
            }
            if(!containsTestMethod && !extendsTestCase) {
            	so.addClass(co);
            }
            fin.close();
		}
		catch(FileNotFoundException fnfe) {fnfe.printStackTrace();}
		catch(IOException ioe) {ioe.printStackTrace();}
	}

    public static SystemObject getSystemObject() {
		return so;
	}

	public static IJavaProject getExaminedProject() {
		return examinedProject;
	}
}