package events
{
	
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import flash.events.Event;
	
	public class EntrySearchEvent extends CairngormEvent
	{
		public static const EVENT_ID:String = "EntrySearch";
		
		public function EntrySearchEvent()
		{
			
			super(EVENT_ID);
		}
		
		override public function clone():Event
		{
			return new EntrySearchEvent();
		}
	}
}