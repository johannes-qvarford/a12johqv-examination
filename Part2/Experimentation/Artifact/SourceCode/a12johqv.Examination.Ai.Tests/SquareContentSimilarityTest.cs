﻿namespace a12johqv.Examination.Ai.Tests
{
    using a12johqv.Examination.Chess;
    using a12johqv.Examination.Core;

    using NUnit.Framework;

    [TestFixture]
    public class SquareContentSimilarityTest
    {
        [Test]
        public void SquareContentSimilarityIsIn0To1ForEqualEmptySquareContent()
        {
            var emptyA = SquareContent.Empty;

            var similarity = SquareContentSimilarity.Similarity(emptyA, emptyA);

            Assert.IsTrue(MathUtility.InRange(similarity, 0, 1));
        }

        [Test]
        public void SquareContentSimilarityIsIn0To1ForEmptySquareContentAndBlackKnight()
        {
            var empty = SquareContent.Empty;
            var blackKnight = SquareContent.FromString("n");

            var similarity = SquareContentSimilarity.Similarity(empty, blackKnight);

            Assert.IsTrue(MathUtility.InRange(similarity, 0, 1));
        }

        [Test]
        public void SquareContentSimilarityIsIn0To1ForEqualBlackKnights()
        {
            var blackKnight = SquareContent.FromString("n");

            var similarity = SquareContentSimilarity.Similarity(blackKnight, blackKnight);

            Assert.IsTrue(MathUtility.InRange(similarity, 0, 1));
        }

        [Test]
        public void SquareContentSimilarityIsIn0To1ForBlackKnightAndWhiteKnight()
        {
            var blackKnight = SquareContent.FromString("n");
            var whiteKnight = SquareContent.FromString("N");

            var similarity = SquareContentSimilarity.Similarity(blackKnight, whiteKnight);

            Assert.IsTrue(MathUtility.InRange(similarity, 0, 1));
        }

        [Test]
        public void SquareContentSimilarityIsIn0To1ForBlackKnightAndBlackKing()
        {
            var blackKnight = SquareContent.FromString("n");
            var blackKing = SquareContent.FromString("k");

            var similarity = SquareContentSimilarity.Similarity(blackKing, blackKnight);

            Assert.IsTrue(MathUtility.InRange(similarity, 0, 1));
        }

        [Test]
        public void SquareContentSimilarityIsIn0To1ForBlackKnightAndWhiteRook()
        {
            var blackKnight = SquareContent.FromString("n");
            var whiteRook = SquareContent.FromString("R");

            var similarity = SquareContentSimilarity.Similarity(blackKnight, whiteRook);

            Assert.IsTrue(MathUtility.InRange(similarity, 0, 1));
        }
    }
}