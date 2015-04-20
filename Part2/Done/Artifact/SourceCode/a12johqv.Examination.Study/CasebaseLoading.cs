namespace a12johqv.Examination.Study
{
    using System;
    using System.Collections.Generic;
    using System.Linq;

    using a12johqv.Examination.Ai;

    using ilf.pgn;
    using ilf.pgn.Data;

    /// Utility functions for loading of players' casebases from pgn files.
    public class CasebaseLoading
    {
        public static IEnumerable<Tuple<Player, Casebase>> LoadCasebases(IEnumerable<Player> selectedPlayers)
        {
            return selectedPlayers.AsParallel().AsUnordered()
                .Select(player => Tuple.Create(player, LoadCasebase(player)));
        }

        private static Casebase LoadCasebase(Player player)
        {
            return PgnDatabaseToCasebaseConversion.ConvertToCasebaseForPlayer(
                    pgnDatabase: LoadDatabase(player),
                    playerName: player.Name);
        }

        private static Database LoadDatabase(Player player)
        {
            var reader = new PgnReader();
            return reader.ReadFromFile(player.Filename);
        }
    }
}