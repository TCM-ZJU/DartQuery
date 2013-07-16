package events.yaoli
{
	import flash.events.Event;
	
	public class ShowStatEvent extends Event
	{
		public static const EVENT_ID:String = "showStatEvent";
		
		public var content:String;
		
		public function ShowStatEvent(content:String)
		{
			super(EVENT_ID, true);
			this.content = content;
		}

	}
}