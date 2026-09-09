package de.hamster.simulation.view.multimedia.opengl.objects;

/**
 * @author chris 
 * 
 * Animationsdaten für Objekte.
 */


import java.util.ArrayList;

public class Animation {

	private ArrayList<Integer> keyframes;

	private ArrayList<Integer> delays; // in millisekunden

	private int successor;

	private Animation switchNextTo;
	
	private Obj obj;

	private String name;

	private int lastKeyframeID = 0;

	public Animation(Animation a) {
		
		this(a.getName());
		
		this.obj = a.getObj(); // das wird übernommen
		
		this.successor = a.getSuccessorID();
		this.switchNextTo = a.getSwitchNextTo();
		this.lastKeyframeID = a.getLastKeyFrameID();
		this.keyframes = new ArrayList<Integer>(a.getKeyframes());
		this.delays = new ArrayList<Integer>(a.getDelays());
	}

	public Animation(String name) {

		this.keyframes = new ArrayList<Integer>();
		this.delays = new ArrayList<Integer>();
		this.successor = 0;
		this.obj = null;
		this.switchNextTo = null;
		this.name = name;
	}

	public void addKeyframeIndex(int k, int d) {

		this.keyframes.add(new Integer(k));
		this.delays.add(new Integer(d));

	}

	public ArrayList<Integer> getDelays() {
		return delays;
	}

	public int getDelay(int i) {
		return delays.get(i).intValue();
	}

	public ArrayList<Integer> getKeyframes() {
		return keyframes;
	}

	public int getKeyframeIndex(int i) {
		return keyframes.get(i).intValue();
	}

	public int getLastKeyFrame() {
		// das ist leicht:
		
		return this.keyframes.get(this.lastKeyframeID);
	}

	public int getLastKeyFrameID() {
		// das ist leicht:
		return this.lastKeyframeID;
	}
	
	public int getNextKeyFrame() {
		// schwerer, denn hier müssen ggf. aniwechsel betrachtet werden:

		// falls wir als nextes hier rausspringen werden, liefern wir den ersten
		// frame
		// dieser sprunganimation:
		if (this.switchNextTo != null) {
			Animation t = switchNextTo;
			return t.getLastKeyFrame();
		}

		// wir springen nicht in eine andere ani, 
		// also schauen, ob wir nur einen keyframe weiter gehen oder loopen
		int tempNewKeyframeID = this.lastKeyframeID + 1;
		
		if (tempNewKeyframeID >= this.keyframes.size()) {

			if (this.successor == -1) { // aha, wir loopen.
				tempNewKeyframeID = 0;	
			} else { 
				// aha, wir sind zuende und springen zum planmäßigen
				// nachfolger:
				Animation s = this.obj.getAnimation(this.successor);			
				
				return s.getLastKeyFrame();
			}
		}
		return this.keyframes.get(tempNewKeyframeID);
	}

	public int getLastDelay() {
		return this.delays.get(this.lastKeyframeID);
	}

	public int numberOfKeyframes() {
		return this.keyframes.size();
	}

	public boolean isLooping() {

		return (this.successor == -1) ? false : true;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSuccessorID() {
		return successor;
	}

	public void setSuccessorID(int next) {
		this.successor = next;
	}

	public Animation skipThisKeyframe() {

		this.lastKeyframeID++;

		// falls wir absichtlich hier rausspringen wollen:
		if (this.switchNextTo != null) {
			this.lastKeyframeID = 0;
			Animation t = switchNextTo;
			this.switchNextTo = null;
			return t;
		}

		// wir springen nicht also schauen, ob wir nur einen keyframe weiter
		// gehen
		if (lastKeyframeID >= this.keyframes.size()) {
			
			if (successor == -1) { // aha, wir loopen.
				this.lastKeyframeID = 0;
			} else { 			
				this.lastKeyframeID = 0;
				return this.obj.getAnimation(this.successor);
			}
		}
		return this;
	}

	public Animation getSwitchNextTo() {
		return switchNextTo;
	}

	public void setSwitchNextTo(Animation switchNextTo) {
		this.switchNextTo = switchNextTo;
	}

	public Obj getObj() {
		return obj;
	}

	public void setObj(Obj o) {
		this.obj = o;
	}

	public void setNextKeyframe(int i) {
			this.lastKeyframeID = i;
		
	}

}

