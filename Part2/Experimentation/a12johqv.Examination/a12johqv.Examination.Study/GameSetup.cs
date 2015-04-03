namespace a12johqv.Examination.Study
{
    using System;
    using System.Collections.Generic;
    using System.Linq;

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

        public IEnumerable<GameReport> Play(int count, Weights weights, Random random)
        {
            return Enumerable.Range(0, count).Select(_ => this.PlayOnce(weights, random));
        }

        private GameReport PlayOnce(Weights weights, Random random)
        {
            // Cycle between ais on every iteration of the loop.
            ChessAi[] ais =
                {
                    new ChessAi(this.whitePlayerWithCasebase.Item2, weights, Color.White),
                    new ChessAi(this.blackPlayerWithCasebase.Item2, weights, Color.Black)
                };

            // Check how fast each player decides their moves to make sure the AI follows the performance requirements. 
            IDictionary<Color, TimeSpan> timeToDecideFirst40Moves = 
                new Dictionary<Color, TimeSpan>()
                    {
                        { Color.White, TimeSpan.Zero },
                        { Color.Black, TimeSpan.Zero }
                    };

            var aiIt = ais.Cycle().GetEnumerator();

            GameReport gameReport = GameReport.CreateFromWhiteAndBlackPlayerWithWeights(
                whitePlayer: this.whitePlayerWithCasebase.Item1,
                blackPlayer: this.blackPlayerWithCasebase.Item1,
                whiteWeights: weights,
                blackWeights: weights);
            Position position = Position.Initial;

            int moveCount = 0;
            Move[] validMoves;
            Result result;
            while ((result = position.GetResult(out validMoves)) == Result.Undecided)
            {
                aiIt.MoveNext();
                var currentAi = aiIt.Current;

                var startTime = DateTime.Now;
                var move = currentAi.DecideMove(
                    position: position,
                    validMoves: validMoves,
                    random: random,
                    gameReport: ref gameReport);
                var endTime = DateTime.Now;

                if (moveCount < 40 * 2)
                {
                    var decisionDuration = endTime - startTime;
                    timeToDecideFirst40Moves[currentAi.Color] += decisionDuration;
                }

                position = position.ByMove(move);
                moveCount++;
            }

            // Insert decision time in report.
            foreach (var color in new[] { Color.White, Color.Black })
            {
                var newPlayerReport = gameReport.GetPlayerReport(color).WithDecisionTimeOfFirst40Moves(timeToDecideFirst40Moves[color]);
                gameReport = gameReport.WithPlayerReportOfColor(newPlayerReport, color);
            }

            return gameReport.WithResult(result);
        }
    }
}