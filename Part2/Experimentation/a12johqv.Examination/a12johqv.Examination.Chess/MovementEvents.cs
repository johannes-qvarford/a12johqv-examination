namespace a12johqv.Examination.Chess
{
    using System;
    using System.Collections.Generic;
    using System.Collections.Immutable;
    using System.Diagnostics;
    using System.Diagnostics.Contracts;
    using System.Linq;

    /// The noteworthy events that has occured during a match,
    /// and the consequences of these events.
    /// 
    /// It records if either player can still perform castling (based on whether or not their kings or rooks have moved).
    /// and if the game is over because no pawns have moved in 50 moves or a position has been repeated three times.
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

        private readonly int movesSinceLastPawnMove;

        private readonly ImmutableDictionary<Position, int> previouslyVisitedPositions;

        private static readonly MovementEvents InitialMovementEventsField = 
            new MovementEvents()
            .Create(previouslyVisitedPositionsP: ImmutableDictionary.Create<Position, int>());

        private MovementEvents(
            Color nextMoveColor,
            Move? lastMove,
            bool whiteKingHasMoved,
            bool blackKingHasMoved,
            bool leftWhiteRookHasMoved,
            bool rightWhiteRookHasMoved,
            bool leftBlackRookHasMoved,
            bool rightBlackRookHasMoved,
            int movesSinceLastPawnMove,
            ImmutableDictionary<Position, int> previouslyVisitedPositions)
        {
            this.nextMoveColor = nextMoveColor;
            this.lastMove = lastMove;
            this.whiteKingHasMoved = whiteKingHasMoved;
            this.blackKingHasMoved = blackKingHasMoved;
            this.leftWhiteRookHasMoved = leftWhiteRookHasMoved;
            this.rightWhiteRookHasMoved = rightWhiteRookHasMoved;
            this.leftBlackRookHasMoved = leftBlackRookHasMoved;
            this.rightBlackRookHasMoved = rightBlackRookHasMoved;
            this.movesSinceLastPawnMove = movesSinceLastPawnMove;
            this.previouslyVisitedPositions = previouslyVisitedPositions;
        }

        public static MovementEvents Initial
        {
            get { return InitialMovementEventsField; }
        }

        /// Returns whether or not at least one move has been performed.
        public bool HasMoved
        {
            get { return this.lastMove.HasValue; }
        }

        /// The last move performed.
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

        /// The color of the player who should perform the next move.
        public Color NextMoveColor
        {
            get { return this.nextMoveColor; }
        }

        /// All unique positions that have been visited during the match.
        public IEnumerable<Position> VisitedPositions
        {
            get { return this.previouslyVisitedPositions.Keys; }
        }

        /// Returns whether or the king of the given color has moved.
        public bool HasKingMoved(Color color)
        {
            return color == Color.White ? this.whiteKingHasMoved : this.blackKingHasMoved;
        }

        /// Returns whether or not the rook of the given color on the given side has moved.
        public bool HasRookMoved(Color color, Side side)
        {
            return color == Color.White && side.IsLeft() ? this.leftWhiteRookHasMoved :
                   color == Color.White && !side.IsLeft() ? this.rightWhiteRookHasMoved :
                   color == Color.Black && side.IsLeft() ? this.leftBlackRookHasMoved :
                   this.rightBlackRookHasMoved;
        }

        public bool IsGameOver(Position currentPosition)
        {
            // Need to include currentPosition here because the position can never be included in its movementsEvents,
            // while movementEvents is included in the position if both are immutable.
            // This means that to figure out if it's game over, we need to provide the latest position.
            var withVisitedPosition = this.WithVisitedPosition(currentPosition);
            return this.movesSinceLastPawnMove >= 50 || withVisitedPosition.previouslyVisitedPositions.Values.Contains(3);
        }

        public MovementEvents WithVisitedPosition(Position position)
        {
            if (this.previouslyVisitedPositions.ContainsKey(position))
            {
                var previousCount = this.previouslyVisitedPositions[position];
                return this.Create(previouslyVisitedPositionsP: this.previouslyVisitedPositions.SetItem(position, previousCount + 1));
            }
            else
            {
                return this.Create(previouslyVisitedPositionsP: this.previouslyVisitedPositions.Add(position, 1));
            }
        }

        public MovementEvents WithMoveByPawn()
        {
            return this.Create(movesSinceLastPawnMoveP: 0);
        }

        public MovementEvents WithMoveByNonPawn()
        {
            return this.Create(movesSinceLastPawnMoveP: this.movesSinceLastPawnMove + 1);
        }

        public MovementEvents WithPerformedMoveByKing(Move move)
        {
            return this.WithKingHasMoved(this.NextMoveColor).WithPerformedMove(move);
        }

        public MovementEvents WithPerformedMoveByRook(Move move)
        {
            Side side = move.From.Column == 0 ? Side.Left : Side.Right;
            Color color = this.NextMoveColor;
            return this.WithRookHasMoved(color: color, side: side).WithPerformedMove(move);
        }

        public MovementEvents WithPerformedMove(Move move)
        {
            return this.WithLastMove(move).WithNextMoveColorFlipped();
        }

        public MovementEvents WithCastling(Move move, Color color, Side side)
        {
            return this.WithKingHasMoved(color).WithRookHasMoved(color, side).WithNextMoveColorFlipped().WithLastMove(move);
        }

        public MovementEvents WithKingHasMoved(Color color)
        {
            return color == Color.White ? this.Create(whiteKingHasMovedP: true) : this.Create(blackKingHasMovedP: true);
        }

        private MovementEvents WithRookHasMoved(Color color, Side side)
        {
            return color == Color.White && side.IsLeft() ? this.Create(leftWhiteRookHasMovedP: true) :
                   color == Color.White && !side.IsLeft() ? this.Create(rightWhiteRookHasMovedP: true) :
                   color == Color.Black && side.IsLeft() ? this.Create(leftBlackRookHasMovedP: true) :
                   this.Create(rightBlackRookHasMovedP: true);
        }

        private MovementEvents WithLastMove(Move move)
        {
            return this.Create(lastMoveP: move);
        }

        public MovementEvents WithNextMoveColorFlipped()
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
            bool? rightBlackRookHasMovedP = null,
            int? movesSinceLastPawnMoveP = null,
            ImmutableDictionary<Position, int> previouslyVisitedPositionsP = null)
        {
            return new MovementEvents(
                nextMoveColor: nextMoveColorP.HasValue ? nextMoveColorP.Value : this.nextMoveColor,
                lastMove: lastMoveP.HasValue ? lastMoveP.Value : this.lastMove,
                whiteKingHasMoved: whiteKingHasMovedP.HasValue ? whiteKingHasMovedP.Value : this.whiteKingHasMoved,
                blackKingHasMoved: blackKingHasMovedP.HasValue ? blackKingHasMovedP.Value : this.blackKingHasMoved,
                leftWhiteRookHasMoved: leftWhiteRookHasMovedP.HasValue ? leftWhiteRookHasMovedP.Value : this.leftWhiteRookHasMoved,
                rightWhiteRookHasMoved: rightWhiteRookHasMovedP.HasValue ? rightWhiteRookHasMovedP.Value : this.rightWhiteRookHasMoved,
                leftBlackRookHasMoved: leftBlackRookHasMovedP.HasValue ? leftBlackRookHasMovedP.Value : this.leftBlackRookHasMoved,
                rightBlackRookHasMoved: rightBlackRookHasMovedP.HasValue ? rightBlackRookHasMovedP.Value : this.rightBlackRookHasMoved,
                movesSinceLastPawnMove: movesSinceLastPawnMoveP.HasValue ? movesSinceLastPawnMoveP.Value : this.movesSinceLastPawnMove,
                previouslyVisitedPositions: previouslyVisitedPositionsP ?? this.previouslyVisitedPositions);
        }
    }
}