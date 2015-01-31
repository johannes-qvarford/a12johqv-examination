How to run the game:

0) Make sure that both the D2 and MMPM projects are in the classpath: https://sourceforge.net/projects/darmok2/

1) If you just want to play against the built-in AI, run this:

bc.Main -i 50 -m maps/simple-18x18.xml -u myusername -p 0|player1|38|40|37|39|32 -p 1|player2|ai-random

the -u parameter is to specify the username (just type your name there)
the -m parameter is to specify which map you want to play, 
the -p parameter is to specify players:
 	-p 0|playername|kayboardconfiguration specifies a human player
	-p 1|playername|AIname specifies a computer player (valid AI names are: ai-random, ai-random-meek, ai-defense, ai-follower or ME)
	
2) If you want to generate a trace, just add: -t file:mytrace.xml

3) If you want to play against a ME (stored in "myme.zip"), just run:

bc.Main -i 50 -m maps/simple-18x18.xml -u myusername -p 0|player1|38|40|37|39|32 -p 1|player2|ME|d2.core.D2@@@zipfile@@@myme.zip@@@gatech.mmpm.learningengine.ThreadedMEExecutor -t file:trace.xml

4) If you want to MEs to play each other you can run:

bc.Main -i 50 -m maps/simple-18x18.xml -u myusername -p 1|player2|ME|d2.core.D2@@@zipfile@@@myme2.zip@@@gatech.mmpm.learningengine.ThreadedMEExecutor -p 1|player2|ME|d2.core.D2@@@zipfile@@@myme1.zip@@@gatech.mmpm.learningengine.ThreadedMEExecutor -t file:trace.xml


IF you have any issues/questions contact Santiago Ontañón at: santi@iiia.csic.es