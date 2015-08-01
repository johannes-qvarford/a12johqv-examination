Arbetet är uppdelat i fyra mappar, den ena innehåller källkoden för allt, 
den andra innehåller en miljö för att utföra studien, den tredje en miljö för att summera en studie,
och den fjärde en mapp för att visualisera schackpartierna.
En bilaga från rapporten har inkluderats och heter "Bilaga A.pgn".

Om det uppstår problem, kontakta mig på a12johqv@student.his.se.

===== Källkod =====

Källkoden är skriven i C# och uppdelad i ett antal C# projekt som finns i mappen "Source Code".
a12johqv.Examination.Core innehåller grundläggande klasser.
a12johqv.Examination.Chess innehåller klasser för att spela schack.
a12johqv.Examination.Ai innehåller den CBR-baserade schack-AI-agenten.
a12johqv.Examination.Study innehåller klasser och pgn filer för att begå studien nämnd i metoden.
a12johqv.Examination.Summary innehåller koden för att summera resultatet från en studie, som användes i rapporten.
pgn.Data/NET/Parse är del av ett tredjepartsbibliotek släppt under Apache-licensen.
packages innehåller paket hämtade med NuGet. Om paketen fortfarande finns tillgängliga på nuget.org kan de även hämtas därifrån.

Notera att produkten i arbetet bara är AI-agenten, men eftersom AI-agenten inte kan köra av sig själv
kan studien och sammanfattningen vara av användning för att visualisera vad den kan göra.

Det bör gå att öppna Solution-filen med Visual Studio 2013 eller senare, kompilera källkoden och utföra studien utan problem.
Var medveten om att hela studien kan ta flera timmar att utföra på en långsam dator med få kärnor.
För att göra en sammanfattning i Visual Studio behövs lite mer jobb.
Sammanfattningsprogrammet behöver två argument: en sökväg till en resultatfil producerad av studien och en sökväg till en Players.config fil där det står vilka spelare som var med i studien.
I Visual Studio går det att ställa in programargument genom att högerklicka på projectet och gå till Properties > Debug > Start Options > Command line arguments.
Argumenten får inte innehålla mellanrum, eller så måste de omringas av citationstecken, så här enMapp/enFil.pgn eller "en mapp/en fil.pgn".

===== Studie =====

Det finns även en mapp för att utföra studien direkt med ett redan kompilerat program. Det finns i "Study Executable".
I mappen "Summary Executable" går det att utföra en sammanfattning direkt.
Det enda som bör krävas för att köra dessa program är .NET 4.5 på windows, och det funkar på skoldatorerna.
Det bör även funka med Mono på linux och mac.

För att utföra studien snabbare:
* öppna filen "Study Executable/Resources/Players.config",
* ta bort/kommentera ut de högre rankade spelarna.
Med bara de två lägst rankade spelarna bör studien inte ta mer än några minuter även på en långsam dator.

Utför studien genom att köra programmet "Study Executable/a12johqv.Examination.Study.exe".
När studien har utförts bör det ha skapats en fil i "Study Executable/Resources/Generated" med resultatet av studien.
Den bör ha namnet "GameReports {guid}" och ha ändelsen .pgn, och kan öppnas i en textredigerare.

===== Sammanfattning =====

Det går snabbt att göra en sammanfattning av resultatet från undersökningen genom att köra "Summary Executable/Run.bat".
Eftersom programmet vanligtvis behöver argument måste det startas från en kommandotolk för att använda en annan resultatfil och Players.config-fil.
Alternativt kan Run.bat ändras. Kommandotolken måste alltid användas om sammanfattning inte görs på windows, eftersom det bara går att köra .bat filer på windows.
Programmet som ska köras via kommandotolken är "Summary Executable/a12johqv.Examination.Summary.exe".
Programmet körs väldigt snabbt och visar resultatet i ett fönster.
För att stänga fönstret och programmet, tryck på Enter.

===== Visualisering =====

Schackpartierna är ganska svåra att tyda bara genom att läsa resultatfilen.
I mappen "Visual" finns schackprogrammet Arena som kan användas för att visualisera en resultatfil, dvs. visa partierna grafiskt.
Arena kan inte läsa alla partier i resultatfilen, vilket får den att avbryta laddningen av filen utan errormeddelande.
Vissa partier kan den inte läsa, och jag förstår inte varför.
Jag har testat att utföra dem för hand, så jag vet inte varför det inte fungerar.
Av denna anledning har jag inkluderat en resultatfil med bara ett parti (det finns flera som fungerar dock) som Arena kan visa upp.
Det kanske fungerar problemfritt med något annat program, men det har jag inte undersökt.

Öppna Arena genom att gå till mappen "Visual" och kör programmet Arena.exe.
Gå till PGN > Open (Välj "Visual/Games/Exempel.pgn") > Dubbelklicka på första partiet.
Om det inte syns något parti, starta om programmet och försök igen, det brukar fungera.
Använd vänster och höger piltangent för att gå bakåt och framåt i partiet.

Arena är byggd för windows, så om du är på mac eller linux och vill visualisera ett parti måste du hitta ett annat visualiseringspogram.