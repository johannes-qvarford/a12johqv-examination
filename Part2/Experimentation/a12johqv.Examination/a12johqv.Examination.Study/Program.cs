namespace a12johqv.Examination.Study
{
    using System;
    using System.Collections.Generic;
    using System.IO;
    using System.Linq;

    using a12johqv.Examination.Ai;

    public static class Program
    {
        /// Play matches between all players and generate a file with a report of each match.
        /// 
        /// Every player is paired up with every other player including themselves.
        /// Every pair of unique players play 5 matches each as white and black (10 matches total).
        /// Players play 5 matches agains themselves.
        /// The generated file is named in "GameReports {guid}" and placed in the directory "Resources/Generated".
        public static void Main(string[] args)
        {
            const int Seed = 1000;
            const int MatchesPerPairOfPlayers = 2;

            var now = DateTime.Now;

            // Get the players to perform the study on.
            var players = PlayersConfiguration.LoadPlayers().Where(player => player.Index <= 2).ToArray();

            // Load their case bases.
            Tuple<Player, Casebase>[] playersAndCasebases = CasebaseLoading.LoadCasebases(players).ToArray();

            // create the carteesian product of playerCasebases and playerCasebases.
            var playerCasebasePairs = playersAndCasebases
                .SelectMany(firstPlayerCasebase => playersAndCasebases.Select(secondPlayerCasebase => new { firstPlayerCasebase, secondPlayerCasebase }))
                .Distinct();

            // Create a match setup for every pair of players.
            var matchSetups = playerCasebasePairs
                .Select(playerCasebasePair => new MatchSetup(
                    playerCasebasePair.firstPlayerCasebase, 
                    playerCasebasePair.secondPlayerCasebase));

            double[,] bareWeightsList =
                {
                    { 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5 },
                    { 0.1, 0.9, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5 },
                    { 0.9, 0.1, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5 },
                    { 0.5, 0.5, 0.9, 0.1, 0.5, 0.5, 0.5, 0.5 },
                    { 0.5, 0.5, 0.1, 0.9, 0.5, 0.5, 0.5, 0.5 },
                    { 0.5, 0.5, 0.5, 0.5, 0.9, 0.2, 0.5, 0.5 },
                    { 0.5, 0.5, 0.5, 0.5, 0.2, 0.1, 0.5, 0.5 },
                    { 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.9, 0.1 },
                    { 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.1, 0.9 }
                };
            IList<Weights> weightsList = ConvertToWeightsList(bareWeightsList).ToArray();

            // Play a number of matches for each setup, and collect their reports.
            // Every match setup has its own random number generator with a unique seed,
            // to avoid race conditions and make matches unique if the same setup is used for more than one match.
            var reports = matchSetups.AsParallel().AsUnordered()
                .SelectMany(matchSetup => weightsList.Select(weights =>
                    new { MatchSetup = matchSetup, Weights = weights, Random = new Random(Seed + matchSetup.GetHashCode()) }))
                .SelectMany(triple => triple.MatchSetup.Play(count: MatchesPerPairOfPlayers, weights: triple.Weights, random: triple.Random));
            
            // Force evaluation of reports before logging them.
            LogReportedGames(reports.AsSequential().ToArray(), now);
        }

        private static IEnumerable<Weights> ConvertToWeightsList(double[,] bareWeightsList)
        {
            for (int i = 0; i < bareWeightsList.GetLength(0); i++)
            {
                yield return new Weights(
                    moveInverseDistanceWeight: bareWeightsList[i, 0],
                    moveSquareContentWeight: bareWeightsList[i, 1],
                    moveInverseDistanceSourceWeight: bareWeightsList[i, 2],
                    moveInverseDistanceTargetWeight: bareWeightsList[i, 3],
                    moveSquareContentSourceWeight: bareWeightsList[i, 4],
                    moveSquareContentTargetWeight: bareWeightsList[i, 5],
                    positionSquareContentSimilarity: bareWeightsList[i, 6],
                    positionSquareWithSquareContentDistance: bareWeightsList[i, 7]);
            }
        }

        private static void LogReportedGames(IEnumerable<MatchReport> gameReports, DateTime dateTime)
        {
            using (Stream stream = GeneratedContentStreaming.OpenStreamForGameReports(dateTime))
            {
                var streamWriter = new StreamWriter(stream);
                var gameReportWriter = new MatchReportWriter(streamWriter);
                gameReportWriter.WriteGameReports(gameReports);
                streamWriter.Flush();
            }
        }
    }
}