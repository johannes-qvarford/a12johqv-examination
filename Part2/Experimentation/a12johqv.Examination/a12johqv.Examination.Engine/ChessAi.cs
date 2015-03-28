namespace a12johqv.Examination.Ai
{
    using System;
    using System.Collections.Generic;

    using a12johqv.Examination.Chess;

    /// Ai playing a certain color that uses a casebase to decide which chess moves to make.
    /// It uses a match report to document the similarity of the current position and the
    /// position in the chosen case,
    /// and the similarity of the adapted move, and the move in the case.
    public struct ChessAi
    {
        private readonly Casebase casebase;

        private readonly Color color;

        public ChessAi(Casebase casebase, Color color)
        {
            this.casebase = casebase;
            this.color = color;
        }

        public Move DecideMove(
            Position position,
            IReadOnlyList<Move> validMoves,
            Random random,
            ref MatchReport matchReport)
        {
            double problemSimilarity;
            var @case = this.casebase.FindMostSimilarCase(
                currentPosition: position,
                color: this.color,
                random: random,
                similarity: out problemSimilarity);
            
            double moveSimilarity;
            var adaptedMove = MoveAdaption.AdaptToOneOfPossibleMoves(
                currentPosition: position,
                possibleMoves: validMoves,
                @case: @case,
                similarity: out moveSimilarity);

            var newPlayerReport = matchReport.GetPlayerReport(this.color)
                .WithCaseResult(problemSimilarity: problemSimilarity, moveSimilarity: moveSimilarity);
            matchReport = matchReport.WithPlayerReportOfColor(newPlayerReport, this.color);
            matchReport.AddMove(adaptedMove);

            return adaptedMove;
        }
    }
}