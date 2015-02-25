namespace a12johqv.Examination.Chess
{
    using System;

    public struct Move : IEquatable<Move>
    {
        private readonly Square from;

        private readonly Square to;

        private Move(Square from, Square to)
        {
            this.from = from;
            this.to = to;
        }

        public Square From
        {
            get { return this.from; }
        }

        public Square To
        {
            get { return this.to; }
        }

        public static Move FromSquareToSquare(Square from, Square to)
        {
            return new Move(from, to);
        }

        public bool Equals(Move move)
        {
            return this.from.Equals(move.from)
                && this.to.Equals(move.to);
        }

        public override bool Equals(object obj)
        {
            Move? move = obj as Move?;
            return move.HasValue && this.Equals(move.Value);
        }

        public override int GetHashCode()
        {
            unchecked
            {
                int hash = (int)2166136261;
                hash = hash * 16777619 ^ this.from.GetHashCode();
                hash = hash * 16777619 ^ this.to.GetHashCode();
                return hash;
            }
        }

        public override string ToString()
        {
            return string.Format("{0}{1}", this.from, this.to);
        }

        public static Move FromString(string serialized)
        {
            if (serialized == null || serialized.Length != 4)
            {
                throw new ArgumentException("Serialized string has the wrong length, or is null");
            }
            try
            {
                // A square string is two characters long.
                return FromSquareToSquare(
                    Square.FromString(serialized.Substring(startIndex: 0, length: 2)),
                    Square.FromString(serialized.Substring(startIndex: 2, length: 2)));
            }
            catch (ArgumentException exception)
            {
                throw new ArgumentException("Serialized string has wrong format", "serialized", exception);
            }
        }
    }
}