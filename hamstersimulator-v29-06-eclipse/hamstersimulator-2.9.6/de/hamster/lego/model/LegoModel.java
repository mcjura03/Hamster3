package de.hamster.lego.model;

import de.hamster.model.HamsterFile;
import de.hamster.workbench.Utils;
import de.hamster.workbench.Workbench;

import java.awt.BorderLayout;
import java.awt.Button;
import java.io.IOException;
import java.util.Observable;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import js.common.CLIToolProgressMonitor;
import js.tinyvm.TinyVM;
import js.tinyvm.TinyVMException;
import lejos.pc.tools.NXJBrowser;
import lejos.pc.tools.NXJBrowserCommandLineParser;
import lejos.pc.tools.NXJUpload;




/**
 * Dies ist das Model der Lego Komponente. Es kombiniert den LegoCompiler, die .nxj-Code Erzeugung und den Upload.
 * @author Christian
 */
public class LegoModel extends Observable {
        
        /**
         * Dieses Argument benachrichtigt über die Aenderung des Uploadstatus`
         */
        public static final String LEGO_UPLOAD = "upload";
        
        /**
         * Dieses Argument informiert darüber, dass gerade kein Upload stattfindet.
         */
        public static final int NOT_CONNECTED = 0;
        
        /**
         * Dieses Argument benachrichtigt über den Erfolg eines Uploads.
         */
        public static final int SUCCESS = 1;
        
        /**
         * Dieses Argument benachrichtigt über einen gescheiterten Uploadversuch.
         */
        public static final int FAILURE = 2;
        
        private LegoCompiler legoCompiler;
        
        private LegoPrecompiler precompiler;
        
        private Workbench workbench;
        
        private Upload upload;
        
        private int state;
        
        /**
         * Der Konstruktor von LegoModel erzeugt lediglich Instanzen von 
         * LegoCompiler, dem Lego-Precompiler und Upload.
         */
        public LegoModel() {
                state = NOT_CONNECTED;
                legoCompiler = new LegoCompiler();
                precompiler = new LegoPrecompiler();
                upload = new Upload();
        }
        
        /**
         * Lädt ein nxj Hamsterfile auf den NXT.
         * @param file 
         *              Das hochzuladene HamsterFile.
         */
        public void uploadFile(HamsterFile file) {
                if(upload.connect()) {
                      try {
                        upload.createNXJ(file);
                        upload.uploadFile(file);
                        setState(SUCCESS);
                        } catch (ExceptionInInitializerError ex) {
                                setState(FAILURE);
                        }  
                } else 
                        setState(FAILURE);
              
//                setChanged();
//		notifyObservers(LEGO_UPLOAD);
        }
        
        /**
         * Ruft den Precompiler und den JavaCompiler auf. 
         * @param file 
         *              Das HamsterFile, das vom JavaCompiler kompiliert werden soll.
         * @throws java.io.IOException 
         */
        public void legoCompile(HamsterFile file) throws IOException{
                precompiler.precompile(file);
                legoCompiler.compile(file);
        }
        

        /**
         * Liefert den Upload-Status.
         * @return Der aktuelle Status.
         */
        public int getState() {
                return state;
        }
        
        /**
         * Setzt den Status des Uploads.
         * @param state Der neue Status.
         */
        public void setState(int state) {
                this.state = state;
                setChanged();
		notifyObservers(LEGO_UPLOAD);
                
        }
}
