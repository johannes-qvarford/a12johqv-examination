namespace a12johqv.Examination.Application
{
    using System;
    using System.Diagnostics;
    using System.Linq;

    using a12johqv.Examination.Chess;

    using ilf.pgn.Data;

    using ChessColor = a12johqv.Examination.Chess.Color;
    using ChessMove = a12johqv.Examination.Chess.Move;
    using ChessSquare = a12johqv.Examination.Chess.Square;
    using PgnMove = ilf.pgn.Data.Move;
    using PgnPieceType = ilf.pgn.Data.PieceType;
    using PgnSquare = ilf.pgn.Data.Square;
    using PieceType = a12johqv.Examination.Chess.PieceType;

    public class MoveConvertingUtility
    {
        public static ChessMove ConvertMove(PgnMove pgnMove, Position position)
        {
            if (IsPawnMove(pgnMove, position))
            {
                return ConvertPawnMove(pgnMove, position);
            }
            else if (IsRookMove(pgnMove, position))
            {
                return ConvertRookMove(pgnMove, position);
            }
            return ChessMove.FromSquareToSquare(
                ConvertPgnSquareToChessSquare(pgnMove.OriginSquare),
                ConvertPgnSquareToChessSquare(pgnMove.TargetSquare));
        }

        private static bool IsRookMove(PgnMove pgnMove, Position position)
        {
            return pgnMove.Piece.HasValue && pgnMove.Piece.Value == PgnPieceType.Rook;
        }

        private static Chess.Move ConvertRookMove(PgnMove pgnMove, Position position)
        {
            ChessSquare to = ConvertPgnSquareToChessSquare(pgnMove.TargetSquare);
            File? file = pgnMove.OriginFile;


            bool isAbsolutelyVertical = pgnMove.OriginRank.HasValue;
            bool isAbsolutelyHorizontal = pgnMove.OriginFile.HasValue;
            bool isAmbigiousOrigin = pgnMove.OriginSquare != null;

            var rookRowsOnColumn = Enumerable.Range(0, 8).Where(i => HasSameColor(position, Chess.Square.FromRowAndColumn(i, to.Column), PieceType.Rook)).ToArray();
            int rookCountOnColumn = rookRowsOnColumn.Count();
            bool hasRookOnColumn = rookCountOnColumn > 0;

            var rookColumnsOnRow = Enumerable.Range(0, 8).Where(i => HasSameColor(position, Chess.Square.FromRowAndColumn(to.Row, i), PieceType.Rook)).ToArray();
            int rookCountOnRow = rookColumnsOnRow.Count();
            bool hasRookOnRow = rookCountOnRow > 0;

            if (isAmbigiousOrigin)
            {
                ChessSquare from = ConvertPgnSquareToChessSquare(pgnMove.OriginSquare);
                return Chess.Move.FromSquareToSquare(from, to);
            }

            // Try a vertical move.
            {
                if (!isAbsolutelyHorizontal && hasRookOnColumn)
                {
                    Debug.Assert(rookCountOnColumn == 1, "If there were more rooks on the column, I assumed the origin would be ambigious");
                    Chess.Square from = Chess.Square.FromRowAndColumn(rookRowsOnColumn.Single(), to.Column);
                    return Chess.Move.FromSquareToSquare(from, to);
                }
            }

            // Try a horizontal move.
            {
                Debug.Assert(!isAbsolutelyVertical && hasRookOnRow, "Has to be horizontal move if not vertical");
                Debug.Assert(rookCountOnRow == 1, "If there were more rooks on the row, I assumed the origin would be ambigious");
                Chess.Square from = Chess.Square.FromRowAndColumn(rookColumnsOnRow.Single(), to.Column);
                return Chess.Move.FromSquareToSquare(from, to);
            }
        }

        private static bool IsPawnMove(PgnMove pgnMove, Position position)
        {
            return pgnMove.Piece.HasValue && pgnMove.Piece.Value == PgnPieceType.Pawn;
        }

        private static ChessMove ConvertPawnMove(PgnMove pgnMove, Position position)
        {
            ChessSquare to = ConvertPgnSquareToChessSquare(pgnMove.TargetSquare);
            File? file = pgnMove.OriginFile;

            int offsetForward = position.MovementEvents.NextMoveColor == ChessColor.White ? 1 : -1;
            bool isAbsolutelyCapture = file.HasValue && (int)file.Value != to.Column;
            bool isAbsolutelyForward = file.HasValue && (int)file.Value == to.Column;
            bool isAbsolutelyFromLeft = file.HasValue && (int)file.Value < to.Column;
            bool isAbsolutelyFromRight = file.HasValue && (int)file.Value > to.Column;

            // Try one step forward.
            // Need to check file, to make sure move isn't a capture.
            {
                if (!isAbsolutelyCapture)
                {
                    ChessSquare from = ChessSquare.FromRowAndColumn(to.Row - offsetForward, to.Column);
                    if (HasSameColor(position, from, Chess.PieceType.Pawn))
                    {
                        return ChessMove.FromSquareToSquare(from, to);
                    }
                }
            }

            // Try two steps forward
            // Need to check file, to make sure move isn't a capture.
            // Check after one step check, to make sure pawn isnt blocked by another pawn straight forward.
            {
                if (!isAbsolutelyCapture)
                {
                    ChessSquare from = ChessSquare.FromRowAndColumn(to.Row - (offsetForward * 2), to.Column);
                    if (HasSameColor(position, from, Chess.PieceType.Pawn))
                    {
                        return ChessMove.FromSquareToSquare(from, to);
                    }
                }
            }

            // Try (from) diagonal forward left
            {
                if (!isAbsolutelyFromRight)
                {
                    ChessSquare from = Chess.Square.FromRowAndColumn(to.Row - offsetForward, to.Column - (-1));
                    if (HasSameColor(position, from, Chess.PieceType.Pawn))
                    {
                        return Chess.Move.FromSquareToSquare(from, to);
                    }
                }
            }

            // Try (from) diagonal forward right
            {
                if (!isAbsolutelyFromLeft)
                {
                    ChessSquare from = Chess.Square.FromRowAndColumn(to.Row - offsetForward, to.Column - 1);
                    if (HasSameColor(position, from, Chess.PieceType.Pawn))
                    {
                        return Chess.Move.FromSquareToSquare(from, to);
                    }
                }
            }

            Debug.Assert(false, "No pawn moves were valid");
            throw new Exception();
        }

        private static bool Has(Position position, Chess.Square square, Chess.Color color, Chess.PieceType pieceType)
        {
            SquareContent content = position[square];
            return !content.IsEmpty && content.ColorOnSquare == position.CurrentColor && content.PieceTypeOnSquare == pieceType;
        }

        private static bool HasSameColor(Position position, Chess.Square square, Chess.PieceType pieceType)
        {
            return Has(position, square, position.CurrentColor, pieceType);
        }

        private static ChessSquare? MaybeSquare(int row, int column)
        {
            try
            {
                return ChessSquare.FromRowAndColumn(row, column);
            }
            catch (ArgumentException)
            {
                return null;
                throw;
            }
        }

        private static ChessSquare ConvertPgnSquareToChessSquare(PgnSquare pgnSquare)
        {
            return ChessSquare.FromRowAndColumn(row: pgnSquare.Rank, column: (int)pgnSquare.File);
        }
    }
}
