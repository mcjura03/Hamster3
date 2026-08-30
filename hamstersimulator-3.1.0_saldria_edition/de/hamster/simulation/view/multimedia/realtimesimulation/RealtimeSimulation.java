package de.hamster.simulation.view.multimedia.realtimesimulation;

import java.util.Vector;

import de.hamster.debugger.model.DebuggerModel;
import de.hamster.simulation.model.Hamster;
import de.hamster.simulation.model.SimulationModel;
import de.hamster.simulation.model.Terrain;
import de.hamster.simulation.view.multimedia.opengl.OpenGLController;
import de.hamster.simulation.view.multimedia.opengl.Scene;
import de.hamster.simulation.view.multimedia.sound.PlayWave2;
import de.hamster.simulation.view.multimedia.sound.SoundClip;
import de.hamster.simulation.view.multimedia.sound.SoundManager;

/**
 * @author chris
 * 
 * Die Echtzeitsimulationskomponente, die aus den diskreten Hamsterpositionen
 * einen Satz an Pixelgenauen Positionen für die Darstellung in 3D generiert.
 * 
 */
public class RealtimeSimulation {

	private Vector<MovementCacheEntry> entries = null;

	private SimulationModel sim;

	private DebuggerModel deb;

	private Vector<MovementSet> interpolatedPositions;

	private int interpolatedHamsters;

	private Terrain interpolatedTerrain = null;

	private long lastInterpolateTime = 0;

	private float speed = 2.5f; // einheiten pro sekunde.

	private float movedSinceLastStage = 0f; // entfernung pro stage = 1f;

	private long stepSoundCounterTemp = 0;

	private PlayWave2 playWave = null;

	private Scene scene;

	private SoundManager soundManager;

	private boolean playSound = true;

	public RealtimeSimulation(SimulationModel s, DebuggerModel d, Scene scene) {
		this.entries = new Vector<MovementCacheEntry>();
		this.sim = s;
		this.deb = d;
		this.interpolatedPositions = new Vector<MovementSet>();
		this.lastInterpolateTime = System.currentTimeMillis();
		this.scene = scene;

		this.soundManager = new SoundManager(s);

		this.soundManager.setSound(SoundManager.SOUND_WALK, new SoundClip(
				"data/schritte.raw", SoundClip.PLAY_LOOPED, 500));

	}

	public void addStage() {

		MovementCacheEntry e = new MovementCacheEntry();
		e.setTerrain(this.sim.getTerrain());

		Hamster h;

		h = sim.getHamster(-1);
		e.addHamster(h.getX(), h.getY(), h.getDir(), false, h.getColor());

		// nun die anderen:
		for (int i = 0; i < sim.getHamster().size(); i++) {
			h = sim.getHamster(i);
			e.addHamster(h.getX(), h.getY(), h.getDir(), false, h.getColor());
		}
		this.entries.add(e);
	}

	public void interpolatePositions(long time) {

		this.speed = 1000 / ((this.deb.getDelay() / 1.1f) + 50);

		soundManager.setSpeed(this.deb.getDelay());

		long delta = time - this.lastInterpolateTime;

		// weil die OO-Hamster zur laufzeit erstellt und gelöscht werden,
		// müssen wir zwangsläufig mit änderungen in der anzahl rehcnen.
		// das muss sich in der anzahl der interpolierten hamster
		// wiederspiegeln:
		if (interpolatedPositions.size() < this.sim.getHamsterIDs().size()) {

			for (int i = interpolatedPositions.size(); i < this.sim
					.getHamsterIDs().size(); i++) {
				interpolatedPositions.add(new MovementSet(0, 0, 1, false, -1));

				interpolatedPositions.get(i).setValues(
						sim.getHamster(i - 1).getX(),
						sim.getHamster(i - 1).getY(),
						sim.getHamster(i - 1).getDir(), false,
						sim.getHamster(i - 1).getColor());
			}
		} else if (interpolatedPositions.size() > this.sim.getHamsterIDs()
				.size()) {
			for (int i = this.sim.getHamsterIDs().size(); i < interpolatedPositions
					.size(); i++) {
				interpolatedPositions.remove(this.sim.getHamsterIDs().size());
			}
		}

		// einfachster Fall: es liegen gar keine gecachten Events vor:
		if (this.entries.size() == 0) {

			// in diesem status läuft keine Simulation, aber die Positionen des
			// Hamsters können sich durch Usereingaben verändern. also setzen
			// wir die interpolierten positionen auf die werte in der simulation
			// zuallererst schauen, ob das feld der interpolierten hamster
			// korrekt ist:

			// setze den defaulthamster auf seine pos:
			this.interpolatedPositions.get(0).setValues(
					sim.getHamster(-1).getX(), sim.getHamster(-1).getY(),
					sim.getHamster(-1).getDir(), false,
					sim.getHamster(-1).getColor());

			this.interpolatedTerrain = new Terrain(this.sim.getTerrain());

			this.lastInterpolateTime = time;

			this.scene.setLoopDelay(40);

			this.soundManager.stop(SoundManager.SOUND_WALK);

		} else {

			// es gibt zumindest ein event:

			// estmal loopen
			while (this.entries.size() != 0) {

				float x, y, dir;
				boolean moving = false;

				// zuerst schauen, ob sich mit dem aktuellen eintrag ein hamster
				// bewegt:
				for (int i = 0; i < this.entries.get(0).numberOfHamsters(); i++) {

					x = interpolatedPositions.get(i).x;
					y = interpolatedPositions.get(i).y;
					dir = interpolatedPositions.get(i).dir;

					if (dir != this.entries.get(0).getHamster(i).dir
							|| x != this.entries.get(0).getHamster(i).x
							|| y != this.entries.get(0).getHamster(i).y) {
						moving = true;
						interpolatedPositions.get(i).moves = true;
					}
				}

				// ok, dann sehen wir mal:
				if (moving == false) { // && keine änderung an den körnern!

					// es wurde kein Hamster bewegt oder gedreht. also ist dies
					// ein leer-event oder zeigt, daß ein neuer OO-Hamster
					// erstellt wurde
					// in jedem Fall müssen wir hier nichts extra tun, sondern
					// löschen
					// das event und nehmen uns einfacc mal das nächste vor,
					// sofern vorhanden.

					entries.remove(0);
					this.scene.setLoopDelay(40);

					// den rest macht die while-schleife.

				} else {

					// ok, es wird ernster, es hat sich mindestens ein hamster
					// bewegt,
					// und wir müssen dieses event abarbeiten. danach loopen wir
					// auch nicht
					// weiter zum nächsten event.

					if (this.playSound)
						this.soundManager.start(SoundManager.SOUND_WALK);

					// anhand von delta feststellen, wieviele einheiten die
					// hamster
					// bewegt werden müssen:
					float dist = this.speed * (float) delta / 1000f;

					// prüfen, ob so viel zeit vergangen ist, daß wir das event
					// ganz abhaken können:
					if (dist + this.movedSinceLastStage >= 1f) {

						float restDistanz = 1f - this.movedSinceLastStage;
						float restZeit = delta / (dist / restDistanz);

						// setze alle hamster auf den zustand der aktuellen
						// stage
						for (int i = 0; i < this.entries.get(0)
								.numberOfHamsters(); i++) {
							MovementSet h = this.entries.get(0).getHamster(i);
							interpolatedPositions.get(i).setValues(h.x, h.y,
									h.dir, false, h.color);
						}

						// die stage aus der liste löschen. falls bereits eine
						// weitere
						// darin ist, wird diese das neue ziel
						entries.remove(0);

						// wir beginnen gleich eine neue stage, also auf null:
						this.movedSinceLastStage = 0;

						// bis dahin haben wir schon.
						this.lastInterpolateTime = time - (long) restZeit;

					} else {

						this.scene.setLoopDelay(4);

						this.interpolatedTerrain = new Terrain(entries.get(0)
								.getTerrain());

						// mit dem schritt werden wir keine stage komplett
						// erreichen
						// also überspringen wir sie nicht, sondern latschen
						// mutig los.

						// alle hamster durchlaufen und ihre positionen anhand
						// der
						// richtung aktualisieren:
						// (wir wissen ja genau, daß mindests (und vermutlich
						// genau) einer läuft)
						for (int i = 0; i < this.entries.get(0)
								.numberOfHamsters(); i++) {

							x = interpolatedPositions.get(i).x;
							y = interpolatedPositions.get(i).y;
							dir = interpolatedPositions.get(i).dir;
							interpolatedPositions.get(i).moves = false;

							// feststellen, ob wir uns drehen, oder ob wir
							// laufen:
							if (dir != this.entries.get(0).getHamster(i).dir) {
								// aha, wir drehen:
								interpolatedPositions.get(i).dir -= dist;
								interpolatedPositions.get(i).moves = true;

							} else if (x != this.entries.get(0).getHamster(i).x
									|| y != this.entries.get(0).getHamster(i).y) {
								// aha, wir laufen:
								interpolatedPositions.get(i).moves = true;

								if (dir == 0f)
									interpolatedPositions.get(i).y -= dist;
								else if (dir == 2f)
									interpolatedPositions.get(i).y += dist;
								else if (dir == 1f)
									interpolatedPositions.get(i).x += dist;
								else if (dir == 3f)
									interpolatedPositions.get(i).x -= dist;
							}
						}

						// die in dieser stage gelaufene distanz aktualisieren:
						this.movedSinceLastStage += dist;
						this.lastInterpolateTime = time;
					}
					return; // aus der while schleifen rauspringen, weil wir
							// jetzt was zu bewegen haben!
				}
			}
		}
	}

	public float getInterpolatedX(int hamster) {
		hamster++; // der doofe defaulthamster mit id -1 ist hier als element 0
		// gespeichert.
		return interpolatedPositions.get(hamster).x;
	}

	public boolean getInterpolatedMoves(int hamster) {

		hamster++; // der doofe defaulthamster mit id -1 ist hier als element 0
		// gespeichert.
		return interpolatedPositions.get(hamster).moves;
	}

	public float getInterpolatedY(int hamster) {
		hamster++; // der doofe defaulthamster mit id -1 ist hier als element 0
		// gespeichert.
		return interpolatedPositions.get(hamster).y;
	}

	public float getInterpolatedDir(int hamster) {
		hamster++; // der doofe defaulthamster mit id -1 ist hier als element 0
		// gespeichert.
		return interpolatedPositions.get(hamster).dir;
	}

	public SimulationModel getSim() {
		return sim;
	}

	public Terrain getTerrain() {
		return this.interpolatedTerrain;
	}

	public void setSim(SimulationModel sim) {
		this.sim = sim;
	}

	// einschließlich des -1er default hamsters:
	public int getNumberOfHamsters() {
		return interpolatedPositions.size();
	}

	public void toggleSound(boolean b) {
		this.playSound = b;
		if (!this.playSound)
			this.soundManager.stop(SoundManager.SOUND_WALK);
	}

}
