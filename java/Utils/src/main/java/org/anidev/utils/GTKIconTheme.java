package org.anidev.utils;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import com.kitfox.svg.app.beans.SVGIcon;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.StringArray;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public class GTKIconTheme {
	private static final int GTK_ICON_LOOKUP_GENERIC_FALLBACK=1<<3;
	private static final HashMap<String,IconInfo> ICON_MAP=new HashMap<>();
	private static Pointer theme=null;
	private static boolean initFailed=false;
	static {
		addIcon("arrow-up",",go-up",16);
		addIcon("arrow-down",",go-down",16);
		addIcon("delete","edit-delete",22);
		addIcon("pause","media-playback-pause",22);
		addIcon("list","view-list-details",22);
		addIcon("text","utilities-terminal",22);
		addIcon("status-bad","dialog-cancel",16);
		addIcon("status-good","dialog-ok",16);
	}

	private static void addIcon(String name,String id,int size) {
		if(id.equals("")) {
			id=name;
		} else if(id.startsWith(",")) {
			id=name+id;
		}
		IconInfo info=new IconInfo(id,size);
		ICON_MAP.put(name,info);
	}

	public static Icon findIcon(String name) {
		if(name==null||"".equals(name)) {
			return null;
		}
		int dotIndex=name.lastIndexOf('.');
		if(dotIndex>-1) {
			name=name.substring(0,dotIndex);
		}
		IconInfo info=ICON_MAP.get(name);
		if(info==null) {
			return null;
		}
		if(info.cachedIcon!=null) {
			return info.cachedIcon;
		}
		init();
		if(initFailed) {
			return null;
		}
		String[] ids=info.id.split(",");
		for(String id:ids) {
			Pointer infoPtr=gtk_icon_theme_lookup_icon(theme,id,info.size,
					GTK_ICON_LOOKUP_GENERIC_FALLBACK);
			if(infoPtr==null) {
				continue;
			}
			String filename=gtk_icon_info_get_filename(infoPtr);
			try {
				Icon icon=null;
				if(filename.toLowerCase().endsWith(".svg")) {
					icon=loadSVG(filename);
				} else {
					Image image=ImageIO.read(new File(filename));
					icon=new ImageIcon(image);
				}
				if(icon==null) {
					continue;
				}
				System.out.println("Found "+id);
				info.cachedIcon=icon;
				return icon;
			} catch(IOException e) {
				continue;
			}
		}
		System.out.println("Did not find "+info.id);
		return null;
	}

	private static void init() {
		if(initFailed) {
			return;
		}
		if(theme!=null) {
			return;
		}
		try {
			Native.register("gtk-x11-2.0");
		} catch(UnsatisfiedLinkError e) {
			initFailed=true;
			return;
		}
		IntByReference argc=new IntByReference(0);
		StringArray args=new StringArray(new String[0]);
		PointerByReference argv=new PointerByReference(args);
		if(!gtk_init_check(argc,argv)) {
			initFailed=true;
			return;
		}
		theme=gtk_icon_theme_get_default();
	}

	private static SVGIcon loadSVG(String filename) {
		URI uri=new File(filename).toURI();
		SVGIcon icon=new SVGIcon();
		icon.setAntiAlias(true);
		icon.setSvgURI(uri);
		return icon;
	}

	private static native boolean gtk_init_check(IntByReference argc,
			PointerByReference argv);

	private static native Pointer gtk_icon_theme_get_default();

	private static native Pointer gtk_icon_theme_lookup_icon(Pointer theme,
			String id,int size,int flags);

	private static native String gtk_icon_info_get_filename(Pointer iconInfo);

	private static class IconInfo {
		public String id;
		public int size;
		public Icon cachedIcon=null;

		public IconInfo(String id,int size) {
			this.id=id;
			this.size=size;
		}
	}
}
