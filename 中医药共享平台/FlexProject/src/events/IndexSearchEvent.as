package events
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import flash.events.Event;
	
	public class IndexSearchEvent extends CairngormEvent
	{
		public static const EVENT_ID:String = "IndexSearch";
		public var key:String;
		public var source:String;
		public var pageIndex:int;
		public var pageSize:int;
		
		public function IndexSearchEvent(key:String, source:String, pageIndex:int, pageSize:int)
		{
			this.key = key;
			this.source = source;
			this.pageIndex = pageIndex;
			this.pageSize = pageSize;
			super(EVENT_ID);
		}
		
		override public function clone():Event
		{
			return new IndexSearchEvent(key, source, pageIndex, pageSize);
		}

	}
}