namespace a12johqv.Examination.Chess.Tests
{
    using NUnit.Framework;

    [TestFixture]
    public class ColorUtilityTest
    {
        [Test]
        public void WhitePiecesAreCapitalizedInCharacterRepresentation()
        {
            Assert.IsTrue(Color.White.IsCharacterRepresentationCapitalized());
        }

        [Test]
        public void BlackPiecesAreNotCapitalizedInCharacterRepresentation()
        {
            Assert.IsFalse(Color.Black.IsCharacterRepresentationCapitalized());
        }
    }
}