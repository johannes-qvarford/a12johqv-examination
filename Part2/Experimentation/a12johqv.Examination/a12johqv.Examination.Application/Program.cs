namespace a12johqv.Examination.Application
{
    using System;
    using System.IO;
    using System.Linq;

    using ilf.pgn;
    using ilf.pgn.Data;
    
    using File = System.IO.File;

    class Program
    {
        static void Main(string[] args)
        {
            PgnReader reader = new PgnReader();
            Database database = reader.ReadFromFile("ChessMosaic2011");
            // WritePlayers(database);
            WritePlayersSortedByGameCount(database);
        }

        private static void WritePlayers(Database database)
        {
            var players = database.Games.SelectMany(game => new[] { game.WhitePlayer, game.BlackPlayer }).Distinct();

            using (var outStream = OpenGeneratedResourceForWriting("Players"))
            {
                var outWriter = new StreamWriter(outStream);
                foreach (var player in players)
                {
                    outWriter.WriteLine(player);
                }
            }
        }

        private static void WritePlayersSortedByGameCount(Database database)
        {
            var playersGameCount = database.Games
                .SelectMany(game => new[] { game.WhitePlayer, game.BlackPlayer })
                .GroupBy(player => player)
                .Select(group => new { Player = group.Key, Count = group.Count() })
                .OrderBy(pair => -pair.Count);

            using (var outStream = OpenGeneratedResourceForWriting("PlayersGameCount"))
            {
                var outWriter = new StreamWriter(outStream);
                foreach (var player in playersGameCount)
                {
                    outWriter.WriteLine("{0}:{1}", player.Player, player.Count);
                }
            }
        }

        private static FileStream OpenResourceForReading(string filename)
        {
            return File.OpenRead(string.Format("Resources/{0}", filename));
        }

        private static FileStream OpenGeneratedResourceForWriting(string filename)
        {
            return File.OpenWrite(string.Format("Resources/Generated/{0}_{1}", filename, DateTime.Now.Ticks));
        }
    }
}
