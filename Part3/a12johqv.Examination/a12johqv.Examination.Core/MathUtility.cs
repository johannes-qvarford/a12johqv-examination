﻿namespace a12johqv.Examination.Core
{
    using System;

    /// Math extention methods.
    public static class MathUtility
    {
        private const double Epsilon = 0.001;

        private static bool AreEqual(double a, double b, double epsilon = Epsilon)
        {
            return Distance(a, b) < epsilon;
        }

        public static bool InRange(double number, double lowerInclusive, double upperExclusive)
        {
            return number >= lowerInclusive - Epsilon && number < upperExclusive + Epsilon;
        }

        public static double Distance(double a, double b)
        {
            return Math.Abs(a - b);
        }
    }
}