//package de.hamster.lego.model;
//
//import lejos.navigation.Pilot;
//import lejos.nxt.LCD;
//import lejos.nxt.LightSensor;
//import lejos.nxt.Motor;
//import lejos.nxt.SensorPort;
//import lejos.nxt.Sound;
//import lejos.nxt.TouchSensor;
//import lejos.nxt.UltrasonicSensor;
//
///**
// * Von dieser Klasse erbt das Hamster-Programm, das auf den NXT-Baustein geladen
// * werden soll. Da leJOS NXJ zum gegenw�rtigen Zeitpunkt (0.4 Beta) noch keine
// * GarbageCollection unterst�tzt und um das Datenvolumen m�glichst gering zu
// * halten, sind alle Variablen, die f�r den Betrieb unerl�sslich sind, global
// * definiert.
// * 
// * @author Christian
// */
//public class LegoHamster {
//
//	/*
//	 * Der Ber�hrungssensor.
//	 */
//	TouchSensor touch = new TouchSensor(SensorPort.S1);
//
//	/*
//	 * Der Lichtsensor.
//	 */
//	LightSensor light = new LightSensor(SensorPort.S3);
//
//	/*
//	 * Der Ultraschallsensor
//	 */
//	UltrasonicSensor ultra = new UltrasonicSensor(SensorPort.S2);
//
//	/*
//	 * Der Pilot, dieser bietet dem NXT Nutzer mehr Komfort
//	 */
//	Pilot pilot = new Pilot(54, 115, Motor.B, Motor.A);
//
//	/*
//	 * Wert f�r den Mittelpunkt
//	 */
//	final int BLACK = 39;
//
//	/*
//	 * Wert f�r die Hintergrundsfl�che
//	 */
//	final int WHITE = 52;
//
//	int value = 10;
//	boolean maulLeer = true;
//	int time;
//	boolean kornDa;
//	int distance = 255;
//	/*
//	 * Minimale und maximale Distanz des Roboters zum Korn
//	 */
//	final int CORN_MIN_DIST = 32;
//	final int CORN_MAX_DIST = 45;
//
//	final int WALL_DIST = 25;
//
//	public void vor() {
//		if (!vornFrei() || kornDa()) {
//			LCD.clear();
//			LCD.drawString("Exception:", 0, 3);
//			if (!vornFrei())
//				LCD.drawString("MauerDa", 0, 4);
//			else
//				LCD.drawString("KornDa", 0, 4);
//
//			LCD.refresh();
//			_intern_play_exception_sound();
//			System.exit(1);
//		}
//		_intern_move_forward_001();
//	}
//
//	public void linksUm() {
//		Motor.A.setSpeed(250);
//		Motor.B.setSpeed(250);
//		Motor.B.backward();
//		Motor.A.forward();
//		try {
//			Thread.sleep(500);
//			while (light.readValue() > WHITE) {
//			}
//			Thread.sleep(65);
//		} catch (InterruptedException ex) {
//		}
//		Motor.A.stop();
//		Motor.B.stop();
//	}
//
//	public void nimm() {
//		if (!vornFrei() || !maulLeer() || !kornDa()) {
//			LCD.clear();
//			LCD.drawString("Exception:", 0, 3);
//			if (!vornFrei())
//				LCD.drawString("MauerDa", 0, 4);
//			else if (!maulLeer())
//				LCD.drawString("MaulNichtLeer", 0, 4);
//			else
//				LCD.drawString("KachelLeer", 0, 4);
//
//			LCD.refresh();
//			_intern_play_exception_sound();
//			System.exit(1);
//
//		}
//		_intern_take_corn_003();
//
//	}
//
//	public void gib() {
//		if (!vornFrei() || maulLeer() || kornDa()) {
//			LCD.clear();
//			LCD.drawString("Exception:", 0, 3);
//			if (!vornFrei())
//				LCD.drawString("MauerDa", 0, 4);
//			else if (maulLeer())
//				LCD.drawString("MaulLeer", 0, 4);
//			else
//				LCD.drawString("KornDa", 0, 4);
//			LCD.refresh();
//			_intern_play_exception_sound();
//			System.exit(1);
//		}
//		_intern_lay_down_002();
//	}
//
//	public boolean vornFrei() {
//		distance = ultra.getDistance();
//		while (distance == 1) {
//			distance = ultra.getDistance();
//		}
//		return !(distance < WALL_DIST);
//	}
//
//	public boolean kornDa() {
//		kornDa = false;
//
//		pilot.rotate(10);
//		for (int i = 0; i < 10 && kornDa == false; i++) {
//			distance = ultra.getDistance();
//			if (distance > CORN_MIN_DIST && distance < CORN_MAX_DIST) {
//				kornDa = true;
//				break;
//			} else {
//				try {
//					Thread.sleep(230);
//				} catch (InterruptedException ex) {
//				}
//				pilot.rotate(-4);
//			}
//
//		}
//
//		Motor.A.setSpeed(250);
//		Motor.B.setSpeed(250);
//		Motor.B.backward();
//		Motor.A.forward();
//		while (light.readValue() > WHITE) {
//		}
//		try {
//			Thread.sleep(65);
//		} catch (InterruptedException ex) {
//		}
//		Motor.A.stop();
//		Motor.B.stop();
//		return kornDa;
//	}
//
//	public boolean maulLeer() {
//		return maulLeer;
//	}
//
//	public void move() {
//		if (!vornFrei() || kornDa()) {
//			LCD.clear();
//			LCD.drawString("Exception:", 0, 3);
//			if (!vornFrei())
//				LCD.drawString("WallInFront", 0, 4);
//			else
//				LCD.drawString("CornThere", 0, 4);
//
//			LCD.refresh();
//			_intern_play_exception_sound();
//			System.exit(1);
//		}
//		_intern_move_forward_001();
//	}
//
//	public void turnLeft() {
//		linksUm();
//	}
//
//	public void pickGrain() {
//		if (!vornFrei() || !maulLeer() || !kornDa()) {
//			LCD.clear();
//			LCD.drawString("Exception:", 0, 3);
//			if (!vornFrei())
//				LCD.drawString("WallInFront", 0, 4);
//			else if (!maulLeer())
//				LCD.drawString("MouthNotEmpty", 0, 4);
//			else
//				LCD.drawString("TileEmpty", 0, 4);
//			LCD.refresh();
//			_intern_play_exception_sound();
//			System.exit(1);
//
//		}
//		_intern_take_corn_003();
//	}
//
//	public void putGrain() {
//		if (!vornFrei() || maulLeer() || kornDa()) {
//			LCD.clear();
//			LCD.drawString("Exception:", 0, 3);
//			if (!vornFrei())
//				LCD.drawString("WallInFront", 0, 4);
//			else if (maulLeer())
//				LCD.drawString("MouthEmpty", 0, 4);
//			else
//				LCD.drawString("CornThere", 0, 4);
//			LCD.refresh();
//			_intern_play_exception_sound();
//			System.exit(1);
//		}
//		_intern_lay_down_002();
//	}
//
//	public boolean frontIsClear() {
//		return vornFrei();
//	}
//
//	public boolean grainAvailable() {
//		return kornDa();
//	}
//
//	public boolean mouthEmpty() {
//		return maulLeer();
//	}
//
//	public void _intern_move_forward_001() {
//		pilot.setSpeed(300);
//		pilot.forward();
//		while (light.readValue() > BLACK) {
//			// von der Linie
//			if (light.readValue() > WHITE) {
//				pilot.stop();
//				for (int i = value; light.readValue() > WHITE; i = i * (-2)) {
//					pilot.rotate(i);
//					if (i < 0)
//						value = -10;
//					else
//						value = 10;
//				}
//				pilot.forward();
//			}
//		}
//		pilot.stop();
//		pilot.travel(77);
//	}
//
//	public void _intern_lay_down_002() {
//		pilot.setSpeed(300);
//		pilot.forward();
//		while (light.readValue() > BLACK) {
//			// von der Linie
//			if (light.readValue() > WHITE) {
//				pilot.stop();
//				for (int i = value; light.readValue() > WHITE; i = i * (-2)) {
//					pilot.rotate(i);
//					if (i < 0)
//						value = -10;
//					else
//						value = 10;
//				}
//				pilot.forward();
//			}
//		}
//		pilot.stop();
//		pilot.travel(-70);
//		Motor.C.rotate(70); // Greifer �ffnen
//		pilot.travel(-80);
//		// Greifer schliessen
//		Motor.C.setSpeed(300);
//		Motor.C.backward();
//		try {
//			Thread.sleep(200);
//		} catch (InterruptedException ex) {
//		}
//		// time = (int) System.currentTimeMillis();
//		// while(200 > (int)System.currentTimeMillis()-time){
//		// }
//		Motor.C.stop();
//		linksUm();
//
//		// vor
//		_intern_move_forward_001();
//
//		linksUm();
//		linksUm();
//		maulLeer = true;
//	}
//
//	public void _intern_take_corn_003() {
//		Motor.C.rotate(70); // Greifer �ffnen
//		pilot.setSpeed(300);
//		pilot.forward();
//		while (!touch.isPressed()) {
//			// von der Linie
//			if (light.readValue() > WHITE) {
//				pilot.stop();
//				for (int i = value; light.readValue() > WHITE; i = i * (-2)) {
//					pilot.rotate(i);
//					if (i < 0)
//						value = -10;
//					else
//						value = 10;
//				}
//				pilot.forward();
//			}
//		}
//		pilot.stop();
//		Motor.C.setSpeed(400);
//		Motor.C.backward();
//		try {
//			Thread.sleep(300);
//		} catch (InterruptedException ex) {
//		}
//		Motor.C.stop();
//		linksUm();
//
//		// vor
//		_intern_move_forward_001();
//
//		linksUm();
//		linksUm();
//		maulLeer = false;
//	}
//
//	public void _intern_play_exception_sound() {
//
//		try {
//			Sound.playTone(2060, 500);
//			Thread.sleep(705);
//			Sound.playTone(2060, 300);
//			Thread.sleep(405);
//			Sound.playTone(2060, 300);
//			Thread.sleep(405);
//			Thread.sleep(6000);
//		} catch (InterruptedException ex) {
//		}
//
//	}
//
//}
