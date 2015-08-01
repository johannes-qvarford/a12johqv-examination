namespace a12johqv.Examination.Study
{
    using System.Collections.Generic;

    using a12johqv.Examination.Ai;
    using a12johqv.Examination.Chess;

    /// A report of a game.
    /// It contains information about the game, such as the result of the game,
    /// and the moves performed during the game.
    /// It also contains the players names, the weights used and if the game followed the time requirements.
    public struct GameReport
    {
        private readonly Result result;

        private readonly IList<Move> moves;

        private readonly string whitePlayerName;

        private readonly string blackPlayerName;

        private readonly int whitePlayerRating;

        private readonly int blackPlayerRating;

        private readonly Weights weights;

        private readonly int round;

        private readonly bool didFollowTimeRequirements;

        public GameReport(
            Result result,
            IList<Move> moves,
            string whitePlayerName,
            string blackPlayerName,
            Weights weights,
            int round,
            bool didFollowTimeRequirements,
            int whitePlayerRating,
            int blackPlayerRating)
        {
            this.result = result;
            this.moves = moves;
            this.whitePlayerName = whitePlayerName;
            this.blackPlayerName = blackPlayerName;
            this.weights = weights;
            this.round = round;
            this.didFollowTimeRequirements = didFollowTimeRequirements;
            this.whitePlayerRating = whitePlayerRating;
            this.blackPlayerRating = blackPlayerRating;
        }

        public IEnumerable<Move> Moves { get { return this.moves; } }

        public Result Result { get { return this.result; } }

        public string WhitePlayerName { get { return this.whitePlayerName; } }

        public string BlackPlayerName { get { return this.blackPlayerName; } }

        public Weights Weights { get { return this.weights; } }

        public bool DidFollowTimeRequirements { get { return this.didFollowTimeRequirements; } }

        public int Round { get { return this.round; } }

        public int WhitePlayerRating { get { return this.whitePlayerRating; } }

        public int BlackPlayerRating { get { return this.blackPlayerRating; } }
    }
}