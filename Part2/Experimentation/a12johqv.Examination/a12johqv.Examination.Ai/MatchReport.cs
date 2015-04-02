namespace a12johqv.Examination.Ai
{
    using System.Collections.Generic;

    using a12johqv.Examination.Chess;

    /// A report of a match.
    /// It contains information about the match, such as the result of the match,
    /// and the moves performed during the match.
    /// It also contains color specific reports.
    /// 
    /// Note: this class is immutable EXCEPT for the move list, which is mutable for performance reasons.
    /// Instances of match report should therefor be used with care,
    /// and not be used across threads without external syncronization.
    public class MatchReport
    {
        private readonly PlayerReport whitePlayerReport;

        private readonly PlayerReport blackPlayerReport;

        private readonly Result result;

        private readonly IList<Move> moves;

        private MatchReport(
            PlayerReport whitePlayerReport,
            PlayerReport blackPlayerReport,
            Result result,
            IList<Move> moves)
        {
            this.whitePlayerReport = whitePlayerReport;
            this.blackPlayerReport = blackPlayerReport;
            this.result = result;
            this.moves = moves;
        }

        public IEnumerable<Move> Moves { get { return this.moves; } } 

        public Result Result { get { return this.result; } }

        public void AddMove(Move move)
        {
            this.moves.Add(move);
        }

        public MatchReport WithPlayerReportOfColor(PlayerReport playerReport, Color color)
        {
            if (color == Color.White)
            {
                return new MatchReport(playerReport, this.blackPlayerReport, this.result, this.moves);
            }
            else
            {
                return new MatchReport(this.whitePlayerReport, playerReport, this.result, this.moves);
            }
        }

        public MatchReport WithResult(Result result)
        {
            return new MatchReport(this.whitePlayerReport, this.blackPlayerReport, result, this.moves);
        }

        public PlayerReport GetPlayerReport(Color color)
        {
            return color == Color.White ? this.whitePlayerReport : this.blackPlayerReport;
        }

        public static MatchReport CreateFromWhiteAndBlackPlayerWithWeights(Player whitePlayer, Player blackPlayer, Weights whiteWeights, Weights blackWeights)
        {
            return new MatchReport(
                whitePlayerReport: PlayerReport.FromPlayerAndWeights(whitePlayer, whiteWeights),
                blackPlayerReport: PlayerReport.FromPlayerAndWeights(blackPlayer, blackWeights),
                result: Chess.Result.Undecided,
                moves: new List<Move>());
        }
    }
}