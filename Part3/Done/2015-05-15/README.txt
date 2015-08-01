Arbetet �r uppdelat i fyra mappar, den ena inneh�ller k�llkoden f�r allt, 
den andra inneh�ller en milj� f�r att utf�ra studien, den tredje en milj� f�r att summera en studie,
och den fj�rde en mapp f�r att visualisera schackpartierna.
En bilaga fr�n rapporten har inkluderats och heter "Bilaga A.pgn".

Om det uppst�r problem, kontakta mig p� a12johqv@student.his.se.

===== K�llkod =====

K�llkoden �r skriven i C# och uppdelad i ett antal C# projekt som finns i mappen "Source Code".
a12johqv.Examination.Core inneh�ller grundl�ggande klasser.
a12johqv.Examination.Chess inneh�ller klasser f�r att spela schack.
a12johqv.Examination.Ai inneh�ller den CBR-baserade schack-AI-agenten.
a12johqv.Examination.Study inneh�ller klasser och pgn filer f�r att beg� studien n�mnd i metoden.
a12johqv.Examination.Summary inneh�ller koden f�r att summera resultatet fr�n en studie, som anv�ndes i rapporten.
pgn.Data/NET/Parse �r del av ett tredjepartsbibliotek sl�ppt under Apache-licensen.
packages inneh�ller paket h�mtade med NuGet. Om paketen fortfarande finns tillg�ngliga p� nuget.org kan de �ven h�mtas d�rifr�n.

Notera att produkten i arbetet bara �r AI-agenten, men eftersom AI-agenten inte kan k�ra av sig sj�lv
kan studien och sammanfattningen vara av anv�ndning f�r att visualisera vad den kan g�ra.

Det b�r g� att �ppna Solution-filen med Visual Studio 2013 eller senare, kompilera k�llkoden och utf�ra studien utan problem.
Var medveten om att hela studien kan ta flera timmar att utf�ra p� en l�ngsam dator med f� k�rnor.
F�r att g�ra en sammanfattning i Visual Studio beh�vs lite mer jobb.
Sammanfattningsprogrammet beh�ver tv� argument: en s�kv�g till en resultatfil producerad av studien och en s�kv�g till en Players.config fil d�r det st�r vilka spelare som var med i studien.
I Visual Studio g�r det att st�lla in programargument genom att h�gerklicka p� projectet och g� till Properties > Debug > Start Options > Command line arguments.
Argumenten f�r inte inneh�lla mellanrum, eller s� m�ste de omringas av citationstecken, s� h�r enMapp/enFil.pgn eller "en mapp/en fil.pgn".

===== Studie =====

Det finns �ven en mapp f�r att utf�ra studien direkt med ett redan kompilerat program. Det finns i "Study Executable".
I mappen "Summary Executable" g�r det att utf�ra en sammanfattning direkt.
Det enda som b�r kr�vas f�r att k�ra dessa program �r .NET 4.5 p� windows, och det funkar p� skoldatorerna.
Det b�r �ven funka med Mono p� linux och mac.

F�r att utf�ra studien snabbare:
* �ppna filen "Study Executable/Resources/Players.config",
* ta bort/kommentera ut de h�gre rankade spelarna.
Med bara de tv� l�gst rankade spelarna b�r studien inte ta mer �n n�gra minuter �ven p� en l�ngsam dator.

Utf�r studien genom att k�ra programmet "Study Executable/a12johqv.Examination.Study.exe".
N�r studien har utf�rts b�r det ha skapats en fil i "Study Executable/Resources/Generated" med resultatet av studien.
Den b�r ha namnet "GameReports {guid}" och ha �ndelsen .pgn, och kan �ppnas i en textredigerare.

===== Sammanfattning =====

Det g�r snabbt att g�ra en sammanfattning av resultatet fr�n unders�kningen genom att k�ra "Summary Executable/Run.bat".
Eftersom programmet vanligtvis beh�ver argument m�ste det startas fr�n en kommandotolk f�r att anv�nda en annan resultatfil och Players.config-fil.
Alternativt kan Run.bat �ndras. Kommandotolken m�ste alltid anv�ndas om sammanfattning inte g�rs p� windows, eftersom det bara g�r att k�ra .bat filer p� windows.
Programmet som ska k�ras via kommandotolken �r "Summary Executable/a12johqv.Examination.Summary.exe".
Programmet k�rs v�ldigt snabbt och visar resultatet i ett f�nster.
F�r att st�nga f�nstret och programmet, tryck p� Enter.

===== Visualisering =====

Schackpartierna �r ganska sv�ra att tyda bara genom att l�sa resultatfilen.
I mappen "Visual" finns schackprogrammet Arena som kan anv�ndas f�r att visualisera en resultatfil, dvs. visa partierna grafiskt.
Arena kan inte l�sa alla partier i resultatfilen, vilket f�r den att avbryta laddningen av filen utan errormeddelande.
Vissa partier kan den inte l�sa, och jag f�rst�r inte varf�r.
Jag har testat att utf�ra dem f�r hand, s� jag vet inte varf�r det inte fungerar.
Av denna anledning har jag inkluderat en resultatfil med bara ett parti (det finns flera som fungerar dock) som Arena kan visa upp.
Det kanske fungerar problemfritt med n�got annat program, men det har jag inte unders�kt.

�ppna Arena genom att g� till mappen "Visual" och k�r programmet Arena.exe.
G� till PGN > Open (V�lj "Visual/Games/Exempel.pgn") > Dubbelklicka p� f�rsta partiet.
Om det inte syns n�got parti, starta om programmet och f�rs�k igen, det brukar fungera.
Anv�nd v�nster och h�ger piltangent f�r att g� bak�t och fram�t i partiet.

Arena �r byggd f�r windows, s� om du �r p� mac eller linux och vill visualisera ett parti m�ste du hitta ett annat visualiseringspogram.