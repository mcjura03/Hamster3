package de.hamster.fsm.controller;

/**
 * Gibt den Modus an, in dem das linke fsmMenü sich gerade befindet
 * @author Raffaela Ferrari
 *
 */
public enum FsmMenuMode {
	editMode, deleteMode, createStateMode, createTransitionMode, createCommentMode,
	markStartStateMode, markFinalStateMode
}
