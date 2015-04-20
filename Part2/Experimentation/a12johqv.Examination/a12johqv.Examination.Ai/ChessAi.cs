namespace a12johqv.Examination.Ai
{
    using System;
    using System.Collections.Generic;

    using a12johqv.Examination.Chess;

    /// Ai playing a certain color that uses a casebase to decide which chess moves to make.
    /// It uses a game report to document the similarity of the current barePosition and the
    /// barePosition in the chosen case,
    /// and the similarity of the adapted move, and the move in the case.
    public struct ChessAi
    {
        private readonly Casebase casebase;

        private readonly Weights weights;

        private readonly Color color;

        public ChessAi(Casebase casebase, Weights weights, Color color)
        {
            this.casebase = casebase;
            this.weights = weights;
            this.color = color;
        }

        public Color Color { get { return this.color; } }

        public Move DecideMove(
            BarePosition position,
            IReadOnlyList<Move> validMoves,
            Random random)
        {
            var @case = this.casebase.FindMostSimilarCase(
                currentPosition: position,
                color: this.Color,
                random: random,
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