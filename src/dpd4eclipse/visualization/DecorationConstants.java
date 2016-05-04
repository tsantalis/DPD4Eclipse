package dpd4eclipse.visualization;

import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.internal.ui.viewsupport.JavaElementImageProvider;
import org.eclipse.jdt.ui.ISharedImages;
import org.eclipse.jdt.ui.JavaElementImageDescriptor;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

@SuppressWarnings("restriction")
public class DecorationConstants {

	public static final Font normalFont = new Font(null, "Arial", 10, SWT.BOLD);
	public static final Font highlightFont = new Font(null, "Arial", 14 , SWT.BOLD);
	public static final Color entityColor = new Color(null,255,255,240);
	public static final Image FIELD = JavaUI.getSharedImages().getImage(ISharedImages.IMG_FIELD_PRIVATE);
	public static final Image METHOD = JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_PUBLIC);
	public static final Image PACKAGE = JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_PACKAGE);
	public static final Color methodToMethodColor = new Color(null,60,179,113);
	public static final int NO_OCCURENCES = -1;
	public static final Color classColor = new Color(null,255,255,206);
	public static final Image CLASS = JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_CLASS);
	public static final Font classFont = new Font(null, "Arial", 12, SWT.BOLD);
	
	public static final Image PRIVATE_STATIC_FIELD =
			JavaPlugin.getImageDescriptorRegistry().get(new JavaElementImageDescriptor(JavaPluginImages.DESC_FIELD_PRIVATE, JavaElementImageDescriptor.STATIC, JavaElementImageProvider.SMALL_SIZE));
	public static final Image PROTECTED_STATIC_FIELD =
			JavaPlugin.getImageDescriptorRegistry().get(new JavaElementImageDescriptor(JavaPluginImages.DESC_FIELD_PROTECTED, JavaElementImageDescriptor.STATIC, JavaElementImageProvider.SMALL_SIZE));
	public static final Image PUBLIC_STATIC_FIELD =
			JavaPlugin.getImageDescriptorRegistry().get(new JavaElementImageDescriptor(JavaPluginImages.DESC_FIELD_PUBLIC, JavaElementImageDescriptor.STATIC, JavaElementImageProvider.SMALL_SIZE));
	public static final Image DEFAULT_STATIC_FIELD =
			JavaPlugin.getImageDescriptorRegistry().get(new JavaElementImageDescriptor(JavaPluginImages.DESC_FIELD_DEFAULT, JavaElementImageDescriptor.STATIC, JavaElementImageProvider.SMALL_SIZE));
	public static final Image PROTECTED_FIELD =
			JavaPlugin.getImageDescriptorRegistry().get(JavaPluginImages.DESC_FIELD_PROTECTED);
	public static final Image PRIVATE_FIELD =
			JavaPlugin.getImageDescriptorRegistry().get(JavaPluginImages.DESC_FIELD_PRIVATE);
	public static final Image DEFAULT_FIELD =
			JavaPlugin.getImageDescriptorRegistry().get(JavaPluginImages.DESC_FIELD_DEFAULT);
	public static final Image PUBLIC_FIELD =
			JavaPlugin.getImageDescriptorRegistry().get(JavaPluginImages.DESC_FIELD_PUBLIC);
	
	
	public static final Image PUBLIC_ABSTRACT_METHOD =
			JavaPlugin.getImageDescriptorRegistry().get(new JavaElementImageDescriptor(JavaPluginImages.DESC_MISC_PUBLIC, JavaElementImageDescriptor.ABSTRACT, JavaElementImageProvider.SMALL_SIZE));
	public static final Image PROTECTED_ABSTRACT_METHOD =
			JavaPlugin.getImageDescriptorRegistry().get(new JavaElementImageDescriptor(JavaPluginImages.DESC_MISC_PROTECTED, JavaElementImageDescriptor.ABSTRACT, JavaElementImageProvider.SMALL_SIZE));
	public static final Image DEFAULT_ABSTRACT_METHOD =
			JavaPlugin.getImageDescriptorRegistry().get(new JavaElementImageDescriptor(JavaPluginImages.DESC_MISC_DEFAULT, JavaElementImageDescriptor.ABSTRACT, JavaElementImageProvider.SMALL_SIZE));
	public static final Image PROTECTED_METHOD =
			JavaPlugin.getImageDescriptorRegistry().get(JavaPluginImages.DESC_MISC_PROTECTED);
	public static final Image PRIVATE_METHOD =
			JavaPlugin.getImageDescriptorRegistry().get(JavaPluginImages.DESC_MISC_PRIVATE);
	public static final Image DEFAULT_METHOD =
			JavaPlugin.getImageDescriptorRegistry().get(JavaPluginImages.DESC_MISC_DEFAULT);
}
