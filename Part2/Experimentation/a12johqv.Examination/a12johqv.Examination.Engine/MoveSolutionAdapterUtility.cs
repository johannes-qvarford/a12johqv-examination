namespace a12johqv.Examination.ChessEngine
{
    using System.Collections;
    using System.Collections.Generic;
    using System.Diagnostics.Contracts;
    using System.Linq;

    using a12johqv.Examination.Chess;

    public static class MoveSolutionAdapterUtility
    {
        public static SolutionAdapter<Move> CreateAdapterToAdaptToMostSimilarMoveInList(IList<Move> moves, SimilarityComparer<Move> comparer)
        {
            Contract.Assert(moves.Any());
            return (moveToAdapt) =>
                {
                    if (moves.Count == 1)
                    {
                        return moves.First();
                    }
                    else
                    {
                        return moves.Select(@move => new { Move = move, Similarity = comparer(move, moveToAdapt) })
                            .Aggregate((bestPair, newPair) => bestPair.Similarity > newPair.Similarity ? bestPair : newPair)
                            .Move;
                    }
                };
        }

    }
}
