package dpd4eclipse.visualization;

import gr.uom.java.bytecode.Access;
import gr.uom.java.bytecode.ClassObject;
import gr.uom.java.bytecode.FieldObject;
import gr.uom.java.bytecode.MethodInvocationObject;
import gr.uom.java.bytecode.MethodObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.ScalableFreeformLayeredPane;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class DesignPatternDiagram {
	private ScalableFreeformLayeredPane root;
	private FreeformLayer primary;
	private ConnectionLayer connections;
	private List<JConnection> connectionList = new ArrayList<JConnection>();

	public DesignPatternDiagram(DesignPatternVisualizationData data) {
		// Create a root figure and simple layout to contain all other figures
		root = new ScalableFreeformLayeredPane();
		primary = new FreeformLayer();
		primary.setLayoutManager(new FreeformLayout());
		root.setFont(Display.getDefault().getSystemFont());
		root.add(primary,"Primary");

		connections = new ConnectionLayer();
		
		int startPointX = 100;
		int startPointY = 50;
		
		Map<String, ClassObject> classMap = data.getClassMap();
		for(String classRoleName : classMap.keySet()) {
			ClassObject classObject = classMap.get(classRoleName);
			Set<MethodObject> methods = data.getMethodsForClass(classObject);
			Set<MethodObject> patternMethods = data.getPatternMethodsForClass(classObject);
			Set<FieldObject> fields = data.getFieldsForClass(classObject);
			int totalMethods = methods.size();
			
			int classWidth = 450;
			boolean oneSection = false;
			if(totalMethods <= 1) {
				oneSection = true;
				classWidth = 200;
			}
			int sectionWidth = classWidth/3;
			
			//Creates Class
			final ClassFigure classFigure = new ClassFigure(classObject.getName(),  DecorationConstants.classColor);
			
			if(oneSection) {
				classFigure.addFieldCompartment();
				for(FieldObject field : fields){
					EntityFigure fieldFigure = new EntityFigure(field.getName(), DecorationConstants.FIELD, true);
					classFigure.getFieldsCompartment().addFigure(fieldFigure);
				}
			}
			else {
				classFigure.addFieldSectionCompartment();
				for(FieldObject field : fields){
					EntityFigure fieldFigure = new EntityFigure(field.getName(), DecorationConstants.FIELD, true);
					classFigure.getFieldSectionCompartment().getSectionTwo().addFigure(fieldFigure);
				}
			}
			
			if(oneSection)
				classFigure.addMethodSectionCompartment(1);
			else
				classFigure.addMethodSectionCompartment(3);
			
			MethodClassSection sectionOne = classFigure.getMethodSectionCompartment().getSectionOne();
			MethodClassSection sectionThree = classFigure.getMethodSectionCompartment().getSectionThree();
			
			//Adds Connections from Methods to other Methods in Extracted Class
			Map<MethodObject, Map<MethodInvocationObject, Integer>> internalMethodInvocationMap = data.getInternalMethodInvocationMapForClass(classObject);
			int bendHeight;
			for(Entry<MethodObject, Map<MethodInvocationObject, Integer>> entry : internalMethodInvocationMap.entrySet()){
				int bendGap = -20;
				MethodObject method = entry.getKey();
				Map<MethodInvocationObject, Integer> connectionMap = entry.getValue();
				EntityFigure connectionSource = null;
				boolean contains= false;
				boolean sourceinRightSection = false;

				EntityFigure methodFigure = new EntityFigure(method.getSignature().toString(), createMethodDecoration(method), true);

				//check if method is in Left Section already
				for(Object child : sectionOne.getChildren()){
					EntityFigure entity = (EntityFigure) child;
					if (entity.getName().equals(methodFigure.getName())){
						connectionSource = entity;
						contains = true;
					}
				}
				//Check if method is in Right Section already
				if(!contains){
					for(Object child : sectionThree.getChildren()){
						EntityFigure entity = (EntityFigure) child;
						if (entity.getName().equals(methodFigure.getName())){
							connectionSource = entity;
							sourceinRightSection = true;
							contains = true;
						}
					}
				}

				//If its not already there, add it so that the two sides are even
				if(!contains){
					if(patternMethods.contains(method))
						sectionOne.addFigure(methodFigure);
					else {
						sectionThree.addFigure(methodFigure);
						sourceinRightSection = true;
					}
					connectionSource = methodFigure;
				}
				
				for(Entry<MethodInvocationObject, Integer> map  : connectionMap.entrySet()){
					contains = false;
					MethodInvocationObject target = map.getKey();
					MethodObject invokedMethod = classObject.findMethodIncludingSuperTypes(target.getSignature());
					Integer occurences = map.getValue();

					if(invokedMethod != null) {
						EntityFigure targetFigure = new EntityFigure(invokedMethod.getSignature().toString(), createMethodDecoration(invokedMethod), true);

						//checks if Target Connection Method is in Left Section
						for(Object child : sectionOne.getChildren()){
							EntityFigure entity = (EntityFigure) child;
							if (entity.getName().equals(targetFigure.getName())){
								//connectionTarget = entity;
								contains = true;
								JConnection connection;
								if(sourceinRightSection){
									connection = connectionSource.addLeftRightMethodConnection(ConnectionType.METHOD_CALL_TARGET,entity, occurences);
									connectionList.add(connection);
									connections.add(connection);
								}
								else {
									if(oneSection)
										bendHeight = classWidth + bendGap;
									else
										bendHeight = sectionWidth + bendGap;
									connection = connectionSource.addToSameClassMethodConnectionLL(ConnectionType.METHOD_CALL_TARGET, entity, occurences, bendHeight);
									connectionList.add(connection);
									connections.add(connection);
								}
							}
						}

						//checks if Target Connection Method is in Right Section
						if(!contains){
							for(Object child : sectionThree.getChildren()){
								EntityFigure entity = (EntityFigure) child;
								if (entity.getName().equals(targetFigure.getName())){
									//connectionTarget = entity;
									contains = true;
									//targetinRightSection = true;
									JConnection connection;
									if(sourceinRightSection){
										bendHeight = sectionWidth + bendGap;
										connection = connectionSource.addToSameClassMethodConnectionLL(ConnectionType.METHOD_CALL_TARGET,entity, occurences, bendHeight);
										connectionList.add(connection);
										connections.add(connection);
									}
									else {
										connection = connectionSource.addRightLeftMethodConnection(ConnectionType.METHOD_CALL_TARGET,entity, occurences);
										connectionList.add(connection);
										connections.add(connection);
									}
								}
							}
						}

						if(!contains){
							if(patternMethods.contains(invokedMethod)) {
								sectionOne.addFigure(targetFigure);
								if(oneSection)
									bendHeight = classWidth + bendGap;
								else
									bendHeight = sectionWidth + bendGap;
								JConnection connection = connectionSource.addToSameClassMethodConnectionLL(ConnectionType.METHOD_CALL_TARGET,targetFigure, occurences, bendHeight);
								connectionList.add(connection);
								connections.add(connection);
							}
							else {
								//If its not already there, add it to Right Section
								sectionThree.addFigure(targetFigure);
								JConnection connection ;
								if(sourceinRightSection){
									if(oneSection)
										bendHeight = classWidth + bendGap;
									else
										bendHeight = sectionWidth + bendGap;
									connection = connectionSource.addToSameClassMethodConnectionLL(ConnectionType.METHOD_CALL_TARGET,targetFigure, occurences, bendHeight);
									connectionList.add(connection);
									connections.add(connection);
								}
								else {
									connection = connectionSource.addRightLeftMethodConnection(ConnectionType.METHOD_CALL_TARGET, targetFigure, occurences);
									connectionList.add(connection);
									connections.add(connection);
								}
							}
						}
					}
				}
			}

			boolean contains;
			//Adds Methods that were not already added
			for(MethodObject method : methods){
				contains = false;
				EntityFigure methodFigure = new EntityFigure(method.getSignature().toString(), createMethodDecoration(method), true);
				//checks if Method is in Left Section
				for(Object child : sectionOne.getChildren()){
					EntityFigure entity = (EntityFigure) child;
					if (entity.getName().equals(methodFigure.getName())){
						//connectionTarget = entity;
						contains = true;
					}

				}

				//checks if Method is in Right Section
				if(!contains){
					for(Object child : sectionThree.getChildren()){
						EntityFigure entity = (EntityFigure) child;
						if (entity.getName().equals(methodFigure.getName())){
							contains= true;
						}
					}
				}

				//If its not already there, add it so it evens out the sections
				if(!contains){
					if(sectionOne.getNumOfMethods()<= sectionThree.getNumOfMethods())
						sectionOne.addFigure(methodFigure);
					else {
						sectionThree.addFigure(methodFigure);
					}
				}
			}
			
			primary.add(classFigure, new Rectangle(startPointX, startPointY, classWidth,-1));
		}
		root.add(connections, "Connections");
	}

	private Image createMethodDecoration(MethodObject method) {
		if(method.isAbstract() && method.getAccess().equals(Access.PUBLIC)) {
			return DecorationConstants.PUBLIC_ABSTRACT_METHOD;
		}
		if(method.isAbstract() && method.getAccess().equals(Access.PROTECTED)) {
			return DecorationConstants.PROTECTED_ABSTRACT_METHOD;
		}
		if(method.isAbstract() && method.getAccess().equals(Access.NONE)) {
			return DecorationConstants.DEFAULT_ABSTRACT_METHOD;
		}
		if(method.getAccess().equals(Access.PROTECTED)) {
			return DecorationConstants.PROTECTED_METHOD;
		}
		if(method.getAccess().equals(Access.PRIVATE)) {
			return DecorationConstants.PRIVATE_METHOD;
		}
		if(method.getAccess().equals(Access.NONE)) {
			return DecorationConstants.DEFAULT_METHOD;
		}
		return DecorationConstants.METHOD;
	}

	public ScalableFreeformLayeredPane getRoot() {
		return root;
	}
}
