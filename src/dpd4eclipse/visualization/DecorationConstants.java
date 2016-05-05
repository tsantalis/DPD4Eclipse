package dpd4eclipse.visualization;

import gr.uom.java.bytecode.BytecodeReader;
import gr.uom.java.bytecode.ClassObject;
import gr.uom.java.bytecode.FieldObject;
import gr.uom.java.bytecode.MethodObject;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

import dpd4eclipse.views.IJavaElementRecovery;

public class DecorationConstants {

	public static final Font normalFont = new Font(null, "Arial", 10, SWT.BOLD);
	public static final Font highlightFont = new Font(null, "Arial", 14 , SWT.BOLD);
	public static final Color entityColor = new Color(null,255,255,240);
	public static final Color methodToMethodColor = new Color(null,60,179,113);
	public static final int NO_OCCURENCES = -1;
	public static final Color classColor = new Color(null,255,255,206);
	public static final Font classFont = new Font(null, "Arial", 12, SWT.BOLD);
		
	public static Image createClassDecoration(ClassObject classObject) {
		IJavaElement iType = IJavaElementRecovery.getIType(BytecodeReader.getExaminedProject(), classObject);
		if(iType != null) {
			return IJavaElementRecovery.getImage(iType);
		}
		return null;
	}
	
	public static Image createFieldDecoration(FieldObject field) {
		IJavaElement iField = IJavaElementRecovery.getIField(BytecodeReader.getExaminedProject(), field);
		if(iField != null) {
			return IJavaElementRecovery.getImage(iField);
		}
		return null;
	}

	public static Image createMethodDecoration(MethodObject method) {
		IJavaElement iMethod = IJavaElementRecovery.getIMethod(BytecodeReader.getExaminedProject(), method);
		if(iMethod != null) {
			return IJavaElementRecovery.getImage(iMethod);
		}
		return null;
	}
}
