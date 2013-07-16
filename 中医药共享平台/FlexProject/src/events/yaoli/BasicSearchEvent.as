package events.yaoli
{
	
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import flash.events.Event;
	
	public class BasicSearchEvent extends CairngormEvent
	{
		
		public static const EVENT_ID:String = "BasicSearch";
		public var key:String;
		public var source:String;
		public var related:String;
		public var cate:String;
		public var pageIndex:int;
		public var pageSize:int;
		public var identity:int;
		
		public function BasicSearchEvent(key:String, cate:String, source:String, related:String,
		 pageIndex:int, pageSize:int, identity:int)
		{
			this.key = key;
			this.source = source;
			this.related = related;
			this.cate = cate;
			this.pageIndex = pageIndex;
			this.pageSize = pageSize;
			this.identity = identity;
			super(EVENT_ID);
		}

		override public function clone():Event
		{
			return new BasicSearchEvent(key, cate, source, related, pageIndex, pageSize, identity);
		}
	}
}