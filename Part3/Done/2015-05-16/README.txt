Arbetet �r uppdelat i fyra mappar, den ena inneh�ller k�llkoden f�r allt, 
den andra inneh�ller en milj� f�r att utf�ra studien, den tredje en milj� f�r att summera en studie,
och den fj�rde en mapp som inneh�ller ett program f�r att visualisera schackpartierna.
En bilaga fr�n rapporten har inkluderats och heter "Bilaga A.pgn".

Om det uppst�r problem, kontakta mig via a12johqv@student.his.se.

===== K�llkod =====

K�llkoden �r skriven i C# och uppdelad i ett antal C# projekt som finns i mappen "Source Code".
a12johqv.Examination.Core inneh�ller grundl�ggande klasser.
a12johqv.Examination.Chess inneh�ller klasser f�r att spela schack.
a12johqv.Examination.Ai inneh�ller den CBR-baserade schack-AI-agenten.
a12johqv.Examination.Study inneh�ller klasser och pgn filer f�r att beg� studien n�mnd i metoden.
a12johqv.Examination.Summary inneh�ller koden f�r att summera resultatet fr�n en studie, som anv�ndes i rapporten.
pgn.Data/NET/Parse �r del av ett tredjepartsbibliotek sl�ppt under Apache-licensen som har modiferats f�r att formatera partier p� ett snyggare s�tt.
packages inneh�ller paket h�mtade med NuGet. Om paketen fortfarande finns tillg�ngliga p� nuget.org kan de �ven h�mtas d�rifr�n.

Notera att produkten i arbetet bara �r AI-agenten, men eftersom AI-agenten inte kan k�ras av sig sj�lv
eftersom det �r ett funktionsbibliotek s� kan studien och sammanfattningen vara av anv�ndning f�r att se vad den kan g�ra.

Det b�r g� att �ppna Solution-filen med Visual Studio 2013 eller senare, kompilera k�llkoden och utf�ra studien utan problem.
Var medveten om att hela studien kan ta flera timmar att utf�ra p� en l�ngsam dator med f� k�rnor.
F�r att g�ra en sammanfattning i Visual Studio beh�vs lite mer jobb.
Sammanfattningsprogrammet beh�ver tv� argument: en s�kv�g till en resultatfil producerad av studien och en s�kv�g till en Players.config fil d�r det st�r vilka spelare som var med i studien.
I Visual Studio g�r det att st�lla in programargument genom att h�gerklicka p� projektet och g� till Properties > Debug > Start Options > Command line arguments.
Argumenten f�r inte inneh�lla mellanrum, eller s� m�ste de omringas av citationstecken, s� h�r enMapp/enFil.pgn eller "en mapp/en fil.pgn".

===== Studie =====

Det finns �ven en mapp f�r att utf�ra studien direkt med ett redan kompilerat program. Det finns i "Study Executable".
I mappen "Summary Executable" g�r det att utf�ra en sammanfattning direkt.
Det enda som b�r kr�vas f�r att k�ra dessa program �r .NET 4.5 p� windows. 
�tminstone s� funkar det p� skoldatorerna.
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
Programmet k�rs v�ldigt snabbt och visar resultatet i ett kommandotolkf�nster/kommandotolkf�nstret.
F�r att st�nga programmet och/eller f�nstret, tryck p� Enter.

===== ChessPad 2 =====

Schackpartierna �r ganska sv�ra att tyda bara genom att l�sa resultatfilen.
I mappen "Chess Pad 2" finns schackprogrammet Chess Pad 2 som kan anv�ndas f�r att visualisera en resultatfil, dvs. visa partierna grafiskt.
Chess Pad 2 finns �ven tillg�ngligt att ladda ner gratis p� http://www.wmlsoftware.com/chesspad.html.
Chess Pad 2 b�r kunna l�sa alla partier i en resultatfil, pr�va t.ex. att �ppna "Bilaga A.pgn" (via Database > Open), v�lj ett parti och se hur det utspelar sig.
Anv�nd v�nster och h�ger piltangent f�r att g� till f�reg�ende eller n�sta drag.
Eftersom Chess Pad 2 bara finns tillg�ngligt till windows, s� m�ste ett annat visualiseringsprogram anv�ndas p� linux och mac.
Det �r m�jligt att andra program inte kan l�sa resultatfilen p� grund av n�gon ovanlig pgn-formatregel som pgn.NET eller visualiseringsprogrammet inte implementerar korrekt. 
Arena fungerar t.ex. inte och ger inget errormeddelande.
