namespace a12johqv.Examination.Application
{
    using System;
    using System.Collections.Generic;
    using System.Collections.Immutable;
    using System.Linq;
    using System.Xml.Linq;

    using a12johqv.Examination.Chess;
    using a12johqv.Examination.Engine;

    using ilf.pgn;
    using ilf.pgn.Data;

    using ChessMove = a12johqv.Examination.Chess.Move;

    public class CasebaseLoader
    {
        private readonly IReadOnlyList<Level> levels; 

        public CasebaseLoader()
        {
            this.levels = ImmutableList.CreateRange(GetLevels());
        }

        public Casebase<Position, ChessMove> LoadCasebase(int index)
        {
            if (index < 0 || index > this.levels.Count - 1)
            {
                throw new ArgumentException("index");
            }
            else
            {
                return PgnDatabaseConversionUtility.ConvertToCasebaseForPlayer(
                    pgnDatabase: this.LoadDatabase(index),
                    playerName: this.GetName(index));
            }
        }

        private string GetName(int index)
        {
            return GetLevel(index).Name;
        }

        private Database LoadDatabase(int index)
        {
            var reader = new PgnReader();
            return reader.ReadFromFile(this.GetFilename(index));
        }

        private string GetFilename(int index)
        {
            return this.GetLevel(index).Filename;
        }

        private Level GetLevel(int index)
        {
            return this.levels.Single(level => level.Index == index);
        }

        private static IEnumerable<Level> GetLevels()
        {
            const string ConfigurationFilename = "Resources/Levels.config";
            XElement root = XElement.Load(ConfigurationFilename);
            return root.Elements().Select(Level.FromXmlElement);
        }

        private class Level
        {
            private readonly string name;

            private readonly int index;

            private readonly int ranking;

            private readonly string filename;

            private Level(string name, int index, int ranking, string filename)
            {
                this.name = name;
                this.index = index;
                this.ranking = ranking;
                this.filename = filename;
            }

            public string Name { get { return this.name; } }

            public int Index { get { return this.index; } }

            public int Ranking { get { return this.ranking; } }

            public string Filename { get { return this.filename; } }

            public static Level FromXmlElement(XElement xElement)
            {
                return new Level(
                    name: xElement.Element("name").Value,
                    index: int.Parse(xElement.Element("index").Value),
                    ranking: int.Parse(xElement.Element("ranking").Value),
                    filename: xElement.Element("filename").Value);
            }
        }
    }
}
