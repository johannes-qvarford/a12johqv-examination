namespace a12johqv.Examination.Chess
{
    /// Utility functions for sides.
    public static class SideUtility
    {
        public static bool IsLeft(this Side side)
        {
            return side == Side.Left;
        }

        public static bool IsRight(this Side side)
        {
            return side == Side.Right;
        }
    }
}