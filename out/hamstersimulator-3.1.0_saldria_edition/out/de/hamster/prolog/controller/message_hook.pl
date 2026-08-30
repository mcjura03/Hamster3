%% Alle Prolog-Ausgaben werden hiermit abgefangen und an den 
%% 'user_output', den Standard-Outputstream umgeleitet. Dadurch
%% wird es möglich, die gesammte Kommunikation mit dem Prolog-
%% Interpreter über die Ein- und Ausgabestreams zu realisieren.

message_hook(_, Severity, Lines) :-
	(severity_prefix(Severity, Prefix),
	print_message_lines(user_output, Prefix, Lines)).
	
	severity_prefix(silent,			'SILENT: '	).
	severity_prefix(error,			'ERROR: '	).
	severity_prefix(warning,		'WARNING: '	).
	severity_prefix(informational,	'INFO: '	).
	severity_prefix(banner,			'BANNER: '	).
	severity_prefix(help,			'HELP: '	).
	severity_prefix(_,				'OTHER: '	).
	
prolog:help_hook(_) :- !,
	write('Die Online-Hilfe wird in der konsolenbasierten '),
	write('Version des SWIProlog-Interpreters nicht unterstützt.').	