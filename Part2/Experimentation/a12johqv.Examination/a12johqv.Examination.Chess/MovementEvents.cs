namespace a12johqv.Examination.Chess
{
    using System;
    using System.Diagnostics.Contracts;

    public struct MovementEvents
    {
        private readonly Color nextMoveColor;

        private readonly Move? lastMove;

        private readonly bool whiteKingHasMoved;

        private readonly bool blackKingHasMoved;

        private readonly bool leftWhiteRookHasMoved;

        private readonly bool rightWhiteRookHasMoved;

        private readonly bool leftBlackRookHasMoved;

        private readonly bool rightBlackRookHasMoved;

        private static readonly MovementEvents InitialMovementEventsField = new MovementEvents();

        private MovementEvents(
            Color nextMoveColor,
            Move? lastMove,
            bool whiteKingHasMoved,
            bool blackKingHasMoved,
            bool leftWhiteRookHasMoved,
            bool rightWhiteRookHasMoved,
            bool leftBlackRookHasMoved,
            bool rightBlackRookHasMoved)
        {
            this.nextMoveColor = nextMoveColor;
            this.lastMove = lastMove;
            this.whiteKingHasMoved = whiteKingHasMoved;
            this.blackKingHasMoved = blackKingHasMoved;
            this.leftWhiteRookHasMoved = leftWhiteRookHasMoved;
            this.rightWhiteRookHasMoved = rightWhiteRookHasMoved;
            this.leftBlackRookHasMoved = leftBlackRookHasMoved;
            this.rightBlackRookHasMoved = rightBlackRookHasMoved;
        }

        public static MovementEvents Initial
        {
            get { return InitialMovementEventsField; }
        }

        public bool HasMoved
        {
            get { return this.lastMove.HasValue; }
        }

        public Move LastMove
        {
            get
            {
                if (!this.lastMove.HasValue)
                {
                    throw new InvalidOperationException("Cannot call MovementEvents.LastMove if no pieces have moved");
                }
                else
                {
                    return this.lastMove.Value;
                }
            }
        }

        public Color NextMoveColor
        {
            get { return this.nextMoveColor; }
        }

        public bool HasKingMoved(Color color)
        {
            return color == Color.White ? this.whiteKingHasMoved : this.blackKingHasMoved;
        }

        public bool HasRookMoved(Color color, bool left)
        {
            return color == Color.White && left ? this.leftWhiteRookHasMoved :
                   color == Color.White && !left ? this.rightWhiteRookHasMoved :
                   color == Color.Black && left ? this.leftBlackRookHasMoved :
                   this.rightBlackRookHasMoved;
        }

        public MovementEvents WithPerformedMoveByKing(Move move)
        {
            return this.WithKingHasMoved(this.NextMoveColor).WithPerformedMove(move);
        }

        public MovementEvents WithPerformedMoveByRook(Move move)
        {
            bool left = move.From.Column == 0;
            Color color = this.NextMoveColor;
            return this.WithRookHasMoved(color: color, left: left).WithPerformedMove(move);
        }

        public MovementEvents WithPerformedMove(Move move)
        {
            return this.WithLastMove(move).WithNextMoveColorFlipped();
        }

        [Pure]
        public MovementEvents WithCastling(Move move, Color color, bool left)
        {
            return this.WithKingHasMoved(color).WithRookHasMoved(color, left).WithNextMoveColorFlipped().WithLastMove(move);
        }

        [Pure]
        private MovementEvents WithKingHasMoved(Color color)
        {
            return color == Color.White ? this.Create(whiteKingHasMovedP: true) : this.Create(blackKingHasMovedP: true);
        }

        [Pure]
        private MovementEvents WithRookHasMoved(Color color, bool left)
        {
            return color == Color.White && left ? this.Create(leftWhiteRookHasMovedP: true) :
                   color == Color.White && !left ? this.Create(rightWhiteRookHasMovedP: true) :
                   color == Color.Black && left ? this.Create(leftBlackRookHasMovedP: true) :
                   this.Create(rightBlackRookHasMovedP: true);
        }

        [Pure]
        private MovementEvents WithLastMove(Move move)
        {
            return this.Create(lastMoveP: move);
        }

        [Pure]
        private MovementEvents WithNextMoveColorFlipped()
        {
            return this.Create(nextMoveColorP: this.nextMoveColor.OppositeColor());
        }

        [Pure]
        private MovementEvents Create(
            Color? nextMoveColorP = null,
            Move? lastMoveP = null,
            bool? whiteKingHasMovedP = null,
            bool? blackKingHasMovedP = null,
            bool? leftWhiteRookHasMovedP = null,
            bool? rightWhiteRookHasMovedP = null,
            bool? leftBlackRookHasMovedP = null,
            bool? rightBlackRookHasMovedP = null)
        {
            return new MovementEvents(
                nextMoveColor: nextMoveColorP.HasValue ? nextMoveColorP.Value : this.nextMoveColor,
                lastMove: lastMoveP.HasValue ? lastMoveP.Value : this.lastMove,
                whiteKingHasMoved: whiteKingHasMovedP.HasValue ? whiteKingHasMovedP.Value : this.whiteKingHasMoved,
                blackKingHasMoved: blackKingHasMovedP.HasValue ? blackKingHasMovedP.Value : this.blackKingHasMoved,
                leftWhiteRookHasMoved: leftWhiteRookHasMovedP.HasValue ? leftWhiteRookHasMovedP.Value : this.leftWhiteRookHasMoved,
                rightWhiteRookHasMoved: rightWhiteRookHasMovedP.HasValue ? rightWhiteRookHasMovedP.Value : this.rightWhiteRookHasMoved,
                leftBlackRookHasMoved: leftBlackRookHasMovedP.HasValue ? leftBlackRookHasMovedP.Value : this.leftBlackRookHasMoved,
                rightBlackRookHasMoved: rightBlackRookHasMovedP.HasValue ? rightBlackRookHasMovedP.Value : this.rightBlackRookHasMoved);
        }
    }
}