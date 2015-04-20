namespace a12johqv.Examination.Chess.Tests
{
    using System;

    using NSubstitute.Core;

    using NUnit.Framework;

    using Assert = NUnit.Framework.Assert;

    [TestFixture]
    public class SquareTest
    {
        private const int ARow = 4;

        private const int AColumn = 3;

        private const int AnIndex = 35;

        private const int ADifferentIndex = 54;

        private const int LowerRowColumnBound = 0;

        private const int UnderLowerRowColumnBound = -1;

        private const int HigherRowColumnBound = 7;

        private const int OverHigherRowColumnBound = 8;

        private const int LowerSquareIndexBound = 0;

        private const int UnderLowerSquareIndexBound = -1;

        private const int HigherSquareIndexBound = 63;

        private const int OverHigherSquareIndexBound = 64;

        private Square aRowColumnSquare;

        private Square anIndexSquare;

        private Square aDifferentIndexSquare;

        [SetUp]
        public void Setup()
        {
            this.aRowColumnSquare = Square.FromRowAndColumn(ARow, AColumn);
            this.anIndexSquare = Square.FromSquareIndex(AnIndex);
            this.aDifferentIndexSquare = Square.FromSquareIndex(ADifferentIndex);
        }

        [Test]
        public void FromRowAndColumnReturnsSquareWithCorrectRow()
        {
            Assert.AreEqual(this.aRowColumnSquare.Row, ARow);
        }

        [Test]
        public void FromRowAndColumnReturnsSquareWithCorrectColumn()
        {
            Assert.AreEqual(this.aRowColumnSquare.Column, AColumn);
        }

        [Test]
        public void FromRowAndColumnReturnsSquareWithCorrectIndex()
        {
            Assert.AreEqual(this.aRowColumnSquare.SquareIndex, AnIndex);
        }

        [Test]
        public void FromRowAndColumnThrowsArgumentExceptionOnToHighRow()
        {
            Assert.Throws<ArgumentException>(() => 
                Square.FromRowAndColumn(OverHigherRowColumnBound, AColumn));
        }

        [Test]
        public void FromRowAndColumnDoesntThrowExceptionOnUpperBoundRow()
        {
            Square.FromRowAndColumn(HigherRowColumnBound, AColumn);
        }

        [Test]
        public void FromRowAndColumnThrowsArgumentExceptionOnToLowRow()
        {
            Assert.Throws<ArgumentException>(() =>
            Square.FromRowAndColumn(UnderLowerRowColumnBound, AColumn));
        }

        [Test]
        public void FromRowAndColumnDoesntThrowExceptionOnLowerBoundRow()
        {
            Square.FromRowAndColumn(LowerRowColumnBound, AColumn);
        }

        [Test]
        public void FromRowAndColumnThrowsArgumentExceptionOnToHighColumn()
        {
            Assert.Throws<ArgumentException>(() => 
                Square.FromRowAndColumn(ARow, OverHigherRowColumnBound));
        }

        [Test]
        public void FromRowAndColumnDoesntThrowExceptionOnUpperBoundColumn()
        {
            Square.FromRowAndColumn(ARow, HigherRowColumnBound);
        }

        [Test]
        public void FromRowAndColumnThrowsArgumentExceptionOnToLowColumn()
        {
            Assert.Throws<ArgumentException>(() => 
                Square.FromRowAndColumn(ARow, UnderLowerRowColumnBound));
        }

        [Test]
        public void FromRowAndColumnDoesntThrowExceptionOnLowerBoundColumn()
        {
            Square.FromRowAndColumn(ARow, LowerRowColumnBound);
        }

        [Test]
        public void FromSquareIndexReturnsSquareWithCorrectRow()
        {
            Assert.AreEqual(this.anIndexSquare.Row, ARow);
        }

        [Test]
        public void FromSquareIndexReturnsSquareWithCorrectColumn()
        {
            Assert.AreEqual(this.anIndexSquare.Column, AColumn);
        }

        [Test]
        public void FromSquareIndexReturnsSquareWithCorrectIndex()
        {
            Assert.AreEqual(this.anIndexSquare.SquareIndex, AnIndex);
        }

        [Test]
        public void FromSquareIndexThrowsArgumentExceptionOnToLowSquareIndex()
        {
            Assert.Throws<ArgumentException>(() =>
                Square.FromSquareIndex(UnderLowerSquareIndexBound));
        }

        [Test]
        public void FromSquareIndexDoesntThrowOnLowerSquareIndexBound()
        {
            Square.FromSquareIndex(LowerSquareIndexBound);
        }

        [Test]
        public void FromSquareIndexThrowsArgumentExceptionOnToHighSquareIndex()
        {
            Assert.Throws<ArgumentException>(() =>
                Square.FromSquareIndex(OverHigherSquareIndexBound));
        }

        [Test]
        public void FromSquareIndexDoesntThrowOnHigherSquareIndexBound()
        {
            Square.FromSquareIndex(HigherSquareIndexBound);
        }

        [Test]
        public void ToStringHasCorrectFormat()
        {
            Assert.AreEqual("b3", Square.FromRowAndColumn(2, 1).ToString());
        }

        [Test]
        public void FromStringReturnsCorrectSquare()
        {
            Assert.AreEqual(Square.FromRowAndColumn(2, 1), Square.FromString("b3"));
        }

        [Test]
        public void FromStringThrowsArgumentExceptionOnInvalidRow()
        {
            Assert.Throws<ArgumentException>(() => Square.FromString("l2"));
        }

        [Test]
        public void FromStringThrowsArgumentExceptionOnInvalidColumn()
        {
            Assert.Throws<ArgumentException>(() => Square.FromString("f0"));
        }

        [Test]
        public void FromStringThrowsArgumentExceptionOnWrongStringLength()
        {
            Assert.Throws<ArgumentException>(() => Square.FromString("a2x"));
        }

        [Test]
        public void FromStringThrowsArgumentExceptionOnNull()
        {
            Assert.Throws<ArgumentException>(() => Square.FromString(null));
        }

        [Test]
        public void SquaresWithDifferentSquareIndicesAreNotEqual()
        {
            Assert.AreNotEqual(this.anIndexSquare, this.aDifferentIndexSquare);
        }

        [Test]
        public void SquaresWithSameSquareIndexAreEqual()
        {
            Square first = Square.FromSquareIndex(AnIndex);
            Square second = Square.FromSquareIndex(AnIndex);

            Assert.AreEqual(first, second);
        }

        [Test]
        public void SquaresAreNotEqualToObjectsOfDifferentTypes()
        {
            Assert.AreNotEqual(10, this.anIndexSquare);
        }

        [Test]
        public void SquaresAreNotEqualToNull()
        {
            Assert.AreNotEqual(null, this.anIndexSquare);
        }

        [Test]
        public void SquaresWithSameSquareIndexHaveSameHashCode()
        {
            Square first = Square.FromSquareIndex(AnIndex);
            Square second = Square.FromSquareIndex(AnIndex);

            Assert.AreEqual(first.GetHashCode(), second.GetHashCode());
        }
    }
}