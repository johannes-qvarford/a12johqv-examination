namespace a12johqv.Examination.Chess
{
    using System.Globalization;

    /// Utility functions for colors.
    public static class ColorUtility
    {
        /// Returns whether the character representation of a chess of the given color
        /// is a capital letter or not.
        /// Returns true if color is white, false otherwise.
        public static bool IsCharacterRepresentationCapitalized(this Color color)
        {
            return color == Color.White;
        }

        /// Returns the color of the chess piece that the given chracter represents.
        /// Returns Color.White if character is upper case, Color.Black otherwise.
        public static Color ColorOfCharacter(char character)
        {
            string str = character.ToString(CultureInfo.InvariantCulture);
            return str == str.ToUpper() ? Color.White : Color.Black;
        }

        /// Returns the opposite color of a given color.
        /// Returns Color.White if Color.Black, Color.Black otherwise.
        public static Color OppositeColor(this Color color)
        {
            return color == Color.White ? Color.Black : Color.White;
        }
    }
}