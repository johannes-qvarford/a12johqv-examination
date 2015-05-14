namespace a12johqv.Examination.Ai
{
    using System;

    using a12johqv.Examination.Chess;

    /// A case represented by a problem part: the bare position + color of player,
    /// and a solution part: the move performed.
    /// This is an optimization to not have to store the entire position.
    public struct Case : IEquatable<Case>
    {
        private readonly BarePosition barePosition;

        private readonly Move move;

        private readonly Color color;

        public Case(BarePosition barePosition, Move move, Color color)
        {
            this.barePosition = barePosition;
            this.move = move;
            this.color = color;
        }

        public BarePosition BarePosition
        {
            get { return this.barePosition; }
        }

        public Move Move
        {
            get { return this.move; }
        }

        public Color Color
        {
            get { return this.color; }
        }

        public override bool Equals(object obj)
        {
            return obj is Case && this.Equals((Case)obj);
        }

        public bool Equals(Case other)
        {
            return this.barePosition.Equals(other.BarePosition) && this.move.Equals(other.Move) && this.color.Equals(other.Color);
        }

        public override int GetHashCode()
        {
            unchecked
            {
                int hash = (int)2166136261;
                hash = hash * 16777619 ^ this.barePosition.GetHashCode();
                hash = hash * 16777619 ^ this.move.GetHashCode();
                hash = hash * 16777619 ^ this.color.GetHashCode();
                return hash;
            }
        }
    }
}