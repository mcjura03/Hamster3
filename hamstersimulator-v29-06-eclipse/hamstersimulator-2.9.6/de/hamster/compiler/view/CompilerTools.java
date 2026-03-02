package de.hamster.compiler.view;

import java.awt.Dimension;
import java.util.Observable;
import java.util.Observer;

import javax.swing.Box;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;

import de.hamster.compiler.controller.CompilerController;
import de.hamster.compiler.model.CompilerModel;
import de.hamster.model.HamsterFile;
import de.hamster.workbench.ForwardAction;
import de.hamster.workbench.Utils;
import de.hamster.workbench.Workbench;

/**
 * Diese Klasse fuegt den Kompilieren Knopf in die Toolbar ein und erzeugt ein
 * Menue mit einem Item Kompilieren.
 * 
 * @author $Author: djasper $
 * @version $Revision: 1.1 $
 */
public class CompilerTools implements Observer {
	/**
	 * Das Model dieser Komponente
	 */
	protected CompilerModel compilerModel;
	
	/**
	 * Der Controller (Daniel, 08.04.2007)
	 */
	protected CompilerController controller;
	
	/**
	 * Diese Aktion loest das Einstellen des CLASSPATH aus.
	 */
	public class ClasspathAction extends ForwardAction {
		public ClasspathAction() {
			super("compiler.classpath", CompilerController.ACTION_CLASSPATH);
		}
	}
	ClasspathAction classpathAction = new ClasspathAction();
	
	/**
	 * Diese Aktion loest das Kompilieren aus.
	 */
	public class CompileAction extends ForwardAction {
		public CompileAction() {
			super("compiler.compile", CompilerController.ACTION_COMPILE);
		}
	}
	CompileAction compileAction = new CompileAction();

	/**
	 * Der Konstruktor
	 * 
	 * @param model
	 *            Das Model der Compiler-Komponente
	 * @param controller
	 *            Der Controller der Compiler-Komponente
	 */
	public CompilerTools(CompilerModel model, CompilerController controller) {
		this.compilerModel = model;
		model.addObserver(this);

		JMenu compileMenu = controller.getWorkbench().getView().findMenu(
				"editor", "compile");
		compileMenu.add(new JMenuItem(compileAction));
		compileMenu.add(new JMenuItem(classpathAction));

		JToolBar toolBar = controller.getWorkbench().getView().findToolBar(
				"editor");
		toolBar.add(Box.createRigidArea(new Dimension(11, 11)));
		toolBar.add(Utils.createButton(compileAction));

		compileAction.addActionListener(controller);
		classpathAction.addActionListener(controller);
		
		compileAction.setEnabled(false);
		// CLASSPATH kann nicht eingestellt werden, wenn lokal ausgefuehrt wird
		//classpathAction.setEnabled(!Utils.runlocally);
	}

	/**
	 * Ueber diese Methode kann das CompilerModel die Tools benachrichtigen.
	 */
	public void update(Observable arg0, Object arg1) {
	}
	
	// Martin
	/**
	 * Mit dieser Funktion koennen die Buttons zum Compilieren deaktiviert werden.
	 */
	public void setButtonsVisible(boolean visible) {
		compileAction.setEnabled(visible);
		// Daniel: Classpath muss ja nicht deaktiviert werden (haengt von runlocally ab)
		// classpathAction.setEnabled(visible);
	}
}