namespace a12johqv.Examination.Study
{
    using System;
    using System.Collections.Generic;
    using System.Linq;

    using a12johqv.Examination.Ai;
    using a12johqv.Examination.Chess;
    using a12johqv.Examination.Core;

    /// A setup for a chess match between players.
    /// A match can be played several times with the same match setup.
    public class MatchSetup
    {
        private readonly Tuple<Player, Casebase> whitePlayerWithCasebase;

        private readonly Tuple<Player, Casebase> blackPlayerWithCasebase;

        public MatchSetup(Tuple<Player, Casebase> whitePlayerWithCasebase, Tuple<Player, Casebase> blackPlayerWithCasebase)
        {
            this.whitePlayerWithCasebase = whitePlayerWithCasebase;
            this.blackPlayerWithCasebase = blackPlayerWithCasebase;
        }

        public IEnumerable<MatchReport> Play(int count, Weights weights, Random random)
        {
            return Enumerable.Range(0, count).Select(_ => this.PlayOnce(weights, random));
        }

        private MatchReport PlayOnce(Weights weights, Random random)
        {
            // Cycle between ais on every iteration of the loop.
            ChessAi[] ais =
                {
                    new ChessAi(this.whitePlayerWithCasebase.Item2, weights, Color.White),
                    new ChessAi(this.blackPlayerWithCasebase.Item2, weights, Color.Black)
                };
            var aiIt = ais.Cycle().GetEnumerator();

            MatchReport matchReport = MatchReport.CreateFromWhiteAndBlackPlayerWithWeights(
                whitePlayer: this.whitePlayerWithCasebase.Item1,
                blackPlayer: this.blackPlayerWithCasebase.Item1,
                whiteWeights: weights,
                blackWeights: weights);
            Position position = Position.Initial;

            Move[] validMoves;
            Result result;
            while ((result = position.GetResult(out validMoves)) == Result.Undecided)
            {
                aiIt.MoveNext();
                var currentAi = aiIt.Current;

                var move = currentAi.DecideMove(
                    position: position,
                    validMoves: validMoves,
                    random: random,
                    matchReport: ref matchReport);

                position = position.ByMove(move);
            }

            return matchReport.WithResult(result);
        }
    }
}