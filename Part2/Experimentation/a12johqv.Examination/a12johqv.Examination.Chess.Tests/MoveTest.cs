namespace a12johqv.Examination.Chess.Tests
{
    using System;

    using NUnit.Framework;

    [TestFixture]
    public class MoveTest
    {
        private Square aFromSquare;

        private Square aToSquare;

        private Move aFromToMove;

        private string aMoveString;

        private Move aFromStringMove;

        private Square aDifferentFromSquare;

        private Square aDifferentToSquare;

        private Move aDifferentFromToMove;

        [SetUp]
        public void Setup()
        {
            this.aFromSquare = Square.FromRowAndColumn(1, 2);
            this.aToSquare = Square.FromRowAndColumn(3, 4);
            this.aFromToMove = Move.FromSquareToSquare(this.aFromSquare, this.aToSquare);

            this.aMoveString = "c2e4";
            this.aFromStringMove = Move.FromString(this.aMoveString);

            this.aDifferentFromSquare = Square.FromRowAndColumn(0, 0);
            this.aDifferentToSquare = Square.FromRowAndColumn(3, 5);
            this.aDifferentFromToMove = Move.FromSquareToSquare(this.aDifferentFromSquare, this.aDifferentToSquare);
        }

        [Test]
        public void FromSquareToSquareReturnsMoveWithGivenFromSquare()
        {
            Assert.AreEqual(this.aFromSquare, this.aFromToMove.From);
        }

        [Test]
        public void FromSquareToSquareReturnsMoveWithGivenToSquare()
        {
            Assert.AreEqual(this.aToSquare, this.aFromToMove.To);
        }

        [Test]
        public void FromStringReturnsMoveFromSquaresEncodedInString()
        {
            Assert.AreEqual(this.aFromToMove, this.aFromStringMove);
        }

        [Test]
        public void FromStringThrowsArgumentExceptionOnStringOfWrongLength()
        {
            Assert.Throws<ArgumentException>(() => Move.FromString(this.aMoveString + "extra"));
        }

        [Test]
        public void FromStringThrowsArgumentExceptionOnNull()
        {
            Assert.Throws<ArgumentException>(() => Move.FromString(null));
        }

        [Test]
        public void ToStringFromStringReturnsTheOriginalMove()
        {
            Assert.AreEqual(this.aFromToMove, Move.FromString(this.aFromToMove.ToString()));
        }

        [Test]
        public void MovesWithDifferentFromSquaresAreNotEqual()
        {
            Assert.AreNotEqual(this.aFromToMove, Move.FromSquareToSquare(this.aFromSquare, this.aDifferentToSquare));
        }

        [Test]
        public void MovesWithDifferentToSquaresAreNotEqual()
        {
            Assert.AreNotEqual(this.aFromToMove, Move.FromSquareToSquare(this.aDifferentFromSquare, this.aToSquare));
        }

        [Test]
        public void MovesWithSameFromAndToSquaresAreEqual()
        {
            Assert.AreEqual(this.aFromToMove, this.aFromStringMove);
        }

        [Test]
        public void MovesWithSameFromAndToSquaresHaveSameHashCodes()
        {
            Assert.AreEqual(this.aFromToMove.GetHashCode(), this.aFromStringMove.GetHashCode());
        }
    }
}