namespace a12johqv.Examination.Application
{
    using System;
    using System.Collections.Generic;
    using System.Linq;

    using a12johqv.Examination.Chess;
    using a12johqv.Examination.Engine;

    using ilf.pgn.Data;

    using ChessColor = a12johqv.Examination.Chess.Color;
    using ChessMove = a12johqv.Examination.Chess.Move;
    using ChessSquare = a12johqv.Examination.Chess.Square;
    using PgnMove = ilf.pgn.Data.Move;
    using PgnSquare = ilf.pgn.Data.Square;
    using PieceType = ilf.pgn.Data.PieceType;

    public static class PgnDatabaseCasebaseConversionUtility
    {
        public static Casebase<Position, ChessMove> ConvertToCasebaseForPlayer(Database pgnDatabase, string playerName)
        {
            var cases = pgnDatabase.Games.SelectMany(game => GetCasesForPlayerInGame(game, playerName));
            return Casebase.FromCases(cases);
        }

        private static IEnumerable<Case<Position, ChessMove>> GetCasesForPlayerInGame(Game game, string playerName)
        {
            Position currentPosition = Position.Initial;
            ChessColor currentColor = ChessColor.White;
            var colorOfPlayer = game.WhitePlayer == playerName ? ChessColor.White : ChessColor.Black;

            foreach (PgnMove pgnMove in game.MoveText.GetMoves())
            {
                ChessMove chessMove = SolveMove(pgnMove, currentPosition);
                if (currentColor == colorOfPlayer)
                {
                    yield return Case.FromProblemAndSolution(currentPosition, chessMove);
                }
                
                currentPosition = currentPosition.ByMove(chessMove);
                currentColor = currentColor.OppositeColor();
            }
        }

        

        private static IEnumerable<TElement> SkipEvery<TElement>(this IEnumerable<TElement> sequence, int step)
        {
            int i = 0;
            var it = sequence.GetEnumerator();
            while (true)
            {
                bool hasMore = it.MoveNext();
                i++;
                if (!hasMore)
                {
                    yield break;
                }
                else if (i == step)
                {
                    i = 0;
                }
                else
                {
                    yield return it.Current;
                }
            }
        }
    }
}
