package de.hamster.simulation.view.multimedia.sound;

/**
 * @author chris
 * 
 * Basisklasse für Sounds,so midifiziert, daß sie wave daten im raw-format (also ohne .wav header)
 * einliest und dabei die abspielgeschwindigkeit variabel einsellt. dieser hack ist zur umgehung der
 * fehlenden controller für die abspielfrequenz in dieser java-version.
 * 
 */

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Sound {

	protected File file;
	protected String filename;
	protected AudioInputStream audioInputStream;
	protected AudioFormat audioFormat;
	protected AudioFormat originalAudioFormat;
	protected DataLine dataLine;
	protected int speed = 5;

	public Sound(String filename, int speed) {

		this.speed = speed;
		this.filename = filename;
		this.file = new File(filename);
		try {

			// this.audioInputStream =
			// AudioSystem.getAudioInputStream(this.file);

			// this.audioInputStream = new AudioInputStream();

			FileInputStream in = new FileInputStream(this.file);
			AudioFormat format = new AudioFormat(44100f, 16, 1, true, false);
			this.audioInputStream = new AudioInputStream(in, format,
					this.file.length());
			this.originalAudioFormat = this.audioInputStream.getFormat();

			this.resetAudioFormat();

			this.load();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void reload() {

	}

	public void close() {
	}

	public void start() {
	}

	public void stop() {
	}

	public void loop() {
	}

	public void load() {

		InputStream is;
		try {

			is = new FileInputStream(this.filename);
			this.audioInputStream = new AudioInputStream(is, this.audioFormat,
					this.file.length());

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void resetAudioFormat() {

		this.audioFormat = new AudioFormat(
				this.originalAudioFormat.getSampleRate() - (this.speed * 6),
				this.originalAudioFormat.getSampleSizeInBits(),
				this.originalAudioFormat.getChannels(), true,
				this.originalAudioFormat.isBigEndian());
	}

}
