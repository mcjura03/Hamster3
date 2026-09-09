package de.hamster.editor.view;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author $Author: djasper $
 * @version $Revision: 1.2 $
 */
public class SimpleTransferable extends ArrayList implements Transferable,
		Serializable {
	public static final DataFlavor SIMPLE_FLAVOR = DataFlavor.javaFileListFlavor;
	static DataFlavor flavors[] = { SIMPLE_FLAVOR };

	public SimpleTransferable(File file) {
		add(file);
	}

	public File getFile() {
		return (File) get(0);
	}

	public boolean isDataFlavorSupported(DataFlavor df) {
		return df.equals(SIMPLE_FLAVOR);
	}

	/** implements Transferable interface */
	public Object getTransferData(DataFlavor df)
			throws UnsupportedFlavorException, IOException {
		if (df.equals(SIMPLE_FLAVOR)) {
			return this;
		} else
			throw new UnsupportedFlavorException(df);
	}

	/** implements Transferable interface */
	public DataFlavor[] getTransferDataFlavors() {
		return flavors;
	}
}