namespace a12johqv.Examination.Study
{
    using System;
    using System.Collections.Generic;
    using System.IO;
    using System.Linq;

    using a12johqv.Examination.Ai;

    using ilf.pgn;

    public static class Program
    {
        /// Play games between all players and generate a file with a report of each match.
        /// 
        /// Every player is paired up with every other player (21 pairs).
        /// Every pair of unique players play 3 games each as white and black (6 * 21 games total).
        /// This is done for every configuration (5 * 126 = 630 games total).
        /// The generated file is named in "GameReports {guid}" and placed in the directory "Resources/Generated".
        public static void Main(string[] args)
        {
            const int GamesPerPairOfPlayers = 3;

            var now = DateTime.Now;

            // Get the players to perform the study on.
            var players = PlayersConfiguration.LoadPlayers().ToArray();

            // Load their case bases.
            Tuple<Player, Casebase>[] playersAndCasebases = CasebaseLoading.LoadCasebases(players).ToArray();

            // create the carteesian product of playerCasebases and playerCasebases, without games where the player plays agains itself.
            var playerCasebasePairs = playersAndCasebases
                .SelectMany(firstPlayerCasebase => playersAndCasebases.Select(secondPlayerCasebase => new { firstPlayerCasebase, secondPlayerCasebase }))
                .Where(cbs => !cbs.firstPlayerCasebase.Item1.Equals(cbs.secondPlayerCasebase.Item1));

            // Create a match setup for every pair of players.
            var matchSetups = playerCasebasePairs
                .Select(playerCasebasePair => new GameSetup(
                    playerCasebasePair.firstPlayerCasebase, 
                    playerCasebasePair.secondPlayerCasebase));

            IList<Weights> weightsList = GetWeightsList().ToArray();

            // Play a number of games for each setup, and collect their reports.
            // Every match setup has its own random number generator with a unique seed,
            // to avoid race conditions and make games unique if the same setup is used for more than one match.
            // Games with the same setup but different weights will have the same seed.
            var reports = matchSetups.AsParallel().AsUnordered()
                .SelectMany(matchSetup => weightsList.Select(weights => new { MatchSetup = matchSetup, Weights = weights }))
                .SelectMany(pair => pair.MatchSetup.Play(count: GamesPerPairOfPlayers, weights: pair.Weights));

            // Force evaluation of reports before logging them.
            LogReportedGames(reports.AsSequential().ToArray(), now);
        }

        private static IEnumerable<Weights> GetWeightsList()
        {
            // Use different sets of weights to see if one stands out.
            double[,] bareWeightsList =
                {
                    { 0.5, 0.5, 0.5, },
                    { 0.8, 0.8, 0.5, },
                    { 0.8, 0.2, 0.5, },
                    { 0.2, 0.5, 0.8, },
                    { 0.2, 0.5, 0.2, },
                };

            for (int i = 0; i < bareWeightsList.GetLength(0); i++)
            {
                yield return new Weights(
                    moveWeight: bareWeightsList[i, 0],
                    distanceWeight: bareWeightsList[i, 1],
                    squareContentWeight: bareWeightsList[i, 2]);
            }
        }

        private static void LogReportedGames(IList<GameReport> gameReports, DateTime dateTime)
        {
            var pgnDatabase = GameReportPgnDatabaseCreation.CreateDatabase(gameReports);

            using (Stream stream = GeneratedContentStreaming.OpenStreamForGameReports(dateTime))
            {
                var pgnWriter = new PgnWriter(stream);
                pgnWriter.Write(pgnDatabase);
            }
        }
    }
}