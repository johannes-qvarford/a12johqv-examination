namespace a12johqv.Examination.Application
{
    using System.IO;
    using System.Xml;
    using System.Xml.Linq;

    class Program
    {
        static void Main(string[] args)
        {
            string s = File.ReadAllText("res/players_list_xml.xml");
            var reader = new XmlTextReader(s);
            XNode node = XNode.ReadFrom(reader);
            int a = 0;
        }
    }
}
