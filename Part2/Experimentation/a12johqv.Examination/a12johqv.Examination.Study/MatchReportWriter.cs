namespace a12johqv.Examination.Study
{
    using System.Collections.Generic;
    using System.IO;
    using System.Linq;
    using System.Text;
    using System.Xml;
    using System.Xml.Linq;

    using a12johqv.Examination.Ai;
    using a12johqv.Examination.Chess;

    using Pgn = ilf.pgn;

    /// Writer of match reports.
    /// The result of every match is written,
    /// as well as both players' index, average problem similarity and average move similarity.
    /// The moves are also used to create a pgn document that gets inbedded with the rest of the data.
    /// 
    /// Match reports are written as XML. 
    /// The top node is named "matchReports", and it has an arbitrary number of child nodes named "matchReport".
    /// Every match report contains a "result" node with the content "Draw", "BlackVictory" or "WhiteVictory".
    /// Every match report also contains a "pgn" node with a pgn encoding of the match as content.
    /// Every match report has a "white" and a "black" node, that contains white's and blacks
    /// respective information, where every attribute of a player is a node with content in "white" or "black".
    public class MatchReportWriter
    {
        private readonly TextWriter writer;

        public MatchReportWriter(TextWriter writer)
        {
            this.writer = writer;
        }

        public void WriteGameReports(IEnumerable<MatchReport> gameReports)
        {
            var root = new XElement("matchReports", gameReports.Select(MatchReportToXElement));
            root.WriteTo(new XmlTextWriter(this.writer){ Formatting = Formatting.Indented });
        }

        private static XElement MatchReportToXElement(MatchReport matchReport)
        {
            return new XElement(
                "matchReport",
                new[] { Color.White, Color.Black }
                    .Select(color => new { Color = color, PlayerReport = matchReport.GetPlayerReport(color) })
                    .Select(pair =>
                        new XElement(
                            pair.Color.ToString().ToLower(),
                            new XElement("playerIndex", pair.PlayerReport.Player.Index),
                            new XElement("averageProblemSimilarity", pair.PlayerReport.AverageProblemSimilarity),
                            new XElement("averageMoveSimilarity", pair.PlayerReport.AverageMoveSimilarity),
                            new XElement("averageMoreThanOneBestMatches", pair.PlayerReport.AverageMoreThanOneBestMatches),
                            new XElement("weights", WeightsToString(pair.PlayerReport.Weights)))),
                new XElement("result", matchReport.Result),
                new XElement("pgn", FormatPgn(GetPgn(matchReport))));
        }

        private static string WeightsToString(Weights weights)
        {
            return string.Format(
                "{0} {1} {2} {3} {4} {5} {6} {7}",
                weights.MoveInverseDistanceWeight,
                weights.MoveSquareContentWeight,
                weights.MoveInverseDistanceSourceWeight,
                weights.MoveInverseDistanceTargetWeight,
                weights.MoveSquareContentSourceWeight,
                weights.MoveSquareContentTargetWeight,
                weights.PositionSquareContentSimilarity,
                weights.PositionSquareWithSquareContentDistance);
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

        private static string GetPgn(MatchReport matchReport)
        {
            var pgnDatabase = MatchReportPgnDatabaseCreation.CreateDatabase(matchReport);
            var fileName = Path.GetRandomFileName();

            using (var stream = File.Open(fileName, FileMode.CreateNew))
            {
                var pgnWriter = new Pgn.PgnWriter(stream);
                pgnWriter.Write(pgnDatabase);
            }

            using (var stream = File.Open(fileName, FileMode.Open))
            {
                var textReader = new StreamReader(stream);
                return textReader.ReadToEnd();
            }
        }
    }
}