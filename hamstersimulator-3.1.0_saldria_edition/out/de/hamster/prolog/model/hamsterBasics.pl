%%% Modellierung der Basisbefehle des Java-Hamsters.
%%%
%%%  Modellierung des Hamsterterritoriums.
%
% territorium(<anzahlReihen>,<anzahlSpalten>)

:- dynamic territorium(_,_).

%%%  Modellierung eines Korns im Hamsterterritorium.
%
%  korn(<reihe>,<spalte>,<anzahl>)

:- dynamic korn(_,_,_).

%%%  Modellierung der Mauern im Hamsterterritorium
%
%  mauer(<reihe>,<spalte>)

:- dynamic mauer(_,_).

%%%  Modellierung des Hamsters selbst.
%
%  hamster(<reihe>,<spalte>,<blickrichtung>,<anzahlKoernerImMaul>)

:- dynamic hamster(_,_,_,_).

%%%
%%% Standard-Aktionen des Hamsters.
%%%

vor :-
	territorium(TReihen,TSpalten),
	hamster(Reihe,Spalte,Blickrichtung,AnzahlKoerner),
	nextPos(Reihe,Spalte,Blickrichtung,ReiheNeu,SpalteNeu),
	ReiheNeu >= 0, ReiheNeu < TReihen,
	SpalteNeu >= 0, SpalteNeu < TSpalten,
	not(mauer(ReiheNeu,SpalteNeu)),
	%% Aktualisiere die Prolog-Datenbank:	
	retract(hamster(Reihe,Spalte,Blickrichtung,AnzahlKoerner)),
	assert(hamster(ReiheNeu,SpalteNeu,Blickrichtung,AnzahlKoerner)),	 
	%% Rufe vor() beim Hamster auf und warte solange dies ausgeführt wird..
	write('prologhamster:vor'),ttyflush,
	read(Return), 
	call(Return),!.
	
vor :-
	%% Rufe vor() beim Hamster auf und warte solange dies ausgeführt wird..
	write('prologhamster:vor'),
	read(Return),false,!.

nextPos(Reihe,Spalte,'NORD',ReiheNeu,Spalte) :-
	ReiheNeu is Reihe - 1, !.
nextPos(Reihe,Spalte,'WEST',Reihe,SpalteNeu) :-
	SpalteNeu is Spalte - 1, !.
nextPos(Reihe,Spalte,'SUED',ReiheNeu,Spalte) :-
	ReiheNeu is Reihe + 1, !.
nextPos(Reihe,Spalte,'OST',Reihe,SpalteNeu) :-
	SpalteNeu is Spalte + 1, !.

vornFrei :-
	hamster(Reihe,Spalte,Blickrichtung,_),
	territorium(TReihen,TSpalten),
	nextPos(Reihe,Spalte,Blickrichtung,ReiheNeu,SpalteNeu),
	ReiheNeu >= 0, ReiheNeu < TReihen,
	SpalteNeu >= 0, SpalteNeu < TSpalten,
	not(mauer(ReiheNeu,SpalteNeu)),
	write('prologhamster:vornFrei'),ttyflush,
	read(Return),call(Return),!.

linksUm :-
	hamster(Reihe,Spalte,Blickrichtung,AnzahlKoerner),
	dreheHamster(Blickrichtung,BlickrichtungNeu),
	% Aktualisiere die Datenbank:
	retract(hamster(Reihe,Spalte,Blickrichtung,AnzahlKoerner)),
	assert(hamster(Reihe,Spalte,BlickrichtungNeu,AnzahlKoerner)),
	% Rufe linksUm() beim Hamster auf.
	write('prologhamster:linksUm'),ttyflush,
	read(Return),call(Return),!.

dreheHamster('NORD','WEST').
dreheHamster('WEST','SUED').
dreheHamster('SUED','OST').
dreheHamster('OST','NORD').

rechtsUm :-
	linksUm,
	linksUm,
	linksUm,!.

nimm :-
	hamster(Reihe,Spalte,Blickrichtung,AnzahlKoernerImMaul),
	korn(Reihe,Spalte,AnzahlKoerner),
	AnzahlKoerner > 0,
	AnzahlKoernerImMaulNeu is AnzahlKoernerImMaul + 1,
	AnzahlKoernerNeu is AnzahlKoerner - 1,
	%% Aktualisiere die Datenbank:
	retract(hamster(Reihe,Spalte,Blickrichtung,AnzahlKoernerImMaul)),
	retract(korn(Reihe,Spalte,AnzahlKoerner)),
	assert(hamster(Reihe,Spalte,Blickrichtung,AnzahlKoernerImMaulNeu)),
	assert(korn(Reihe,Spalte,AnzahlKoernerNeu)),
	%% Rufe nimm() beim Hamster auf.
	write('prologhamster:nimm'),ttyflush,
	read(Return),call(Return),!.

nimm :-
	%% Rufe nimm() beim Hamster auf.
	write('prologhamster:nimm'),ttyflush,
	read(Return),false, !.

gib :-
	hamster(Reihe,Spalte,Blickrichtung,AnzahlKoernerImMaul),	
	korn(Reihe,Spalte,AnzahlKoerner),
	AnzahlKoernerImMaul > 0,
	AnzahlKoernerImMaulNeu is AnzahlKoernerImMaul - 1,
	AnzahlKoernerNeu is AnzahlKoerner + 1,
	%% Aktualisiere die Datenbank:
	retract(hamster(Reihe,Spalte,Blickrichtung,AnzahlKoernerImMaul)),
	retract(korn(Reihe,Spalte,AnzahlKoerner)),
	assert(hamster(Reihe,Spalte,Blickrichtung,AnzahlKoernerImMaulNeu)),
	assert(korn(Reihe,Spalte,AnzahlKoernerNeu)),
	%% Rufe gib() beim Hamster auf.
	write('prologhamster:gib'),ttyflush,
	read(Return),call(Return),!.

%% falls es noch keine koerner auf dieser Kachel gibt..
gib :-
	hamster(Reihe,Spalte,Blickrichtung,AnzahlKoernerImMaul),	
	AnzahlKoernerImMaul > 0,
	AnzahlKoernerImMaulNeu is AnzahlKoernerImMaul - 1,
	AnzahlKoernerNeu is 1,
	%% Aktualisiere die Datenbank:
	retract(hamster(Reihe,Spalte,Blickrichtung,AnzahlKoernerImMaul)),
	assert(hamster(Reihe,Spalte,Blickrichtung,AnzahlKoernerImMaulNeu)),
	assert(korn(Reihe,Spalte,AnzahlKoernerNeu)),
	%% Rufe gib() beim Hamster auf.
	write('prologhamster:gib'),ttyflush,
	read(Return),call(Return),!.

gib :-
	%% Rufe gib() beim Hamster auf.
	write('prologhamster:gib'),ttyflush,
	read(Return),false,!.

kornDa :-
	hamster(Reihe,Spalte,_,_),
	korn(Reihe,Spalte,AnzahlKoerner),
	AnzahlKoerner > 0,
	write('prologhamster:kornDa'),ttyflush,
	read(Return),call(Return),!.

maulLeer :-
	hamster(_,_,_,AnzahlKoernerImMaul),
	AnzahlKoernerImMaul = 0,
	write('prologhamster:maulLeer'),ttyflush,
	read(Return),call(Return),!.