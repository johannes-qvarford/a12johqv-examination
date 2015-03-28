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
    /// w1 * I(m, n) + w2 * C(m, n)
    /// Where w1 and w2 are weights, m and n are moves,
    /// I is the inverse distance similarity of the moves and C is content similarity of the moves.
    /// 
    /// The content similarity of the moves is defined as:
    /// w3 * F(p[m.From], c[n.From]) + w4 * F(p[m.To], c[n.To])
    /// Where w3 and w4 are weights, p is the current position, c is the case position,
    /// and F is the similarity function for square content defined in SquareContentSimilarity.
    /// 
    /// The inverse distance similarity of the moves is defined as:
    /// w5 * (1 - (G(m.From, n.From) + G(m.To, n.To)))
    /// where w5 is a weight, and G is the normalized manhattan distance of squares.
    /// 
    /// normalized Manhattan distance of squares is defined as:
    /// (D(s.Column, t.Column) + D(s.Row, t.Row)) / 14.0
    /// where s and t are squares,
    /// and D is the absolute difference of the given numbers.
    public static class MoveAdaption
    {
        public static Move AdaptToOneOfPossibleMoves(Position currentPosition, Case @case, IReadOnlyList<Move> possibleMoves, out double similarity)
        {
            Debug.Assert(possibleMoves != null && possibleMoves.Count > 0, "Cannot adopt to a move if there are no possibilities.");

            if (possibleMoves.Count == 1)
            {
                similarity = MoveSimilarity(@case.Position, currentPosition, @case.Move, possibleMoves.First());
                return possibleMoves.First();
            }
            else
            {
                var moveSimilarityPair = possibleMoves
                    .Select(move => new { Move = move, Similarity = MoveSimilarity(@case.Position, currentPosition, @case.Move, move) })
                    .Aggregate((bestPair, newPair) => bestPair.Similarity >= newPair.Similarity ? bestPair : newPair);
                similarity = moveSimilarityPair.Similarity;
                return moveSimilarityPair.Move;
            }
        }

        private static double MoveSimilarity(Position positionA, Position positionB, Move a, Move b)
        {
            const double DistanceWeight = 0.8;
            const double SquareWeight = 0.2;
            Debug.Assert(MathUtility.AreEqual(DistanceWeight + SquareWeight, 1), "Weights has to add up to 1");

            var distanceSimilarity = MoveSimilarityByInverseDistance(a, b);
            var squareSimilarity = MoveSimilarityBySquareContentOnSourceAndTargetSquares(positionA, positionB, a, b);

            var similarity = (distanceSimilarity * DistanceWeight) + (squareSimilarity * SquareWeight);
            Debug.Assert(MathUtility.InRange(similarity, 0, 1), "Similarity has to be in range");
            return similarity;
        }

        private static double MoveSimilarityBySquareContentOnSourceAndTargetSquares(Position positionA, Position positionB, Move a, Move b)
        {
            const double SourceWeight = 0.7;
            const double TargetWeight = 0.3;

            var sourceA = positionA[a.From];
            var sourceB = positionB[b.From];
            var targetA = positionA[a.To];
            var targetB = positionA[b.To];
            return (SourceWeight * SquareContentSimilarity.Similarity(sourceA, sourceB)) + (TargetWeight * SquareContentSimilarity.Similarity(targetA, targetB));
        }

        private static double MoveSimilarityByInverseDistance(Move a, Move b)
        {
            const double FromWeight = 0.5;
            const double ToWeight = 0.5;

            Debug.Assert(MathUtility.AreEqual(FromWeight + ToWeight, 1), "Multiplier weights should add up to 1, so the following is true: a,b in [0,1) -> a*w1+b*w2 in [0,1)");

            double fromDistance = NormalizedManhattanDistanceOfSquares(a.From, b.From);
            double toDistance = NormalizedManhattanDistanceOfSquares(a.To, b.To);
            Debug.Assert(MathUtility.InRange(fromDistance, 0, 1), "Normalized implies in [0,1)");
            Debug.Assert(MathUtility.InRange(toDistance, 0, 1), "Normalized implies in [0,1)");

            double distanceSum = (fromDistance * FromWeight) + (toDistance * ToWeight);
            Debug.Assert(MathUtility.InRange(distanceSum, 0, 1), "Given correctly distributed weights, the sum should be in [0, 1)");
            double inverseOfDistanceSum = Inverse(distanceSum);

            Contract.Ensures(MathUtility.InRange(inverseOfDistanceSum, 0, 1), "Similarity has to be in [0, 1)");
            return inverseOfDistanceSum;
        }

        private static double NormalizedManhattanDistanceOfSquares(Square a, Square b)
        {
            const int MaxManhattanDistance = 14;
            return (MathUtility.Distance(a.Column, b.Column) + MathUtility.Distance(a.Row, b.Row)) / MaxManhattanDistance;
        }

        private static double Inverse(double zeroToOne)
        {
            return 1 - zeroToOne;
        }
    }
}