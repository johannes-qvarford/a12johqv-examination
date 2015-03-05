namespace a12johqv.Examination.Chess
{
    using System;

    public struct Move : IEquatable<Move>
    {
        private readonly Square from;

        private readonly Square to;

        private readonly PieceType? promotionType;

        private Move(Square from, Square to, PieceType? promotionType)
        {
            this.from = from;
            this.to = to;
            this.promotionType = promotionType;
        }

        public Square From
        {
            get { return this.from; }
        }

        public Square To
        {
            get { return this.to; }
        }

        public PieceType PromotionType
        {
            get
            {
                if (!this.promotionType.HasValue)
                {
                    throw new InvalidOperationException("Cannot use PromotionType property if move isn't a promotion.");
                }
                else
                {
                    return this.promotionType.Value;
                }
            }
        }

        public bool IsPromotion
        {
            get { return this.promotionType.HasValue; }
        }

        public static Move FromSquareToSquare(Square from, Square to)
        {
            return new Move(from, to, null);
        }

        public static Move FromSquareToSquareWithPromotion(Square from, Square to, PieceType promotion)
        {
            return new Move(from, to, promotion);
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
                if (this.promotionType.HasValue)
                {
                    hash = hash * 16777619 ^ this.promotionType.Value.GetHashCode();
                }
                
                return hash;
            }
        }

        public override string ToString()
        {
            if (this.promotionType.HasValue)
            {
                return string.Format("{0}{1}{2}", this.from, this.to, this.PromotionType.AsCharacter());
            }
            else
            {
                return string.Format("{0}{1}", this.from, this.to);
            }
            
        }

        public static Move FromString(string serialized)
        {
            if (serialized == null || serialized.Length < 4 || serialized.Length > 5)
            {
                throw new ArgumentException("Serialized string has the wrong length, or is null");
            }
            try
            {
                var from = Square.FromString(serialized.Substring(startIndex: 0, length: 2));
                var to = Square.FromString(serialized.Substring(startIndex: 2, length: 2));
                if (serialized.Length == 5)
                {
                    PieceType promotionType = PieceTypeUtility.FromCharacter(serialized[4]);
                    return FromSquareToSquareWithPromotion(from, to, promotionType);
                }
                else
                {
                    return FromSquareToSquare(from, to);
                }
            }
            catch (ArgumentException exception)
            {
                throw new ArgumentException("Serialized string has wrong format", "serialized", exception);
            }
        }
    }
}