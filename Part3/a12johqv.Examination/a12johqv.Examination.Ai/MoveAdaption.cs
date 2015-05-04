namespace a12johqv.Examination.Ai
{
    using System.Collections.Generic;
    using System.Diagnostics;
    using System.Diagnostics.Contracts;
    using System.Linq;

    using a12johqv.Examination.Chess;
    using a12johqv.Examination.Core;

    /// Utility functions for move adaptions.
    /// It's very discrete which moves can and cannot be performed in chess.
    /// Also, since the domain of valid moves is much smaller than the domain of expressive moves
    /// adaption is only towards any of the valid moves.
    /// The move to adapt to is the one whose most similar to the move in the case.
    /// Similarity is expressed as follows:
    /// w1 * I(m, n) + (1 - w1) * C(m, n)
    /// Where w1 is a weight, m and n are moves,
    /// I is the inverse distance similarity of the moves and C is content similarity of the moves.
    /// 
    /// The content similarity of the moves is defined as:
    /// w2 * F(p[m.From], c[n.From]) + (1 - w2) * F(p[m.To], c[n.To])
    /// Where w3 and w4 are weights, p is the current barePosition, c is the case barePosition,
    /// and F is the similarity function for square content defined in SquareContentSimilarity.
    /// 
    /// The inverse distance similarity of the moves is defined as:
    /// (1 - (w3 * G(m.From, n.From) + (1 - w3) * G(m.To, n.To)))
    /// where w5 and w6 are weights, and G is the normalized manhattan distance of squares.
    /// 
    /// normalized Manhattan distance of squares is defined as:
    /// (D(s.Column, t.Column) + D(s.Row, t.Row)) / 14.0
    /// where s and t are squares,
    /// and D is the absolute difference of the given numbers.
    public static class MoveAdaption
    {
        public static Move AdaptToOneOfPossibleMoves(BarePosition currentPosition, Case @case, IReadOnlyList<Move> possibleMoves, Weights weights)
        {
            Debug.Assert(possibleMoves != null && possibleMoves.Count > 0, "Cannot adopt to a move if there are no possibilities.");

            if (possibleMoves.Count == 1)
            {
                return possibleMoves.First();
            }
            else
            {
                var moveSimilarityPair = possibleMoves
                    .Select(move => new { Move = move, Similarity = MoveSimilarity(@case.BarePosition, currentPosition, @case.Move, move, weights) })
                    .Aggregate((bestPair, newPair) => bestPair.Similarity >= newPair.Similarity ? bestPair : newPair);
                return moveSimilarityPair.Move;
            }
        }

        private static double MoveSimilarity(BarePosition positionA, BarePosition positionB, Move a, Move b, Weights weights)
        {
            var distanceSimilarity = MoveSimilarityByInverseDistance(a, b, weights);
            var squareSimilarity = MoveSimilarityBySquareContentOnSourceAndTargetSquares(positionA, positionB, a, b, weights);

            var similarity = (distanceSimilarity * weights.MoveWeight) + (squareSimilarity * (1 - weights.MoveWeight));
            Debug.Assert(MathUtility.InRange(similarity, 0, 1), "Similarity has to be in range");
            return similarity;
        }

        private static double MoveSimilarityBySquareContentOnSourceAndTargetSquares(BarePosition positionA, BarePosition positionB, Move a, Move b, Weights weights)
        {
            var sourceA = positionA[a.From];
            var sourceB = positionB[b.From];
            var targetA = positionA[a.To];
            var targetB = positionA[b.To];
            return (weights.SquareContentWeight * SquareContentSimilarity.Similarity(sourceA, sourceB))
                + ((1 - weights.SquareContentWeight) * SquareContentSimilarity.Similarity(targetA, targetB));
        }

        private static double MoveSimilarityByInverseDistance(Move a, Move b, Weights weights)
        {
            double fromDistance = SquareDistance.NormalizedManhattanDistance(a.From, b.From);
            double toDistance = SquareDistance.NormalizedManhattanDistance(a.To, b.To);

            double distanceSum = (fromDistance * weights.DistanceWeight) + (toDistance * (1 - weights.DistanceWeight));
            double inverseOfDistanceSum = Inverse(distanceSum);

            Contract.Ensures(MathUtility.InRange(inverseOfDistanceSum, 0, 1), "Similarity has to be in [0, 1)");
            return inverseOfDistanceSum;
        }

        private static double Inverse(double zeroToOne)
        {
            return 1 - zeroToOne;
        }
    }
}