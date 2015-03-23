namespace a12johqv.Examination.Engine
{
    using System;
    using System.Linq;

    using a12johqv.Examination.Chess;
    using a12johqv.Examination.ChessEngine;

    using ChessCasebase = Casebase<a12johqv.Examination.Chess.Position, a12johqv.Examination.Chess.Move>;
    using MoveAdapter = a12johqv.Examination.ChessEngine.MoveSolutionAdapterUtility;
    using MoveSim = a12johqv.Examination.ChessEngine.MoveSimilarityUtility;
    using PositionSim = a12johqv.Examination.ChessEngine.PositionSimilarityComparerUtility;
    using SquareContentSim = a12johqv.Examination.ChessEngine.SquareContentSimilarityComparerUtility;

    public struct ChessCbrEngine
    {
        private readonly ChessCasebase casebase;

        private static readonly SimilarityComparer<Position> PositionSimilarityComparer =
                PositionSim.CreateSimilarityByAverageOfSquareContentSimilarity(SquareContentSim.SimilarityByEqualColorThenByEqualPieceType);

        public ChessCbrEngine(ChessCasebase casebase)
        {
            this.casebase = casebase;
        }

        public Move DecideMove(Position position, Random random)
        {
            var mostSimilarCase = this.casebase.FindMostSimilarCase(position, PositionSimilarityComparer, random);
            var validMoves = position.ValidMoves.ToArray();
            var adapter = MoveAdapter.CreateAdapterToAdaptToMostSimilarMoveInList(validMoves, GetMoveSimilarityComparer(position));
            return adapter(mostSimilarCase.Solution);
        }

        public static ChessCbrEngine FromCaseBase(ChessCasebase casebase)
        {
            return new ChessCbrEngine(casebase);
        }

        private static SimilarityComparer<Move> GetMoveSimilarityComparer(Position actualPosition)
        {
            SimilarityComparer<Move> squareContentComparer = MoveSim.CreateSimilarityBySquareContentOnFromSquareSimilarity(
                actualPosition,
                SquareContentSim.SimilarityByEqualColorThenByEqualPieceType);
            SimilarityComparer<Move> distanceComparer = MoveSim.SimilarityByInverseManhattanDistanceOfFromAndToSquares;
            return (a, b) => (squareContentComparer(a, b) + distanceComparer(a, b)) / 2.0;
        }
    }
}
