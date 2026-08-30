package de.hamster.simulation.view.multimedia.sound;

import de.hamster.simulation.model.SimulationModel;

/**
 * @author chris
 * 
 *         Verwaltet die Sounds.
 *
 */

public class SoundManager {

	public static final int SOUND_IDLE = 0;
	public static final int SOUND_WALK = 1;
	public static final int SOUND_EAT = 2;
	public static final int SOUND_COLLIDE = 3;

	public final int MAX_SOUNDS = 4;

	private Sound[] sounds = new Sound[MAX_SOUNDS];

	private int speed = 500;
	private SimulationModel simModel;

	public SoundManager(SimulationModel simModel) {
		this.simModel = simModel;
		for (int i = 0; i < this.MAX_SOUNDS; i++)
			sounds[i] = null;

	}

	public void setSound(int pos, Sound s) {
		this.sounds[pos] = s;
	}

	public void start(int pos) {
		if (sounds[pos] != null)
			this.sounds[pos].start();
	}

	public void stop(int pos) {
		if (sounds[pos] != null)
			this.sounds[pos].stop();
	}

	public void closeAll() {
		for (int i = 0; i < this.MAX_SOUNDS; i++)
			if (sounds[i] != null)
				this.sounds[i].close();
	}

	public void setSpeed(int speed) {

		if (speed == this.speed)
			return;

		this.speed = speed;

		for (int i = 0; i < this.MAX_SOUNDS; i++) {

			if (sounds[i] != null) {
				this.sounds[i].stop();
				SoundClip neu = new SoundClip(this.sounds[i].filename,
						((SoundClip) this.sounds[i]).type, speed);
				this.sounds[i] = neu;
			}
		}

	}

}
