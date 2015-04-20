namespace a12johqv.Examination.Ai
{
    using System;

    /// A report of a match for a player.
    /// It contains the player, and the decision time for the players first 40 moves.
    public struct PlayerReport
    {
        private readonly Player player;

        private readonly TimeSpan decisionTimeOfFirst40Moves;

        public PlayerReport(Player player, TimeSpan decisionTimeOfFirst40Moves)
        {
            this.player = player;
            this.decisionTimeOfFirst40Moves = decisionTimeOfFirst40Moves;
        }

        public Player Player
        {
            get { return this.player; }
        }

        public TimeSpan DecisionTimeOfFirst40Moves
        {
            get { return this.decisionTimeOfFirst40Moves; }
        }
    }
}