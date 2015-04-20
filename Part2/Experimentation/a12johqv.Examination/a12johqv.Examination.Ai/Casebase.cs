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
    /// S = w7 * C(p, q) + w8 * I(p, q)
    /// where p and q are ordered lists of the square content in the two positions,
    /// C is the average square content similarity function,
    /// D is the average inverse distance to square with square content
    /// and w7 and w8 are weights.
    /// 
    /// The average square content similarity function is defined as:
    /// average({F(p[i],q[i]) | i in [0, 64)})
    /// where F is the square content similarity function defined in SquareContentSimilarity.
    /// 
    /// The average inverse normalized distance to square with square content is defined as:
    /// average({D(i) | i in [0, 64)})
    /// where D is the inverse distance to square with square content function.
    /// 
    /// The inverse normalized distance to square with square content is defined as:
    /// { (1 - (A(i) / M))  if any j in [0, 64) p[i] = q[j],
    ///   0.0               otherwise }
    /// where A is the shortest manhattan distance to square in q containing p[i],
    /// and M is the max manhattan distance.
    public unsafe struct Casebase
    {
        private readonly IReadOnlyList<Case> cases;

        public Casebase(IReadOnlyList<Case> cases)
        {
            this.cases = cases;
        }

        public Case FindMostSimilarCase(BarePosition currentPosition, Color color, Random random, Weights weights)
        {
            Contract.Assert(this.cases.Count > 1, "Needs at least one case to use");

            var currentPositionBytes = currentPosition.Bytes;
            double highestSimilarity = double.MinValue;

            List<Case> bestMatchingCases = new List<Case>();
            foreach (var @case in this.cases)
            {
                double caseSimilarity = Similarity(@case.BarePosition.Bytes, currentPositionBytes);
                if (@case.Color == color && MathUtility.IsGreaterThen(caseSimilarity, highestSimilarity))
                {
                    bestMatchingCases.Clear();
                    bestMatchingCases.Add(@case);
                    highestSimilarity = caseSimilarity;
                }
                else if (MathUtility.AreEqual(caseSimilarity, highestSimilarity))
                {
                    bestMatchingCases.Add(@case);
                }
            }
            Debug.Assert(bestMatchingCases.Any(), "There has to be at least one case that has a similarity higher than minus infinity");

            return bestMatchingCases[random.Next(maxValue: bestMatchingCases.Count)];
        }

        private static double Similarity(byte* a, byte* b)
        {
            // Hotspot, implemented for performance.
            double similaritySum = 0;
            for (int i = 0; i < 64; i++)
            {
                similaritySum += SquareContentSimilarity.Similarity(
                    SquareContent.FromByte(*(a + i)),
                    SquareContent.FromByte(*(b + i)));
            }

            var averageSquareContentSimilarity = similaritySum / 64;
            Debug.Assert(MathUtility.InRange(averageSquareContentSimilarity, 0, 1), "Similairity should be in [0, 1)");
            return averageSquareContentSimilarity;
        }
    }
}