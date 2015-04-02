namespace a12johqv.Examination.Ai
{
    using System;

    using a12johqv.Examination.Chess;

    /// A case represented by a problem part: the position + color of player,
    /// and a solution part: the move performed.
    public struct Case : IEquatable<Case>
    {
        private readonly Position position;

        private readonly Move move;

        private readonly Color color;

        public Case(Position position, Move move, Color color)
        {
            this.position = position;
            this.move = move;
            this.color = color;
        }

        public Position Position
        {
            get { return this.position; }
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
            return this.position.Equals(other.Position) && this.move.Equals(other.Move) && this.color.Equals(other.Color);
        }

        public override int GetHashCode()
        {
            unchecked
            {
                int hash = (int)2166136261;
                hash = hash * 16777619 ^ this.position.GetHashCode();
                hash = hash * 16777619 ^ this.move.GetHashCode();
                hash = hash * 16777619 ^ this.color.GetHashCode();
                return hash;
            }
        }
    }
}