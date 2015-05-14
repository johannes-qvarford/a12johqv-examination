namespace a12johqv.Examination.Study
{
    using System;
    using System.IO;

    /// Utility functions for writing generated content.
    public static class GeneratedContentStreaming
    {
        public static Stream OpenStreamForGameReports(DateTime dateTime)
        {
            return File.Open(CreateFilenameForGameReports(dateTime), FileMode.CreateNew);
        }

        private static string CreateFilenameForGameReports(DateTime dateTime)
        {
            const string Directory = "Resources/Generated";
            const string Basename = "GameReports";
            const string Extension = ".pgn";

            var guid = Guid.NewGuid();
            return string.Format("{0}/{1} {2}{3}", Directory, Basename, guid, Extension);
        }
    }
}