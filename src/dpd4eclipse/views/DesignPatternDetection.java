package dpd4eclipse.views;

import gr.uom.java.bytecode.BytecodeReader;
import gr.uom.java.bytecode.FieldObject;
import gr.uom.java.bytecode.MethodObject;
import gr.uom.java.bytecode.SystemObject;
import gr.uom.java.pattern.BehavioralData;
import gr.uom.java.pattern.ClusterResult;
import gr.uom.java.pattern.ClusterSet;
import gr.uom.java.pattern.MatrixContainer;
import gr.uom.java.pattern.PatternDescriptor;
import gr.uom.java.pattern.PatternEnum;
import gr.uom.java.pattern.PatternGenerator;
import gr.uom.java.pattern.PatternInstance;
import gr.uom.java.pattern.PatternResult;
import gr.uom.java.pattern.SimilarityAlgorithm;
import gr.uom.java.pattern.SystemGenerator;
import gr.uom.java.pattern.PatternInstance.RoleType;
import gr.uom.java.pattern.inheritance.Enumeratable;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.part.*;
import org.eclipse.ui.progress.IProgressService;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModelMarker;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.SWT;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;


/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class DesignPatternDetection extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "dpd4eclipse.views.DesignPatternDetection";
	public static final Image CLASS = JavaUI.getSharedImages().getImage(org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_CLASS);
	public static final Image METHOD = JavaUI.getSharedImages().getImage(org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_PUBLIC);
	public static final Image FIELD = JavaUI.getSharedImages().getImage(org.eclipse.jdt.ui.ISharedImages.IMG_FIELD_PRIVATE);
	private static final String MESSAGE_DIALOG_TITLE = "Design Pattern Detection";

	private TreeViewer viewer;
	private Action detectDesignPatterns;
	private Action doubleClickAction;

	private IJavaProject selectedProject;
	private IJavaProject activeProject;
	private PatternResult[] patternResults;

	class ViewContentProvider implements ITreeContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}
		public void dispose() {
		}
		public Object[] getElements(Object parent) {
			if(patternResults!=null) {
				return patternResults;
			}
			else {
				return new PatternResult[] {};
			}
		}
		public Object[] getChildren(Object arg) {
			if(arg instanceof PatternResult) {
				return ((PatternResult)arg).getPatternInstances().toArray();
			}
			else if(arg instanceof PatternInstance) {
				return ((PatternInstance)arg).getEntrySet().toArray();
			}
			else {
				return new PatternInstance[] {};
			}
		}
		public Object getParent(Object arg) {
			if(arg instanceof PatternInstance) {
				PatternInstance patternInstance = (PatternInstance)arg;
				for(int i=0; i<patternResults.length; i++) {
					if(patternResults[i].getPatternInstances().contains(patternInstance))
						return patternResults[i];
				}
			}
			else if(arg instanceof PatternInstance.Entry) {
				PatternInstance.Entry patternInstanceEntry = (PatternInstance.Entry)arg;
				for(int i=0; i<patternResults.length; i++) {
					for(PatternInstance patternInstance : patternResults[i].getPatternInstances()) {
						if(patternInstance.getEntrySet().contains(patternInstanceEntry)) {
							return patternInstance;
						}
					}
				}
			}
			return null;
		}
		public boolean hasChildren(Object arg0) {
			return getChildren(arg0).length > 0;
		}
	}

	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			if(obj instanceof PatternResult) {
				PatternResult patternResult = (PatternResult)obj;
				switch (index) {
				case 0:
					return patternResult.getPatternName();
				default:
					return "";
				}
			}
			else if(obj instanceof PatternInstance) {
				PatternInstance patternInstance = (PatternInstance)obj;
				switch (index) {
				case 0:
					return "Instance";
				default:
					return "";
				}
			}
			else if(obj instanceof PatternInstance.Entry) {
				PatternInstance.Entry entry = (PatternInstance.Entry)obj;
				switch (index) {
				case 0:
					return entry.getRoleName() + " -> " + entry.getElementName();
				default:
					return "";
				}
			}
			else {
				return "";
			}
		}
		public Image getColumnImage(Object obj, int index) {
			Image image = null;
			if(obj instanceof PatternInstance.Entry) {
				PatternInstance.Entry entry = (PatternInstance.Entry)obj;
				RoleType roleType = entry.getRoleType();
				switch (index) {
				case 0:
					if(roleType.equals(RoleType.CLASS))
						image = CLASS;
					else if(roleType.equals(RoleType.FIELD))
						image = FIELD;
					else if(roleType.equals(RoleType.METHOD))
						image = METHOD;
				default:
					break;
				}
			}
			return image;
		}
		public Image getImage(Object obj) {
			return null;
		}
	}

	private ISelectionListener selectionListener = new ISelectionListener() {
		public void selectionChanged(IWorkbenchPart sourcepart, ISelection selection) {
			if (selection instanceof IStructuredSelection) {
				IStructuredSelection structuredSelection = (IStructuredSelection)selection;
				Object element = structuredSelection.getFirstElement();
				IJavaProject javaProject = null;
				if(element instanceof IJavaProject) {
					javaProject = (IJavaProject)element;
				}
				else if(element instanceof IPackageFragmentRoot) {
					IPackageFragmentRoot packageFragmentRoot = (IPackageFragmentRoot)element;
					javaProject = packageFragmentRoot.getJavaProject();
				}
				else if(element instanceof IPackageFragment) {
					IPackageFragment packageFragment = (IPackageFragment)element;
					javaProject = packageFragment.getJavaProject();
				}
				else if(element instanceof ICompilationUnit) {
					ICompilationUnit compilationUnit = (ICompilationUnit)element;
					javaProject = compilationUnit.getJavaProject();
				}
				else if(element instanceof IType) {
					IType type = (IType)element;
					javaProject = type.getJavaProject();
				}
				else if(element instanceof IMethod) {
					IMethod method = (IMethod)element;
					javaProject = method.getJavaProject();
				}
				else if(element instanceof IField) {
					IField field = (IField)element;
					javaProject = field.getJavaProject();
				}
				if(javaProject != null && !javaProject.equals(selectedProject)) {
					selectedProject = javaProject;
					detectDesignPatterns.setEnabled(true);
				}
			}
		}
	};

	/**
	 * The constructor.
	 */
	public DesignPatternDetection() {
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		//viewer.setSorter(new NameSorter());
		viewer.setInput(getViewSite());
		TableLayout layout = new TableLayout();
		layout.addColumnData(new ColumnWeightData(100, true));
		viewer.getTree().setLayout(layout);
		viewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		TreeColumn column0 = new TreeColumn(viewer.getTree(),SWT.LEFT);
		column0.setText("Pattern");
		column0.setResizable(true);
		column0.pack();
		viewer.expandAll();

		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "DPD4Eclipse.viewer");
		makeActions();
		hookDoubleClickAction();
		contributeToActionBars();

		getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(selectionListener);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(detectDesignPatterns);
	}

	private void makeActions() {
		detectDesignPatterns = new Action() {
			public void run() {
				activeProject = selectedProject;
				try {
					String relativePathToOutputLocation = activeProject.getOutputLocation().toOSString();
					String relativePathToProject = activeProject.getPath().toOSString();
					String pathToProject = activeProject.getResource().getLocation().toOSString();
					String pathToOutputLocation = pathToProject.replace(relativePathToProject, relativePathToOutputLocation);
					final File filePathToProject = new File(pathToOutputLocation);
					IWorkbench wb = PlatformUI.getWorkbench();
					IProgressService ps = wb.getProgressService();
					ps.busyCursorWhile(new IRunnableWithProgress() {
						public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
							try {
								patternResults = detectDesignPatternInstances(filePathToProject, monitor);
							} catch (CompilationErrorDetectedException e) {
								Display.getDefault().asyncExec(new Runnable() {
									public void run() {
										MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), MESSAGE_DIALOG_TITLE,
												"Compilation errors were detected in the project. Fix the errors before using DPD.");
									}
								});
							}
						}
					});
					viewer.setContentProvider(new ViewContentProvider());
				} catch (JavaModelException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		detectDesignPatterns.setText("Detect Design Patterns");
		detectDesignPatterns.setToolTipText("Detect Design Patterns");
		detectDesignPatterns.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

		doubleClickAction = new Action() {
			public void run() {
				IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
				if(selection.getFirstElement() instanceof PatternInstance.Entry) {
					PatternInstance.Entry entry = (PatternInstance.Entry)selection.getFirstElement();
					String elementFullName = entry.getElementName();
					if(entry.getRoleType().equals(RoleType.CLASS)) {
						try {
							String compilationUnitName = null;
							if(elementFullName.contains("$")) {
								//inner class
								String enclosingClass = elementFullName.substring(0, elementFullName.indexOf("$"));
								compilationUnitName = enclosingClass.replace(".", "/") + ".java";
							}
							else {
								compilationUnitName = elementFullName.replace(".", "/") + ".java";
							}
							ICompilationUnit sourceJavaElement = getICompilationUnit(activeProject, compilationUnitName);
							if(sourceJavaElement != null) {
								IType type = getIType(sourceJavaElement, elementFullName);
								ITextEditor sourceEditor = (ITextEditor)JavaUI.openInEditor(sourceJavaElement);
								if(type != null) {
									ISourceRange sourceRange = type.getNameRange();
									int offset = sourceRange.getOffset();
									int length = sourceRange.getLength();
									sourceEditor.setHighlightRange(offset, length, true);
								}
							}
						} catch (PartInitException e) {
							e.printStackTrace();
						} catch (JavaModelException e) {
							e.printStackTrace();
						}
					}
					else if(entry.getRoleType().equals(RoleType.METHOD)) {
						try {
							String qualifiedClassName = elementFullName.substring(0, elementFullName.indexOf("::"));
							String methodFullSignature = elementFullName.substring(elementFullName.indexOf("::")+2, elementFullName.length());
							String compilationUnitName = null;
							if(qualifiedClassName.contains("$")) {
								//inner class
								String enclosingClass = qualifiedClassName.substring(0, qualifiedClassName.indexOf("$"));
								compilationUnitName = enclosingClass.replace(".", "/") + ".java";
							}
							else {
								compilationUnitName = qualifiedClassName.replace(".", "/") + ".java";
							}
							ICompilationUnit sourceJavaElement = getICompilationUnit(activeProject, compilationUnitName);
							if(sourceJavaElement != null) {
								IType type = getIType(sourceJavaElement, qualifiedClassName);
								ITextEditor sourceEditor = (ITextEditor)JavaUI.openInEditor(sourceJavaElement);
								if(type != null) {
									IMethod method = getIMethod(type, methodFullSignature);
									if(method != null) {
										ISourceRange sourceRange = method.getNameRange();
										int offset = sourceRange.getOffset();
										int length = sourceRange.getLength();
										sourceEditor.setHighlightRange(offset, length, true);
									}
								}
							}
						} catch (PartInitException e) {
							e.printStackTrace();
						} catch (JavaModelException e) {
							e.printStackTrace();
						}
					}
				}
			}
		};
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}

	private PatternResult[] detectDesignPatternInstances(File inputDir, IProgressMonitor monitor) throws CompilationErrorDetectedException {
		List<IMarker> markers = buildProject(activeProject, monitor);
		if(!markers.isEmpty()) {
			throw new CompilationErrorDetectedException(markers);
		}
		BytecodeReader br = new BytecodeReader(inputDir, monitor);
		SystemObject so = br.getSystemObject();
		SystemGenerator sg = new SystemGenerator(so);
		SortedSet<ClusterSet.Entry> clusterSet = sg.getClusterSet().getInvokingClusterSet();
		List<Enumeratable> hierarchyList = sg.getHierarchyList();

		PatternEnum[] patternEnum = PatternEnum.values();
		PatternResult[] patternResults = new PatternResult[patternEnum.length];
		if(monitor != null)
			monitor.beginTask("Detecting design patterns", patternEnum.length);
		for(int i=0; i<patternEnum.length; i++) {
			if(monitor != null && monitor.isCanceled())
				throw new OperationCanceledException();
			String patternName = patternEnum[i].toString();
			if(monitor != null)
				monitor.subTask(patternName);
			PatternResult patternResult = new PatternResult(patternName);
			PatternDescriptor patternDescriptor = PatternGenerator.getPattern(patternName);
			if(patternDescriptor.getNumberOfHierarchies() == 0) {
				MatrixContainer systemContainer = sg.getMatrixContainer();
				double[][] systemMatrix = null;
				BehavioralData behavioralData = null;
				if(patternName.equals(PatternEnum.SINGLETON.toString())) {
					systemMatrix = systemContainer.getSingletonMatrix();
					behavioralData = systemContainer.getSingletonBehavioralData();
				}
				else if(patternName.equals(PatternEnum.TEMPLATE_METHOD.toString())) {
					systemMatrix = systemContainer.getTemplateMethodMatrix();
					behavioralData = systemContainer.getTemplateMethodBehavioralData();
				}
				else if(patternName.equals(PatternEnum.FACTORY_METHOD.toString())) {
					systemMatrix = systemContainer.getFactoryMethodMatrix();
					behavioralData = systemContainer.getFactoryMethodBehavioralData();
				}

				for(int j=0; j<systemMatrix.length; j++) {
					if(systemMatrix[j][j] == 1.0) {
						PatternInstance patternInstance = new PatternInstance();
						patternInstance.addEntry(patternInstance.new Entry(RoleType.CLASS, patternDescriptor.getClassNameList().get(0),systemContainer.getClassNameList().get(j),j));
						if(behavioralData != null) {
							if(patternDescriptor.getFieldRoleName() != null) {
								Set<FieldObject> fields = behavioralData.getFields(j, j);
								if(fields != null) {
									for(FieldObject field : fields) {
										patternInstance.addEntry(patternInstance.new Entry(RoleType.FIELD, patternDescriptor.getFieldRoleName(), field.toString(), -1));
									}
								}
							}
							if(patternDescriptor.getMethodRoleName() != null) {
								Set<MethodObject> methods = behavioralData.getMethods(j, j);
								if(methods != null) {
									for(MethodObject method : methods) {
										patternInstance.addEntry(patternInstance.new Entry(RoleType.METHOD, patternDescriptor.getMethodRoleName(), method.getSignature().toString(), -1));
									}
								}
							}
						}
						patternResult.addInstance(patternInstance);
					}
				}
			}
			else if(patternDescriptor.getNumberOfHierarchies() == 1) {
				for(Enumeratable ih : hierarchyList) {
					List<Enumeratable> tempList = new ArrayList<Enumeratable>();
					tempList.add(ih);
					MatrixContainer hierarchyMatrixContainer = sg.getHierarchiesMatrixContainer(tempList);
					generateResults(hierarchyMatrixContainer, patternDescriptor, patternResult);
				}
			}
			else if(patternDescriptor.getNumberOfHierarchies() == 2) {
				Iterator<ClusterSet.Entry> it = clusterSet.iterator();
				while(it.hasNext()) {
					ClusterSet.Entry entry = it.next();
					MatrixContainer hierarchiesMatrixContainer = sg.getHierarchiesMatrixContainer(entry.getHierarchyList());
					generateResults(hierarchiesMatrixContainer, patternDescriptor, patternResult);
				}
			}
			patternResults[i] = patternResult;
			if(monitor != null)
				monitor.worked(1);
		}
		if(monitor != null)
			monitor.done();
		return patternResults;
	}

	private void generateResults(MatrixContainer systemContainer, PatternDescriptor patternDescriptor, PatternResult patternResult) {
		double[][] results = SimilarityAlgorithm.getTotalScore(systemContainer,patternDescriptor);
		if(results != null) {
			ClusterResult clusterResult = new ClusterResult(results, patternDescriptor, systemContainer);
			List<PatternInstance> list = clusterResult.getPatternInstanceList();
			for (PatternInstance pi : list) {
				if (!patternResult.containsInstance(pi))
					patternResult.addInstance(pi);
			}
		}
	}

	private List<IMarker> buildProject(IJavaProject iJavaProject, IProgressMonitor pm) {
		ArrayList<IMarker> result = new ArrayList<IMarker>();
		try {
			IProject project = iJavaProject.getProject();
			project.refreshLocal(IResource.DEPTH_INFINITE, pm);	
			project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, pm);
			IMarker[] markers = null;
			markers = project.findMarkers(IJavaModelMarker.JAVA_MODEL_PROBLEM_MARKER, true, IResource.DEPTH_INFINITE);
			for (IMarker marker: markers) {
				Integer severityType = (Integer) marker.getAttribute(IMarker.SEVERITY);
				if (severityType.intValue() == IMarker.SEVERITY_ERROR) {
					result.add(marker);
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return result;
	}

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

	private static IMethod getIMethod(IType iType, String methodFullSignature) throws JavaModelException {
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

	private static IType getIType(ICompilationUnit iCompilationUnit, String qualifiedClassName) throws JavaModelException {
		List<IType> allTypes = getAllTypesIncludingAnonymous(iCompilationUnit);
		for(IType iType : allTypes) {
			if(iType.getFullyQualifiedName().equals(qualifiedClassName)) {
				return iType;
			}
		}
		return null;
	}

	private static List<IType> getAllTypesIncludingAnonymous(ICompilationUnit iCompilationUnit) throws JavaModelException {
		List<IType> allTypes = new ArrayList<IType>();
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
		return allTypes;
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}