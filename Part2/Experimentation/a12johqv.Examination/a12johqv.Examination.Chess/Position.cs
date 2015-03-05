namespace a12johqv.Examination.Chess
{
    using System;
    using System.Collections.Generic;
    using System.Collections.Immutable;
    using System.Globalization;
    using System.Linq;

    public struct Position : IEquatable<Position>
    {
        private readonly IReadOnlyList<SquareContent> squaresContent;

        private readonly MovementEvents movementEvents;

        private static readonly Position InitialField = new Position(InitialPositionUtility.InitialSquares, MovementEvents.Initial);

        private Position(
            IReadOnlyList<SquareContent> squaresContent,
            MovementEvents movementEvents)
        {
            this.squaresContent = squaresContent;
            this.movementEvents = movementEvents;
        }

        #region Properties And Indexers

        public IEnumerable<SquareContent> SquareContentsFromLowRowAndColumn
        {
            get { return this.squaresContent; }
        }

        public MovementEvents MovementEvents
        {
            get { return this.movementEvents; }
        }

        public IEnumerable<Move> ValidMoves
        {
            get { return this.GetValidMoves(ignoreThreatenedKing: false); }
        }

        public static Position Initial
        {
            get { return InitialField; }
        }

        private SquareContent this[Square square]
        {
            get { return this.squaresContent.Skip(square.SquareIndex).First(); }
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
                    return FromSquareContents(squaresOnRightRows, movementEvents);
                }
                catch (ArgumentException exception)
                {
                    throw new ArgumentException("Serialized string format was wrong.", "serialized", exception);
                }
            }
        }

        private static Position FromSquareContents(IEnumerable<SquareContent> squares, MovementEvents movementEvents)
        {
            return new Position(squares.ToImmutableList(), movementEvents);
        }

        private static int IndexOnUpsideDownPosition(int i)
        {
            int row = i / 8;
            int column = i % 8;
            return ((7 - row) * 8) + column;
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
                return this.WithMovementEventsBasedOnMove(move).ByPlainMovement(move);
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

        private bool IsAnPassant(Move move)
        {
            return this.IsPawnCapturingPiece(move) && this[move.To].IsEmpty;
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
            bool leftRook = move.From.Column < move.To.Column;
            return this.movementEvents.WithCastling(move: move, color: color, left: leftRook);
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
            var newSquareContent = this.squaresContent
                .Select((content, i) => move.To.SquareIndex == i ? contentForMovingPiece : content)
                .Select((content, i) => move.From.SquareIndex == i ? SquareContent.GetEmptySquare() : content);

            return FromSquareContents(newSquareContent, this.movementEvents);
        }

        private Position WithMovementEvents(MovementEvents movementEvents)
        {
            return FromSquareContents(this.squaresContent, movementEvents);
        }

        #endregion

        #region Valid Moves

        public IEnumerable<Move> GetValidMoves(bool ignoreThreatenedKing)
        {
            var theThis = this;
            var moves = Enumerable.Range(start: 0, count: 8 * 8)
                .SelectMany(i => theThis.GetValidMovesForSquare(Square.FromSquareIndex(i)));
            return ignoreThreatenedKing ? moves : 
                moves.Where(move => !theThis.MoveLeadsToCapturedKing(move));
        }

        private bool MoveLeadsToCapturedKing(Move move)
        {
            // this.MovementEvents.CurrentColor is the one doing 'move'.
            var afterFirstMove = this.ByMove(move);
            var opponentValidMoves = afterFirstMove.GetValidMoves(ignoreThreatenedKing: true);
            return opponentValidMoves.Any(opponentMove => !afterFirstMove.ByMove(opponentMove).HasKing());
        }

        private bool HasKing()
        {
            Position theThis = this;
            return this.squaresContent.Any(content => 
                !content.IsEmpty && content.ColorOnSquare == theThis.MovementEvents.NextMoveColor && content.PieceTypeOnSquare == PieceType.King);
        }

        private IEnumerable<Move> GetValidMovesForSquare(Square square)
        {
            SquareContent squareContent = this[square];
            bool correctColor = !squareContent.IsEmpty && squareContent.ColorOnSquare == this.movementEvents.NextMoveColor;

            if (!correctColor)
            {
                return ImmutableList.Create<Move>();
            }
            else
            {
                PieceType pieceType = squareContent.PieceTypeOnSquare;
                switch (pieceType)
                {
                    case PieceType.Pawn:
                        return this.GetValidMovesForPawn(square);
                    case PieceType.Rook:
                        return this.GetValidMovesForRook(square);
                    case PieceType.Knight:
                        return this.GetValidMovesForKnight(square);
                    case PieceType.Bishop:
                        return this.GetValidMovesForBishop(square);
                    case PieceType.Queen:
                        return this.GetValidMovesForQueen(square);
                    case PieceType.King:
                        return this.GetValidMovesForKing(square);
                    default:
                        throw new Exception("Unexpected piece type.");
                }
            }
        }

        private IEnumerable<Move> GetValidMovesForPawn(Square square)
        {
            Color color = this.movementEvents.NextMoveColor;
            int row = square.Row;
            int column = square.Column;
            int forwardOffset = color == Color.White ? 1 : -1;
            int pawnStartRow = color == Color.White ? 1 : 6;
            int forwardRow = square.Row + forwardOffset;
            int promotionRow = color == Color.White ? 0 : 7;

            Square? forwardLeft = MaybeFromRowAndColumn(row + forwardOffset, column - 1);
            Square? forwardRight = MaybeFromRowAndColumn(row + forwardOffset, column + 1);

            Square forward = Square.FromRowAndColumn(forwardRow, column);
            Square? longForward = MaybeFromRowAndColumn(row + (forwardOffset * 2), column);
            
            bool forwardLeftValid = forwardLeft.HasValue
                && (IsOccupiedByColor(forwardLeft.Value, color.OppositeColor()) || this.SquareWasCrossedByPawnLastMove(forwardLeft.Value));
            bool forwardRightValid = forwardRight.HasValue
                && (IsOccupiedByColor(forwardRight.Value, color.OppositeColor()) || this.SquareWasCrossedByPawnLastMove(forwardRight.Value));
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
                    if (forwardRow == promotionRow)
                    {
                        var promotion = Enum.GetValues(typeof(PieceType))
                            .Cast<PieceType>()
                            .Select(type => Move.FromSquareToSquareWithPromotion(square, forward, type));
                        foreach (var move in promotion)
                        {
                            validMoves.Add(move);
                        }
                    }
                    else
                    {
                        validMoves.Add(Move.FromSquareToSquare(square, forward));
                    }
                }
                if (longForwardValid)
                {
                    validMoves.Add(Move.FromSquareToSquare(square, longForward.Value));
                }
                return validMoves;
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

        private IEnumerable<Move> GetValidMovesForRook(Square square)
        {
            IEnumerable<Square> horizontalSquares = GetHorizontalSquares(row: square.Row);
            IEnumerable<Square> verticalSquares = GetVerticalSquares(column: square.Column);

            var movesWithoutObstructions = horizontalSquares.Union(verticalSquares)
                .Select(toSquare => Move.FromSquareToSquare(square, toSquare))
                .Where(move => !move.From.Equals(move.To));

            return this.FilterMovesBasedOnObstructingPieces(movesWithoutObstructions);
        }

        private IEnumerable<Move> GetValidMovesForKnight(Square square)
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
            var movesWithoutObstructions = squaresWithoutObstructions
                .Where(maybeSquare => maybeSquare.HasValue)
                .Select(maybeSquare => maybeSquare.Value)
                .Select(toSquare => Move.FromSquareToSquare(square, toSquare));

            Position theThis = this;
            return movesWithoutObstructions
                .Where(move => !theThis.IsOccupiedByPieceOfColor(move.To, theThis.movementEvents.NextMoveColor));
        }

        private IEnumerable<Move> GetValidMovesForBishop(Square square)
        {
            IEnumerable<Square> diagonalSquares = GetDiagonalSquares(square);

            var movesWithoutObstructions = diagonalSquares
                .Select(toSquare => Move.FromSquareToSquare(square, toSquare));

            return FilterMovesBasedOnObstructingPieces(movesWithoutObstructions);
        }

        private IEnumerable<Move> GetValidMovesForQueen(Square square)
        {
            IEnumerable<Square> diagonalSquares = GetDiagonalSquares(square);
            IEnumerable<Square> horizontalSquares = GetHorizontalSquares(row: square.Row);
            IEnumerable<Square> verticalSquares = GetVerticalSquares(column: square.Column);

            var movesWithoutObstructions = diagonalSquares.Union(horizontalSquares).Union(verticalSquares)
                .Select(toSquare => Move.FromSquareToSquare(square, toSquare));

            return FilterMovesBasedOnObstructingPieces(movesWithoutObstructions);
        }

        private IEnumerable<Move> GetValidMovesForKing(Square square)
        {
            return GetValidMovesForQueen(square)
                .Where(move => Math.Abs(move.From.Column - move.To.Column) <= 1 && Math.Abs(move.From.Row - move.To.Row) <= 1);
        }

        private static IEnumerable<Square> GetDiagonalSquares(Square square)
        {
            return GetDiagonalSquaresWithIncrements(square, 1, 1)
                .Union(GetDiagonalSquaresWithIncrements(square, 1, -1))
                .Union(GetDiagonalSquaresWithIncrements(square, -1, 1))
                .Union(GetDiagonalSquaresWithIncrements(square, -1, -1));
        }

        private static IEnumerable<Square> GetDiagonalSquaresWithIncrements(Square square, int rowIncrement, int columnIncrement)
        {
            for (int i = 0; i < 8; i++)
            {
                Square? newSquare = MaybeFromRowAndColumn(square.Row + (i * rowIncrement), square.Column + (i * columnIncrement));
                if (newSquare.HasValue)
                {
                    yield return newSquare.Value;
                }
                else
                {
                    break;
                }
            }
        }

        private IEnumerable<Move> FilterMovesBasedOnObstructingPieces(IEnumerable<Move> moves)
        {
            var theThis = this;
            return moves
                .Where(theThis.CanDoMoveWithoutPassingOverPiece)
                .Where(move => !theThis.IsOccupiedByPieceOfColor(move.To, theThis.movementEvents.NextMoveColor));
        }

        private bool IsOccupiedByPieceOfColor(Square square, Color color)
        {
            return !this[square].IsEmpty && this[square].ColorOnSquare == color;
        }

        private static IEnumerable<Square> GetVerticalSquares(int column)
        {
            for (int i = 0; i < 8; i++)
            {
                yield return Square.FromRowAndColumn(i, column);
            }
        }

        private static IEnumerable<Square> GetHorizontalSquares(int row)
        {
            for (int i = 0; i < 8; i++)
            {
                yield return Square.FromRowAndColumn(row, i);
            }
        }

        private bool CanDoMoveWithoutPassingOverPiece(Move move)
        {
            Position theThis = this;
            if (move.ToString() == "b4e1")
            {
                int a = 0;
            }
            var onTheWay = SquaresOnTheWay(move);
            bool allOnTheWayAreEmpty = onTheWay.All(this.SquareIsEmpty);
            return allOnTheWayAreEmpty;
        }

        private bool SquareIsEmpty(Square square)
        {
            return this[square].IsEmpty;
        }

        private static IEnumerable<Square> SquaresOnTheWay(Move move)
        {
            if (IsVerticalMove(move))
            {
                return VerticalSquaresOnTheWay(move);
            }
            else if (IsHorizontalMove(move))
            {
                return HorizontalSquaresOnTheWay(move);
            }
            else if (IsDiagonalMove(move))
            {
                return DiagonalSquaresOnTheWay(move);
            }
            else
            {
                throw new Exception("Unexpected move.");
            }
        }

        private static IEnumerable<Square> DiagonalSquaresOnTheWay(Move move)
        {
            int maxColumn = Math.Max(move.From.Column, move.To.Column);
            int minColumn = Math.Min(move.From.Column, move.To.Column);
            int columnIncrement = move.From.Column < move.To.Column ? 1 : -1;
            int rowIncrement = move.From.Row < move.To.Row ? 1 : -1;
            var squares = GetDiagonalSquaresWithIncrements(move.From, rowIncrement: rowIncrement, columnIncrement: columnIncrement);
            return squares.Where(square => square.Column > minColumn && square.Column < maxColumn);
        }

        private static IEnumerable<Square> HorizontalSquaresOnTheWay(Move move)
        {
            int maxColumn = Math.Max(move.From.Column, move.To.Column);
            int minColumn = Math.Min(move.From.Column, move.To.Column);
            return GetHorizontalSquares(row: move.From.Row)
                .Where(square => square.Column > minColumn && square.Column < maxColumn);
        }

        private static bool IsVerticalMove(Move move)
        {
            return move.From.Column == move.To.Column && move.From.Row != move.To.Row;
        }

        private static IEnumerable<Square> VerticalSquaresOnTheWay(Move move)
        {
            int maxRow = Math.Max(move.From.Row, move.To.Row);
            int minRow = Math.Min(move.From.Row, move.To.Row);
            return GetVerticalSquares(column: move.From.Column)
                .Where(square => square.Row > minRow && square.Row < maxRow);
        }

        private static bool IsHorizontalMove(Move move)
        {
            return move.From.Row == move.To.Row && move.From.Column != move.To.Column;
        }

        private static bool IsDiagonalMove(Move move)
        {
            return Math.Abs(move.From.Row - move.To.Row) == Math.Abs(move.From.Column - move.To.Column);
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

        public bool Equals(Position position)
        {
            return this.squaresContent.Zip(position.squaresContent, Tuple.Create)
                .All(tuple => tuple.Item1.Equals(tuple.Item2));
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
                return this.squaresContent.Aggregate(
                    (int)2166136261,
                    (hash, content) => hash * 16777619 ^ content.GetHashCode());
            }
        }

        #endregion

        #region To String

        public override string ToString()
        {
            var rows = this.squaresContent.Select((content, i) => new { Content = content, Index = i })
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
            return FromSquareContents(InitialPositionUtility.InitialSquares, Chess.MovementEvents.Initial);
        }

        private static class InitialPositionUtility
        {
            private static readonly SquareContent Pw = SquareContent.FromPieceAndColor(PieceType.Pawn, Color.White);

            private static readonly SquareContent Rw = SquareContent.FromPieceAndColor(PieceType.Rook, Color.White);

            private static readonly SquareContent Nw = SquareContent.FromPieceAndColor(PieceType.Knight, Color.White);

            private static readonly SquareContent Bw = SquareContent.FromPieceAndColor(PieceType.Bishop, Color.White);

            private static readonly SquareContent Qw = SquareContent.FromPieceAndColor(PieceType.Queen, Color.White);

            private static readonly SquareContent Kw = SquareContent.FromPieceAndColor(PieceType.King, Color.White);

            private static readonly SquareContent Pb = SquareContent.FromPieceAndColor(PieceType.Pawn, Color.Black);

            private static readonly SquareContent Rb = SquareContent.FromPieceAndColor(PieceType.Rook, Color.Black);

            private static readonly SquareContent Nb = SquareContent.FromPieceAndColor(PieceType.Knight, Color.Black);

            private static readonly SquareContent Bb = SquareContent.FromPieceAndColor(PieceType.Bishop, Color.Black);

            private static readonly SquareContent Qb = SquareContent.FromPieceAndColor(PieceType.Queen, Color.Black);

            private static readonly SquareContent Kb = SquareContent.FromPieceAndColor(PieceType.King, Color.Black);

            private static readonly SquareContent Em = SquareContent.GetEmptySquare();

            public static readonly SquareContent[] InitialSquares = 
                {
                    Rw, Nw, Bw, Qw, Kw, Bw, Nw, Rw,
                    Pw, Pw, Pw, Pw, Pw, Pw, Pw, Pw,
                    Em, Em, Em, Em, Em, Em, Em, Em,
                    Em, Em, Em, Em, Em, Em, Em, Em,
                    Em, Em, Em, Em, Em, Em, Em, Em,
                    Em, Em, Em, Em, Em, Em, Em, Em,
                    Pb, Pb, Pb, Pb, Pb, Pb, Pb, Pb,
                    Rb, Nb, Bb, Qb, Kb, Bb, Nb, Rb
                };
        }

        #endregion
    }
}