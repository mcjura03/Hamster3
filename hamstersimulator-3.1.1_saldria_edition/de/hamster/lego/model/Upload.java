package de.hamster.lego.model;

import de.hamster.model.HamsterFile;
import de.hamster.workbench.Utils;
import de.hamster.workbench.Workbench;

import java.io.IOException;

import js.common.CLIToolProgressMonitor;
import js.tinyvm.TinyVM;
import js.tinyvm.TinyVMException;
import lejos.pc.comm.*;
import lejos.pc.tools.NXJUpload;

/**
 * Beinhaltet die Methoden zum Verbinden mit dem NXT und dem Hochladen einer Datei.
 * @author Christian
 */
public class Upload {
        
        private NXTComm nxtComm;
        
        public Upload() {
        }
        
        /**
         * Überprüft, ob eine Verbindung zu einem NXT aufgebaut werden kann oder nicht.
         *
         * @return true 
         *              Falls ein NXT gefunden wurde.
         * @return false 
         *              Wenn keine Verbindung.
         */
        public boolean connect() {
        	try {
                NXTInfo[] nxtInfo ;
                
                nxtComm = new NXTCommLibnxt();
                System.out.println("searching");
                nxtInfo = nxtComm.search(null, NXTCommFactory.USB);
                if (nxtInfo.length == 0) {
                        System.out.println("No NXT Found");
                        return false;
                }
                nxtComm.open(nxtInfo[0]);
                System.out.println(" Opened "+nxtInfo[0].name);
                try {
                        
                        nxtComm.close();
                } catch (IOException ex) {
                        ex.printStackTrace();
                }
                return true;
        	} catch (Throwable t) {
        		t.printStackTrace();
        		return false;
        	}
        }
        
        /**
         * Lädt ein nxj Hamsterfile auf den NXT.
         *
         * @param file 
         *              Das hochzuladene HamsterFile.
         * @throws java.lang.ExceptionInInitializerError 
         *              wird geworfen, falls keine Verbindung besteht.
         */
        public void uploadFile(HamsterFile file) throws ExceptionInInitializerError {
                String path = file.getAbsolute();
                path = path.substring(0, path.lastIndexOf("."))+"Lego";
                String[] argU = {path + ".nxj"};
                NXJUpload upload = new NXJUpload();
                try {
                        
                        upload.run(argU);       //hochladen auf den NXT
                        
                } catch(js.tinyvm.TinyVMException tvexc) {
                        System.err.println("Error: " + tvexc.getMessage());
                }
        }
        
        /**
         * Erstellt eine nxj-Datei aus dem Hamster-Programm. Dabei wird der 
         * zuvor durch den Precompiler generierte Java-Code in den Zielcode  
         * des NXT übersetzt.
         *
         * @param file 
         *              Das hochzuladene HamsterFile.
         */
        public void createNXJ(HamsterFile file) {
                String classpath = System.getProperty("java.class.path");
                String path = file.getAbsolute();
                path = path.substring(0, path.lastIndexOf("."))+"Lego";
                String newClasspath = file.getAbsolute().substring(0,
                        file.getAbsolute().lastIndexOf(Utils.FSEP))
                        + Utils.PSEP
                        + classpath
                        + Utils.PSEP
                        + Workbench.getWorkbench().getProperty("classpath", "");
                String[] arg = {
                        "--classpath"
                                ,newClasspath
                                ,"--writeorder"
                                ,"LE"
                                ,file.getName()+"Lego"
                                ,"-o"
                                ,path + ".nxj"
                };
                TinyVM tiny = new TinyVM(new CLIToolProgressMonitor());
                try {
                        
                        tiny.start(arg);        //erstellen der nxj Datei
                        
                } catch(js.tinyvm.TinyVMException tvexc) {
                        System.err.println("Error: " + tvexc.getMessage());
                }
        
        }
}
