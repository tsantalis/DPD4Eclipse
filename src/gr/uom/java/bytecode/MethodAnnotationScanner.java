package gr.uom.java.bytecode;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MethodAnnotationScanner extends MethodVisitor {
	
	private boolean isTestMethod = false;
    public MethodAnnotationScanner() {
        super(Opcodes.ASM5);
    }

    public boolean isTestMethod() {
		return isTestMethod;
	}

	@Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		if(desc.equals("Lorg/junit/Test;")) {
			isTestMethod = true;
		}
        return super.visitAnnotation(desc, visible);
    }
}
