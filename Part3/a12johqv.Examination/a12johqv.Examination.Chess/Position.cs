namespace a12johqv.Examination.Chess
{
    using System;
    using System.Collections.Generic;
    using System.Globalization;
    using System.Linq;

    /// The state of the chess board in a match.
    /// Position contains the content of all 64 squares on the board,
    /// as well as movement events that has happened, which is used determine the legality of certain moves,
    /// and when a match ends.
    /// A position can only be created from a string representation,
    /// or by continously performing moves on the initial board setup.
    /// 
    /// The propoerty "ValidMoves" can be queried to determine which moves can be performed in the current position,
    /// and the method "ByMove()" advances to a new position with the move performed.
    /// ByMove should ONLY be called with moves from "ValidMoves", but this is not checked during runtime.
    /// 
    /// Moves created by ValidMoves and used by ByMove() uses special move representations for certain types of moves.
    /// Normal captures and movement is just represented with a move from the square the moving piece moved from, to the square it moved to, 
    /// with a potential promotion.
    /// En passant is represented like a normal capture of the square the pawn is moving to, which means that whether or not a move
    /// is en passant depends on the position.
    /// Long and short castling is represented as normal movement of the king two steps to left or right respectivly.
    /// A position ca be queried whether or not it's en passent or castling in a certain direction.
    /// 
    /// The position knows when the match ends and the result of that match,
    /// and follows the rule that the match is a draw if the same position is repeated three times,
    /// or if no pawns move in 50 moves.
    /// 
    /// Be aware that two positions are equal if they contain the same square content in the same order,
    /// regardless of their respective movement events.
    public struct Position : IEquatable<Position>
    {
        private readonly MovementEvents movementEvents;

        private BarePosition barePosition;

        private static readonly Position InitialField = GetInitialPosition();

        private Position(
            ref BarePosition barePosition,
            MovementEvents movementEvents)
        {
            this.barePosition = barePosition;
            this.movementEvents = movementEvents;
        }

        #region Properties And Indexers

        public bool IsChecked
        {
            get { return this.IsOwnKingIsThreatened(); }
        }

        public Color CurrentColor
        {
            get { return this.movementEvents.NextMoveColor; }
        }

        public BarePosition BarePosition
        {
            get { return this.barePosition; }
        }

        public IEnumerable<Move> ValidMoves
        {
            get
            {
                ICollection<Move> moves = new List<Move>();
                this.GetValidMoves(ignoreThreatenedKing: false, moves: moves);
                return moves;
            }
        }


        public static Position Initial
        {
            get { return InitialField; }
        }

        public MovementEvents MovementEvents { get { return this.movementEvents; } }

        public SquareContent this[Square square]
        {
            get { return this.barePosition[square.SquareIndex]; }
        }
        #endregion

        #region Factory Methods

        public static Position FromString(string serialized, MovementEvents movementEvents)
        {
            if (serialized == null || serialized.Length != 8 * 8)
            {
                throw new ArgumentException("Serialized string has to be of length 64, and not null", serialized);
            }
            else
            {
                try
                {
                    // Print row order is opposite of storage order (i.e white rook is first internal content, but black rook is first print content).
                    var contentOnUpsideDownRows = serialized.Select(character => SquareContent.FromString(character.ToString(CultureInfo.InvariantCulture))).ToArray();
                    var squaresOnRightRows = Enumerable.Range(start: 0, count: 64).Select(i => contentOnUpsideDownRows[IndexOnUpsideDownPosition(i)]);
                    var newBarePosition = BarePosition.FromSquareContents(squaresOnRightRows.ToArray());
                    return FromSquareContents(ref newBarePosition, movementEvents);
                }
                catch (ArgumentException exception)
                {
                    throw new ArgumentException("Serialized string format was wrong.", "serialized", exception);
                }
            }
        }

        private static Position FromSquareContents(ref BarePosition barePosition, MovementEvents movementEvents)
        {
            return new Position(ref barePosition, movementEvents);
        }

        private static int IndexOnUpsideDownPosition(int i)
        {
            int row = i / 8;
            int column = i % 8;
            return ((7 - row) * 8) + column;
        }

        #endregion

        #region Queries

        public Result GetResult(out Move[] validMoves)
        {
            validMoves = this.ValidMoves.ToArray();
            var result = validMoves.Any() && !this.movementEvents.IsGameOver(this) ? Result.Undecided :
                    this.IsOwnKingIsThreatened() ?
                        (this.movementEvents.NextMoveColor == Color.White ? Result.BlackVictory : Result.WhiteVictory) :
                        Result.Draw;
            return result;
        }

        public bool IsAnPassant(Move move)
        {
            return this.IsPawnCapturingPiece(move) && this[move.To].IsEmpty;
        }

        public bool IsCapture(Move move)
        {
            return !this[move.To].IsEmpty;
        }

        /// Get the direction of the (potential) castling move.
        /// Returns null if move is not a castling move,
        /// true if castling direction is left and false otherwise.
        public bool? GetCastlingDirection(Move move)
        {
            return this.IsCastling(move) ? move.From.Column > move.To.Column : (bool?)null;
        }

        #endregion

        #region By Move

        public Position ByMove(Move move)
        {
            if (this.IsAnPassant(move))
            {
                return this.ByAnPassant(move);
            }
            else if (this.IsCastling(move))
            {
                return this.ByCastling(move);
            }
            else
            {
                var position = this.WithMovementEventsBasedOnMove(move).ByPlainMovement(move);
                return position;
            }
        }

        private Position ByAnPassant(Move move)
        {
            Square opponentPawnSquare = SquareOfAnPassantCapturedPawn(move);
            Move moveToOpponentPawn = Move.FromSquareToSquare(move.From, opponentPawnSquare);
            Move moveToFinalSquare = Move.FromSquareToSquare(opponentPawnSquare, move.To);
            var ev = this.movementEvents.WithPerformedMove(move);

            return this.WithMovementEvents(ev)
                .ByPlainMovement(moveToOpponentPawn)
                .ByPlainMovement(moveToFinalSquare);
        }

        private static Square SquareOfAnPassantCapturedPawn(Move move)
        {
            // The captured pawn is beside the capturing pawn, in the column that the capturing pawn is moving to.
            int capturedColumn = move.To.Column;
            int capturedRow = move.From.Row;
            return Square.FromRowAndColumn(capturedRow, capturedColumn);
        }

        private bool IsPawnCapturingPiece(Move move)
        {
            SquareContent content = this[move.From];
            PieceType pieceType = content.PieceTypeOnSquare;
            bool isPawn = pieceType == PieceType.Pawn;
            bool isCapturing = move.From.Column != move.To.Column;
            return isPawn && isCapturing;
        }

        private Position ByCastling(Move move)
        {
            Move rookMove = GetRookMoveForCastling(move);

            return this.WithMovementEventsBasedOnMove(move).ByPlainMovement(move).ByPlainMovement(rookMove);
        }

        private static Move GetRookMoveForCastling(Move move)
        {
            bool kingMovingLeft = move.From.Column > move.To.Column;
            bool kingIsWhite = move.From.Row == 0;
            int rookRow = kingIsWhite ? 0 : 7;
            Square from = Square.FromRowAndColumn(rookRow, kingMovingLeft ? 0 : 7);
            Square to = Square.FromRowAndColumn(rookRow, kingMovingLeft ? 3 : 5);
            return Move.FromSquareToSquare(from, to);
        }

        private bool IsCastling(Move move)
        {
            SquareContent squareContent = this[move.From];
            int columnDistance = Math.Abs(move.From.Column - move.To.Column);
            bool isKingMoving = !squareContent.IsEmpty && squareContent.PieceTypeOnSquare == PieceType.King;
            bool movingMoveThanOneSpace = columnDistance > 1;
            return isKingMoving && movingMoveThanOneSpace;
        }

        private Position WithMovementEventsBasedOnMove(Move move)
        {
            var events = this.IsCastling(move) ? this.MovementEventsForCastling(move) : this.MovementEventsForNonCastlingMove(move);
            return this.WithMovementEvents(events);
        }

        private MovementEvents MovementEventsForCastling(Move move)
        {
            Color color = this.movementEvents.NextMoveColor;
            return this.movementEvents.WithCastling(move: move, color: color, side: RookSide(move));
        }

        private static Side RookSide(Move move)
        {
            return move.From.Column > move.To.Column ? Side.Left : Side.Right;
        }

        private MovementEvents MovementEventsForNonCastlingMove(Move move)
        {
            PieceType pieceType = this[move.From].PieceTypeOnSquare;
            switch (pieceType)
            {
                case PieceType.King:
                    return this.movementEvents.WithPerformedMoveByKing(move);
                case PieceType.Rook:
                    return this.movementEvents.WithPerformedMoveByRook(move);
                default:
                    return this.movementEvents.WithPerformedMove(move);
            }
        }

        private Position ByPlainMovement(Move move)
        {
            // Check for promotion.
            var pieceTypeForMovingPiece = move.IsPromotion ? move.PromotionType : this[move.From].PieceTypeOnSquare;
            var contentForMovingPiece = SquareContent.FromPieceAndColor(pieceTypeForMovingPiece, this[move.From].ColorOnSquare);

            // Overwrite to-space with from-space content, and remove content from from-space.
            var newSquareContent = this.barePosition
                .Select((content, i) => move.To.SquareIndex == i ? contentForMovingPiece : content)
                .Select((content, i) => move.From.SquareIndex == i ? SquareContent.Empty : content);
            var newBarePosition = BarePosition.FromSquareContents(newSquareContent.ToArray());
            var movementEventsWithPossiblePawnMove = this[move.From].PieceTypeOnSquare == PieceType.Pawn ? 
                this.movementEvents.WithMoveByPawn() : this.movementEvents.WithMoveByNonPawn();
            return FromSquareContents(ref newBarePosition, movementEventsWithPossiblePawnMove.WithVisitedPosition(this));
        }

        private Position WithMovementEvents(MovementEvents movementEvents)
        {
            return FromSquareContents(ref this.barePosition, movementEvents);
        }

        #endregion

        #region Valid Moves

        private void GetValidMoves(bool ignoreThreatenedKing, ICollection<Move> moves)
        {
            ICollection<Move> unthreatenedMoves = new List<Move>();
            for (int i = 0; i < 64; i++)
            {
                this.GetValidMovesForSquare(Square.FromSquareIndex(i), ignoreThreatenedKing, unthreatenedMoves);
            }

            // Filter away moves that were blocked by other chess pieces.
            ICollection<Move> unthreatenedNonBlockedMoves = ignoreThreatenedKing ? moves : new List<Move>();
            foreach (var move in unthreatenedMoves)
            {
                if (!move.From.Equals(move.To)
                    && this.IsSquareOnTheWayBlocking(move)
                    && !this.IsOccupiedByPieceOfColor(move.To, this.movementEvents.NextMoveColor))
                {
                    unthreatenedNonBlockedMoves.Add(move);
                }
                else
                {
                    int a = 0;
                }
            }

            if (!ignoreThreatenedKing)
            {
                // Moves ignore moves that caused the king to threatened.
                var normalMoves = new List<Move>();
                foreach (var move in unthreatenedNonBlockedMoves)
                {
                    if (!this.MoveLeadsToCapturedKing(move))
                    {
                        normalMoves.Add(move);
                    }
                }

                foreach (var move in normalMoves)
                {
                    if (this.IsCastling(move))
                    {
                        // We need to remove castling, if king crosses a square that it cannot reach (that is threatened).
                        int row = move.From.Row;
                        int passedColumn = RookSide(move).IsLeft() ? 3 : 5;
                        Square passedSquare = Square.FromRowAndColumn(row, passedColumn);
                        Move kingToCrossedSquareMove = Move.FromSquareToSquare(move.From, passedSquare);
                        if (normalMoves.Contains(kingToCrossedSquareMove))
                        {
                            moves.Add(move);
                        }
                    }
                    else
                    {
                        moves.Add(move);
                    }
                }
            }
        }

        private bool MoveLeadsToCapturedKing(Move move)
        {
            // this.MovementEvents.CurrentColor is the one doing 'move'.
            return this.ByMove(move).OpponentKingIsThreatened();
        }

        private bool IsOwnKingIsThreatened()
        {
            return this.WithMovementEvents(this.movementEvents.WithNextMoveColorFlipped()).OpponentKingIsThreatened();
        }

        private bool OpponentKingIsThreatened()
        {
            var validMoves = new List<Move>();
            this.GetValidMoves(ignoreThreatenedKing: true, moves: validMoves);

            var oppositeColor = this.CurrentColor.OppositeColor();
            foreach (var move in validMoves)
            {
                var toContent = this[move.To];
                if (!toContent.IsEmpty && toContent.ColorOnSquare == oppositeColor && toContent.PieceTypeOnSquare == PieceType.King)
                {
                    return true;
                }
            }
            return false;
        }

        private void GetValidMovesForSquare(Square square, bool ignoreThreatenedKing, ICollection<Move> moves)
        {
            SquareContent squareContent = this[square];
            bool correctColor = !squareContent.IsEmpty && squareContent.ColorOnSquare == this.movementEvents.NextMoveColor;

            if (correctColor)
            {
                PieceType pieceType = squareContent.PieceTypeOnSquare;
                switch (pieceType)
                {
                    case PieceType.Pawn:
                        this.GetValidMovesForPawn(square, moves);
                        break;
                    case PieceType.Rook:
                        GetValidMovesForRook(square, moves);
                        break;
                    case PieceType.Knight:
                        GetValidMovesForKnight(square, moves);
                        break;
                    case PieceType.Bishop:
                        GetValidMovesForBishop(square, moves);
                        break;
                    case PieceType.Queen:
                        GetValidMovesForQueen(square, moves);
                        break;
                    case PieceType.King:
                        this.GetValidMovesForKing(square, ignoreThreatenedKing, moves);
                        break;
                    default:
                        throw new Exception("Unexpected piece type.");
                }
            }
        }

        private void GetValidMovesForPawn(Square square, ICollection<Move> moves)
        {
            Color color = this.movementEvents.NextMoveColor;
            int row = square.Row;
            int column = square.Column;
            int forwardOffset = color == Color.White ? 1 : -1;
            int pawnStartRow = color == Color.White ? 1 : 6;
            int forwardRow = square.Row + forwardOffset;

            Square? forwardLeft = MaybeFromRowAndColumn(row + forwardOffset, column - 1);
            Square? forwardRight = MaybeFromRowAndColumn(row + forwardOffset, column + 1);

            Square forward = Square.FromRowAndColumn(forwardRow, column);
            Square? longForward = MaybeFromRowAndColumn(row + (forwardOffset * 2), column);
            
            bool forwardLeftValid = forwardLeft.HasValue
                && (this.IsOccupiedByColor(forwardLeft.Value, color.OppositeColor()) || this.SquareWasCrossedByPawnLastMove(forwardLeft.Value));
            bool forwardRightValid = forwardRight.HasValue
                && (this.IsOccupiedByColor(forwardRight.Value, color.OppositeColor()) || this.SquareWasCrossedByPawnLastMove(forwardRight.Value));
            bool forwardValid = this[forward].IsEmpty;
            bool longForwardValid = longForward.HasValue
                && row == pawnStartRow
                && this[forward].IsEmpty
                && this[longForward.Value].IsEmpty;

            {
                IList<Move> validMoves = new List<Move>();
                if (forwardLeftValid)
                {
                    validMoves.Add(Move.FromSquareToSquare(square, forwardLeft.Value));
                }
                if (forwardRightValid)
                {
                    validMoves.Add(Move.FromSquareToSquare(square, forwardRight.Value));
                }
                if (forwardValid)
                {
                    validMoves.Add(Move.FromSquareToSquare(square, forward));
                }
                if (longForwardValid)
                {
                    validMoves.Add(Move.FromSquareToSquare(square, longForward.Value));
                }

                var validPromotions = new[] { PieceType.Rook, PieceType.Knight, PieceType.Bishop, PieceType.Queen };

                // Transform moves that land on the first or final rows to promotion moves.
                foreach (var validMove in validMoves)
                {
                    if (validMove.To.Row == 0 || validMove.To.Row == 7)
                    {
                        foreach (var validPromotion in validPromotions)
                        {
                            moves.Add(Move.FromSquareToSquareWithPromotion(validMove.From, validMove.To, validPromotion));
                        }
                    }
                    else
                    {
                        moves.Add(validMove);
                    }
                }

            }
        }

        private bool SquareWasCrossedByPawnLastMove(Square value)
        {
            if (!this.movementEvents.HasMoved)
            {
                return false;
            }
            else
            {
                Color lastMoveColor = this.movementEvents.NextMoveColor.OppositeColor();
                Move lastMove = this.movementEvents.LastMove;
                SquareContent lastMoveToContent = this[lastMove.To];
                bool didLongMove = !lastMoveToContent.IsEmpty
                    && lastMoveToContent.PieceTypeOnSquare == PieceType.Pawn
                    && Math.Abs(lastMove.From.Row - lastMove.To.Row) == 2;

                if (didLongMove)
                {
                    // Okay the opponent did a long move, but did it cross the square in question?
                    // If so, the long move target square is one row in front of the checked square, seen from long move players perspective.
                    int forwardOffsetForLastMovePlayer = lastMoveColor == Color.White ? 1 : -1;
                    Square squareInFrontOfCrossedSquare = Square.FromRowAndColumn(value.Row + forwardOffsetForLastMovePlayer, value.Column);
                    return squareInFrontOfCrossedSquare.Equals(lastMove.To);
                }
                else
                {
                    return false;
                }
            }
        }

        private static void GetValidMovesForRook(Square square, ICollection<Move> moves)
        {
            ICollection<Square> horizontalVerticalSquares = new List<Square>();
            GetHorizontalSquares(row: square.Row, squares: horizontalVerticalSquares);
            GetVerticalSquares(column: square.Column, squares: horizontalVerticalSquares);

            foreach (var hvSquare in horizontalVerticalSquares)
            {
                var move = Move.FromSquareToSquare(square, hvSquare);
                moves.Add(move);
            }
        }

        private static void GetValidMovesForKnight(Square square, ICollection<Move> moves)
        {
            var squaresWithoutObstructions = new[]
                {
                    MaybeFromRowAndColumn(square.Row + 1, square.Column + 2),
                    MaybeFromRowAndColumn(square.Row + 1, square.Column - 2),
                    MaybeFromRowAndColumn(square.Row + 2, square.Column + 1),
                    MaybeFromRowAndColumn(square.Row + 2, square.Column - 1),
                    MaybeFromRowAndColumn(square.Row - 1, square.Column + 2),
                    MaybeFromRowAndColumn(square.Row - 1, square.Column - 2),
                    MaybeFromRowAndColumn(square.Row - 2, square.Column + 1),
                    MaybeFromRowAndColumn(square.Row - 2, square.Column - 1)
                };

            foreach (var maybeSquare in squaresWithoutObstructions)
            {
                if (maybeSquare.HasValue)
                {
                    var move = Move.FromSquareToSquare(square, maybeSquare.Value);
                    moves.Add(move);
                }
            }
        }

        private static void GetValidMovesForBishop(Square square, ICollection<Move> moves)
        {
            var diagonalSquares = new List<Square>();
            GetDiagonalSquares(square, diagonalSquares);

            foreach (var surroundingSquare in diagonalSquares)
            {
                moves.Add(Move.FromSquareToSquare(square, surroundingSquare));
            }
        }

        private static void GetValidMovesForQueen(Square square, ICollection<Move> moves)
        {
            ICollection<Square> surroundingSquares = new List<Square>();
            GetDiagonalSquares(square, squares: surroundingSquares);
            GetHorizontalSquares(row: square.Row, squares: surroundingSquares);
            GetVerticalSquares(column: square.Column, squares: surroundingSquares);

            foreach (var surroundingSquare in surroundingSquares)
            {
                moves.Add(Move.FromSquareToSquare(square, surroundingSquare));
            }
        }

        private void GetValidMovesForKing(Square square, bool ignoreThreatenedKing, ICollection<Move> moves)
        {
            // These moves are only valid if king can move one step to the right or left without being captured.
            // We don't know that right now, so we may filter away these later.
            bool kingCanMoveForCastling = !this.HasCurrentKingMoved() && (ignoreThreatenedKing || !this.IsOwnKingIsThreatened());
            
            bool canDoLeftCastling = kingCanMoveForCastling
                && !this.HasCurrentRookMoved(side: Side.Left)
                && this.DoesRookOfCurrentColorExists(Square.FromRowAndColumn(square.Row, 0))
                && this.IsEmpty(Square.FromRowAndColumn(square.Row, square.Column - 1))
                && this.IsEmpty(Square.FromRowAndColumn(square.Row, square.Column - 2))
                && this.IsEmpty(Square.FromRowAndColumn(square.Row, square.Column - 3));

            bool canDoRightCastling = kingCanMoveForCastling
                && !this.HasCurrentRookMoved(side: Side.Right)
                && this.DoesRookOfCurrentColorExists(Square.FromRowAndColumn(square.Row, 7))
                && this.IsEmpty(Square.FromRowAndColumn(square.Row, square.Column + 1))
                && this.IsEmpty(Square.FromRowAndColumn(square.Row, square.Column + 2));

            if (canDoLeftCastling)
            {
                moves.Add(Move.FromSquareToSquare(square, Square.FromRowAndColumn(square.Row, square.Column - 2)));
            }

            if (canDoRightCastling)
            {
                 moves.Add(Move.FromSquareToSquare(square, Square.FromRowAndColumn(square.Row, square.Column + 2)));
            }

            for (int i = -1; i <= 1; i++)
            {
                for (int j = -1; j <= 1; j++)
                {
                    if (i != 0 || j != 0)
                    {
                        int row = square.Row + i;
                        int column = square.Column + j;
                        
                        if (row >= 0 && row < 8 && column >= 0 && column < 8)
                        {
                            var target = Square.FromRowAndColumn(row, column);
                            if (!this.IsOccupiedByPieceOfColor(target, this.MovementEvents.NextMoveColor))
                            {
                                moves.Add(Move.FromSquareToSquare(square, target));
                            }
                        }
                    }
                }
            }
        }

        private bool DoesRookOfCurrentColorExists(Square square)
        {
            var content = this[square];
            return !content.IsEmpty
                && content.ColorOnSquare == this.movementEvents.NextMoveColor
                && content.PieceTypeOnSquare == PieceType.Rook;
        }

        private bool HasCurrentRookMoved(Side side)
        {
            return this.movementEvents.HasRookMoved(color: this.movementEvents.NextMoveColor, side: side);
        }

        private bool HasCurrentKingMoved()
        {
            return this.movementEvents.HasKingMoved(this.movementEvents.NextMoveColor);
        }

        private bool IsEmpty(Square square)
        {
            return this[square].IsEmpty;
        }

        private static void GetDiagonalSquares(Square square, ICollection<Square> squares)
        {
            GetDiagonalSquaresWithIncrements(square, 1, 1, squares);
            GetDiagonalSquaresWithIncrements(square, 1, -1, squares);
            GetDiagonalSquaresWithIncrements(square, -1, 1, squares);
            GetDiagonalSquaresWithIncrements(square, -1, -1, squares);
        }

        private static void GetDiagonalSquaresWithIncrements(Square square, int rowIncrement, int columnIncrement, ICollection<Square> squares)
        {
            for (int i = 0; i < 8; i++)
            {
                Square? newSquare = MaybeFromRowAndColumn(square.Row + (i * rowIncrement), square.Column + (i * columnIncrement));
                if (newSquare.HasValue)
                {
                    squares.Add(newSquare.Value);
                }
                else
                {
                    break;
                }
            }
        }

        private bool IsOccupiedByPieceOfColor(Square square, Color color)
        {
            return !this[square].IsEmpty && this[square].ColorOnSquare == color;
        }

        private static void GetVerticalSquares(int column, ICollection<Square> squares)
        {
            for (int i = 0; i < 8; i++)
            {
                squares.Add(Square.FromRowAndColumn(i, column));
            }
        }

        private static void GetHorizontalSquares(int row, ICollection<Square> squares)
        {
            for (int i = 0; i < 8; i++)
            {
                squares.Add(Square.FromRowAndColumn(row, i));
            }
        }

        private bool SquareIsEmpty(Square square)
        {
            return this[square].IsEmpty;
        }

        private static bool IsVerticalMove(Move move)
        {
            return move.From.Column == move.To.Column && move.From.Row != move.To.Row;
        }

        private static bool IsHorizontalMove(Move move)
        {
            return move.From.Row == move.To.Row && move.From.Column != move.To.Column;
        }

        private static bool IsDiagonalMove(Move move)
        {
            return Math.Abs(move.From.Row - move.To.Row) == Math.Abs(move.From.Column - move.To.Column);
        }

        private bool IsVerticalSquareOnTheWayBlocking(Move move)
        {
            int maxRow = Math.Max(move.From.Row, move.To.Row);
            int minRow = Math.Min(move.From.Row, move.To.Row);

            var verticalSquares = new List<Square>();
            GetVerticalSquares(column: move.From.Column, squares: verticalSquares);
            foreach (var square in verticalSquares)
            {
                if (square.Row > minRow && square.Row < maxRow)
                {
                    if (!this[square].IsEmpty)
                    {
                        return false;
                    }
                }
            }
            return true;
        }

        private bool IsSquareOnTheWayBlocking(Move move)
        {
            Square start = move.From;

            int columnIncrement = move.From.Column < move.To.Column ? 1 
                : move.From.Column > move.To.Column ? -1 : 0;
            int rowIncrement = move.From.Row < move.To.Row ? 1
                : move.From.Row > move.To.Row ? -1 : 0;
            int increment = (rowIncrement * 8) + columnIncrement;

            for (int i = start.SquareIndex + increment;
                i >= 0 && i < 64
                && (i / 8 != move.To.Row || rowIncrement == 0)
                && (i % 8 != move.To.Column || columnIncrement == 0);
                i += increment)
            {
                if (!this.BarePosition[i].IsEmpty)
                {
                    return false;
                }
            }
            return true;
        }

        private bool IsOccupiedByColor(Square square, Color color)
        {
            return !this[square].IsEmpty && this[square].ColorOnSquare == color;
        }

        private static Square? MaybeFromRowAndColumn(int row, int column)
        {
            return row >= 0 && row < 8 && column >= 0 && column < 8 ? Square.FromRowAndColumn(row, column) : new Square?();
        }
        #endregion

        #region Equality

        public bool Equals(Position other)
        {
            return this.barePosition.Equals(other.BarePosition);
        }

        public override bool Equals(object obj)
        {
            Position? position = obj as Position?;
            return position.HasValue && this.Equals(position.Value);
        }

        public override int GetHashCode()
        {
            unchecked
            {
                int hash = (int)2166136261;
                for (int i = 0; i < 64; i++)
                {
                    hash = hash * 16777619 ^ this.barePosition[i].GetHashCode();
                }
                return hash;
            }
        }

        #endregion

        #region To String

        public override string ToString()
        {
            var rows = this.barePosition.Select((content, i) => new { Content = content, Index = i })
                .GroupBy((contentI) => contentI.Index / 8).OrderBy(group => group.Key);

            // When iterating over rows, the black rows should be added first (the ones with higher row number).
            var reversedRows = rows.Reverse();

            // Select all characters in group, and concatenate them.
            var stringRows = reversedRows.Select(group =>
                    group.Select(contentI => contentI.Content.ToString())
                        .Aggregate(string.Concat));

            return string.Concat(stringRows);
        }

        #endregion

        #region Initial Position

        private static Position GetInitialPosition()
        {
            const string InitialString = 
                  "rnbqkbnr"
                + "pppppppp"
                + "........"
                + "........"
                + "........"
                + "........"
                + "PPPPPPPP"
                + "RNBQKBNR";
            return FromString(InitialString, Chess.MovementEvents.Initial);
        }

        #endregion
    }
}