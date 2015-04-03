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

        private PieceType aDifferentPromotion;

        private string aDifferentMoveString;

        private Move aDifferentFromToMove;

        private Move aDifferentFromStringMove;

        [SetUp]
        public void Setup()
        {
            this.aFromSquare = Square.FromRowAndColumn(1, 2);
            this.aToSquare = Square.FromRowAndColumn(3, 4);
            this.aFromToMove = Move.FromSquareToSquare(this.aFromSquare, this.aToSquare);

            this.aMoveString = "c2e4";
            this.aFromStringMove = Move.FromString(this.aMoveString);

            this.aDifferentFromSquare = Square.FromRowAndColumn(6, 0);
            this.aDifferentToSquare = Square.FromRowAndColumn(7, 0);
            this.aDifferentPromotion = PieceType.Queen;
            this.aDifferentFromToMove = Move.FromSquareToSquareWithPromotion(this.aDifferentFromSquare, this.aDifferentToSquare, this.aDifferentPromotion);

            this.aDifferentMoveString = "a7a8Q";
            this.aDifferentFromStringMove = Move.FromString(this.aDifferentMoveString);
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
        public void FromStringReturnsRegularMoveFromSquaresEncodedInStringWithoutPromotion()
        {
            Assert.AreEqual(this.aFromToMove, this.aFromStringMove);
        }

        [Test]
        public void FromStringReturnsPromotionMoveFromSquaresAndPromotionTypeEncodedInString()
        {
            Assert.AreEqual(this.aDifferentFromToMove, this.aDifferentFromStringMove);
        }

        [Test]
        public void FromStringThrowsArgumentExceptionOnToLongString()
        {
            Assert.Throws<ArgumentException>(() => Move.FromString(this.aDifferentMoveString + "_"));
        }

        [Test]
        public void FromStringThrowsArgumentExceptionOnNull()
        {
            Assert.Throws<ArgumentException>(() => Move.FromString(null));
        }

        [Test]
        public void FromSquareToSquareWithPromotionReturnsMoveWithGivenFromSquare()
        {
            Assert.AreEqual(this.aDifferentFromSquare, this.aDifferentFromToMove.From);
        }

        [Test]
        public void FromSquareToSquareWithPromotionReturnsMoveWithGivenToSquare()
        {
            Assert.AreEqual(this.aDifferentToSquare, this.aDifferentFromToMove.To);
        }

        [Test]
        public void FromSquareToSquareWithPromotionIsPromotion()
        {
            Assert.IsTrue(this.aDifferentFromToMove.IsPromotion);
        }

        [Test]
        public void FromSquareToSquareWithPromotionHasGivenPromotionType()
        {
            Assert.AreEqual(this.aDifferentPromotion, this.aDifferentFromToMove.PromotionType);
        }

        [Test]
        public void ToStringFromStringReturnsTheOriginalMoveForRegularMove()
        {
            Assert.AreEqual(this.aFromToMove, Move.FromString(this.aFromToMove.ToString()));
        }

        [Test]
        public void ToStringFromStringReturnsTheOriginalMoveForPromotionMove()
        {
            Assert.AreEqual(this.aDifferentFromToMove, Move.FromString(this.aDifferentFromToMove.ToString()));
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