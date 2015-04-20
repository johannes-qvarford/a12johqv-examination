namespace a12johqv.Examination.Study
{
    using System.Collections.Generic;
    using System.Globalization;
    using System.IO;
    using System.Linq;
    using System.Text;
    using System.Xml;
    using System.Xml.Linq;

    using a12johqv.Examination.Ai;

    using Pgn = ilf.pgn;

    /// Writer of game reports.
    /// The result of every game is written,
    /// as well as both players' index, average problem similarity and average move similarity.
    /// The moves are also used to create a pgn document that gets inbedded with the rest of the data.
    /// 
    /// Game reports are written as XML. 
    /// The top node is named "gameReports", and it has an arbitrary number of child nodes named "GameReport".
    /// Every game report contains a "result" node with the content "Draw", "BlackVictory" or "WhiteVictory".
    /// Every game report also contains a "pgn" node with a pgn encoding of the game as content.
    /// Every game report has a "white" and a "black" node, that contains white's and blacks
    /// respective information, where every attribute of a player is a node with content in "white" or "black".
    public struct StudyGameReportXmlWriter
    {
        private readonly TextWriter writer;

        public StudyGameReportXmlWriter(TextWriter writer)
        {
            this.writer = writer;
        }

        public void WriteStudyGameReports(IEnumerable<StudyGameReport> gameReports)
        {
            var root = new XElement("gameReports", gameReports.Select(StudyGameReportToXElement));
            root.WriteTo(new XmlTextWriter(this.writer) { Formatting = Formatting.Indented });
        }

        private static XElement StudyGameReportToXElement(StudyGameReport studyGameReport)
        {
            return new XElement(
                "studyGameReport",
                new XElement(
                    "didFollowTimeRequirement", studyGameReport.DidFollowTimeRequirement),
                new XElement("weights", WeightsToString(studyGameReport.Weights)),
                new XElement("pgn", FormatPgn(GetPgn(studyGameReport))));
        }

        private static string WeightsToString(Weights weights)
        {
            return string.Format(
                CultureInfo.InvariantCulture,
                "{0} {1} {2}",
                weights.MoveWeight,
                weights.DistanceWeight,
                weights.SquareContentWeight);
        }

        private static string FormatPgn(string pgn)
        {
            const int IndentCharacterCount = 8;
            const int MaxSpacesPerLine = 10;

            string brokenLines = BreakLinesWithSpaces(pgn, maxSpacesPerLine: MaxSpacesPerLine);
            return Indent(brokenLines, characterCount: IndentCharacterCount);
        }

        private static string BreakLinesWithSpaces(string str, int maxSpacesPerLine)
        {
            var sb = new StringBuilder();
            int spacesOnLine = 0;
            for (int i = 0; i < str.Length; i++)
            {
                char characterInString = str[i];
                char characterToWrite = 
                    spacesOnLine == (maxSpacesPerLine - 1)
                    && characterInString == ' ' ? '\n' : characterInString;
                
                sb.Append(characterToWrite);
                spacesOnLine = characterToWrite == ' ' ? spacesOnLine + 1 :
                    characterToWrite == '\n' ? 0 : spacesOnLine;
            }

            return sb.ToString();
        }

        private static string Indent(string str, int characterCount)
        {
            return str.Replace("\n", "\n" + new string(c: ' ', count: characterCount));
        }

        private static string GetPgn(StudyGameReport studyGameReport)
        {
            // Pgn writer closes a stream after wring to it.
            // To avoid closing our filestream,
            // create a temporary file, write the pgn to it,
            // Read it back as a string, and delete the file.
            var pgnDatabase = GameReportPgnDatabaseCreation.CreateDatabase(studyGameReport.GameReport);
            var fileName = Path.GetRandomFileName();

            using (var stream = File.Open(fileName, FileMode.CreateNew))
            {
                var pgnWriter = new Pgn.PgnWriter(stream);
                pgnWriter.Write(pgnDatabase);
            }

            string text;
            using (var stream = File.Open(fileName, FileMode.Open))
            {
                var textReader = new StreamReader(stream);
                text = textReader.ReadToEnd();
            }

            File.Delete(fileName);
            return text;
        }
    }
}