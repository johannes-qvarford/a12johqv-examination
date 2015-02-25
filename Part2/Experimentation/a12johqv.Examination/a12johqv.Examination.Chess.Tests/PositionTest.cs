namespace a12johqv.Examination.Chess.Tests
{
    using System;

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
            Position startPosition = Position.FromString(StartPositionString);

            Position actualPosition = startPosition.ByMove(move);

            Assert.AreEqual(ExpectedPositionString, actualPosition.ToString());
        }
    }
}
