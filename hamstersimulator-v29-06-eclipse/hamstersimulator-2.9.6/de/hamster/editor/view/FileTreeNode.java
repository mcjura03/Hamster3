package de.hamster.editor.view;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import de.hamster.model.HamsterFile;
import de.hamster.workbench.HamsterFileFilter;

/**
 * 
 * @author $Author: djasper $
 * @version $Revision: 1.1 $
 */
public class FileTreeNode extends DefaultMutableTreeNode implements Comparable {
	boolean loaded;

	public FileTreeNode(HamsterFile file) {
		super(file);
		loaded = false;
	}

	public HamsterFile getHamsterFile() {
		return (HamsterFile) getUserObject();
	}

	public boolean isLeaf() {
		return !getHamsterFile().isDirectory();
	}

	public int getChildCount() {
		HamsterFile file = getHamsterFile();
		if (file.isDirectory()) {
			if (!loaded) {
				loaded = true;
				File[] files = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					if (HamsterFileFilter.HAM_FILTER.accept(files[i])
							|| HamsterFileFilter.TER_FILTER.accept(files[i])) // dibo
																				// 260110
						add(new FileTreeNode(HamsterFile
								.getHamsterFile(files[i].getAbsolutePath())));
				}
			}
		}
		return super.getChildCount();
	}

	public void update() {
		if (!loaded)
			return;
		HashMap existingFiles = new HashMap();
		for (int i = 0; i < getChildCount(); i++) {
			FileTreeNode ftn = (FileTreeNode) getChildAt(i);
			if (!ftn.getHamsterFile().exists()) {
				remove(ftn);
				i--;
			} else {
				ftn.update();
				existingFiles.put(ftn.getHamsterFile(), "j");
			}
		}
		File[] files = getHamsterFile().listFiles();
		for (int i = 0; i < files.length; i++) {
			if (!(HamsterFileFilter.HAM_FILTER.accept(files[i]) || HamsterFileFilter.TER_FILTER
					.accept(files[i]))) // dibo 260110
				continue;
			HamsterFile file = HamsterFile.getHamsterFile(files[i]
					.getAbsolutePath());
			if (existingFiles.get(file) == null) {
				add(new FileTreeNode(file));
			}
		}
		if (children != null) {
			children.sort(null); // dibo 21.12.2017
			//Collections.sort(children);
		}
	}

	public boolean equals(Object o) {
		if (!(o instanceof FileTreeNode))
			return false;
		return ((FileTreeNode) o).getHamsterFile() == (getHamsterFile());
	}

	public int compareTo(Object o) {
		if (!(o instanceof FileTreeNode))
			return 0;
		FileTreeNode f = (FileTreeNode) o;
		return getHamsterFile().compareTo(f.getHamsterFile());
	}
}