package events
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import flash.events.Event;
	
	public class LinchuangPicSearchEvent extends CairngormEvent
	{
		public static const EVENT_ID:String = "LinchuangPicSearch";
		public var key:String;
		public var source:String;
		public var related:String;
		public var pageIndex:int;
		public var pageSize:int;
		
		public function LinchuangPicSearchEvent(key:String, source:String, 
			related:String, pageIndex:int, pageSize:int)
		{
			this.key = key;
			this.source = source;
			this.related = related;
			this.pageIndex = pageIndex;
			this.pageSize = pageSize;
			super(EVENT_ID);
		}
		
		override public function clone():Event
		{
			return new LinchuangPicSearchEvent(key, source, related, pageIndex, pageSize);
		}
	}
}