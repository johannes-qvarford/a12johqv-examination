namespace a12johqv.Examination.Study
{
    using System;
    using System.Collections.Generic;
    using System.Diagnostics;

    using a12johqv.Examination.Ai;
    using a12johqv.Examination.Chess;
    using a12johqv.Examination.Core;

    /// A setup for a chess game between players.
    /// A game can be played several times with the same game setup.
    public class GameSetup
    {
        private readonly Tuple<Player, Casebase> whitePlayerWithCasebase;

        private readonly Tuple<Player, Casebase> blackPlayerWithCasebase;

        public GameSetup(Tuple<Player, Casebase> whitePlayerWithCasebase, Tuple<Player, Casebase> blackPlayerWithCasebase)
        {
            this.whitePlayerWithCasebase = whitePlayerWithCasebase;
            this.blackPlayerWithCasebase = blackPlayerWithCasebase;
        }

        public IEnumerable<GameReport> Play(int count, Weights weights)
        {
            for (int i = 0; i < count; i++)
            {
                yield return this.PlayOnce(weights, i);
            }
        }

        private GameReport PlayOnce(Weights weights, int round)
        {
            // Cycle between ais on every iteration of the loop.
            ChessAi[] ais =
                {
                    new ChessAi(this.whitePlayerWithCasebase.Item2, weights, new Random(round)),
                    new ChessAi(this.blackPlayerWithCasebase.Item2, weights, new Random(round))
                };

            // Check how fast each player decides their moves to make sure the AI follows the performance requirements. 
            IDictionary<Color, TimeSpan> timeToDecideFirst40Moves = 
                new Dictionary<Color, TimeSpan>()
                    {
                        { Color.White, TimeSpan.Zero },
                        { Color.Black, TimeSpan.Zero }
                    };

            var aiIt = ais.Cycle().GetEnumerator();
            Position position = Position.Initial;
            IList<Move> performedMoves = new List<Move>();

            Move[] validMoves;
            Result result;
            while ((result = position.GetResult(out validMoves)) == Result.Undecided)
            {
                aiIt.MoveNext();
                var currentAi = aiIt.Current;

                var stopWatch = new Stopwatch();
                stopWatch.Start();
                var move = currentAi.DecideMove(
                    position: position.BarePosition,
                    color: position.CurrentColor,
                    validMoves: validMoves);
                stopWatch.Stop();

                if (performedMoves.Count < 40 * 2)
                {
                    timeToDecideFirst40Moves[position.CurrentColor] += stopWatch.Elapsed;
                }

                performedMoves.Add(move);
                position = position.ByMove(move);
            }

            return new GameReport(
                result: result,
                moves: performedMoves,
                whitePlayerName: this.whitePlayerWithCasebase.Item1.Name,
                blackPlayerName: this.blackPlayerWithCasebase.Item1.Name,
                weights: weights,
                round: round,
                didFollowTimeRequirements: timeToDecideFirst40Moves[Color.White] < TimeSpan.FromMinutes(90)
                    && timeToDecideFirst40Moves[Color.Black] < TimeSpan.FromMinutes(90));
        }
    }
}