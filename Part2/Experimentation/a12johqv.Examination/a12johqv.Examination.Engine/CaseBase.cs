namespace a12johqv.Examination.Ai
{
    using System;
    using System.Collections.Generic;
    using System.Diagnostics;
    using System.Diagnostics.Contracts;
    using System.Linq;

    using a12johqv.Examination.Chess;
    using a12johqv.Examination.Core;

    /// A group of cases that can be queried to find the case whose problem is most similar to the current situation.
    /// 
    /// The function used to evaluate the simularity of two positions is as follows:
    /// average({F(p[i],q[i]) | i in [0, 64)})
    /// Where p and q are ordered lists of the square content in the two positions,
    /// and F is the square content similarity function defined in SquareContentSimilarity.
    public struct Casebase
    {
        private readonly IReadOnlyList<Case> cases;

        public Casebase(IReadOnlyList<Case> cases)
        {
            this.cases = cases;
        }

        public Case FindMostSimilarCase(Position currentPosition, Color color, Random random, out double similarity)
        {
            Contract.Assert(this.cases.Count > 1, "Needs at least one case to use");

            var caseSimilarities = this.cases
                .Where(@case => @case.Color == color)
                .Select(@case => new
                                     {
                                         Case = @case,
                                         Similarity = Similarity(@case.Position, currentPosition)
                                     });

            double highestSimilarity = Double.MinValue;
            List<Case> bestMatchingCases = new List<Case>();
            foreach (var caseSimilarity in caseSimilarities)
            {
                if (MathUtility.IsGreaterThen(caseSimilarity.Similarity, highestSimilarity))
                {
                    bestMatchingCases.Clear();
                    bestMatchingCases.Add(caseSimilarity.Case);
                    highestSimilarity = caseSimilarity.Similarity;
                }
                else if (MathUtility.AreEqual(caseSimilarity.Similarity, highestSimilarity))
                {
                    bestMatchingCases.Add(caseSimilarity.Case);
                }
            }
            Debug.Assert(bestMatchingCases.Any(), "There has to be at least one case that has a similarity higher than minus infinity");

            similarity = highestSimilarity;
            Contract.Ensures(MathUtility.InRange(similarity, 0, 1), "Similarity has to be in [0,1)");
            return bestMatchingCases[random.Next(maxValue: bestMatchingCases.Count)];
        }

        private static double Similarity(Position a, Position b)
        {
            var squareContentSimilarities = a.SquareContents.Zip(b.SquareContents, Tuple.Create)
                .Select(t => SquareContentSimilarity.Similarity(t.Item1, t.Item2));

            var averageSquareContentSimilarity = squareContentSimilarities.Average();
            Debug.Assert(MathUtility.InRange(averageSquareContentSimilarity, 0, 1), "Similairity should be in [0, 1)");
            return averageSquareContentSimilarity;
        }
    }
}