namespace a12johqv.Examination.Study
{
    using System;
    using System.Collections.Generic;
    using System.Diagnostics;
    using System.Globalization;
    using System.IO;
    using System.Linq;

    using a12johqv.Examination.Ai;
    using a12johqv.Examination.Chess;

    using ilf.pgn.Data.MoveText;

    using Pgn = ilf.pgn.Data;

    /// Utility functions for creating pgn databases from game reports.
    public static class GameReportPgnDatabaseCreation
    {
        public static Pgn.Database CreateDatabase(IEnumerable<GameReport> gameReports)
        {
            var database = new Pgn.Database();
            database.Games.AddRange(gameReports.Select(CreateGame));
            return database;
        }

        private static Pgn.Game CreateGame(GameReport gameReport)
        {
            var game = new Pgn.Game
                           {
                               Event = "a12johqv 2015 Examination",
                               Site = "Högskolan i Skövde",
                               Round = gameReport.Round.ToString(CultureInfo.InvariantCulture),
                               Year = DateTime.Now.Year,
                               Month = DateTime.Now.Month,
                               Day = DateTime.Now.Day,
                               Result = ConvertChessToPgnResult(gameReport.Result),
                               WhitePlayer = gameReport.WhitePlayerName,
                               BlackPlayer = gameReport.BlackPlayerName,
                               MoveText = CreateMoveText(gameReport.Moves, gameReport.Result),
                           };
            game.AdditionalInfo.Add(new Pgn.GameInfo("DidFollowTimeRequirements", gameReport.DidFollowTimeRequirements.ToString()));
            game.AdditionalInfo.Add(new Pgn.GameInfo("Weights", WeightsToString(gameReport.Weights)));
            game.AdditionalInfo.Add(new Pgn.GameInfo("WhiteElo", gameReport.WhitePlayerRating.ToString(CultureInfo.InvariantCulture)));
            game.AdditionalInfo.Add(new Pgn.GameInfo("BlackElo", gameReport.BlackPlayerRating.ToString(CultureInfo.InvariantCulture)));
            game.AdditionalInfo.Add(new Pgn.GameInfo("ECO", "?"));
            return game;
        }

        private static string WeightsToString(Weights weights)
        {
            return string.Format(
                CultureInfo.InvariantCulture,
                "{0} {1} {2}",
                weights.MoveWeight,
                weights.DistanceWeight,
                weights.SquareContentWeight);
        }

        private static MoveTextEntryList CreateMoveText(IEnumerable<Move> moves, Chess.Result result)
        {
            var position = Position.Initial;
            var moveTextEntryList = new MoveTextEntryList();
            var it = moves.GetEnumerator();

            int moveNumber = 1;
            while (true)
            {
                if (!it.MoveNext())
                {
                    moveTextEntryList.Add(new Pgn.GameEndEntry(ConvertChessToPgnResult(result)));
                    return moveTextEntryList;
                }
                else
                {
                    var first = it.Current;
                    Position nextPosition = position.ByMove(first);
                    if (!it.MoveNext())
                    {
                        moveTextEntryList.Add(CreateHalfMoveTextEntry(position, nextPosition, first, moveNumber));
                        moveTextEntryList.Add(new Pgn.GameEndEntry(ConvertChessToPgnResult(result)));
                        return moveTextEntryList;
                    }
                    else
                    {
                        var second = it.Current;
                        Position afterNextPosition = nextPosition.ByMove(second);
                        moveTextEntryList.Add(CreateSingleMoveTextEntry(position, nextPosition, afterNextPosition, first, second, moveNumber));
                        position = afterNextPosition;
                    }
                }
                moveNumber++;
            }
        }

        private static Pgn.MoveTextEntry CreateHalfMoveTextEntry(Position position, Position nextPosition, Move move, int moveNumber)
        {
            return new Pgn.HalfMoveEntry(CreateMove(position, nextPosition, move)) { MoveNumber = moveNumber };
        }

        private static Pgn.MoveTextEntry CreateSingleMoveTextEntry(Position position, Position nextPosition, Position afterNextPosition, Move first, Move second, int moveNumber)
        {
            var whiteMove = CreateMove(position, nextPosition, first);
            var blackMove = CreateMove(nextPosition, afterNextPosition, second);
            return new Pgn.MovePairEntry(whiteMove, blackMove) { MoveNumber = moveNumber };
        }

        private static Pgn.Move CreateMove(Position position, Position nextPosition, Move move)
        {
            var pgnMove = new Pgn.Move();
            Move[] nextPositionValidMoves;
            var nextResult = nextPosition.GetResult(out nextPositionValidMoves);
            Move[] validMoves;
            var result = position.GetResult(out validMoves);
            
            pgnMove.IsCheckMate = nextResult != Result.Draw && nextResult != Result.Undecided;
            pgnMove.IsCheck = !pgnMove.IsCheckMate.Value && nextPosition.IsChecked;
            pgnMove.PromotedPiece = move.IsPromotion ? CreatePromotedPiece(move.PromotionType) : null;
            pgnMove.Type = CreateType(position, move);

            // Just to be safe, don't add to much information.
            // Don't know what would happen to the pgn writer if it finds something inconsistent.
            if (pgnMove.Type != Pgn.MoveType.CastleKingSide || pgnMove.Type != Pgn.MoveType.CastleQueenSide)
            {
                var ambiguity = OriginAmbiguity(position, validMoves, move);
                bool isCapturingPawn = position[move.From].PieceTypeOnSquare == PieceType.Pawn && pgnMove.Type == Pgn.MoveType.Capture;

                Debug.Assert(!isCapturingPawn || ambiguity != Ambiguity.DifferentRowsAndColumns, "Both the row and column cannot be ambigious for pawn capture");

                pgnMove.OriginSquare = ambiguity == Ambiguity.DifferentRowsAndColumns ? CreateSquare(move.From) : null;

                // If some have same row, then the column determines which should move.
                // Always include a file for a capturing pawn.
                pgnMove.OriginFile = ambiguity == Ambiguity.SomeShareSameRow || isCapturingPawn ? ColumnToFile(move.From.Column) : (Pgn.File?)null;
                pgnMove.OriginRank = ambiguity == Ambiguity.SomeShareSameColumn ? RowToRank(move.From.Row) : (int?)null;
                pgnMove.Piece = CreatePromotedPiece(position[move.From].PieceTypeOnSquare);
                pgnMove.TargetSquare = CreateSquare(move.To);
            }

            return pgnMove;
        }

        private static int RowToRank(int row)
        {
            return row + 1;
        }

        private static Pgn.File ColumnToFile(int column)
        {
            return (Pgn.File)column + 1;
        }

        private static Ambiguity OriginAmbiguity(Position position, IEnumerable<Move> validMoves, Move move)
        {
            var type = position[move.From].PieceTypeOnSquare;

            // Truncate promotion moves with same source and target to just one of the moves.
            // Then select moves to same target as move using the same type of piece.
            var possibleMoves = validMoves
                .GroupBy(validMove => new { validMove.From, validMove.To })
                .Select(gr => gr.First())
                .Where(validMove => position[validMove.From].PieceTypeOnSquare == type && validMove.To.Equals(move.To))
                .ToArray();
            
            if (possibleMoves.Count() == 1)
            {
                return Ambiguity.None;
            }
            else
            {
                var someShareRows = possibleMoves.Select(possibleMove => possibleMove.From.Row).Distinct().Count() != possibleMoves.Count();
                var someShareColumns = possibleMoves.Select(possibleMove => possibleMove.From.Column).Distinct().Count() != possibleMoves.Count();
                return someShareRows && someShareColumns ? Ambiguity.DifferentRowsAndColumns : someShareRows ? Ambiguity.SomeShareSameRow : Ambiguity.SomeShareSameColumn;
            }
        }

        private static Pgn.MoveType CreateType(Position position, Move move)
        {
            var castlingDirection = position.GetCastlingDirection(move);

            // Ignore giving information about en passant, because it appends an optional suffix to the moves which some visualization software cannot parse.
            // The move is still interpreted correctly if it's a capture to the moving pawn's destination square.
            return castlingDirection.HasValue ? (castlingDirection.Value ? Pgn.MoveType.CastleQueenSide : Pgn.MoveType.CastleKingSide) :
                position.IsAnPassant(move) || position.IsCapture(move) ? Pgn.MoveType.Capture : Pgn.MoveType.Simple;
        }

        private static Pgn.PieceType? CreatePromotedPiece(Chess.PieceType promotionType)
        {
            Pgn.PieceType pieceType;
            if (Enum.TryParse(promotionType.ToString(), out pieceType))
            {
                return pieceType;
            }
            else
            {
                return null;
            }
        }

        private static Pgn.Square CreateSquare(Square square)
        {
            return new Pgn.Square(file: (Pgn.File)(square.Column + 1), rank: square.Row + 1);
        }

        private static Pgn.GameResult ConvertChessToPgnResult(Chess.Result result)
        {
            return result == Chess.Result.BlackVictory ? Pgn.GameResult.Black :
                result == Chess.Result.Draw ? Pgn.GameResult.Draw :
                result == Chess.Result.WhiteVictory ? Pgn.GameResult.White : Pgn.GameResult.Open;
        }

        private enum Ambiguity
        {
            DifferentRowsAndColumns,
            SomeShareSameRow,
            SomeShareSameColumn,
            None,
        }
    }
}