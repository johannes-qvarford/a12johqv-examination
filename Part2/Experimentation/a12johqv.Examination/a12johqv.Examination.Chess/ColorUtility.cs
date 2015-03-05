namespace a12johqv.Examination.Chess
{
    using System.Globalization;

    public static class ColorUtility
    {
        public static bool IsCharacterRepresentationCapitalized(this Color color)
        {
            return color == Color.White;
        }

        public static Color ColorOfCharacter(char character)
        {
            string str = character.ToString(CultureInfo.InvariantCulture);
            return str == str.ToUpper() ? Color.White : Color.Black;
        }

        public static Color OppositeColor(this Color color)
        {
            return color == Color.White ? Color.Black : Color.White;
        }
    }
}
