namespace a12johqv.Examination.Ai
{
    using System;
    using System.Collections.Generic;

    using a12johqv.Examination.Chess;

    /// Ai that uses a casebase to decide which chess moves to make, using weights and a random number generator
    /// to affect its decisions.
    public struct ChessAi
    {
        private readonly Casebase casebase;

        private readonly Weights weights;

        private readonly Random random;

        public ChessAi(Casebase casebase, Weights weights, Random random)
        {
            this.casebase = casebase;
            this.weights = weights;
            this.random = random;
        }

        public Move DecideMove(
            BarePosition position,
            IReadOnlyList<Move> validMoves,
            Color color)
        {
            var @case = this.casebase.FindMostSimilarCase(
                currentPosition: position,
                color: color,
                random: this.random,
                weights: this.weights);
            
            var adaptedMove = MoveAdaption.AdaptToOneOfPossibleMoves(
                currentPosition: position,
                possibleMoves: validMoves,
                @case: @case,
                weights: this.weights);

            return adaptedMove;
        }
    }
}