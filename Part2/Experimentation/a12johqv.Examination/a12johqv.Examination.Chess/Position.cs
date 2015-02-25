namespace a12johqv.Examination.Chess
{
    using System;
    using System.Collections;
    using System.Collections.Generic;
    using System.Diagnostics;
    using System.Globalization;
    using System.Linq;

    public struct Position : IEquatable<Position>
    {
        private readonly SquareContent[] squaresContent;

        private static readonly Position InitialField = new Position(InitialPositionUtility.InitialSquares);

        private Position(SquareContent[] squaresContent)
        {
            this.squaresContent = squaresContent;
        }

        public static Position Initial
        {
            get { return InitialField; }
        }

        private SquareContent this[Square square]
        {
            get { return this.squaresContent[(square.Row * 8) + square.Column]; }
        }

        public static Position FromString(string serialized)
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
                    return FromSquareContents(squaresOnRightRows);
                }
                catch (ArgumentException exception)
                {
                    throw new ArgumentException("Serialized string format was wrong.", "serialized", exception);
                }
            }
        }

        private static int IndexOnUpsideDownPosition(int i)
        {
            int row = i / 8;
            int column = i % 8;
            return ((7 - row) * 8) + column;
        }

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
                return this.ByNormalMove(move);
            }
        }

        private Position ByAnPassant(Move move)
        {
            Square opponentPawnSquare = SquareOfAnPassantCapturedPawn(move);
            Move moveToOpponentPawn = Move.FromSquareToSquare(move.From, opponentPawnSquare);
            Move moveToFinalSquare = Move.FromSquareToSquare(opponentPawnSquare, move.To);
            return this.ByNormalMove(moveToOpponentPawn).ByNormalMove(moveToFinalSquare);
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
            PieceType? pieceType = this[move.From].PieceTypeOnSquare;
            bool isPawn = pieceType.HasValue && pieceType.Value == PieceType.Pawn;
            bool isCapturing = move.From.Column != move.To.Column;
            return isPawn && isCapturing;
        }

        private Position ByCastling(Move move)
        {
            Move rookMove = GetRookMoveForCastling(move);
            return this.ByNormalMove(move).ByNormalMove(rookMove);
        }

        private static Move GetRookMoveForCastling(Move move)
        {
            bool kingMovingLeft = move.From.Column < move.To.Column;
            bool kingIsWhite = move.From.Row == 0;
            int rookRow = kingIsWhite ? 0 : 7;
            Square from = Square.FromRowAndColumn(rookRow, kingMovingLeft ? 0 : 7);
            Square to = Square.FromRowAndColumn(rookRow, kingMovingLeft ? 3 : 5);
            return Move.FromSquareToSquare(from, to);
        }

        private bool IsCastling(Move move)
        {
            PieceType? fromPieceType = this[move.From].PieceTypeOnSquare;
            int columnDistance = Math.Abs(move.From.Column - move.To.Column);
            bool isKingMoving = fromPieceType.HasValue && fromPieceType.Value == PieceType.King;
            bool movingMoveThanOneSpace = columnDistance > 1;
            return isKingMoving && movingMoveThanOneSpace;
        }

        private Position ByNormalMove(Move move)
        {
            var thisCopy = this;

            // Overwrite to-space with from-space content, and remove content from from-space.
            var newSquareContent = this.squaresContent
                .Select((content, i) => move.To.SquareIndex == i ? thisCopy[move.From] : content)
                .Select((content, i) => move.From.SquareIndex == i ? SquareContent.GetEmptySquare() : content);
            return FromSquareContents(newSquareContent);
        }

        private static Position GetInitialPosition()
        {
            return FromSquareContents(InitialPositionUtility.InitialSquares);
        }

        private static Position FromSquareContents(IEnumerable<SquareContent> squares)
        {
            return new Position(squares.ToArray());
        }

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

        public override string ToString()
        {
            var rows = this.squaresContent.Select((content, i) => new {Content = content, Index = i})
                .GroupBy((contentI) => contentI.Index / 8).OrderBy(group => group.Key);

            // When iterating over rows, the black rows should be added first (the ones with higher row number).
            var reversedRows = rows.Reverse();

            // Select all characters in group, and concatenate them.
            var stringRows = reversedRows.Select(group => 
                    group.Select(contentI => contentI.Content.ToString())
                        .Aggregate(string.Concat));

            return string.Concat(stringRows);
        }
    }
}