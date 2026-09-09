package de.hamster.simulation.view.multimedia.sound;

/**
 * @author chris
 * 
 * Clip-Klasse zum abspielen von vorher in den SPeicher geladenen Soundeffekten
 * 
 */

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Control;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.DataLine.Info;

public class SoundClip extends Sound{

	private Info info;
	private Clip clip;
	public int type = 0;
	
	public static final int PLAY_ONCE   = 0;
	public static final int PLAY_LOOPED = 1;
	
	public SoundClip(String filename, int type, int speed) {
		super(filename, speed);
		
		this.type = type;
	
		this.info = new Info(Clip.class, this.audioFormat); 
		
		try {
			this.clip = (Clip) AudioSystem.getLine(this.info);
						
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
		
		try {
			try {
				this.clip.open(this.audioInputStream);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}
	
	
	public void start() {
		if (this.type == SoundClip.PLAY_LOOPED) {
			if (!this.clip.isRunning()) 
				this.clip.loop(-1); 
		}
		else {
			if (this.type == SoundClip.PLAY_ONCE)  this.clip.start();	
		}

	}
	
	public void stop() {
		this.clip.stop();
	}

	public void close() {
	
		this.stop();
		this.clip.close();		
	}
	
}
