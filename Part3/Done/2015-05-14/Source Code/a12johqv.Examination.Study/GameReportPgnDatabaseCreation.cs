namespace a12johqv.Examination.Study
{
    using System;
    using System.Collections.Generic;
    using System.Diagnostics;
    using System.Globalization;
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
                               MoveText = CreateMoveText(gameReport.Moves, gameReport.Result)
                           };
            game.AdditionalInfo.Add(new Pgn.GameInfo("DidFollowTimeRequirements", gameReport.DidFollowTimeRequirements.ToString()));
            game.AdditionalInfo.Add(new Pgn.GameInfo("Weights", WeightsToString(gameReport.Weights)));
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
            Move[] validMoves;
            var result = nextPosition.GetResult(out validMoves);
            
            pgnMove.IsCheckMate = result != Result.Draw && result != Result.Undecided;
            pgnMove.IsCheck = !pgnMove.IsCheckMate.Value && position.IsChecked;
            pgnMove.PromotedPiece = move.IsPromotion ? CreatePromotedPiece(move.PromotionType) : null;
            pgnMove.Type = CreateType(position, move);

            // Just to be safe, don't add to much information.
            // Don't know what would happen to the pgn writer if it finds something inconsistent.
            if (pgnMove.Type != Pgn.MoveType.CastleKingSide || pgnMove.Type != Pgn.MoveType.CastleQueenSide)
            {
                pgnMove.OriginSquare = CreateSquare(move.From);
                pgnMove.Piece = CreatePromotedPiece(position[move.From].PieceTypeOnSquare);
                pgnMove.TargetSquare = CreateSquare(move.To);
            }

            return pgnMove;
        }

        private static Pgn.MoveType CreateType(Position position, Move move)
        {
            var castlingDirection = position.GetCastlingDirection(move);
            return castlingDirection.HasValue ? (castlingDirection.Value ? Pgn.MoveType.CastleQueenSide : Pgn.MoveType.CastleKingSide) :
                position.IsAnPassant(move) ? Pgn.MoveType.CaptureEnPassant :
                position.IsCapture(move) ? Pgn.MoveType.Capture : Pgn.MoveType.Simple;
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
    }
}