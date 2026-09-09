package de.hamster.simulation.view.multimedia.sound;

/**
 * @author chris 
 * 
 * Spielt eine Midi-Datei in einem eigenen Thread ab.
 */

import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;

import de.hamster.simulation.model.SimulationModel;

public class MidiFile {

	private Thread thread = null;
	private boolean running = false;

	private SimulationModel simModel = null;

	public MidiFile(SimulationModel simModel) {
		this.simModel = simModel;
	}

	public void stop() {
		running = false;
	}

	public void play(String filename) {

		final File file = new File(filename);

		if (thread != null) {
			running = false;
			thread = null;
		}

		thread = new Thread() {
			public void run() {

				try {

					Sequencer sequencer = MidiSystem.getSequencer();
					sequencer.setSequence(MidiSystem.getSequence(file));
					sequencer.open();
					sequencer.start();
					running = true;

					while (running) {
						if (sequencer.isRunning()) {
							try {
								Thread.sleep(400);
							} catch (InterruptedException ignore) {
								break;
							}
						} else {
							sequencer.stop();
							sequencer.setSequence(MidiSystem.getSequence(file));
							sequencer.open();
							sequencer.start();
						}
					}
					sequencer.stop();
					running = false;

				} catch (MidiUnavailableException mue) {
					System.out.println("Midi device unavailable!");
				} catch (InvalidMidiDataException imde) {
					System.out.println("Invalid Midi data!");
				} catch (IOException ioe) {
					System.out.println("I/O Error!");
				}

			}
		};

		thread.start();

	}
}
