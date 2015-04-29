namespace a12johqv.Examination.Study
{
    using System.Collections.Generic;

    using a12johqv.Examination.Ai;
    using a12johqv.Examination.Chess;

    /// A report of a game.
    /// It contains information about the game, such as the result of the game,
    /// and the moves performed during the game.
    /// It also contains color specific reports.
    /// 
    /// Note: this class is immutable EXCEPT for the move list, which is mutable for performance reasons.
    /// Instances of game report should therefor be used with care,
    /// and not be used across threads without external syncronization.
    public struct GameReport
    {
        private readonly Result result;

        private readonly IList<Move> moves;

        private readonly string whitePlayerName;

        private readonly string blackPlayerName;

        private readonly Weights weights;

        private readonly bool didFollowTimeRequirements;

        public GameReport(
            Result result,
            IList<Move> moves,
            string whitePlayerName,
            string blackPlayerName,
            Weights weights,
            bool didFollowTimeRequirements)
        {
            this.result = result;
            this.moves = moves;
            this.whitePlayerName = whitePlayerName;
            this.blackPlayerName = blackPlayerName;
            this.weights = weights;
            this.didFollowTimeRequirements = didFollowTimeRequirements;
        }

        public IEnumerable<Move> Moves { get { return this.moves; } }

        public Result Result { get { return this.result; } }

        public string WhitePlayerName { get { return this.whitePlayerName; } }

        public string BlackPlayerName { get { return this.blackPlayerName; } }

        public Weights Weights { get { return this.weights; } }

        public bool DidFollowTimeRequirements { get { return this.didFollowTimeRequirements; } }
    }
}