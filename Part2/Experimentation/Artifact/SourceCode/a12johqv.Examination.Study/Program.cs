﻿namespace a12johqv.Examination.Study
{
    using System;
    using System.Collections.Generic;
    using System.IO;
    using System.Linq;

    using a12johqv.Examination.Ai;

    public static class Program
    {
        /// Play games between all players and generate a file with a report of each match.
        /// 
        /// Every player is paired up with every other player including themselves.
        /// Every pair of unique players play 5 games each as white and black (10 games total).
        /// Players play 5 games agains themselves.
        /// The generated file is named in "GameReports {guid}" and placed in the directory "Resources/Generated".
        public static void Main(string[] args)
        {
            const int Seed = 1000;
            const int GamesPerPairOfPlayers = 2;

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
            var reports = matchSetups.AsParallel().AsUnordered()
                .SelectMany(matchSetup => weightsList.Select(weights =>
                    new { MatchSetup = matchSetup, Weights = weights, Random = new Random(Seed + matchSetup.GetHashCode()) }))
                .SelectMany(triple => triple.MatchSetup.Play(count: GamesPerPairOfPlayers, weights: triple.Weights, random: triple.Random));
            
            // Force evaluation of reports before logging them.
            LogReportedGames(reports.AsSequential().ToArray(), now);
        }

        private static IEnumerable<Weights> GetWeightsList()
        {
            double[,] bareWeightsList =
                {
                    { 0.5, 0.5, 0.5, 0.5, 0.5, 0.5 },
                    { 0.9, 0.1, 0.5, 0.5, 0.5, 0.5 },
                    { 0.1, 0.9, 0.5, 0.5, 0.5, 0.5 },
                    { 0.5, 0.5, 0.9, 0.1, 0.5, 0.5 },
                    { 0.5, 0.5, 0.1, 0.9, 0.5, 0.5 },
                    { 0.5, 0.5, 0.5, 0.5, 0.9, 0.1 },
                    { 0.5, 0.5, 0.5, 0.5, 0.1, 0.9 }
                };

            for (int i = 0; i < bareWeightsList.GetLength(0); i++)
            {
                yield return new Weights(
                    moveInverseDistanceWeight: bareWeightsList[i, 0],
                    moveSquareContentWeight: bareWeightsList[i, 1],
                    moveInverseDistanceSourceWeight: bareWeightsList[i, 2],
                    moveInverseDistanceTargetWeight: bareWeightsList[i, 3],
                    moveSquareContentSourceWeight: bareWeightsList[i, 4],
                    moveSquareContentTargetWeight: bareWeightsList[i, 5]);
            }
        }

        private static void LogReportedGames(IEnumerable<GameReport> gameReports, DateTime dateTime)
        {
            using (Stream stream = GeneratedContentStreaming.OpenStreamForGameReports(dateTime))
            {
                var streamWriter = new StreamWriter(stream);
                var gameReportWriter = new GameReportWriter(streamWriter);
                gameReportWriter.WriteGameReports(gameReports);
                streamWriter.Flush();
            }
        }
    }
}