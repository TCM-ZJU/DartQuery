package events.yaoli
{
	import flash.events.Event;
	
	public class ShowRecordEvent extends Event
	{
		public static const EVENT_ID:String = "showRecordEvent";
		
		public var content:String;
		
		public function ShowRecordEvent(content:String)
		{
			super(EVENT_ID, true);
			this.content = content;
		}

	}
}