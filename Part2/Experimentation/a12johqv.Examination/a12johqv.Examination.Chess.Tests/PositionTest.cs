namespace a12johqv.Examination.Chess.Tests
{
    using System;
    using System.Collections.Generic;
    using System.Collections.Immutable;
    using System.Linq;

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

        #region String Representation

        [Test]
        public void ToStringContentMatchesInitialString()
        {
            Assert.AreEqual(InitialString, Position.Initial.ToString());
        }

        [Test]
        public void FromStringToStringReturnsTheOriginalString()
        {
            var movementEvents = MovementEvents.Initial;
            Assert.AreEqual(InitialString, Position.FromString(InitialString, movementEvents).ToString());
        }

        #endregion

        #region Square Contents From Low Row And Column

        [Test]
        public void SquareContentsFromLowRowAndColumnHasAWhiteRookAsFirstElement()
        {
            Position initialPosition = Position.Initial;
            IEnumerable<SquareContent> squaresContents = initialPosition.SquareContentsFromLowRowAndColumn;

            bool isWhiteRook = HasColorAndPieceType(squaresContents.First(), Color.White, PieceType.Rook);

            Assert.IsTrue(isWhiteRook);
        }

        [Test]
        public void SquareContentsFromLowRowAndColumnHasWhiteKnightAsSeventhElement()
        {
            Position initialPosition = Position.Initial;
            IEnumerable<SquareContent> squaresContents = initialPosition.SquareContentsFromLowRowAndColumn;

            // Using 6 because index is 0 based.
            bool isWhiteKnight = HasColorAndPieceType(squaresContents.ElementAt(6), Color.White, PieceType.Knight);

            Assert.IsTrue(isWhiteKnight);
        }

        [Test]
        public void SquareContentsFromLowRowAndColumnHasBlackBishopAsFiftyNinthElement()
        {
            Position initialPosition = Position.Initial;
            IEnumerable<SquareContent> squaresContents = initialPosition.SquareContentsFromLowRowAndColumn;

            bool isBlackBishop = HasColorAndPieceType(squaresContents.ElementAt(58), Color.Black, PieceType.Bishop);

            Assert.IsTrue(isBlackBishop);
        }

        private static bool HasColorAndPieceType(SquareContent squareContent, Color color, PieceType pieceType)
        {
            Color theColor = squareContent.ColorOnSquare;
            PieceType thePieceType = squareContent.PieceTypeOnSquare;
            return theColor == color && thePieceType == pieceType;
        }

        #endregion

        #region By Move

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
        public void ByMovePromotesPieceWhenDoingPawnPromotion()
        {
            Move move = Move.FromString("c7c8Q");
            const string StartPositionString =
                  "........"
                + "..P....k"
                + "........"
                + "........"
                + "........"
                + "........"
                + "........"
                + "....K...";
            const string ExpectedPositionString =
                  "..Q....."
                + ".......k"
                + "........"
                + "........"
                + "........"
                + "........"
                + "........"
                + "....K...";

            AssertByMove(
                startPositionString: StartPositionString,
                move: move,
                expectedPositionString: ExpectedPositionString);
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
            var movementEvents = MovementEvents.Initial;
            Position startPosition = Position.FromString(startPositionString, movementEvents);
            Position expectedPosition = Position.FromString(expectedPositionString, movementEvents);

            Position actualPosition = startPosition.ByMove(move);

            Assert.AreEqual(expectedPosition, actualPosition);
        }

        #endregion

        #region Valid Moves

        [Test]
        public void ValidMovesForInitialPositionIsAllShortAndLongWhitePawnMovesAndTheKnightsForwardMoves()
        {
            string[] expectedMoveStrings = {
                                               "a2a3", "a2a4", "b2b3", "b2b4", 
                                               "c2c3", "c2c4", "d2d3", "d2d4", 
                                               "e2e3", "e2e4", "f2f3", "f2f4",
                                               "g2g3", "g2g4", "h2h3", "h2h4",
                                               "b1a3", "b1c3", "g1f3", "g1h3"
                                           };
            AssertValidMovesForPosition(expectedMoveStrings);
        }

        [Test]
        public void ValidMovesForWhiteKingIsMovementToOneSquaresAwayHorizontallyVerticallyAndDiagonally()
        {
            const string PositionString =
                  "........"
                + "......k."
                + "........"
                + "........"
                + "........"
                + "........"
                + "......K."
                + "........";
            string[] expectedMoveStrings = { "g2g1", "g2g3", "g2f2", "g2h2", "g2f1", "g2f3", "g2h1", "g2h3" };

            AssertValidMovesForPosition(expectedMoveStrings, PositionString);
        }

        [Test]
        public void ValidMovesForBlackKingIsMovementToOneSquareAwayHorizontallyVerticallyAndDiagonally()
        {
            const string PositionString =
                  "........"
                + "......k."
                + "........"
                + "........"
                + "........"
                + "........"
                + "......K."
                + "........";
            string[] expectedMoveStrings = { "g7g6", "g7g8", "g7f7", "g7h7", "g7f6", "g7f8", "g7h6", "g7h8" };

            // Needed to make it blacks turn.
            var movementEvents = MovementEvents.Initial.WithPerformedMoveByKing(Move.FromString("g1g2"));

            AssertValidMovesForPosition(expectedMoveStrings, PositionString, movementEvents);
        }

        [Test]
        public void ValidMovesForWhiteKingDoesNotIncludeThoseThatWouldLeaveItExposedForCapturing()
        {
            const string PositionString =
                  "........"
                + "........"
                + "........"
                + "........"
                + "......k."
                + "........"
                + "......K."
                + "........";
            string[] expectedMoveStrings = { "g2g1", "g2f2", "g2h2", "g2f1", "g2h1" };

            AssertValidMovesForPosition(expectedMoveStrings, PositionString);
        }

        [Test]
        public void ValidMovesForBlackKingDoesNotIncludedThoseThatWorldLeaveItExposedForCapturing()
        {
            const string PositionString =
                  "........"
                + "........"
                + "........"
                + "........"
                + "......k."
                + "........"
                + "......K."
                + "........";
            string[] expectedMoveStrings = { "g4g5", "g4f4", "g4h4", "g4f5", "g4h5" };

            // Needed to make it blacks turn.
            var movementEvents = MovementEvents.Initial.WithPerformedMoveByKing(Move.FromString("g1g2"));

            AssertValidMovesForPosition(expectedMoveStrings, PositionString, movementEvents);
        }

        [Test]
        public void ValidMovesForNotMovedPawnAndKingIsShortAndLongMoveForPawnAndKingMoves()
        {
            const string PositionString = 
                  ".......k"
                + "........"
                + "........"
                + "........"
                + "........"
                + "........"
                + "P......."
                + ".......K";
            string[] expectedMoveStrings = { "a2a3", "a2a4", "h1h2", "h1g1", "h1g2" };

            AssertValidMovesForPosition(expectedMoveStrings, PositionString);
        }

        [Test]
        public void ValidMovesForMovedPawnAndKingIsShortMoveForPawnAndKingMoves()
        {
            const string PositionString =
                  ".......k"
                + "........"
                + "........"
                + "........"
                + "........"
                + "P......."
                + "........"
                + ".......K";
            string[] expectedMoveStrings = { "a3a4", "h1h2", "h1g1", "h1g2" };

            AssertValidMovesForPosition(expectedMoveStrings, PositionString);
        }

        [Test]
        public void ValidMovesForPawnThatCanCaptureAndKingIsPawnCapturingMoveRestOfPawnMovesAndKingMoves()
        {
            const string PositionString =
                  ".......k"
                + "........"
                + "........"
                + "........"
                + ".p......"
                + "P......."
                + "........"
                + ".......K";
            string[] expectedMoveStrings = { "a3a4", "a3b4", "h1h2", "h1g1", "h1g2" };

            AssertValidMovesForPosition(expectedMoveStrings, PositionString);
        }

        [Test]
        public void ValidMovesForPawnAndKingDoesntIncludeMovesThatTargetsEachOthersSquares()
        {
            const string PositionString =
                  ".......k"
                + "........"
                + "........"
                + "........"
                + "K......."
                + "P......."
                + "........"
                + "........";
            string[] expectedMoveStrings = { "a4a5", "a4b5", "a4b4", "a4b3" };

            AssertValidMovesForPosition(expectedMoveStrings, PositionString);
        }

        [Test]
        public void ValidMovesForQueenIsMovementHorizontallyVerticallyAndDiagonally()
        {
            const string PositionString =
                  ".......k"
                + "........"
                + ".p.p...."
                + "........"
                + "KQ.p...."
                + "........"
                + ".p.p...."
                + "........";
            string[] expectedMoveStrings =
                {
                    // King moves
                    "a4a3", "a4b3", "a4b5",

                    // Up, down
                    "b4b2", "b4b3", "b4b5", "b4b6",

                    // Right
                    "b4c4", "b4d4",

                    // Diagonal left
                    "b4a3", "b4a5",

                    // Diagonal right
                    "b4c5", "b4d6", "b4c3", "b4d2"
                };

            AssertValidMovesForPosition(expectedMoveStrings, PositionString);
        }

        [Test]
        public void ValidMovesForKnightIsTwoStepsAlongOneAxisAndOneStepAlongTheOtherAxis()
        {
            const string PositionString =
                  ".......k"
                + "........"
                + "........"
                + "........"
                + "........"
                + "..N....."
                + "........"
                + ".K......";
            string[] expectedMoveStrings =
                {
                    // Knight moves
                    "c3d1", "c3e2", "c3e4", "c3d5", "c3b5", "c3a4", "c3a2",

                    // King moves
                    "b1a1", "b1a2", "b1b2", "b1c2", "b1c1"
                };

            AssertValidMovesForPosition(expectedMoveStrings, PositionString);
        }

        [Test]
        public void ValidMovesForBishopIsMovementDiagonally()
        {
            const string PositionString =
                  ".......k"
                + "........"
                + "........"
                + "....p..."
                + "........"
                + "..B....."
                + ".K......"
                + "........";
            string[] expectedMoveStrings =
                {
                    // Diagonally up left
                    "c3b4", "c3a5",

                    // Diagonally down right
                    "c3d2", "c3e1",

                    // Diagonally up right
                    "c3d4", "c3e5",

                    // King moves
                    "b2a1", "b2a2", "b2a3", "b2b1", "b2b3", "b2c1", "b2c2"
                };
        }

        private static void AssertValidMovesForPosition(IEnumerable<string> moveStrings, string positionString = null, MovementEvents? movementEvents = null)
        {
            var realMovementEvents = movementEvents.HasValue ? movementEvents.Value : MovementEvents.Initial;
            var position = positionString != null ? Position.FromString(positionString, realMovementEvents) : Position.Initial;
            ImmutableHashSet<Move> moves = ImmutableHashSet.CreateRange(moveStrings.Select(Move.FromString));

            ImmutableHashSet<Move> actualMoves = ImmutableHashSet.CreateRange(position.ValidMoves);

            Assert.AreEqual(moves, actualMoves);
        }

        #endregion
    }
}
