namespace a12johqv.Examination.Study
{
    using System.Collections.Generic;
    using System.Linq;
    using System.Xml.Linq;

    using a12johqv.Examination.Ai;

    /// Utility functions for loading player list from files.
    public static class PlayersConfiguration
    {
        private const string ConfigurationFilename = "Resources/Players.config";

        public static IEnumerable<Player> LoadPlayers(string filename = ConfigurationFilename)
        {
            XElement root = XElement.Load(filename);
            return root.Elements().Select(CreatePlayerFromXElement);
        }

        private static Player CreatePlayerFromXElement(XElement xElement)
        {
            return new Player(
                name: xElement.Element("name").Value,
                index: int.Parse(xElement.Element("index").Value),
                ranking: int.Parse(xElement.Element("ranking").Value),
                filename: xElement.Element("filename").Value);
        }
    }
}