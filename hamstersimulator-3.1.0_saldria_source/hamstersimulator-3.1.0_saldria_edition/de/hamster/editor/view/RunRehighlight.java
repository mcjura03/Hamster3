package de.hamster.editor.view;


/**
 * @author Daniel
 */
public class RunRehighlight implements Runnable {
	int offset;
	int length;
	HamsterDocument document;
	//SchemeDocument document2; // Martin
	
	public RunRehighlight(int offset, int length, HamsterDocument document) {
		this.offset = offset;
		this.length = length;		
		this.document = document;
	}
	
	/*
	// martin
	public RunRehighlight(int offset, int length, SchemeDocument document) {
		this.offset = offset;
		this.length = length;		
		this.document2 = document;
	}
	*/
	
	public void run() {
		// Martin
		if(document != null) {
			document.rehighlight(offset, length);
		}
		/*else if(document2 != null) {
			document2.rehighlight(offset, length);
		}*/
	}
}
