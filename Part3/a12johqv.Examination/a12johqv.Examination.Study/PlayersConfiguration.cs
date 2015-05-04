namespace a12johqv.Examination.Study
{
    using System.Collections.Generic;
    using System.Linq;
    using System.Xml.Linq;

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
            var name = xElement.Element("name").Value;
            var index = int.Parse(xElement.Element("index").Value);
            var ranking = int.Parse(xElement.Element("ranking").Value);
            var filename = xElement.Element("filename").Value;
            return new Player(name: name, index: index, ranking: ranking, filename: filename);
        }
    }
}