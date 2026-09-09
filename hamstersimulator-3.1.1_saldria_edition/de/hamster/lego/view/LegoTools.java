package de.hamster.lego.view;

import de.hamster.lego.controller.LegoController;
import de.hamster.lego.model.LegoModel;
import de.hamster.model.HamsterFile;
import de.hamster.workbench.ForwardAction;
import de.hamster.workbench.Utils;
import java.awt.Dimension;
import java.util.Observable;
//import java.util.Observer;
import javax.swing.Box;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Diese Klasse fuegt den Lego Knopf (Zahnrad) in die Toolbar ein.
 * @author Christian
 */
public class LegoTools /*implements Observer*/ {
        
        private HamsterFile activeFile;
        private LegoModel model;
        private LegoController controller;
        private UploadAction uploadAction = new UploadAction();
        
        /**
         * Diese Komponente löst das Kompilieren der Lego-Classdatei, das Übertragen 
         * in den Zielcode und das Hochladen auf den NXT aus.
         */
        public class UploadAction extends ForwardAction {
                /**
                 * 
                 */
                public UploadAction() {
                        super("lego.upload", LegoController.ACTION_UPLOAD);
                }
        }
        
        
        /**
	 * Der Konstruktor fügt den Button ein.
	 * 
	 * @param model
	 *            Das Model der Lego-Komponente
	 * @param controller
	 *            Der Controller der Lego-Komponente
	 */
        public LegoTools(LegoModel model, LegoController controller) {
                this.model = model;
                JToolBar toolBar = controller.getWorkbench().getView().findToolBar(
				"editor");
                
                toolBar.add(Utils.createButton(uploadAction), toolBar.getComponentCount()-1);
                toolBar.add(Box.createRigidArea(new Dimension(2, 2)), toolBar.getComponentCount()-1);
             
                uploadAction.addActionListener(controller);
                updateButtonStates();
        }


        /**
         * Falls activeFile ein Legoprogramm sein soll, wird der Zahnradbutton 
         * sichtbar gemacht.
         */
        private void updateButtonStates() {
                uploadAction.setEnabled(activeFile != null 
                                        && activeFile.getType() == HamsterFile.LEGOPROGRAM);
        }
        
        /**
         * Setzten des neuen activeFile.
         * @param file HamsterFile, das gerade bearbeitet wird.
         */
        public void setActiveFile(HamsterFile file) {
                activeFile = file;
                updateButtonStates();
        }
        
}
