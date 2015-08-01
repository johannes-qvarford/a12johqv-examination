namespace a12johqv.Examination.Chess.Tests
{
    using System;

    using NUnit.Framework;

    using Assert = NUnit.Framework.Assert;

    [TestFixture]
    public class SquareContentTest
    {
        private const PieceType APiece = PieceType.Bishop;

        private const Color AColor = Color.Black;

        private const PieceType ADifferentPiece = PieceType.King;

        private const Color ADifferentColor = Color.White;

        [Test]
        public void FromPieceAndColorReturnsSquareContentWithPieceAndColor()
        {
            SquareContent squareContent = SquareContent.FromPieceAndColor(APiece, AColor);

            Assert.AreEqual(APiece, squareContent.PieceTypeOnSquare);
            Assert.AreEqual(AColor, squareContent.ColorOnSquare);
        }

        [Test]
        public void FromPieceAndColorReturnsNonEmptySquareContent()
        {
            SquareContent squareContent = SquareContent.FromPieceAndColor(APiece, AColor);

            Assert.IsFalse(squareContent.IsEmpty);
        }

        [Test]
        public void GetColorOnSquareThrowsInvalidOperationExceptionOnEmptySquare()
        {
            SquareContent squareContent = SquareContent.Empty;

            Assert.Throws<InvalidOperationException>(delegate { var v = squareContent.ColorOnSquare; });
        }

        [Test]
        public void GetPieceTypeOnSquareThrowsInvalidOperationExceptionOnEmptySquare()
        {
            SquareContent squareContent = SquareContent.Empty;

            Assert.Throws<InvalidOperationException>(delegate { var v = squareContent.PieceTypeOnSquare; });
        }

        [Test]
        public void FromStringReturnsBlackBishopForLowerB()
        {
            Assert.AreEqual(SquareContent.FromPieceAndColor(PieceType.Bishop, Color.Black), SquareContent.FromString("b"));
        }

        [Test]
        public void FromStringReturnsWhiteKingForUpperK()
        {
            Assert.AreEqual(SquareContent.FromPieceAndColor(PieceType.King, Color.White), SquareContent.FromString("K"));
        }

        [Test]
        public void FromStringReturnsEmptySquareContentForDot()
        {
            Assert.AreEqual(SquareContent.Empty, SquareContent.FromString("."));
        }

        [Test]
        public void FromStringThrowsArgumentExceptionOnInvalidCharacterRepresentation()
        {
            Assert.Throws<ArgumentException>(() => SquareContent.FromString("w"));
        }

        [Test]
        public void FromStringThrowsArgumentExceptionOnStringWithInvalidLength()
        {
            Assert.Throws<ArgumentException>(() => SquareContent.FromString("K."));
        }

        [Test]
        public void FromStringThrowsArgumentExceptionOnNullString()
        {
            Assert.Throws<ArgumentException>(() => SquareContent.FromString(null));
        }

        public void ToStringReturnsLowerBForBlackBishop()
        {
            Assert.AreEqual("b", SquareContent.FromPieceAndColor(PieceType.Bishop, Color.Black).ToString());
        }

        public void ToStringReturnsUpperKForWhiteKing()
        {
            Assert.AreEqual("K", SquareContent.FromPieceAndColor(PieceType.King, Color.White).ToString());
        }

        public void ToStringReturnsDotForEmptySquareContent()
        {
            Assert.AreEqual(".", SquareContent.Empty);
        }

        #region Equality

        [Test]
        public void EmptySquareIsEqualToEmptySquare()
        {
            SquareContent first = SquareContent.Empty;
            SquareContent second = SquareContent.Empty;
            
            Assert.AreEqual(first, second);
        }

        [Test]
        public void EmptySquareIsNotEqualToSquareWithPiece()
        {
            SquareContent first = SquareContent.Empty;
            SquareContent second = SquareContent.FromPieceAndColor(APiece, AColor);

            Assert.AreNotEqual(first, second);
        }

        [Test]
        public void SquaresWithDifferentPiecesAreNotEqual()
        {
            SquareContent first = SquareContent.FromPieceAndColor(ADifferentPiece, AColor);
            SquareContent second = SquareContent.FromPieceAndColor(APiece, AColor);

            Assert.AreNotEqual(first, second);
        }

        [Test]
        public void SquaresWithDifferentColorsAreNotEqual()
        {
            SquareContent first = SquareContent.FromPieceAndColor(APiece, AColor);
            SquareContent second = SquareContent.FromPieceAndColor(APiece, ADifferentColor);

            Assert.AreNotEqual(first, second);
        }

        [Test]
        public void SquaresWithSameColorsAndPiecesAreEqual()
        {
            SquareContent first = SquareContent.FromPieceAndColor(APiece, AColor);
            SquareContent second = SquareContent.FromPieceAndColor(APiece, AColor);

            Assert.AreEqual(first, second);
        }

        [Test]
        public void SquaresWithSameColorsAndPiecesHaveSameHashCode()
        {
            SquareContent first = SquareContent.FromPieceAndColor(APiece, AColor);
            SquareContent second = SquareContent.FromPieceAndColor(APiece, AColor);

            Assert.AreEqual(first.GetHashCode(), second.GetHashCode());
        }

        [Test]
        public void EmptySquaresHaveSameHashCode()
        {
            SquareContent first = SquareContent.Empty;
            SquareContent second = SquareContent.Empty;

            Assert.AreEqual(first.GetHashCode(), second.GetHashCode());
        }

        #endregion
    }
}