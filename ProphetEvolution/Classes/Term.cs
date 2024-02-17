namespace ProphetEvolution.Classes
{
    public class Term
    {
        public string Name {  get; set; }

        public string Definition { get; set; } = null;

        public List<string>? Alias { get; set; }

        public string? Parent { get; set; } = null;


    }
}
