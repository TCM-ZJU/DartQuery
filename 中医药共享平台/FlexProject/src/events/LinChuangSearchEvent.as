package events
{
	
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import flash.events.Event;
	
	public class LinChuangSearchEvent extends CairngormEvent
	{
		public static const EVENT_ID:String = "LinchuangSearch";
		
		public function LinChuangSearchEvent()
		{
			super(EVENT_ID);
		}
		
		override public function clone():Event
		{
			return new LinChuangSearchEvent();
		}

	}
}