Arbetet är uppdelat i två mappar, den ena innehåller källkoden för artifakten och pilotstudien
och den andra är en miljö för att utföra pilotstudien.
Var medveten om att hela studien kan ta flera timmar att utföra.

Källkoden är skriven i C# och uppdelad i ett antal C# projekt.
a12johqv.Examination.Core innehåller grundläggande klasser.
a12johqv.Examination.Chess innehåller klasser för att spela schack.
a12johqv.Examination.Ai innehåller den CBR-baserade schack-AI-agenten.
a12johqv.Examination.Study innhehåller klasser och pgn filer för att begå studien nämnd i metoden.
pgn.Data/NET/Parse är del av ett tredjepartsbibliotek släppt under Apache-licensen.
Det bör gå att öppna Solution-filen med Visual Studio 2013 eller senare, kompilera källkoden och utföra pilotstudien utan problem.

Det finns även en mapp för att utföra pilotstudien direkt med ett redan kompilerat program.
Det enda som lär krävas för att utföra studien är .NET 4.5 på windows, och det funkar på skoldatorerna.
Det lär även funka med Mono på linux och mac.

För att utföra studien snabbare:
* öppna filen PilotStudy/Resources/Levels.config,
* ta bort/kommentera ut de högre rankade spelarna.
Med bara de två lägst rankade spelarna bör pilotstudien inte ta mer än några minuter även på en långsam dator.

När studien har utförts bör det ha skapats en fil i "PilotStudy/Resources/Generated" med resultatet av studien.
Den bör ha namnet "GameReports {guid}" och sakna ändelse, men det är ett xml dokument som kan öppnas i en textredigerare.
Pilotstudien samlar i nuläget mer information än vad som beskrivs i metoden.