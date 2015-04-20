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
    public struct GameReport
    {
        private readonly bool didFollowTimeRequirement;

        private readonly Result result;

        private readonly IList<Move> moves;

        public GameReport(
            bool didFollowTimeRequirement,
            Result result,
            IList<Move> moves)
        {
            this.didFollowTimeRequirement = didFollowTimeRequirement;
            this.result = result;
            this.moves = moves;
        }

        public bool DidFollowTimeRequirement { get { return this.didFollowTimeRequirement; } }

        public IEnumerable<Move> Moves { get { return this.moves; } }

        public Result Result { get { return this.result; } }
    }
}