package ctdownloader.util;

import java.io.File;
import java.io.FileFilter;

public class FileFilterUtil {

	public FileFilter getXmlFilter() {
		FileFilter xmlFilter = new FileFilter() {

			@Override
			public boolean accept(File f) {
				if (f.getName().endsWith(".xml")) {
					return true;
				}
				return false;
			}
		};
		return xmlFilter;
	}

}
