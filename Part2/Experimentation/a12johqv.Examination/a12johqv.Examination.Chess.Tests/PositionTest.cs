namespace a12johqv.Examination.Chess.Tests
{
    using System;
    using System.Collections.Generic;

    using NUnit.Framework;

    [TestFixture]
    public class PositionTest
    {
        private const string InitialString = 
              "rnbqkbnr"
            + "pppppppp"
            + "........"
            + "........"
            + "........"
            + "........"
            + "PPPPPPPP"
            + "RNBQKBNR";

        [Test]
        public void ToStringContentMatchesInitialString()
        {
            Assert.AreEqual(InitialString, Position.Initial.ToString());
        }

        [Test]
        public void FromStringToStringReturnsTheOriginalString()
        {
            Assert.AreEqual(InitialString, Position.FromString(InitialString).ToString());
        }

        [Test]
        public void SquareContentsFromLowRowAndColumnHasAWhiteRookAsFirstElement()
        {
            Position initialPosition = Position.Initial;
            IReadOnlyList<SquareContent> squaresContents = initialPosition.SquareContentsFromLowRowAndColumn;

            bool isWhiteRook = HasColorAndPieceType(squaresContents[0], Color.White, PieceType.Rook);

            Assert.IsTrue(isWhiteRook);
        }

        [Test]
        public void SquareContentsFromLowRowAndColumnHasWhiteKnightAsSeventhElement()
        {
            Position initialPosition = Position.Initial;
            IReadOnlyList<SquareContent> squaresContents = initialPosition.SquareContentsFromLowRowAndColumn;

            // Using 6 because index is 0 based.
            bool isWhiteKnight = HasColorAndPieceType(squaresContents[6], Color.White, PieceType.Knight);

            Assert.IsTrue(isWhiteKnight);
        }

        [Test]
        public void SquareContentsFromLowRowAndColumnHasBlackBishopAsFiftyNinthElement()
        {
            Position initialPosition = Position.Initial;
            IReadOnlyList<SquareContent> squaresContents = initialPosition.SquareContentsFromLowRowAndColumn;

            bool isBlackBishop = HasColorAndPieceType(squaresContents[58], Color.Black, PieceType.Bishop);

            Assert.IsTrue(isBlackBishop);
        }

        private static bool HasColorAndPieceType(SquareContent squareContent, Color color, PieceType pieceType)
        {
            Color? theColor = squareContent.ColorOnSquare;
            PieceType? thePieceType = squareContent.PieceTypeOnSquare;
            return theColor.HasValue
                && theColor.Value == color
                && thePieceType.HasValue
                && thePieceType.Value == pieceType;
        }

        [Test]
        public void ByMoveMovesPieceFromSquareToSquare()
        {
            Move move = Move.FromString("d2d4");
            const string ExpectedPositonString = 
                  "rnbqkbnr"
                + "pppppppp"
                + "........"
                + "........"
                + "...P...."
                + "........"
                + "PPP.PPPP"
                + "RNBQKBNR";

            Position actualPosition = Position.Initial.ByMove(move);

            Assert.AreEqual(ExpectedPositonString, actualPosition.ToString());
        }

        [Test]
        public void ByMovePerformsAnPassantCorrectlyFromWhitesSide()
        {
            Move move = Move.FromString("d5c6");
            const string StartPositionString =
                  "rnbqkbnr"
                + "pp.ppppp"
                + "........"
                + "..pP...."
                + "........"
                + "........"
                + "PPP.PPPP"
                + "RNBQKBNR";
            const string ExpectedPositionString =
                  "rnbqkbnr"
                + "pp.ppppp"
                + "..P....."
                + "........"
                + "........"
                + "........"
                + "PPP.PPPP"
                + "RNBQKBNR";

            AssertByMove(
                startPositionString: StartPositionString,
                move: move,
                expectedPositionString: ExpectedPositionString);
        }

        [Test]
        public void ByMovePerformsAnPassantCorrectlyFromBlacksSide()
        {
            Move move = Move.FromString("c4d3");
            const string StartPositionString =
                  "rnbqkbnr"
                + "pp.ppppp"
                + "........"
                + "........"
                + "..pP...."
                + "........"
                + "PPP.PPPP"
                + "RNBQKBNR";
            const string ExpectedPositionString =
                  "rnbqkbnr"
                + "pp.ppppp"
                + "........"
                + "........"
                + "........"
                + "...p...."
                + "PPP.PPPP"
                + "RNBQKBNR";

            AssertByMove(
                startPositionString: StartPositionString,
                move: move,
                expectedPositionString: ExpectedPositionString);
        }

        [Test]
        public void ByMovePerformsShortCastlingCorrectlyFromWhitesSide()
        {
            Move move = Move.FromString("e1g1");
            const string StartPositionString =
                  "r...k..r"
                + "........"
                + "........"
                + "........"
                + "........"
                + "........"
                + "........"
                + "R...K..R";
            const string ExpectedPositionString =
                  "r...k..r"
                + "........"
                + "........"
                + "........"
                + "........"
                + "........"
                + "........"
                + "R....RK.";

            AssertByMove(
                startPositionString: StartPositionString,
                move: move,
                expectedPositionString: ExpectedPositionString);
        }

        [Test]
        public void ByMovePerformsLongCastlingCorrectlyFromWhitesSide()
        {
            Move move = Move.FromString("e1c1");
            const string StartPositionString =
                  "r...k..r"
                + "........"
                + "........"
                + "........"
                + "........"
                + "........"
                + "........"
                + "R...K..R";
            const string ExpectedPositionString =
                  "r...k..r"
                + "........"
                + "........"
                + "........"
                + "........"
                + "........"
                + "........"
                + "..KR...R";

            AssertByMove(
                startPositionString: StartPositionString,
                move: move,
                expectedPositionString: ExpectedPositionString);
        }

        [Test]
        public void ByMovePerformsShortCastlingCorrectlyFromBlacksSide()
        {
            Move move = Move.FromString("e8g8");
            const string StartPositionString =
                  "r...k..r"
                + "........"
                + "........"
                + "........"
                + "........"
                + "........"
                + "........"
                + "R...K..R";
            const string ExpectedPositionString =
                  "r....rk."
                + "........"
                + "........"
                + "........"
                + "........"
                + "........"
                + "........"
                + "R...K..R";

            AssertByMove(
                startPositionString: StartPositionString,
                move: move,
                expectedPositionString: ExpectedPositionString);
        }

        [Test]
        public void ByMovePerformsLongCastlingCorrectlyFromBlacksSide()
        {
            Move move = Move.FromString("e8c8");
            const string StartPositionString =
                  "r...k..r"
                + "........"
                + "........"
                + "........"
                + "........"
                + "........"
                + "........"
                + "R...K..R";
            const string ExpectedPositionString =
                  "..kr...r"
                + "........"
                + "........"
                + "........"
                + "........"
                + "........"
                + "........"
                + "R...K..R";

            AssertByMove(
                startPositionString: StartPositionString,
                move: move,
                expectedPositionString: ExpectedPositionString);
        }

        private static void AssertByMove(string startPositionString, Move move, string expectedPositionString)
        {
            Position startPosition = Position.FromString(startPositionString);
            Position expectedPosition = Position.FromString(expectedPositionString);

            Position actualPosition = startPosition.ByMove(move);

            Assert.AreEqual(expectedPosition, actualPosition);
        }
    }
}
