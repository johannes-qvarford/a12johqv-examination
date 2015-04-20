namespace a12johqv.Examination.Ai.Tests
{
    using a12johqv.Examination.Chess;

    using NUnit.Framework;

    [TestFixture]
    public class CaseTest
    {
        private readonly Move moveA = Move.FromSquareToSquare(Square.FromSquareIndex(4), Square.FromSquareIndex(5));

        private readonly Position positionA = Position.Initial;

        private readonly Move moveB = Move.FromSquareToSquare(Square.FromSquareIndex(3), Square.FromSquareIndex(0));

        private readonly Position positionB = Position.Initial.ByMove(Move.FromString("b2b4"));

        private readonly Color colorA = Color.White;

        private readonly Color colorB = Color.Black;

        [Test]
        public void PositionIsTheOneItIsConstructedWith()
        {
            var @case = new Case(this.positionA, this.moveA, this.colorA);

            Assert.AreEqual(this.positionA, @case.Position);
        }

        [Test]
        public void MoveIsTheOneItIsConstructedWith()
        {
            var @case = new Case(this.positionA, this.moveA, this.colorA);

            Assert.AreEqual(this.moveA, @case.Move);
        }

        [Test]
        public void ColorIsTheOneItIsConstructedWith()
        {
            var @case = new Case(this.positionA, this.moveA, this.colorA);

            Assert.AreEqual(this.colorA, @case.Color);
        }

        [Test]
        public void CasesAreEqualIfBothOfTheirPositionsAndMovesAreEqual()
        {
            var caseA = new Case(this.positionA, this.moveA, this.colorA);
            var caseB = new Case(this.positionA, this.moveA, this.colorA);

            Assert.AreEqual(caseA, caseB);
        }

        [Test]
        public void CasesAreNotEqualIfTheirPositionsAreNotEqual()
        {
            var caseA = new Case(this.positionA, this.moveA, this.colorA);
            var caseB = new Case(this.positionB, this.moveA, this.colorA);

            Assert.AreNotEqual(caseA, caseB);
        }

        [Test]
        public void CasesAreNotEqualIfTheirMovesAreNotEqual()
        {
            var caseA = new Case(this.positionA, this.moveA, this.colorA);
            var caseB = new Case(this.positionA, this.moveB, this.colorA);

            Assert.AreNotEqual(caseA, caseB);
        }

        [Test]
        public void CasesAreNotEqualIfTheirColorsAreNotEqual()
        {
            var caseA = new Case(this.positionA, this.moveA, this.colorA);
            var caseB = new Case(this.positionA, this.moveA, this.colorB);

            Assert.AreNotEqual(caseA, caseB);
        }

        [Test]
        public void EqualCasesHaveEqualHashCodes()
        {
            var caseA = new Case(this.positionA, this.moveA, this.colorA);
            var caseB = new Case(this.positionA, this.moveA, this.colorA);

            Assert.AreEqual(caseA.GetHashCode(), caseB.GetHashCode());
        }
    }
}