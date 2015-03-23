namespace a12johqv.Examination.Application
{
    using System;
    using System.Collections.Generic;
    using System.Diagnostics;
    using System.Linq;

    using a12johqv.Examination.Engine;

    using Pgn = ilf.pgn.Data;
    using Chess = a12johqv.Examination.Chess;

    public static class PgnDatabaseConversionUtility
    {
        public static Casebase<Chess.Position, Chess.Move> ConvertToCasebaseForPlayer(Pgn.Database pgnDatabase, string playerName)
        {
            var cases = pgnDatabase.Games.SelectMany(game => GetCasesForPlayerInGame(game, playerName));
            return Casebase.FromCases(cases);
        }

        private static IEnumerable<Case<Chess.Position, Chess.Move>> GetCasesForPlayerInGame(Pgn.Game game, string playerName)
        {
            var preTime = DateTime.Now;
            Chess.Position currentPosition = Chess.Position.Initial;
            var currentColor = Chess.Color.White;
            var colorOfPlayer = game.WhitePlayer == playerName ? Chess.Color.White : Chess.Color.Black;

            foreach (Pgn.Move pgnMove in game.MoveText.GetMoves())
            {
                Chess.Move chessMove = ConvertMove(pgnMove, currentPosition);

                if (currentColor == colorOfPlayer)
                {
                    yield return Case.FromProblemAndSolution(currentPosition, chessMove);
                }
                
                currentPosition = currentPosition.ByMove(chessMove);
                currentColor = Chess.ColorUtility.OppositeColor(currentColor);
            }

            var afterTime = DateTime.Now;
            Console.WriteLine("game for player {0} done, time: {1}", playerName, (afterTime.Ticks - preTime.Ticks) / TimeSpan.TicksPerSecond);
        }

        private static Chess.Move ConvertMove(Pgn.Move pgnMove, Chess.Position position)
        {
            Chess.PieceType pieceType = ChessPieceTypeFromPgnMove(pgnMove);
            Pgn.File? file = pgnMove.OriginFile;

            return TryConvertMoveOfPiece(pgnMove, position, pieceType, file).Value;

            //// Due to  a _bug in the pgn parser, a move that starts with a small b
            //// is interpreted as a bishop move, instead of a pawn move from a square on column b.
            //// Therefor, try to do the move as a pawn when doing a bishop move, and try to figure out what it actually is.
            //// If it's impossible to determine, give up and don't use this case.
            //Chess.PieceType pieceType = ChessPieceTypeFromPgnMove(pgnMove);
            //Pgn.File? file = pgnMove.OriginFile;

            //var firstMove = TryConvertMoveOfPiece(pgnMove, position, pieceType, file);
            //var pawnMove = TryConvertMoveOfPiece(pgnMove, position, Chess.PieceType.Pawn, Pgn.File.B);

            //var isBishopMove = pgnMove.Piece.HasValue && pgnMove.Piece.Value == Pgn.PieceType.Bishop;

            //if (!isBishopMove)
            //{
            //    // We're not doing a bishop move, just do the normal move.
            //    return firstMove.Value;
            //}
            //else if (firstMove.HasValue && !pawnMove.HasValue)
            //{
            //    // We are doing a bishop move, and it's guranteed that it's not a pawn move.
            //    return firstMove.Value;
            //}
            //else if (!firstMove.HasValue && pawnMove.HasValue)
            //{
            //    // We think we are doing a bishop move, but it's actually a pawn move.
            //    return pawnMove.Value;
            //}
            //else
            //{
            //    // It is ambigious, just give up
            //    throw new InvalidOperationException("bleh");
            //}
        }

        private static Chess.Move? TryConvertMoveOfPiece(Pgn.Move pgnMove, Chess.Position position, Chess.PieceType pieceType, Pgn.File? file)
        {
            return IsCastling(pgnMove) ? ConvertCastlingMove(pgnMove, position) : TryConvertNormalMoveOfPiece(pgnMove, position, pieceType, file);
        }

        private static Chess.Move ConvertCastlingMove(Pgn.Move pgnMove, Chess.Position position)
        {
            // Castling is represented as king moving two steps to the right or left in Chess.Move.
            const int FromColumn = 4;

            int row = position.CurrentColor == Chess.Color.White ? 0 : 7;
            int toColumn = pgnMove.Type == Pgn.MoveType.CastleQueenSide ? 2 : 6;
            return Chess.Move.FromSquareToSquare(Chess.Square.FromRowAndColumn(row, FromColumn), Chess.Square.FromRowAndColumn(row, toColumn));
        }

        private static bool IsCastling(Pgn.Move pgnMove)
        {
            return pgnMove.Type == Pgn.MoveType.CastleKingSide || pgnMove.Type == Pgn.MoveType.CastleQueenSide;
        }

        private static Chess.Move? TryConvertNormalMoveOfPiece(Pgn.Move pgnMove, Chess.Position position, Chess.PieceType pieceType, Pgn.File? file)
        {
            Chess.Square to = ConvertPgnSquareToChessSquare(pgnMove.TargetSquare);
            int? rank = pgnMove.OriginRank;
            bool ambigiousFile = file.HasValue;
            bool ambigiousRank = rank.HasValue;
            bool reallyAmbigious = pgnMove.OriginSquare != null;

            // Is pawn if no promotion.
            Chess.PieceType promotionPieceType = ChessPieceTypePromotionFromPgnMove(pgnMove);

            var allowedSquares =
                reallyAmbigious ? new[] { ConvertPgnSquareToChessSquare(pgnMove.OriginSquare) } :
                ambigiousFile ? Enumerable.Range(0, 8).Select(i => Chess.Square.FromRowAndColumn(i, (int)file.Value - 1)) :
                ambigiousRank ? Enumerable.Range(0, 8).Select(i => Chess.Square.FromRowAndColumn(rank.Value - 1, i)) :
                Enumerable.Range(0, 64).Select(Chess.Square.FromSquareIndex);

            var validMoves = position.ValidMoves.ToArray();
            var validMovesOfPieceType = validMoves.Where(move => !position[move.From].IsEmpty && position[move.From].PieceTypeOnSquare == pieceType);

            // Always check promotion moves: it's needed when the move is a promotion and doesn't hurt otherwise even if the promotion is nonsensical.
            var moves = allowedSquares
                .Where(square => !position[square].IsEmpty && position[square].PieceTypeOnSquare == pieceType)
                .SelectMany(square => new[] { Chess.Move.FromSquareToSquare(square, to), Chess.Move.FromSquareToSquareWithPromotion(square, to, promotionPieceType) });

            var theCorrectMoves = moves.Where(validMoves.Contains).ToArray();
            if (theCorrectMoves.Count() == 1)
            {
                return theCorrectMoves.First();
            }
            else
            {
                return null;
            }
        }

        private static Chess.PieceType ChessPieceTypeFromPgnMove(Pgn.Move pgnMove)
        {
            bool isPawn = !pgnMove.Piece.HasValue;
            if (!isPawn)
            {
                // The members in Chess.PieceType and PgnPieceType have the same names by coincidence.
                Chess.PieceType pieceType;
                Enum.TryParse(pgnMove.Piece.Value.ToString(), out pieceType);
                return pieceType;
            }
            else
            {
                return Chess.PieceType.Pawn;
            }
        }

        private static Chess.PieceType ChessPieceTypePromotionFromPgnMove(Pgn.Move pgnMove)
        {
            bool isPromotion = pgnMove.PromotedPiece.HasValue;
            if (isPromotion)
            {
                // The members in Chess.PieceType and PgnPieceType have the same names by coincidence.
                Chess.PieceType pieceType;
                Enum.TryParse(pgnMove.PromotedPiece.Value.ToString(), out pieceType);
                return pieceType;
            }
            else
            {
                return Chess.PieceType.Pawn;
            }
        }

        private static Chess.Move ConvertPawnMove(Pgn.Move pgnMove, Chess.Position position)
        {
            Chess.Square to = ConvertPgnSquareToChessSquare(pgnMove.TargetSquare);
            Pgn.File? file = pgnMove.OriginFile;

            int offsetForward = position.MovementEvents.NextMoveColor == Chess.Color.White ? 1 : -1;
            bool isAbsolutelyCapture = file.HasValue && (int)file.Value != to.Column;
            bool isAbsolutelyForward = file.HasValue && (int)file.Value == to.Column;
            bool isAbsolutelyFromLeft = file.HasValue && (int)file.Value < to.Column;
            bool isAbsolutelyFromRight = file.HasValue && (int)file.Value > to.Column;

            // Try one step forward.
            // Need to check file, to make sure move isn't a capture.
            {
                if (!isAbsolutelyCapture)
                {
                    Chess.Square from = Chess.Square.FromRowAndColumn(to.Row - offsetForward, to.Column);
                    if (HasSameColor(position, from, Chess.PieceType.Pawn))
                    {
                        return Chess.Move.FromSquareToSquare(from, to);
                    }
                }
            }

            // Try two steps forward
            // Need to check file, to make sure move isn't a capture.
            // Check after one step check, to make sure pawn isnt blocked by another pawn straight forward.
            {
                if (!isAbsolutelyCapture)
                {
                    Chess.Square from = Chess.Square.FromRowAndColumn(to.Row - (offsetForward * 2), to.Column);
                    if (HasSameColor(position, from, Chess.PieceType.Pawn))
                    {
                        return Chess.Move.FromSquareToSquare(from, to);
                    }
                }
            }

            // Try (from) diagonal forward left
            {
                if (!isAbsolutelyFromRight)
                {
                    Chess.Square from = Chess.Square.FromRowAndColumn(to.Row - offsetForward, to.Column - (-1));
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
                    Chess.Square from = Chess.Square.FromRowAndColumn(to.Row - offsetForward, to.Column - 1);
                    if (HasSameColor(position, from, Chess.PieceType.Pawn))
                    {
                        return Chess.Move.FromSquareToSquare(from, to);
                    }
                }
            }

            Debug.Assert(false, "No pawn moves were valid");
            throw new Exception();
        }

        private static bool HasSameColor(Chess.Position position, Chess.Square square, Chess.PieceType pieceType)
        {
            Chess.SquareContent content = position[square];
            return !content.IsEmpty && content.ColorOnSquare == position.CurrentColor && content.PieceTypeOnSquare == pieceType;
        }

        private static Chess.Square ConvertPgnSquareToChessSquare(Pgn.Square pgnSquare)
        {
            return Chess.Square.FromRowAndColumn(row: pgnSquare.Rank - 1, column: (int)pgnSquare.File - 1);
        }
    }

    public class PgnMoveConversionFailureException : Exception
    {
        public Chess.Position Position { get; private set; }

        public Pgn.Move PgnMove { get; private set; }

        public PgnMoveConversionFailureException(Chess.Position position, Pgn.Move pgnMove)
            : base(string.Format("Failed conversion with position ({0}) and move {1}", position, pgnMove))
        {
            this.Position = position;
            this.PgnMove = pgnMove;
        }
    }
}