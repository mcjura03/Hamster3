package de.hamster.lego.controller;

import de.hamster.lego.model.*;
//import de.hamster.lego.view.Browser;
import de.hamster.lego.view.LegoTools;
import de.hamster.model.HamsterFile;
import de.hamster.workbench.Workbench;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Dies ist der Controller-Teil der Lego-Komponente.
 * 
 * @author Christian
 */
public class LegoController implements ActionListener {
        private Workbench workbench;
        private LegoModel model;
        private HamsterFile activeFile;
        private LegoTools tools;
         
        /**
	 * Dieser ActionCommand wird von einer Action benutzt, die das Hochladen
         * einer Lego-Datei startet.
	 */
        public static final String ACTION_UPLOAD = "upload";
        
        
        /**
	 * Der Konstruktor des LegoControllers. Er erzeugt die View-Komponenten.
	 * 
	 * @param model
	 *            Das schon erzeugte Model
	 * @param workbench
	 *            Die Werkbank
	 */
        public LegoController(LegoModel model, Workbench workbench) {
                this.workbench = workbench;
                this.model = model;
                this.tools = new LegoTools(model, this);
        }

        /**
	 * Wird von der View-Komponente ausgeloest und fuehrt die entsprechende
	 * Aktion aus.
	 * 
	 * @param e
	 *            Der ActionEvent, der die Aktion beschreibt.
	 */
        public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand() == ACTION_UPLOAD) {
                        if (workbench.ensureSaved(activeFile)
                                                && workbench.ensureCompiled(activeFile)) {
                                try {
                                        model.legoCompile(activeFile);
                                } catch (IOException ex) {
                                        ex.printStackTrace();
                                }
                                model.uploadFile(activeFile);
                        }
                } 
        }
        
        
        public Workbench getWorkbench() {
		return workbench;
	}
        
        /**
	 * Setzt den neuen Wert von activeFile und übergibt diesen auch 
         * gleich der Viewkomponente.
	 * 
	 * @param activeFile
	 *            Der neue Wert von activeFile.
	 */
        public void setActiveFile(HamsterFile file){
                activeFile = file;
                tools.setActiveFile(file);
        }
 }
