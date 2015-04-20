namespace a12johqv.Examination.Ai
{
    using System.Collections.Generic;

    using a12johqv.Examination.Chess;

    /// A report of a game.
    /// It contains information about the game, such as the result of the game,
    /// and the moves performed during the game.
    /// It also contains color specific reports.
    /// 
    /// Note: this class is immutable EXCEPT for the move list, which is mutable for performance reasons.
    /// Instances of game report should therefor be used with care,
    /// and not be used across threads without external syncronization.
    public class GameReport
    {
        private readonly PlayerReport whitePlayerReport;

        private readonly PlayerReport blackPlayerReport;

        private readonly Result result;

        private readonly IList<Move> moves;

        private GameReport(
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

        public GameReport WithPlayerReportOfColor(PlayerReport playerReport, Color color)
        {
            if (color == Color.White)
            {
                return new GameReport(playerReport, this.blackPlayerReport, this.result, this.moves);
            }
            else
            {
                return new GameReport(this.whitePlayerReport, playerReport, this.result, this.moves);
            }
        }

        public GameReport WithResult(Result result)
        {
            return new GameReport(this.whitePlayerReport, this.blackPlayerReport, result, this.moves);
        }

        public PlayerReport GetPlayerReport(Color color)
        {
            return color == Color.White ? this.whitePlayerReport : this.blackPlayerReport;
        }

        public static GameReport CreateFromWhiteAndBlackPlayerWithWeights(Player whitePlayer, Player blackPlayer, Weights whiteWeights, Weights blackWeights)
        {
            return new GameReport(
                whitePlayerReport: PlayerReport.FromPlayerAndWeights(whitePlayer, whiteWeights),
                blackPlayerReport: PlayerReport.FromPlayerAndWeights(blackPlayer, blackWeights),
                result: Chess.Result.Undecided,
                moves: new List<Move>());
        }
    }
}