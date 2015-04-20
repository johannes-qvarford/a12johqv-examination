namespace a12johqv.Examination.Study
{
    using System;
    using System.Collections.Generic;
    using System.Collections.Immutable;
    using System.Diagnostics;
    using System.Linq;

    using a12johqv.Examination.Ai;

    using Chess = Chess;
    using Pgn = ilf.pgn.Data;

    /// Utility functions for converting from pgn databases to casebases.
    public static class PgnDatabaseToCasebaseConversion
    {
        public static Casebase ConvertToCasebaseForPlayer(Pgn.Database pgnDatabase, string playerName)
        {
            var cases = pgnDatabase.Games.AsParallel().AsSequential().SelectMany(game => GetCasesForPlayerInGame(game, playerName));
            return new Casebase(ImmutableList.CreateRange(cases));
        }

        private static IEnumerable<Case> GetCasesForPlayerInGame(Pgn.Game game, string playerName)
        {
            Chess.Position currentPosition = Chess.Position.Initial;
            var currentColor = Chess.Color.White;

            Debug.Assert(
                game.WhitePlayer == playerName || game.BlackPlayer == playerName,
                "We have to be able to identify which color the player is playing as.");
            var colorOfPlayer = game.WhitePlayer == playerName ? Chess.Color.White : Chess.Color.Black;

            foreach (Pgn.Move pgnMove in game.MoveText.GetMoves())
            {
                Chess.Move chessMove = ConvertMove(pgnMove, currentPosition);

                if (currentColor == colorOfPlayer)
                {
                    yield return new Case(currentPosition.BarePosition, chessMove, colorOfPlayer);
                }
                
                currentPosition = currentPosition.ByMove(chessMove);
                currentColor = Chess.ColorUtility.OppositeColor(currentColor);
            }
        }

        private static Chess.Move ConvertMove(Pgn.Move pgnMove, Chess.Position position)
        {
            Chess.PieceType pieceType = ChessPieceTypeFromPgnMove(pgnMove);
            Pgn.File? file = pgnMove.OriginFile;

            return TryConvertMoveOfPiece(pgnMove, position, pieceType, file);
        }

        private static Chess.Move TryConvertMoveOfPiece(Pgn.Move pgnMove, Chess.Position position, Chess.PieceType pieceType, Pgn.File? file)
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

        private static Chess.Move TryConvertNormalMoveOfPiece(Pgn.Move pgnMove, Chess.Position position, Chess.PieceType pieceType, Pgn.File? file)
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
                ambigiousFile ? Enumerable.Range(0, 8).Select(i => Chess.Square.FromRowAndColumn(i, FileToColumn(file.Value))) :
                ambigiousRank ? Enumerable.Range(0, 8).Select(i => Chess.Square.FromRowAndColumn(RankToRow(rank.Value), i)) :
                Enumerable.Range(0, 64).Select(Chess.Square.FromSquareIndex);

            var validMoves = position.ValidMoves.ToArray();

            // Always check promotion moves: it's needed when the move is a promotion and doesn't hurt otherwise even if the promotion is nonsensical.
            var moves = allowedSquares
                .Where(square => !position[square].IsEmpty && position[square].PieceTypeOnSquare == pieceType)
                .SelectMany(square => new[] { Chess.Move.FromSquareToSquare(square, to), Chess.Move.FromSquareToSquareWithPromotion(square, to, promotionPieceType) });

            return moves.Single(validMoves.Contains);
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

        private static bool HasSameColor(Chess.Position position, Chess.Square square, Chess.PieceType pieceType)
        {
            Chess.SquareContent content = position[square];
            return !content.IsEmpty && content.ColorOnSquare == position.CurrentColor && content.PieceTypeOnSquare == pieceType;
        }

        private static Chess.Square ConvertPgnSquareToChessSquare(Pgn.Square pgnSquare)
        {
            return Chess.Square.FromRowAndColumn(row: pgnSquare.Rank - 1, column: (int)pgnSquare.File - 1);
        }

        private static int RankToRow(int rank)
        {
            return rank - 1;
        }

        private static int FileToColumn(Pgn.File file)
        {
            return (int)file - 1;
        }
    }
}