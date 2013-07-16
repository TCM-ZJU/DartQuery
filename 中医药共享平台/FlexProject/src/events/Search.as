package events
{
	import flash.events.Event;

	public class Search extends Event
	{
		public static const TYPE:String = "search";
		public function Search(type:String)
		{
			super(type);
		}
		
	}
}