Arbetet �r uppdelat i tv� mappar, den ena inneh�ller k�llkoden f�r artifakten och pilotstudien
och den andra �r en milj� f�r att utf�ra pilotstudien.
Var medveten om att hela studien kan ta flera timmar att utf�ra.

K�llkoden �r skriven i C# och uppdelad i ett antal C# projekt.
a12johqv.Examination.Core inneh�ller grundl�ggande klasser.
a12johqv.Examination.Chess inneh�ller klasser f�r att spela schack.
a12johqv.Examination.Ai inneh�ller den CBR-baserade schack-AI-agenten.
a12johqv.Examination.Study innheh�ller klasser och pgn filer f�r att beg� studien n�mnd i metoden.
pgn.Data/NET/Parse �r del av ett tredjepartsbibliotek sl�ppt under Apache-licensen.
Det b�r g� att �ppna Solution-filen med Visual Studio 2013 eller senare, kompilera k�llkoden och utf�ra pilotstudien utan problem.

Det finns �ven en mapp f�r att utf�ra pilotstudien direkt med ett redan kompilerat program.
Det enda som l�r kr�vas f�r att utf�ra studien �r .NET 4.5 p� windows, och det funkar p� skoldatorerna.
Det l�r �ven funka med Mono p� linux och mac.

F�r att utf�ra studien snabbare:
* �ppna filen PilotStudy/Resources/Levels.config,
* ta bort/kommentera ut de h�gre rankade spelarna.
Med bara de tv� l�gst rankade spelarna b�r pilotstudien inte ta mer �n n�gra minuter �ven p� en l�ngsam dator.

N�r studien har utf�rts b�r det ha skapats en fil i "PilotStudy/Resources/Generated" med resultatet av studien.
Den b�r ha namnet "GameReports {guid}" och sakna �ndelse, men det �r ett xml dokument som kan �ppnas i en textredigerare.
Pilotstudien samlar i nul�get mer information �n vad som beskrivs i metoden.