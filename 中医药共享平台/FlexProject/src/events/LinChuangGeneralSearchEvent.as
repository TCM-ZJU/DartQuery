package events
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import flash.events.Event;
	
	public class LinChuangGeneralSearchEvent extends CairngormEvent
	{
		public static const EVENT_ID:String = "LinchuangGeneralSearch";
		public var key:String;
		public var source:String;
		public var related:String;
		
		public function LinChuangGeneralSearchEvent(key:String, source:String, related:String)
		{
			this.key = key;
			this.source = source;
			this.related = related;
			super(EVENT_ID);
		}
		
		override public function clone():Event
		{
			return new LinChuangGeneralSearchEvent(key, source, related);
		}

	}
}