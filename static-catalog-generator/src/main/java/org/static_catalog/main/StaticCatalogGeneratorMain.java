/* Search-able catalog for static generated sites - static-catalog.org 2019 */
package org.static_catalog.main;

import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;

import org.static_catalog.ui.StaticCatalogGeneratorMainWindow;

/** Main class, launch main window */
public class StaticCatalogGeneratorMain {

	/** The main */
	public static void main(String[] args) {

		/* Root folder */
		String classFolderPath = L.class.getProtectionDomain().getCodeSource().getLocation().getFile();
		File classFolder = new File(classFolderPath).getParentFile().getParentFile();
//		if (classFolder.getAbsolutePath().endsWith("lib")) {
//			classFolder = classFolder.getParentFile();
//		}
		
		String rootFolder = classFolder.getAbsolutePath(); 
//		L.p("rootFolder = " + rootFolder);

		/* Log */
		L.init(rootFolder + "/log/static-catalog.log");
	    
		/* All uncaught exceptions */
	    UncaughtExceptionHandler uncaughtExceptionHandler = new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread thread, Throwable throwable) {
				L.e("Uncaught exception: " + throwable.toString(), throwable);
			}
		};
	    Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);

		/* Start main window */
	    StaticCatalogGeneratorMainWindow mainWindow = new StaticCatalogGeneratorMainWindow(rootFolder);
		mainWindow.runMainWindow();

		/* JVM bug */
		System.exit(0);
	}
}
