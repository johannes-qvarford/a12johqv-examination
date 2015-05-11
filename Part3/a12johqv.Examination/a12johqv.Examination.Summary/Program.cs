namespace a12johqv.Examination.Summary
{
    using System;
    using System.Collections;
    using System.Collections.Generic;
    using System.Collections.Immutable;
    using System.Diagnostics;
    using System.Globalization;
    using System.Linq;

    using a12johqv.Examination.Study;

    using ilf.pgn;
    using ilf.pgn.Data;

    class Program
    {
        private static IList<Player> players;

        static void Main(string[] args)
        {
            string databaseFilename = args[0];
            string playersFilename = args[1];
            var database = new PgnReader().ReadFromFile(databaseFilename);
            players = PlayersConfiguration.LoadPlayers(playersFilename).ToList();

            Console.WriteLine("Summary of {0}", databaseFilename);

            foreach (var gamesByConfiguration in database.Games.GroupBy(WeightsOfGame))
            {
                PrintConfiguration(gamesByConfiguration);
            }

            Console.ReadLine();
        }

        private static void PrintConfiguration(IGrouping<string, Game> gamesByConfiguration)
        {
            Console.WriteLine();
            PrintSeperator();
            
            Console.WriteLine(gamesByConfiguration.Key);
            
            Console.WriteLine("Met time requirements: {0}", gamesByConfiguration.All(DidMeetTimeRequirementsOfGame));

            IList<Player> longestOrder = GetLongestOrder(gamesByConfiguration);
            var longestOrderIndices = longestOrder
                .Select(player => 7 - player.Index)
                .Select(i => i.ToString(CultureInfo.InvariantCulture));
            Console.WriteLine("The longest order: {0}", string.Join(" ", longestOrderIndices));
            Console.WriteLine("Matches Elo-ranking order: {0}", players.Zip(longestOrder.Reverse(), Tuple.Create).All(t => t.Item1.Equals(t.Item2)));

            Console.WriteLine("Percentage of non-draw games: {0} %", gamesByConfiguration.Count(game => game.Result != GameResult.Draw));
        }

        private static IList<Player> GetLongestOrder(IEnumerable<Game> gamesByConfiguration)
        {
            // Extract the most important things from each game.
            var games = gamesByConfiguration.Select(
                game => 
                    new {
                    First = PlayerByName(game.WhitePlayer),
                    Second = PlayerByName(game.BlackPlayer),
                    Result = game.Result == GameResult.White ? 1 : game.Result == GameResult.Black ? -1 : 0
                });

            // Make sure that every game has has the player with lowest index first, and the result for the lowest index.
            var orderedByIndex = games.Select(
                game =>
                    {
                        bool wrongOrder = game.First.Index > game.Second.Index;
                        return new { First = wrongOrder ? game.Second : game.First, Second = wrongOrder ? game.First : game.Second, Result = wrongOrder ? -game.Result : game.Result };
                    });

            // Group games by pairs of players.
            Debug.Assert(players.All(player => player.Index < 10));
            var groupedBySamePair = orderedByIndex.GroupBy(game => game.First.Index + (game.Second.Index * 10));

            // Fold every group of games to a final result for every pair.
            var pairsWithTotalPoints = groupedBySamePair.Select(pair => pair.Aggregate((acc, e) => new { First = e.First, Second = e.Second, Result = e.Result + acc.Result }));
            
            // Create orderings, where every ordering says that the first player is better than the second player.
            // Ignore pairs who were equal, and flip the order of the players if the first player (with lowest index) lost.
            var orderings = pairsWithTotalPoints
                .Where(pair => pair.Result != 0)
                .Select(pair => pair.Result > 0 ? Tuple.Create(pair.First, pair.Second) : Tuple.Create(pair.Second, pair.First)).ToArray();

            // Find the longest order given orderings of players.
            return LongestOrderForOrderings(orderings);
        }

        private static IList<Player> LongestOrderForOrderings(Tuple<Player, Player>[] orderings)
        {
            // Longest order from every starting point.
            return orderings.Select(ordering => LongestOrderForOrdering(ordering, orderings)).OrderByDescending(order => order.Count()).First();
        }

        private static IList<Player> LongestOrderForOrdering(Tuple<Player, Player> ordering, Tuple<Player, Player>[] orderings)
        {
            // Longest order for a starting point, the two players in the ordering are the first to be put in the order.
            return LongestOrderForOrderingHelper(orderings, ImmutableList.Create(ordering.Item1).Add(ordering.Item2));
        }

        private static IList<Player> LongestOrderForOrderingHelper(Tuple<Player, Player>[] orderings, ImmutableList<Player> order)
        {
            var lastPlayer = order.Last();

            // Check for orderings from the current last player in the order, that isn't already in the order (avoiding infinite loop).
            var deeperOrderings = orderings.Where(o => o.Item1.Equals(lastPlayer) && !order.Contains(o.Item2));

            // Get all deeper orders assuming the current order as a base.
            var deeperOrders = deeperOrderings.Select(o => LongestOrderForOrderingHelper(orderings, order.Add(o.Item2))).ToArray();

            // If there are any deeper orders, take the one with the greatest length, otherwise use the current order.
            return deeperOrders.Any() ? deeperOrders.OrderByDescending(or => or.Count).First() : order;
        }

        private static Player PlayerByName(string playerName)
        {
            return players.First(player => player.Name == playerName);
        }

        private static bool DidMeetTimeRequirementsOfGame(Game game)
        {
            return GetInfoValue(game, "DidFollowTimeRequirements") == "True";
        }

        private static string WeightsOfGame(Game game)
        {
            return GetInfoValue(game, "Weights");
        }

        private static string GetInfoValue(Game game, string name)
        {
            return game.AdditionalInfo.First(info => info.Name == name).Value;
        }

        private static void PrintSeperator()
        {
            Console.WriteLine("====================");
        }
    }
}
